package com.nephren.raven.apiclient.serviceExample.server;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ServerController {

  @GetMapping(path = "/getRequest")
  public Mono<ResponseEntity<String>> getRequest() {
    return Mono.just(ResponseEntity.ok("Hello, World!"));
  }

  @GetMapping(path = "/getRequest-ISE")
  public Mono<ResponseEntity<String>> getRequestISE() {
    return Mono.just(ResponseEntity.internalServerError()
        .body("Internal Server Error - message from server"));
  }

  @PostMapping(path = "/postRequest")
  public Mono<ResponseEntity<ServerResponseBody>> postRequest(
      @RequestBody ServerRequestBody requestBody) {
    String message = "Hello, " + requestBody.getName() + "!";
    return Mono.just(ResponseEntity.ok(ServerResponseBody.builder().message(message).build()));
  }

}
