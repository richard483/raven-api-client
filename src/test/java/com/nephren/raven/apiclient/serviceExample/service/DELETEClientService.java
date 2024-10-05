package com.nephren.raven.apiclient.serviceExample.service;

import com.nephren.raven.apiclient.serviceExample.client.DELETEExampleClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DELETEClientService {
  @Autowired
  private DELETEExampleClient deleteExampleClient;

  public Mono<ResponseEntity<String>> deleteRequest() {
    return deleteExampleClient.deleteRequest();
  }

}
