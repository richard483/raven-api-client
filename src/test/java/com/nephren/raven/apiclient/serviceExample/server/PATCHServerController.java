package com.nephren.raven.apiclient.serviceExample.server;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class PATCHServerController {

  @PatchMapping(path = "/patchRequest")
  public Mono<ResponseEntity<ServerResponseBody>> patchRequest(
      @RequestBody ServerRequestBody requestBody) {
    String message = "Hello, " + requestBody.getName() + "!";
    return Mono.just(ResponseEntity.ok(ServerResponseBody.builder().message(message).build()));
  }

}
