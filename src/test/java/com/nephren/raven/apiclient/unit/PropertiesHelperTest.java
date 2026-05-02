package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.properties.PropertiesHelper;
import com.nephren.raven.apiclient.properties.RavenApiClientProperties.ApiClientConfigProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Locks in the inheritance contract of
 * {@link PropertiesHelper#copyConfigPropertiesFromSourceToTarget}: optional fields set on
 * {@code configs.default} must flow through to named configs that omit them, but explicit
 * values on the named config must still win.
 *
 * <p>Specifically guards the {@code isolate-pool} regression Copilot flagged on PR #21
 * comment #4 — modeling the field as primitive {@code boolean} silently overwrote an
 * inherited {@code true} back to {@code false} for any named config that did not set the
 * key explicitly.</p>
 */
class PropertiesHelperTest {

  /** Equivalent to {@code mergeApiClientConfigProperties} in {@code RequestMappingMetadataBuilder}. */
  private static ApiClientConfigProperties merge(
      ApiClientConfigProperties defaults, ApiClientConfigProperties named) {
    ApiClientConfigProperties merged = new ApiClientConfigProperties();
    PropertiesHelper.copyConfigPropertiesFromSourceToTarget(defaults, merged);
    PropertiesHelper.copyConfigPropertiesFromSourceToTarget(named, merged);
    return merged;
  }

  @Test
  void isolatePool_setOnDefault_inheritsToNamedConfigThatOmitsIt() {
    ApiClientConfigProperties defaults = new ApiClientConfigProperties();
    defaults.setIsolatePool(true);
    ApiClientConfigProperties named = new ApiClientConfigProperties();
    // named omits isolate-pool — the merged value must come from defaults

    ApiClientConfigProperties merged = merge(defaults, named);

    Assertions.assertThat(merged.getIsolatePool()).isTrue();
  }

  @Test
  void isolatePool_explicitOnNamedConfig_overridesDefault() {
    ApiClientConfigProperties defaults = new ApiClientConfigProperties();
    defaults.setIsolatePool(true);
    ApiClientConfigProperties named = new ApiClientConfigProperties();
    named.setIsolatePool(false);

    ApiClientConfigProperties merged = merge(defaults, named);

    Assertions.assertThat(merged.getIsolatePool()).isFalse();
  }

  @Test
  void isolatePool_unsetOnBoth_remainsNullSoCallersTreatItAsFalse() {
    ApiClientConfigProperties merged = merge(
        new ApiClientConfigProperties(), new ApiClientConfigProperties());

    Assertions.assertThat(merged.getIsolatePool()).isNull();
  }

  @Test
  void otherOptionalFields_followTheSameInheritancePattern() {
    ApiClientConfigProperties defaults = new ApiClientConfigProperties();
    defaults.setUrl("http://default-host:8080");
    defaults.getHeaders().put("X-Common", "yes");
    ApiClientConfigProperties named = new ApiClientConfigProperties();
    named.setUrl("http://named-host:9090");

    ApiClientConfigProperties merged = merge(defaults, named);

    Assertions.assertThat(merged.getUrl()).isEqualTo("http://named-host:9090");
    Assertions.assertThat(merged.getHeaders()).containsEntry("X-Common", "yes");
  }
}
