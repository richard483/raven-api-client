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
  }

}