package com.nephren.raven.apiclient.serviceExample.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ClientController {
  @Autowired
  private ClientService clientService;

  @GetMapping("/client/getRequest")
  public Mono<ResponseEntity<String>> getRequest() {
    return clientService.getRequest();
  }

  @GetMapping("/client/getRequest-ISE")
  public Mono<ResponseEntity<String>> getRequestISE() {
    return clientService.getRequestISE();
  }

  @GetMapping("/client/getRequest-ISE-fallback")
  public Mono<ResponseEntity<String>> getRequestISEWithFallback() {
    return clientService.getRequestWithFallback();
  }

}
