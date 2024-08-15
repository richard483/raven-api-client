package com.nephren.raven.apiclient.serviceExample;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "asd")
public interface Asd {
  @GetMapping(value = "/",
      produces = MediaType.TEXT_PLAIN_VALUE)
  Mono<String> asd();

  @RequestMapping(value = "/", method = RequestMethod.GET,
      produces = MediaType.TEXT_PLAIN_VALUE)
  Mono<String> asda();

}
