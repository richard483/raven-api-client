package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

//@Profile("test")
@RavenApiClient(name = "exampleClient")
public interface ExampleClient {
  @GetMapping(value = "/getRequest",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequest();

}
