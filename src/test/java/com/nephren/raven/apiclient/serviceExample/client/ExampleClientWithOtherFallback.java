package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import com.nephren.raven.apiclient.serviceExample.client.fallback.ExampleClientWithFallbackOtherFallback;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "exampleClientWithOtherFallback",
    fallback = ExampleClientWithFallbackOtherFallback.class)
public interface ExampleClientWithOtherFallback {

  @GetMapping(value = "/getRequest-ISE",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestISE();

  @GetMapping(value = "/getRequest-ISE",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestISEWithThrowableParam();

}
