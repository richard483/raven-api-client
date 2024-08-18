package com.nephren.raven.apiclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientGetTests {
  private final WebTestClient webTestClient;

  @Autowired
  public RavenApiClientGetTests(WebTestClient webTestClient) {
    this.webTestClient = webTestClient;
  }
  @Test
  void getRequest() {
    webTestClient.get().uri("http://localhost:8080/client/getRequest")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Hello, World!");
  }
  @Test
  void getRequestISE() {
    webTestClient.get().uri("http://localhost:8080/client/getRequest-ISE")
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody(String.class).isEqualTo("Internal Server Error - message from server");
  }

  @Test
  void getRequestISEFallback() {
    webTestClient.get().uri("http://localhost:8080/client/getRequest-ISE-fallback")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Fallback during calling getRequest");
  }

}