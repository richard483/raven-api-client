package com.nephren.raven.apiclient.serviceExample.service;

import com.nephren.raven.apiclient.serviceExample.client.ExampleClient;
import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class POSTClientService {
  @Autowired
  private ExampleClient exampleClient;

  public Mono<ResponseEntity<ServerResponseBody>> postRequest(ServerRequestBody name) {
    return exampleClient.postRequest(name);
  }

}
