package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientPostTests {
  private final WebTestClient webTestClient;

  @Autowired
  public RavenApiClientPostTests(WebTestClient webTestClient) {
    this.webTestClient = webTestClient;
  }

  @Test
  void postRequest() {
    ServerRequestBody requestBody = ServerRequestBody.builder().name("Richard").build();
    ServerResponseBody expected = ServerResponseBody.builder().message("Hello, Richard!").build();
    webTestClient.post().uri("http://localhost:8080/post")
        .bodyValue(requestBody)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ServerResponseBody.class).isEqualTo(expected);
  }

  @Test
  void postRequestMultipart() throws IOException {
    Path path = Paths.get(new ClassPathResource("multipart.txt").getURI());
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("file", new ByteArrayResource(Files.readAllBytes(path))).filename("multipart.txt");
    ServerResponseBody expected =
        ServerResponseBody.builder()
            .message("Hello, World! Your file content is: Towa Sama Maji Tenshi")
            .build();
    webTestClient.post().uri("http://localhost:8080/post/multipart")
        .body(BodyInserters.fromMultipartData(builder.build()))
        .header("Content-Type", "multipart/form-data")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ServerResponseBody.class).isEqualTo(expected);
  }

}