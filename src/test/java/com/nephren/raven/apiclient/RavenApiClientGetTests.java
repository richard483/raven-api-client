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
    webTestClient.get().uri("http://localhost:8080/get")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Hello, World!");
  }
  @Test
  void getRequestISE() {
    webTestClient.get().uri("http://localhost:8080/get/ISE")
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody(String.class).isEqualTo("Internal Server Error - message from server");
  }

  @Test
  void getRequestISEFallback() {
    webTestClient.get().uri("http://localhost:8080/get/ISE-fallback")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Fallback during calling getRequest");
  }

  @Test
  void getRequestWithHeader() {
    webTestClient.get().uri("http://localhost:8080/get/withHeader")
        .header("X-Test-Header", "Hola!")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Message received with header: Hola!");
  }

  @Test
  void getRequestWithHeader2() {
    webTestClient.get().uri("http://localhost:8080/get/withHeader2")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Message received with header: Hola!");
  }

  @Test
  void getRequestWithQueryParam() {
    webTestClient.get().uri("http://localhost:8080/get/withQueryParam?name=Richard&age=22")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Message received with name: Richard and age: 22");
  }

  @Test
  void getRequestWithPathVariable() {
    webTestClient.get().uri("http://localhost:8080/get/withPathVariable/TowaSama")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Message received with path variable: TowaSama");
  }

}