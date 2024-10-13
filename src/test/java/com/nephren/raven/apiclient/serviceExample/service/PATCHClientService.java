package com.nephren.raven.apiclient.serviceExample.service;

import com.nephren.raven.apiclient.serviceExample.client.PATCHExampleClient;
import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PATCHClientService {

  private final PATCHExampleClient patchExampleClient;

  @Autowired
  public PATCHClientService(PATCHExampleClient patchExampleClient) {
    this.patchExampleClient = patchExampleClient;
  }

  public Mono<ResponseEntity<ServerResponseBody>> patchRequest(ServerRequestBody name) {
    return patchExampleClient.patchRequest(name);
  }

}
