package com.nephren.raven.apiclient.serviceExample.service;

import com.nephren.raven.apiclient.serviceExample.client.ExampleClientWithFallback;
import com.nephren.raven.apiclient.serviceExample.client.ExampleClientWithOtherFallback;
import com.nephren.raven.apiclient.serviceExample.client.GETExampleClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GETClientService {

  private final GETExampleClient getExampleClient;

  private final ExampleClientWithFallback exampleClientWithFallback;

  private final ExampleClientWithOtherFallback exampleClientWithOtherFallback;

  @Autowired
  public GETClientService(
      GETExampleClient getExampleClient,
      ExampleClientWithFallback exampleClientWithFallback,
      ExampleClientWithOtherFallback exampleClientWithOtherFallback) {
    this.getExampleClient = getExampleClient;
    this.exampleClientWithFallback = exampleClientWithFallback;
    this.exampleClientWithOtherFallback = exampleClientWithOtherFallback;
  }

  public Mono<ResponseEntity<String>> getRequestNoPath() {
    return getExampleClient.getRequestNoPath();
  }

  public Mono<ResponseEntity<String>> getRequest() {
    return getExampleClient.getRequest();
  }

  public Mono<ResponseEntity<String>> getRequestISE() {
    return getExampleClient.getRequestISE();
  }

  public Mono<ResponseEntity<String>> getRequestWithFallback() {
    return exampleClientWithFallback.getRequestISE();
  }

  public Mono<ResponseEntity<String>> getRequestWithOtherFallback() {
    return exampleClientWithOtherFallback.getRequestISE();
  }

  public Mono<ResponseEntity<String>> getRequestWithOtherFallbackNoFallbackMethod() {
    return exampleClientWithOtherFallback.getRequestISENoFallbackMethod();
  }

  public Mono<ResponseEntity<String>> getRequestISEWithThrowableParam() {
    return exampleClientWithOtherFallback.getRequestISEWithThrowableParam();
  }

  public Mono<ResponseEntity<String>> getRequestWithHeader(String header) {
    return getExampleClient.getRequestWithHeader(header);
  }

  public Mono<ResponseEntity<String>> getRequestWithHeader2() {
    return getExampleClient.getRequestWithHeader2();
  }

  public Mono<ResponseEntity<String>> getRequestWithHeader3() {
    return getExampleClient.getRequestWithHeader3();
  }

  public Mono<ResponseEntity<String>> getRequestWithQueryParam(
      String nameQueryParam, String ageQueryParam) {
    return getExampleClient.getRequestWithQueryParam(nameQueryParam, ageQueryParam);
  }

  public Mono<ResponseEntity<String>> getRequestWithPathVariable(String var) {
    return getExampleClient.getRequestPathVariable(var);
  }

  public Mono<ResponseEntity<String>> getRequestWithCookieParam(String username) {
    return getExampleClient.getRequestWithCookieParam(username);
  }

}
