package com.nephren.raven.apiclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientDeleteTests {
  private final WebTestClient webTestClient;

  @Autowired
  public RavenApiClientDeleteTests(WebTestClient webTestClient) {
    this.webTestClient = webTestClient;
  }

  @Test
  void deleteRequest() {
    webTestClient.delete().uri("http://localhost:8080/delete")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Hello, World!");
  }

}