package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientPutTests {
  private final WebTestClient webTestClient;

  @Autowired
  public RavenApiClientPutTests(WebTestClient webTestClient) {
    this.webTestClient = webTestClient;
  }

  @Test
  void putRequest() {
    ServerRequestBody requestBody = ServerRequestBody.builder().name("Richard").build();
    ServerResponseBody expected = ServerResponseBody.builder().message("Hello, Richard!").build();
    webTestClient.put().uri("http://localhost:8080/put")
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ServerResponseBody.class).isEqualTo(expected);
  }

}