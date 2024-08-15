package com.nephren.raven.apiclient.serviceExample.server;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

//@Profile("test")
@Service
public class ServerService {
  public Mono<String> getRequest() {
    return Mono.just("Hello, World!");
  }

}
