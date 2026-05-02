package com.nephren.raven.apiclient.http;

import com.nephren.raven.apiclient.properties.RavenApiClientProperties.ApiClientConfigProperties;
import reactor.netty.http.client.HttpClient;

/**
 * Extension point for materializing the Reactor Netty {@link HttpClient} that backs each
 * {@code @RavenApiClient}'s {@code WebClient}.
 *
 * <p>The default implementation shares connection pools across clients targeting the same
 * scheme+host:port with the same read/write timeouts, and applies per-client
 * connect/read/write timeouts on top. Reactor Netty's event-loop threads
 * ({@code LoopResources}) come from the process-global instance regardless of pool key, so
 * isolation here is at the {@code ConnectionProvider} level only — replace this bean to
 * also isolate event loops, customize TLS, instrument the pool with metrics, or use a
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
   * The default pool key the bundled implementation uses:
   * {@code scheme://host:port|r=<read>,w=<write>} for shared pools, or
   * {@code "isolated:" + clientName} when the client is configured with
   * {@code isolate-pool: true}.
   *
   * <p>Read and write timeouts are folded into the shared key because Reactor Netty's
   * {@link io.netty.handler.timeout.ReadTimeoutHandler} /
   * {@link io.netty.handler.timeout.WriteTimeoutHandler} attach to the channel, so two
   * clients with different read/write timeouts cannot safely share a pooled channel. Connect
   * timeout is intentionally <em>not</em> part of the key — it only affects opening new
   * sockets and is reapplied per call as a channel option, so clients that differ only in
   * connect timeout can still share a pool. Custom factories that apply timeouts at the
   * request level (e.g. via {@code responseTimeout}) may use a narrower key.</p>
   */
  static String defaultPoolKey(String clientName, ApiClientConfigProperties config) {
    if (Boolean.TRUE.equals(config.getIsolatePool())) {
      return "isolated:" + clientName;
    }
    return PoolKeys.fromUrl(config.getUrl())
        + "|r=" + config.getReadTimeout().toMillis()
        + ",w=" + config.getWriteTimeout().toMillis();
  }

  /**
   * Returns a base-URL string suitable for {@code WebClient.Builder.baseUrl(...)} — the
   * configured value as-is when it already carries a scheme, otherwise prefixed with
   * {@code http://} so the documented {@code localhost:8080} format works at request time
   * the same way it works for pool-key derivation. Returns the input unchanged when null
   * or blank so existing validation/error paths still see the original value.
   */
  static String normalizedBaseUrl(String url) {
    if (url == null || url.isBlank()) {
      return url;
    }
    return url.contains("://") ? url : "http://" + url;
  }
}
