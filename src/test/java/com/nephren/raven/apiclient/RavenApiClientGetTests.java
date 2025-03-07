package com.nephren.raven.apiclient;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
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
  void getRequestWithRequestMapping() {
    webTestClient.get().uri("http://localhost:8080/get/request-mapping")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Hello, World!");
  }

  @Test
  void getRequestNoPath() {
    webTestClient.get().uri("http://localhost:8080/get/no-path")
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
        .expectBody(String.class).isEqualTo("Fallback during calling getRequestISE");
  }

  @Test
  void getRequestISEOtherFallback() {
    webTestClient.get().uri("http://localhost:8080/get/ISE-other-fallback")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Fallback during calling getRequestISE");
  }

  @Test
  void getRequestISEOtherFallbackNoFallbackMethod() {
    webTestClient.get().uri("http://localhost:8080/get/ISE-other-fallback-no-fallback-method")
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.timestamp").isNotEmpty()
        .jsonPath("$.path").isEqualTo("/get/ISE-other-fallback-no-fallback-method")
        .jsonPath("$.status").isEqualTo(500)
        .jsonPath("$.error").isEqualTo("Internal Server Error")
        .jsonPath("$.requestId").isNotEmpty();
  }

  @Test
  void getRequestISEWithThrowableParam() {
    webTestClient.get().uri("http://localhost:8080/get/ISE-other-fallback-throwable")
        .exchange()
        .expectStatus()
        .is5xxServerError()
        .expectBody(String.class)
        .value(message -> {
          Assertions.assertThat(message)
              .contains("Connection refused")
              .contains("localhost")
              .contains("8081");
        });
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
  void getRequestWithHeader3() {
    webTestClient.get().uri("http://localhost:8080/get/withHeader3")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Message received with header: ");
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
  void getRequestWithQueryParamCollection() {
    webTestClient.get().uri("http://localhost:8080/get/withQueryParamAndCollection?names=Richard,Nephren,William")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Message received with names: [Richard, Nephren, William] with the length of: 3");
  }

  @Test
  void getRequestWithPathVariable() {
    webTestClient.get().uri("http://localhost:8080/get/withPathVariable/TowaSama")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class).isEqualTo("Message received with path variable: TowaSama");
  }

  @Test
  void getRequestWithCookieParam() {
    webTestClient.get().uri("http://localhost:8080/get/withCookieParam")
        .cookie("username", "TowaSama")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("Message received and contain username cookie of TowaSama");
  }

  @Test
  void getRequestList() {
    // TODO: need to investigate why the list response is wrapped in another list, and why it was
    //  there (based on reference library), and remove it if it's not needed
    webTestClient.get().uri("http://localhost:8080/get/list")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(String.class)
        .isEqualTo("[\"[\\\"Hello\\\",\\\"こんいちわ\\\",\\\"Hola\\\",\\\"Bonjour\\\",\\\"Hallo\\\"]\"]");
  }

}