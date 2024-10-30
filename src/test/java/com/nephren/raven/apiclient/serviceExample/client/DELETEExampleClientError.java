package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "deleteExampleClientError")
public interface DELETEExampleClientError {

  @DeleteMapping(value = "/deleteRequest",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> deleteRequest();
}
