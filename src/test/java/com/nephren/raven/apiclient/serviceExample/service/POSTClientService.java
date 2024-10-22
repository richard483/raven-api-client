package com.nephren.raven.apiclient.serviceExample.service;

import com.nephren.raven.apiclient.serviceExample.client.POSTExampleClient;
import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class POSTClientService {

  private final POSTExampleClient postExampleClient;

  @Autowired
  public POSTClientService(POSTExampleClient postExampleClient) {
    this.postExampleClient = postExampleClient;
  }

  public Mono<ResponseEntity<ServerResponseBody>> postRequest(ServerRequestBody name) {
    return postExampleClient.postRequest(name);
  }

  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipart(FilePart file) {
    return postExampleClient.postRequestMultipart(file);
  }

  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartReactive(
      Flux<FilePart> file) {
    return postExampleClient.postRequestMultipartReactive(file);
  }

  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartReactiveMono(
      Mono<FilePart> file) {
    return postExampleClient.postRequestMultipartReactiveMono(file);
  }

}
