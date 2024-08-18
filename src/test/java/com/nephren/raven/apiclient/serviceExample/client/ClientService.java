package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ClientService {
  @Autowired
  private ExampleClient exampleClient;

  @Autowired
  private ExampleClientWithFallback exampleClientWithFallback;

  public Mono<ResponseEntity<String>> getRequest() {
    return exampleClient.getRequest();
  }
  public Mono<ResponseEntity<String>> getRequestISE() {
    return exampleClient.getRequestISE();
  }

  public Mono<ResponseEntity<String>> getRequestWithFallback() {
    return exampleClientWithFallback.getRequestISE();
  }

  public Mono<ResponseEntity<ServerResponseBody>> postRequest(ServerRequestBody name) {
    return exampleClient.postRequest(name);
  }

}
