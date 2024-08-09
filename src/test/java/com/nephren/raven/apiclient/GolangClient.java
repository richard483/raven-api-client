package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "", url = "http://localhost:8080")
public interface GolangClient {
  @GetMapping("/hello")
  Mono<String> hello();

}
