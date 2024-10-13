package com.nephren.raven.apiclient.serviceExample.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ExampleClientWithFallbackOtherFallback {

  public Mono<ResponseEntity<String>> getRequestISE() {
    log.error("#ExampleClientWithFallbackOtherFallback.getRequest - Fallback during calling getRequestISE");
    return Mono.just(ResponseEntity.ok("Fallback during calling getRequestISE"));
  }

  public Mono<ResponseEntity<String>> getRequestISEWithThrowableParam(Throwable throwable) {
    log.error("#ExampleClientWithFallbackOtherFallback.getRequestISEWithThrowableParam - Fallback during calling getRequestISE");
    return Mono.just(ResponseEntity.ok(throwable.getMessage()));
  }

}
