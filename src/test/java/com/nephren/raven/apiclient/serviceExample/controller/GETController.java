package com.nephren.raven.apiclient.serviceExample.controller;

import com.nephren.raven.apiclient.serviceExample.service.GETClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("get")
public class GETController {
  @Autowired
  private GETClientService clientService;

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

  @GetMapping("/withHeader")
  public Mono<ResponseEntity<String>> getRequestWithHeader() {
    return clientService.getRequestWithHeader("Hola!");
  }

  @GetMapping("/withHeader2")
  public Mono<ResponseEntity<String>> getRequestWithHeader2() {
    return clientService.getRequestWithHeader2();
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

}
