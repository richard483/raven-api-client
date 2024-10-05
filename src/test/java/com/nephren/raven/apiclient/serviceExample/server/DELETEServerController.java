package com.nephren.raven.apiclient.serviceExample.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DELETEServerController {

  @DeleteMapping(path = "/deleteRequest")
  public Mono<ResponseEntity<String>> deleteRequest() {
    return Mono.just(ResponseEntity.ok("Hello, World!"));
  }

}
