package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.serviceExample.client.GETExampleClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies that {@link Object} methods invoked on a proxied {@code @RavenApiClient}
 * (toString/equals/hashCode) do not violate their declared return types — the regression
 * flagged by Copilot/CodeRabbit on PR #17. JDK proxies route these through the
 * MethodInterceptor, so the unmapped-method guard must avoid returning a {@code Mono} when
 * the contract demands {@code String}, {@code int}, or {@code boolean}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientObjectMethodsTests {

  private final GETExampleClient client;

  @Autowired
  public RavenApiClientObjectMethodsTests(GETExampleClient client) {
    this.client = client;
  }

  @Test
  void toString_doesNotThrowAndReturnsString() {
    Assertions.assertThat(client.toString()).isNotNull();
  }

  @Test
  void hashCode_doesNotThrowAndReturnsInt() {
    int h = client.hashCode();
    Assertions.assertThat(h).isEqualTo(client.hashCode());
  }

  @Test
  void equals_doesNotThrow_andIsReflexive() {
    Assertions.assertThat(client.equals(client)).isTrue();
    Assertions.assertThat(client.equals(new Object())).isFalse();
  }
}
