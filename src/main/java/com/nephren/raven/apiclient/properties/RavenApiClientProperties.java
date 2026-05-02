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
     * When {@code true}, this client gets its own Reactor Netty {@code HttpClient} (and hence
     * its own connection pool and event loops) instead of sharing the pool keyed by
     * scheme+host:port. Treated as {@code false} when unset (most callers benefit from
     * sharing). Set to {@code true} when this client has materially different TLS/proxy
     * needs from other clients targeting the same host, or when its load profile would
     * starve them.
     *
     * <p>Modeled as the wrapper {@link Boolean} so that a value set on
     * {@code configs.default.isolate-pool} can be inherited by named configs that omit the
     * key — a primitive default would unconditionally overwrite the inherited value back to
     * {@code false} during property merging.</p>
     */
    private Boolean isolatePool;
  }

}