package com.nephren.raven.apiclient.serviceExample.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

//@Profile("test")
@Service
public class ClientService {
  @Autowired
  private ExampleClient exampleClient;

  public Mono<ResponseEntity<String>> getRequest() {
    return exampleClient.getRequest();
  }

}
