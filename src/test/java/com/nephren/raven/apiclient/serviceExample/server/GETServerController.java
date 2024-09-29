package com.nephren.raven.apiclient.serviceExample.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class GETServerController {

  @GetMapping(path = "/")
  public Mono<ResponseEntity<String>> getRequestNoPath() {
    return Mono.just(ResponseEntity.ok("Hello, World!"));
  }

  @GetMapping(path = "/getRequest")
  public Mono<ResponseEntity<String>> getRequest() {
    return Mono.just(ResponseEntity.ok("Hello, World!"));
  }

  @GetMapping(path = "/getRequest-ISE")
  public Mono<ResponseEntity<String>> getRequestISE() {
    return Mono.just(ResponseEntity.internalServerError()
        .body("Internal Server Error - message from server"));
  }

  @GetMapping(path = "/getRequest-withHeader")
  public Mono<ResponseEntity<String>> getRequestWithHeader(
      @RequestHeader("X-Test-Header") String header) {
    return Mono.just(ResponseEntity.ok()
        .body("Message received with header: " + header));
  }

  @GetMapping(path = "/getRequest-queryParam")
  public Mono<ResponseEntity<String>> getRequestQueryParam(
      @RequestParam("name") String nameQueryParam, @RequestParam("age") String ageQueryParam) {
    return Mono.just(ResponseEntity.ok()
        .body("Message received with name: " + nameQueryParam + " and age: " + ageQueryParam));
  }

  @GetMapping(path = "/getRequest-pathVariable/{variable}")
  public Mono<ResponseEntity<String>> getRequestPathVariable(
      @PathVariable("variable") String variable) {
    return Mono.just(ResponseEntity.ok()
        .body("Message received with path variable: " + variable));
  }

  @GetMapping(path = "/getRequest-cookieParam")
  public Mono<ResponseEntity<String>> getRequestCookieParam(
      @CookieValue("username") String username) {
    return Mono.just(ResponseEntity.ok()
        .body("Message received and contain username cookie of " + username));
  }

}
