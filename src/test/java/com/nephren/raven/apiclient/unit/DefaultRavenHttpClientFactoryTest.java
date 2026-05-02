package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.http.DefaultRavenHttpClientFactory;
import com.nephren.raven.apiclient.http.RavenHttpClientFactory;
import com.nephren.raven.apiclient.properties.RavenApiClientProperties;
import com.nephren.raven.apiclient.properties.RavenSharedPoolProperties;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultRavenHttpClientFactoryTest {

  private DefaultRavenHttpClientFactory factory;

  @BeforeEach
  void setUp() {
    factory = new DefaultRavenHttpClientFactory(new RavenSharedPoolProperties());
  }

  @AfterEach
  void tearDown() {
    factory.destroy();
  }

  private static RavenApiClientProperties.ApiClientConfigProperties config(String url) {
    RavenApiClientProperties.ApiClientConfigProperties c =
        new RavenApiClientProperties.ApiClientConfigProperties();
    c.setUrl(url);
    return c;
  }

  private static RavenApiClientProperties.ApiClientConfigProperties config(
      String url, long connectMs, long readMs, long writeMs) {
    RavenApiClientProperties.ApiClientConfigProperties c = config(url);
    c.setConnectTimeout(Duration.ofMillis(connectMs));
    c.setReadTimeout(Duration.ofMillis(readMs));
    c.setWriteTimeout(Duration.ofMillis(writeMs));
    return c;
  }

  @Test
  void defaultPoolKey_byDefault_usesSchemeHostPortPlusTimeoutFingerprint() {
    String key = RavenHttpClientFactory.defaultPoolKey(
        "any-name", config("http://example.com:8080/api", 1000, 2000, 3000));
    Assertions.assertThat(key).isEqualTo("http://example.com:8080|c=1000,r=2000,w=3000");
  }

  @Test
  void defaultPoolKey_resolvesDefaultPortPerScheme() {
    Assertions.assertThat(
            RavenHttpClientFactory.defaultPoolKey("a", config("http://example.com")))
        .startsWith("http://example.com:80|");
    Assertions.assertThat(
            RavenHttpClientFactory.defaultPoolKey("b", config("https://example.com")))
        .startsWith("https://example.com:443|");
  }

  @Test
  void defaultPoolKey_whenIsolatePoolSet_returnsIsolatedKeyWithClientName() {
    RavenApiClientProperties.ApiClientConfigProperties c = config("http://shared-host:8080");
    c.setIsolatePool(true);

    Assertions.assertThat(RavenHttpClientFactory.defaultPoolKey("specialClient", c))
        .isEqualTo("isolated:specialClient");
  }

  @Test
  void httpClient_withSamePoolKey_sharesUnderlyingBaseClient() {
    factory.httpClient("a", config("http://example.com:8080", 2000, 2000, 2000));
    factory.httpClient("b", config("http://example.com:8080", 2000, 2000, 2000));
    factory.httpClient("c", config("http://example.com:8080", 2000, 2000, 2000));

    Assertions.assertThat(factory)
        .extracting("baseClients", InstanceOfAssertFactories.MAP)
        .hasSize(1);
  }

  @Test
  void httpClient_withDifferentHosts_allocatesDistinctBaseClients() {
    factory.httpClient("a", config("http://host-a:80", 2000, 2000, 2000));
    factory.httpClient("b", config("http://host-b:80", 2000, 2000, 2000));

    Assertions.assertThat(factory)
        .extracting("baseClients", InstanceOfAssertFactories.MAP)
        .hasSize(2);
  }

  @Test
  void httpClient_withSameHostButDifferentTimeouts_allocatesDistinctBaseClients() {
    // Channel-bound ReadTimeoutHandler/WriteTimeoutHandler would otherwise leak across
    // clients sharing a pooled channel; the default key folds timeouts in to prevent it.
    factory.httpClient("a", config("http://example.com:80", 2000, 2000, 2000));
    factory.httpClient("b", config("http://example.com:80", 5000, 5000, 5000));

    Assertions.assertThat(factory)
        .extracting("baseClients", InstanceOfAssertFactories.MAP)
        .hasSize(2);
  }

  @Test
  void isolatedClients_eachOwnTheirOwnConnectionProvider() {
    RavenApiClientProperties.ApiClientConfigProperties a = config("http://shared:80");
    a.setIsolatePool(true);
    RavenApiClientProperties.ApiClientConfigProperties b = config("http://shared:80");
    b.setIsolatePool(true);

    factory.httpClient("client-a", a);
    factory.httpClient("client-b", b);

    // Two isolated clients on the same backend must NOT contend on a single pool —
    // verify each got its own owned ConnectionProvider entry.
    Assertions.assertThat(factory)
        .extracting("ownedProviders", InstanceOfAssertFactories.MAP)
        .hasSize(2)
        .extracting(java.util.Map::keySet, InstanceOfAssertFactories.iterable(String.class))
        .contains("isolated:client-a", "isolated:client-b");
  }

  @Test
  void destroy_clearsAllCachedState() {
    factory.httpClient("a", config("http://host-a:80", 2000, 2000, 2000));
    RavenApiClientProperties.ApiClientConfigProperties iso = config("http://shared:80");
    iso.setIsolatePool(true);
    factory.httpClient("special", iso);

    factory.destroy();

    Assertions.assertThat(factory)
        .extracting("baseClients", InstanceOfAssertFactories.MAP).isEmpty();
    Assertions.assertThat(factory)
        .extracting("ownedProviders", InstanceOfAssertFactories.MAP).isEmpty();
  }
}
