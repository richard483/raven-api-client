package com.nephren.raven.apiclient.http;

import com.nephren.raven.apiclient.properties.RavenApiClientProperties.ApiClientConfigProperties;
import com.nephren.raven.apiclient.properties.RavenSharedPoolProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * Default {@link RavenHttpClientFactory} implementation.
 *
 * <p>Maintains a {@link ConcurrentHashMap} of base {@link HttpClient} instances keyed by
 * pool key. Each base owns a dedicated {@link ConnectionProvider} sized by
 * {@link RavenSharedPoolProperties}; isolated keys ({@code "isolated:<clientName>"}) get
 * their own provider too — they are isolated from <em>each other</em> as well as from the
 * shared pools, which would not be the case if the implementation fell back to
 * {@code HttpClient.create()} (that uses Reactor Netty's process-global pool).</p>
 *
 * <p>The pool key folds the configured timeouts in because the
 * {@link ReadTimeoutHandler} / {@link WriteTimeoutHandler} we install attach at the channel
 * level. Sharing a pooled channel between clients with different timeouts would mean the
 * second client gets the first one's handlers, which is silently incorrect. Pool reuse
 * therefore happens only between clients that target the same backend <em>with the same
 * timeouts</em> — typically the entire application.</p>
 *
 * <p>Implements {@link DisposableBean} so the cached {@link ConnectionProvider}s release
 * their resources on application context shutdown. Disposal is time-bounded so a stuck
 * provider cannot hang the shutdown phase.</p>
 */
@Slf4j
public class DefaultRavenHttpClientFactory implements RavenHttpClientFactory, DisposableBean {

  private static final Duration DISPOSE_TIMEOUT = Duration.ofSeconds(30);

  private final RavenSharedPoolProperties sharedPoolProperties;
  private final Map<String, HttpClient> baseClients = new ConcurrentHashMap<>();
  private final Map<String, ConnectionProvider> ownedProviders = new ConcurrentHashMap<>();

  public DefaultRavenHttpClientFactory(RavenSharedPoolProperties sharedPoolProperties) {
    this.sharedPoolProperties = sharedPoolProperties;
  }

  @Override
  public HttpClient httpClient(String clientName, ApiClientConfigProperties config) {
    int connectMs = validateTimeoutMillis(config.getConnectTimeout(), "connect", clientName);
    long readMs = validateTimeoutMillis(config.getReadTimeout(), "read", clientName);
    long writeMs = validateTimeoutMillis(config.getWriteTimeout(), "write", clientName);

    String poolKey = RavenHttpClientFactory.defaultPoolKey(clientName, config);
    HttpClient base = baseClients.computeIfAbsent(poolKey, this::buildBase);
    return base
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectMs)
        .doOnConnected(connection -> connection
            .addHandlerLast(new ReadTimeoutHandler(readMs, TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(writeMs, TimeUnit.MILLISECONDS)));
  }

  /**
   * Reject null, non-positive, or int-overflowing timeouts up front so a misconfiguration
   * surfaces with a clear message instead of silent truncation in Netty.
   */
  private static int validateTimeoutMillis(java.time.Duration value, String name,
      String clientName) {
    if (value == null) {
      throw new IllegalArgumentException(
          "#RavenHttpClientFactory " + name + "Timeout must not be null for client '"
              + clientName + "'");
    }
    long millis = value.toMillis();
    if (millis <= 0) {
      throw new IllegalArgumentException(
          "#RavenHttpClientFactory " + name + "Timeout must be > 0ms for client '"
              + clientName + "', got " + millis + "ms");
    }
    if (millis > Integer.MAX_VALUE) {
      throw new IllegalArgumentException(
          "#RavenHttpClientFactory " + name + "Timeout exceeds the supported range for"
              + " client '" + clientName + "': " + millis + "ms (max "
              + Integer.MAX_VALUE + "ms)");
    }
    return (int) millis;
  }

  private HttpClient buildBase(String poolKey) {
    ConnectionProvider.Builder builder = ConnectionProvider.builder("raven-" + sanitize(poolKey))
        .maxConnections(sharedPoolProperties.getMaxConnections());
    int pendingMax = sharedPoolProperties.getPendingAcquireMaxCount();
    // -1 sentinel → leave the builder unset so Reactor Netty's library default
    // (2 * maxConnections) applies. Any other value is a deliberate override.
    if (pendingMax >= 0) {
      builder.pendingAcquireMaxCount(pendingMax);
    }
    ConnectionProvider provider = builder.build();
    ownedProviders.put(poolKey, provider);
    log.debug(
        "#RavenHttpClientFactory creating HttpClient for {} (maxConnections={},"
            + " pendingAcquireMaxCount={})",
        poolKey,
        sharedPoolProperties.getMaxConnections(),
        pendingMax < 0 ? "default(2*maxConnections)" : pendingMax);
    return HttpClient.create(provider);
  }

  /**
   * Reactor Netty {@code ConnectionProvider} names cannot contain certain characters; strip
   * the URL-flavored ones so the provider name remains valid and human-readable.
   */
  private static String sanitize(String poolKey) {
    return poolKey
        .replace("://", "-")
        .replace(":", "-")
        .replace("/", "-")
        .replace("|", "-")
        .replace(",", "-")
        .replace("=", "-");
  }

  /**
   * Disposes every cached {@link ConnectionProvider} in parallel and waits at most
   * {@link #DISPOSE_TIMEOUT} for the whole batch to complete. Per-provider failures (or a
   * provider that exceeds the budget) are logged but do not block the others — disposal of
   * one stalled provider must not gate shutdown of the rest of the application.
   */
  @Override
  public void destroy() {
    if (ownedProviders.isEmpty()) {
      return;
    }
    List<Mono<Void>> disposals = ownedProviders.entrySet().stream()
        .map(entry -> entry.getValue().disposeLater()
            .doOnError(err -> log.warn(
                "#RavenHttpClientFactory failed to dispose connection provider for {}",
                entry.getKey(), err))
            .onErrorResume(err -> Mono.empty()))
        .toList();
    try {
      Mono.when(disposals).block(DISPOSE_TIMEOUT);
    } catch (RuntimeException e) {
      log.warn("#RavenHttpClientFactory disposal exceeded {} budget; some providers"
          + " may not have shut down cleanly", DISPOSE_TIMEOUT, e);
    }
    ownedProviders.clear();
    baseClients.clear();
  }
}
