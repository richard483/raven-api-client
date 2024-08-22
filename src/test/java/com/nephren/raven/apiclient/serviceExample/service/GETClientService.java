package com.nephren.raven.apiclient.serviceExample.service;

import com.nephren.raven.apiclient.serviceExample.client.ExampleClient;
import com.nephren.raven.apiclient.serviceExample.client.ExampleClientWithFallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GETClientService {
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

  public Mono<ResponseEntity<String>> getRequestWithHeader(String header) {
    return exampleClient.getRequestWithHeader(header);
  }

  public Mono<ResponseEntity<String>> getRequestWithHeader2() {
    return exampleClient.getRequestWithHeader2();
  }

  public Mono<ResponseEntity<String>> getRequestWithQueryParam(
      String nameQueryParam, String ageQueryParam) {
    return exampleClient.getRequestWithQueryParam(nameQueryParam, ageQueryParam);
  }

  public Mono<ResponseEntity<String>> getRequestWithPathVariable(String var) {
    return exampleClient.getRequestPathVariable(var);
  }

  public Mono<ResponseEntity<String>> getRequestWithCookieParam(String username) {
    return exampleClient.getRequestWithCookieParam(username);
  }

}
