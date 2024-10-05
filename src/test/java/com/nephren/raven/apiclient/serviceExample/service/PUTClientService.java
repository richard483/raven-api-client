package com.nephren.raven.apiclient.serviceExample.service;

import com.nephren.raven.apiclient.serviceExample.client.PUTExampleClient;
import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PUTClientService {
  @Autowired
  private PUTExampleClient putExampleClient;

  public Mono<ResponseEntity<ServerResponseBody>> putRequest(ServerRequestBody name) {
    return putExampleClient.putRequest(name);
  }

}
