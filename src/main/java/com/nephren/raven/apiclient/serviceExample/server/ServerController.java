package com.nephren.raven.apiclient.serviceExample.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

//@Profile("test")
@RestController
public class ServerController {
  @Autowired
  private ServerService serverService;

  @PatchMapping(path = "/getRequest")
  public ResponseEntity<Mono<String>> getRequest() {
    return ResponseEntity.ok(serverService.getRequest());
  }

}
