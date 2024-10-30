package com.nephren.raven.apiclient.serviceExample.controller;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import com.nephren.raven.apiclient.serviceExample.service.POSTClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("post")
public class POSTController {

  private final POSTClientService clientService;

  @Autowired
  public POSTController(POSTClientService clientService) {
    this.clientService = clientService;
  }

  @PostMapping()
  public Mono<ResponseEntity<ServerResponseBody>> postRequest(@RequestBody ServerRequestBody body) {
    return clientService.postRequest(body);
  }

  @PostMapping(value = "multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipart(
      @RequestPart("file") FilePart file) {
    return clientService.postRequestMultipart(file);
  }

  @PostMapping(value = "multipart-noArgs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipart() {
    return clientService.postRequestMultipart(null);
  }

  @PostMapping(value = "multipart-noBody", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartNoBody() {
    return clientService.postRequestMultipartNoBody();
  }

  @PostMapping(value = "multipart-noBody-pathVariable/{name}", consumes =
      MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartNoBodyPathVariable(
      @PathVariable String name) {
    return clientService.postRequestMultipartNoBodyPathVariable(name);
  }

  @PostMapping(value = "multipart-reactive", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartReactive(
      @RequestPart("file") Flux<FilePart> file) {
    return clientService.postRequestMultipartReactiveFlux(file);
  }

  @PostMapping(value = "multipart-reactive-mono", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartReactiveMono(
      @RequestPart("file") Mono<FilePart> file) {
    return clientService.postRequestMultipartReactiveMono(file);
  }

  @PostMapping(value = "applicationForm", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public Mono<ResponseEntity<ServerResponseBody>> postRequestApplicationForm(
      ServerWebExchange serverWebExchange) {
    return serverWebExchange.getFormData().flatMap(clientService::postRequestApplicationForm);
  }

  @PostMapping(value = "applicationForm-noBody",
      consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public Mono<ResponseEntity<ServerResponseBody>> postRequestApplicationFormNoBody() {
    return clientService.postRequestApplicationFormNoBody();
  }

}
