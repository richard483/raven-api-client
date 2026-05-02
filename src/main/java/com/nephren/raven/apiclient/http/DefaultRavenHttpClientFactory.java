package com.nephren.raven.apiclient.http;

import com.nephren.raven.apiclient.properties.RavenSharedPoolProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
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
 * pool key. Each base client owns a {@link ConnectionProvider} sized by
 * {@link RavenSharedPoolProperties}; pool keys starting with {@code "isolated:"} bypass the
 * shared provider and use Reactor Netty's default pool, giving the caller a private set of
 * resources.</p>
 *
 * <p>Per-call timeouts are layered on top of the cached base via {@code .option(...)} and
 * {@code .doOnConnected(...)}; Reactor Netty's {@code HttpClient} is copy-on-configure, so
 * those calls return a new wrapper that reuses the underlying pool/loop resources rather
 * than allocating a fresh one.</p>
 *
 * <p>Implements {@link DisposableBean} so the cached {@code ConnectionProvider}s release
 * their resources cleanly on application context shutdown — important for tests and short-
 * lived programs.</p>
 */
@Slf4j
public class DefaultRavenHttpClientFactory implements RavenHttpClientFactory, DisposableBean {

  private final RavenSharedPoolProperties sharedPoolProperties;
  private final Map<String, HttpClient> baseClients = new ConcurrentHashMap<>();
  private final Map<String, ConnectionProvider> ownedProviders = new ConcurrentHashMap<>();

  public DefaultRavenHttpClientFactory(RavenSharedPoolProperties sharedPoolProperties) {
    this.sharedPoolProperties = sharedPoolProperties;
  }

  @Override
  public HttpClient httpClient(String poolKey, ConfigTimeouts timeouts) {
    HttpClient base = baseClients.computeIfAbsent(poolKey, this::buildBase);
    return base
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeouts.connect().toMillis())
        .doOnConnected(connection -> connection
            .addHandlerLast(new ReadTimeoutHandler(
                timeouts.read().toMillis(), TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(
                timeouts.write().toMillis(), TimeUnit.MILLISECONDS)));
  }

  private HttpClient buildBase(String poolKey) {
    if (poolKey.startsWith("isolated:")) {
      log.debug("#RavenHttpClientFactory creating isolated HttpClient for {}", poolKey);
      return HttpClient.create();
    }
    ConnectionProvider provider = ConnectionProvider.builder("raven-" + sanitize(poolKey))
        .maxConnections(sharedPoolProperties.getMaxConnections())
        .pendingAcquireMaxCount(sharedPoolProperties.getPendingAcquireMaxCount())
        .build();
    ownedProviders.put(poolKey, provider);
    log.debug(
        "#RavenHttpClientFactory creating shared HttpClient for {} (maxConnections={},"
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
    return poolKey.replace("://", "-").replace(":", "-").replace("/", "-");
  }

  @Override
  public void destroy() {
    ownedProviders.values().forEach(provider -> {
      try {
        provider.disposeLater().block();
      } catch (RuntimeException e) {
        log.warn("#RavenHttpClientFactory failed to dispose connection provider", e);
      }
    });
    ownedProviders.clear();
    baseClients.clear();
  }
}
