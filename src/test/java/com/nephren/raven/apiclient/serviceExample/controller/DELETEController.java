package com.nephren.raven.apiclient.serviceExample.controller;

import com.nephren.raven.apiclient.serviceExample.service.DELETEClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("delete")
public class DELETEController {

  private final DELETEClientService clientService;

  @Autowired
  public DELETEController(DELETEClientService clientService) {
    this.clientService = clientService;
  }

  @DeleteMapping()
  public Mono<ResponseEntity<String>> deleteRequest() {
    return clientService.deleteRequest();
  }

  @DeleteMapping("/error")
  public Mono<ResponseEntity<String>> deleteRequestError() {
    return clientService.deleteRequestError();
  }

}
