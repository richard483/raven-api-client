package com.nephren.raven.apiclient.properties;

import com.nephren.raven.apiclient.errorresolver.ApiErrorResolver;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("nephren.raven.apiclient")
public class RavenApiClientProperties {

  public static final String DEFAULT = "default";

  private Map<String, ApiClientConfigProperties> configs = new HashMap<>();

  private String packages;

  /**
   * Don't forget to update merge method with default properties at
   * {@link PropertiesHelper#copyConfigPropertiesFromSourceToTarget(ApiClientConfigProperties,
   * ApiClientConfigProperties)}
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ApiClientConfigProperties {

    private String url = "localhost";

    private Class<?> fallback;

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration readTimeout = Duration.ofMillis(2000L);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration connectTimeout = Duration.ofMillis(2000L);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration writeTimeout = Duration.ofMillis(2000L);

    private Map<String, String> headers = new HashMap<>();

    private Class<? extends ApiErrorResolver> errorResolver;

    /**
     * When {@code true}, this client uses its own dedicated Reactor Netty
     * {@code ConnectionProvider} instead of one keyed by scheme+host:port (and read/write
     * timeouts). Treated as {@code false} when unset — most callers benefit from sharing.
     * Set to {@code true} when this client has TLS/proxy needs that differ from siblings
     * targeting the same host, or when its load profile would otherwise starve them on the
     * shared connection pool.
     *
     * <p><strong>Scope:</strong> the default factory only isolates the
     * {@code ConnectionProvider} (i.e. the connection pool); event-loop threads come from
     * Reactor Netty's process-global {@code LoopResources} and are still shared across all
     * clients in the JVM. A custom {@link com.nephren.raven.apiclient.http.RavenHttpClientFactory}
     * implementation can replace that if full thread isolation is required.</p>
     *
     * <p>Modeled as the wrapper {@link Boolean} so that a value set on
     * {@code configs.default.isolate-pool} can be inherited by named configs that omit the
     * key — a primitive default would unconditionally overwrite the inherited value back to
     * {@code false} during property merging.</p>
     */
    private Boolean isolatePool;
  }

}