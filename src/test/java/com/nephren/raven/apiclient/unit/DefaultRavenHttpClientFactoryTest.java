package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.http.DefaultRavenHttpClientFactory;
import com.nephren.raven.apiclient.http.RavenHttpClientFactory;
import com.nephren.raven.apiclient.properties.RavenApiClientProperties;
import com.nephren.raven.apiclient.properties.RavenSharedPoolProperties;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.netty.http.client.HttpClient;

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

  private static RavenHttpClientFactory.ConfigTimeouts timeouts() {
    return new RavenHttpClientFactory.ConfigTimeouts(
        Duration.ofMillis(2000), Duration.ofMillis(2000), Duration.ofMillis(2000));
  }

  @Test
  void poolKeyFor_byDefault_usesSchemeHostPort() {
    RavenApiClientProperties.ApiClientConfigProperties config =
        new RavenApiClientProperties.ApiClientConfigProperties();
    config.setUrl("http://example.com:8080/api");

    String key = RavenHttpClientFactory.poolKeyFor("any-name", config);

    Assertions.assertThat(key).isEqualTo("http://example.com:8080");
  }

  @Test
  void poolKeyFor_resolvesDefaultPortPerScheme() {
    RavenApiClientProperties.ApiClientConfigProperties http =
        new RavenApiClientProperties.ApiClientConfigProperties();
    http.setUrl("http://example.com");
    RavenApiClientProperties.ApiClientConfigProperties https =
        new RavenApiClientProperties.ApiClientConfigProperties();
    https.setUrl("https://example.com");

    Assertions.assertThat(RavenHttpClientFactory.poolKeyFor("a", http))
        .isEqualTo("http://example.com:80");
    Assertions.assertThat(RavenHttpClientFactory.poolKeyFor("b", https))
        .isEqualTo("https://example.com:443");
  }

  @Test
  void poolKeyFor_whenIsolatePoolSet_returnsIsolatedKeyWithClientName() {
    RavenApiClientProperties.ApiClientConfigProperties config =
        new RavenApiClientProperties.ApiClientConfigProperties();
    config.setUrl("http://shared-host:8080");
    config.setIsolatePool(true);

    String key = RavenHttpClientFactory.poolKeyFor("specialClient", config);

    Assertions.assertThat(key).isEqualTo("isolated:specialClient");
  }

  @Test
  void httpClient_withSamePoolKey_returnsConfigurationsThatShareUnderlyingResources() {
    HttpClient first = factory.httpClient("http://example.com:8080", timeouts());
    HttpClient second = factory.httpClient("http://example.com:8080", timeouts());

    // Reactor Netty HttpClient is copy-on-configure, so the per-call layer (.option/.doOnConnected)
    // makes the returned wrappers non-identical; the cached base client is what the factory pools.
    Assertions.assertThat(first).isNotNull();
    Assertions.assertThat(second).isNotNull();
    // The factory returns wrappers per call, so we cannot compare references directly. We assert
    // instead that the cache size stays at 1 entry — i.e. the second call did not allocate a
    // second base client / ConnectionProvider.
    factory.httpClient("http://example.com:8080", timeouts());
    factory.httpClient("http://example.com:8080", timeouts());
    Assertions.assertThat(factory)
        .extracting("baseClients", org.assertj.core.api.InstanceOfAssertFactories.MAP)
        .hasSize(1);
  }

  @Test
  void httpClient_withDifferentPoolKeys_allocatesDistinctBaseClients() {
    factory.httpClient("http://host-a:80", timeouts());
    factory.httpClient("http://host-b:80", timeouts());

    Assertions.assertThat(factory)
        .extracting("baseClients", org.assertj.core.api.InstanceOfAssertFactories.MAP)
        .hasSize(2);
  }

  @Test
  void isolatedPoolKey_doesNotCreateAnOwnedConnectionProvider() {
    factory.httpClient("isolated:special", timeouts());

    Assertions.assertThat(factory)
        .extracting("baseClients", org.assertj.core.api.InstanceOfAssertFactories.MAP)
        .hasSize(1);
    Assertions.assertThat(factory)
        .extracting("ownedProviders", org.assertj.core.api.InstanceOfAssertFactories.MAP)
        .isEmpty();
  }

  @Test
  void sharedPoolKey_createsOwnedConnectionProvider() {
    factory.httpClient("http://example.com:80", timeouts());

    Assertions.assertThat(factory)
        .extracting("ownedProviders", org.assertj.core.api.InstanceOfAssertFactories.MAP)
        .hasSize(1);
  }

  @Test
  void destroy_clearsAllCachedState() {
    factory.httpClient("http://host-a:80", timeouts());
    factory.httpClient("isolated:special", timeouts());

    factory.destroy();

    Assertions.assertThat(factory)
        .extracting("baseClients", org.assertj.core.api.InstanceOfAssertFactories.MAP).isEmpty();
    Assertions.assertThat(factory)
        .extracting("ownedProviders", org.assertj.core.api.InstanceOfAssertFactories.MAP).isEmpty();
  }
}
