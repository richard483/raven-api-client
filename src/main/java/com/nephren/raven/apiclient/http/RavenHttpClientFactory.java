package com.nephren.raven.apiclient.http;

import com.nephren.raven.apiclient.properties.RavenApiClientProperties.ApiClientConfigProperties;
import reactor.netty.http.client.HttpClient;

/**
 * Extension point for materializing the Reactor Netty {@link HttpClient} that backs each
 * {@code @RavenApiClient}'s {@code WebClient}.
 *
 * <p>The default implementation shares connection pools and event loops across all clients
 * targeting the same scheme+host:port (with the same configured timeouts) and applies the
 * per-client connect/read/write timeouts on top. Replace this bean to take full control of
 * pooling — typical reasons include custom TLS configuration, a metric-instrumented
 * {@code ConnectionProvider}, sharing pools with other parts of an application, or a
 * different keying strategy.</p>
 *
 * <p>The full per-client configuration is passed to {@link #httpClient(String,
 * ApiClientConfigProperties)} so a replacement implementation can make decisions based on
 * URL, headers, fallback class, or any other property. Implementations must be thread-safe;
 * {@link #httpClient} is called once per declared client at bean initialization, but the
 * returned {@link HttpClient} may be invoked concurrently from any thread.</p>
 */
public interface RavenHttpClientFactory {

  /**
   * Resolve an {@link HttpClient} for the given client. Implementations are free to share
   * an underlying {@code ConnectionProvider}/{@code LoopResources} between calls; the
   * default implementation does so when {@code config} resolves to the same pool key.
   *
   * @param clientName the {@code @RavenApiClient(name = ...)} value, used by the default
   *                   implementation when {@code isolate-pool} is set.
   * @param config     the merged {@link ApiClientConfigProperties} for this client (after
   *                   defaults are applied), giving the implementation access to URL,
   *                   timeouts, headers, etc.
   * @return an {@link HttpClient} ready for use; the underlying connection pool may be
   *         shared with other clients depending on the factory's keying strategy.
   */
  HttpClient httpClient(String clientName, ApiClientConfigProperties config);

  /**
   * The default pool key the bundled implementation uses: {@code scheme://host:port|<timeouts>}
   * for shared pools, or {@code "isolated:" + clientName} when the client is configured with
   * {@code isolate-pool: true}.
   *
   * <p>Timeouts are folded into the shared key because Reactor Netty's
   * {@code ReadTimeoutHandler} / {@code WriteTimeoutHandler} attach to the channel, so two
   * clients with different read/write timeouts cannot safely share a pooled channel. Custom
   * factories that apply timeouts at the request level (e.g. via {@code responseTimeout})
   * may use a narrower key.</p>
   */
  static String defaultPoolKey(String clientName, ApiClientConfigProperties config) {
    if (Boolean.TRUE.equals(config.getIsolatePool())) {
      return "isolated:" + clientName;
    }
    return PoolKeys.fromUrl(config.getUrl())
        + "|c=" + config.getConnectTimeout().toMillis()
        + ",r=" + config.getReadTimeout().toMillis()
        + ",w=" + config.getWriteTimeout().toMillis();
  }
}
