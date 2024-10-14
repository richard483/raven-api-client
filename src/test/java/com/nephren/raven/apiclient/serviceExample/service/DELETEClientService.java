package com.nephren.raven.apiclient.serviceExample.service;

import com.nephren.raven.apiclient.serviceExample.client.DELETEExampleClient;
import com.nephren.raven.apiclient.serviceExample.client.DELETEExampleClientError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DELETEClientService {

  private final DELETEExampleClient deleteExampleClient;

  private final DELETEExampleClientError deleteExampleClientError;

  @Autowired
  public DELETEClientService(DELETEExampleClient deleteExampleClient, DELETEExampleClientError deleteExampleClientError) {
    this.deleteExampleClient = deleteExampleClient;
    this.deleteExampleClientError = deleteExampleClientError;
  }

  public Mono<ResponseEntity<String>> deleteRequest() {
    return deleteExampleClient.deleteRequest();
  }

  public Mono<ResponseEntity<String>> deleteRequestError() {
    return deleteExampleClientError.deleteRequest();
  }

}
