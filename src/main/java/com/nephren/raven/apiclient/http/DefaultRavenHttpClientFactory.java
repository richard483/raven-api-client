package com.nephren.raven.apiclient.http;

import com.nephren.raven.apiclient.properties.RavenApiClientProperties.ApiClientConfigProperties;
import com.nephren.raven.apiclient.properties.RavenSharedPoolProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
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
    String poolKey = RavenHttpClientFactory.defaultPoolKey(clientName, config);
    HttpClient base = baseClients.computeIfAbsent(poolKey, this::buildBase);
    return base
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
            (int) config.getConnectTimeout().toMillis())
        .doOnConnected(connection -> connection
            .addHandlerLast(new ReadTimeoutHandler(
                config.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(
                config.getWriteTimeout().toMillis(), TimeUnit.MILLISECONDS)));
  }

  private HttpClient buildBase(String poolKey) {
    ConnectionProvider provider = ConnectionProvider.builder("raven-" + sanitize(poolKey))
        .maxConnections(sharedPoolProperties.getMaxConnections())
        .pendingAcquireMaxCount(sharedPoolProperties.getPendingAcquireMaxCount())
        .build();
    ownedProviders.put(poolKey, provider);
    log.debug(
        "#RavenHttpClientFactory creating HttpClient for {} (maxConnections={},"
            + " pendingAcquireMaxCount={})",
        poolKey,
        sharedPoolProperties.getMaxConnections(),
        sharedPoolProperties.getPendingAcquireMaxCount());
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

  @Override
  public void destroy() {
    ownedProviders.forEach((key, provider) -> {
      try {
        provider.disposeLater().block(DISPOSE_TIMEOUT);
      } catch (RuntimeException e) {
        log.warn("#RavenHttpClientFactory failed to dispose connection provider for {}", key, e);
      }
    });
    ownedProviders.clear();
    baseClients.clear();
  }
}
