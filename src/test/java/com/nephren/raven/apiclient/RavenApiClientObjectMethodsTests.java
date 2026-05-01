package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.serviceExample.client.GETExampleClient;
import com.nephren.raven.apiclient.serviceExample.client.POSTExampleClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies that {@link Object} methods invoked on a proxied {@code @RavenApiClient}
 * (toString/equals/hashCode) respect the contract of {@link Object} — the regression
 * flagged by Copilot/CodeRabbit on PR #17. JDK proxies route these through the
 * MethodInterceptor, so the unmapped-method guard must (a) avoid returning a
 * {@code Mono} when the contract demands {@code String}, {@code int}, or
 * {@code boolean}, and (b) implement equals/hashCode against proxy identity so
 * {@code proxy.equals(proxy)} is reflexive.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientObjectMethodsTests {

  private final GETExampleClient client;
  private final POSTExampleClient otherClient;

  @Autowired
  public RavenApiClientObjectMethodsTests(GETExampleClient client, POSTExampleClient otherClient) {
    this.client = client;
    this.otherClient = otherClient;
  }

  @Test
  void toString_returnsString_describingTheClientInterface() {
    String s = client.toString();
    Assertions.assertThat(s).isNotBlank().contains(GETExampleClient.class.getName());
  }

  @Test
  void hashCode_isStableAcrossInvocations() {
    Assertions.assertThat(client.hashCode()).isEqualTo(client.hashCode());
  }

  @Test
  void equals_isReflexive_andDistinguishesDifferentProxies() {
    Assertions.assertThat(client.equals(client)).isTrue();
    Assertions.assertThat(client.equals(otherClient)).isFalse();
    Assertions.assertThat(client.equals(new Object())).isFalse();
    Assertions.assertThat(client.equals(null)).isFalse();
  }
}
