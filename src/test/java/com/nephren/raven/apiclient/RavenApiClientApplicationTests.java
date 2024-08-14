package com.nephren.raven.apiclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class RavenApiClientApplicationTests {
  private final WebTestClient webTestClient;

  @Autowired
  public RavenApiClientApplicationTests(WebTestClient webTestClient) {
    this.webTestClient = webTestClient;
  }
  @Test
  void getRequest() {
    webTestClient.get().uri("http://localhost:8080/client/getRequest").exchange().expectStatus()
        .isOk();
  }

}