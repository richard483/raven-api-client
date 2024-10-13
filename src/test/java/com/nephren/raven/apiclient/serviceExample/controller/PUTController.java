package com.nephren.raven.apiclient.serviceExample.controller;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import com.nephren.raven.apiclient.serviceExample.service.PUTClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("put")
public class PUTController {

  private final PUTClientService clientService;

  @Autowired
  public PUTController(PUTClientService clientService) {
    this.clientService = clientService;
  }

  @PutMapping()
  public Mono<ResponseEntity<ServerResponseBody>> putRequest(@RequestBody ServerRequestBody body) {
    return clientService.putRequest(body);
  }

}
