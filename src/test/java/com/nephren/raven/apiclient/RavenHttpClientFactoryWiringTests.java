package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.http.DefaultRavenHttpClientFactory;
import com.nephren.raven.apiclient.http.RavenHttpClientFactory;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies that {@link RavenHttpClientFactory} is actually consulted by every
 * {@code @RavenApiClient} interceptor at startup, and that clients targeting the same
 * scheme+host:port collapse onto a single shared {@code HttpClient}.
 *
 * <p>The test app declares clients on three distinct backends (after default-config
 * resolution):
 * <ul>
 *   <li>{@code localhost:8080} — getExampleClient, postExampleClient, putExampleClient,
 *       patchExampleClient, deleteExampleClient (5 clients)</li>
 *   <li>{@code localhost:8081} — exampleClientWithFallback, exampleClientWithOtherFallback,
 *       deleteExampleClientError (3 clients)</li>
 *   <li>{@code localhost:80} — emptyExampleClient (no explicit URL, falls through to the
 *       {@code ApiClientConfigProperties} default of {@code "localhost"}, 1 client)</li>
 * </ul>
 * All use the default read/write timeouts and none set {@code isolate-pool}, so the
 * factory should end up with exactly three cached base clients and three owned providers
 * — proof that the interceptor delegates to the factory and that the keying strategy
 * actually shares pools across siblings on the same backend.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenHttpClientFactoryWiringTests {

  private final RavenHttpClientFactory factory;

  @Autowired
  public RavenHttpClientFactoryWiringTests(RavenHttpClientFactory factory) {
    this.factory = factory;
  }

  @Test
  void interceptorWiring_collapsesClientsByBackendOntoSharedBaseClients() {
    Assertions.assertThat(factory).isInstanceOf(DefaultRavenHttpClientFactory.class);

    Map<?, ?> baseClients = (Map<?, ?>) org.springframework.test.util.ReflectionTestUtils
        .getField(factory, "baseClients");
    Map<?, ?> ownedProviders = (Map<?, ?>) org.springframework.test.util.ReflectionTestUtils
        .getField(factory, "ownedProviders");

    // Three distinct backends → exactly three pool keys, regardless of how many
    // @RavenApiClient interfaces target each one.
    Assertions.assertThat(baseClients).hasSize(3);
    Assertions.assertThat(ownedProviders).hasSize(3);

    Assertions.assertThat(baseClients.keySet())
        .asInstanceOf(InstanceOfAssertFactories.iterable(Object.class))
        .allSatisfy(key -> Assertions.assertThat(key.toString())
            .matches("http://localhost:(80|8080|8081)\\|r=\\d+,w=\\d+"));
  }
}
