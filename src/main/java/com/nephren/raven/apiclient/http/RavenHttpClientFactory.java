package com.nephren.raven.apiclient.http;

import com.nephren.raven.apiclient.properties.RavenApiClientProperties;
import java.time.Duration;
import reactor.netty.http.client.HttpClient;

/**
 * Extension point for materializing the Reactor Netty {@link HttpClient} that backs each
 * {@code @RavenApiClient}'s {@code WebClient}.
 *
 * <p>The default implementation shares connection pools and event loops across all clients
 * targeting the same scheme+host:port and applies the per-client connect/read/write timeouts
 * on top. Replace this bean to take full control of pooling — typical reasons include custom
 * TLS configuration, a metric-instrumented {@code ConnectionProvider}, or sharing pools with
 * other parts of an application.</p>
 *
 * <p>Implementations must be thread-safe; {@link #httpClient(String, ConfigTimeouts)} is
 * called once at bean initialization time per declared client, but consumers may also call
 * the returned {@link HttpClient} concurrently from any thread.</p>
 */
public interface RavenHttpClientFactory {

  /**
   * Resolve an {@link HttpClient} for the given client config and pool key. The factory is
   * free to share an underlying {@code ConnectionProvider}/{@code LoopResources} between
   * calls with the same {@code poolKey}; per-call configuration like timeouts is applied on
   * top of the cached base client and does not affect the cache.
   *
   * @param poolKey  identifies the pool. The default factory uses the scheme+host:port of
   *                 the configured URL, or {@code "isolated:" + clientName} when the client
   *                 is configured with {@code isolate-pool: true}.
   * @param timeouts per-call connect/read/write timeouts to apply to the returned client.
   * @return an {@link HttpClient} configured with the given timeouts; the underlying
   *         connection pool may be shared across calls with the same {@code poolKey}.
   */
  HttpClient httpClient(String poolKey, ConfigTimeouts timeouts);

  /**
   * Helper that derives the pool key from a single client's config: the scheme+host:port of
   * its configured URL by default, or {@code "isolated:" + clientName} when
   * {@link RavenApiClientProperties.ApiClientConfigProperties#isIsolatePool()} is set.
   */
  static String poolKeyFor(String clientName,
      RavenApiClientProperties.ApiClientConfigProperties config) {
    if (config.isIsolatePool()) {
      return "isolated:" + clientName;
    }
    return PoolKeys.fromUrl(config.getUrl());
  }

  /** Timeouts applied to the returned {@link HttpClient}. */
  record ConfigTimeouts(Duration connect, Duration read, Duration write) {}
}
