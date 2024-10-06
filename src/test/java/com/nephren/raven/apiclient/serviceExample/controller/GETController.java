package com.nephren.raven.apiclient.serviceExample.controller;

import com.nephren.raven.apiclient.serviceExample.service.GETClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("get")
public class GETController {
  @Autowired
  private GETClientService clientService;

  @GetMapping("/no-path")
  public Mono<ResponseEntity<String>> getRequestNoPath() {
    return clientService.getRequestNoPath();
  }

  @GetMapping()
  public Mono<ResponseEntity<String>> getRequest() {
    return clientService.getRequest();
  }

  @GetMapping("/ISE")
  public Mono<ResponseEntity<String>> getRequestISE() {
    return clientService.getRequestISE();
  }

  @GetMapping("/ISE-fallback")
  public Mono<ResponseEntity<String>> getRequestISEWithFallback() {
    return clientService.getRequestWithFallback();
  }

  @GetMapping("/ISE-other-fallback")
  public Mono<ResponseEntity<String>> getRequestISEWithOtherFallback() {
    return clientService.getRequestWithOtherFallback();
  }

  @GetMapping("/withHeader")
  public Mono<ResponseEntity<String>> getRequestWithHeader() {
    return clientService.getRequestWithHeader("Hola!");
  }

  @GetMapping("/withHeader2")
  public Mono<ResponseEntity<String>> getRequestWithHeader2() {
    return clientService.getRequestWithHeader2();
  }

  @GetMapping("/withHeader3")
  public Mono<ResponseEntity<String>> getRequestWithHeader3() {
    return clientService.getRequestWithHeader3();
  }

  @GetMapping("/withQueryParam")
  public Mono<ResponseEntity<String>> getRequestWithQueryParam(
      @RequestParam String name, @RequestParam String age) {
    return clientService.getRequestWithQueryParam(name, age);
  }

  @GetMapping("/withPathVariable/{var}")
  public Mono<ResponseEntity<String>> getRequestWithPathVariable(
      @PathVariable(value = "var") String var) {
    return clientService.getRequestWithPathVariable(var);
  }

  @GetMapping("/withCookieParam")
  public Mono<ResponseEntity<String>> getRequestWithCookieParam(
      @CookieValue("username") String username) {
    return clientService.getRequestWithCookieParam(username);
  }

}
