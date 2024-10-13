package com.nephren.raven.apiclient.serviceExample.controller;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import com.nephren.raven.apiclient.serviceExample.service.POSTClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("post")
public class POSTController {

  private final POSTClientService clientService;

  @Autowired
  public POSTController(POSTClientService clientService) {
    this.clientService = clientService;
  }

  @PostMapping()
  public Mono<ResponseEntity<ServerResponseBody>> postRequest(@RequestBody ServerRequestBody body) {
    return clientService.postRequest(body);
  }

}
