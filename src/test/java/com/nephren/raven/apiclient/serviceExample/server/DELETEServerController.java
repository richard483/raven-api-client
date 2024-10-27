package com.nephren.raven.apiclient.serviceExample.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class DELETEServerController {

  @DeleteMapping(path = "/deleteRequest/{name}")
  public Mono<ResponseEntity<String>> deleteRequest(@PathVariable("name") String name) {
    return Mono.just(ResponseEntity.ok("Hello, " + name + "!"));
  }

}
