package com.nephren.raven.apiclient.serviceExample.client.fallback;

import com.nephren.raven.apiclient.serviceExample.client.POSTExampleClient;
import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class POSTExampleClientFallback implements POSTExampleClient {
  // this fallback should never be called, for testing purposes only
  @Override
  public Mono<ResponseEntity<ServerResponseBody>> postRequest(ServerRequestBody requestBody) {
    log.error("#POSTExampleClientFallback.postRequest - Fallback during calling postRequest");
    ServerResponseBody serverResponseBody = new ServerResponseBody();
    serverResponseBody.setMessage("Fallback during calling postRequest");
    return Mono.just(ResponseEntity.ok(serverResponseBody));
  }

  @Override
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipart(FilePart file) {
    return null;
  }

}
