package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import com.nephren.raven.apiclient.serviceExample.client.fallback.ExampleClientWithFallbackFallback;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "exampleClientWithFallback",
    fallback = ExampleClientWithFallbackFallback.class)
public interface ExampleClientWithFallback {

  @GetMapping(value = "/getRequest-ISE",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestISE();

}
