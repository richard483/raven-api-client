package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "deleteExampleClient")
public interface DELETEExampleClient {

  @RequestMapping(value = "/deleteRequest/{name}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
  Mono<ResponseEntity<String>> deleteRequest(@PathVariable("name") String name);
}
