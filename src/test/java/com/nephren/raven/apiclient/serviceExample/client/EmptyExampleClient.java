package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "emptyExampleClient")
public interface EmptyExampleClient {
  Mono<ResponseEntity<String>> emptyUnimplementedRequest();
}
