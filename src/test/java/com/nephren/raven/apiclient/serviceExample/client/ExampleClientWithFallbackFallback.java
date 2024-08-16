package com.nephren.raven.apiclient.serviceExample.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ExampleClientWithFallbackFallback implements ExampleClientWithFallback {

  @Override
  public Mono<ResponseEntity<String>> getRequest() {
    return null;
  }
  @Override
  public Mono<ResponseEntity<String>> getRequestISE() {
    log.error("#ExampleClientWithFallbackFallback.getRequest - Fallback during calling getRequest");
    return Mono.just(ResponseEntity.ok("Fallback during calling getRequest"));
  }

}
