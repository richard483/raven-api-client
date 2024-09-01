package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "postExampleClient")
public interface POSTExampleClient {

  @PostMapping(value = "/postRequest",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<ServerResponseBody>> postRequest(@RequestBody ServerRequestBody requestBody);

}
