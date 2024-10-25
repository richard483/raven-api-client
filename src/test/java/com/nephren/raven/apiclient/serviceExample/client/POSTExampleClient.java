package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import com.nephren.raven.apiclient.serviceExample.client.fallback.POSTExampleClientFallback;
import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RavenApiClient(name = "postExampleClient", fallback = POSTExampleClientFallback.class)
public interface POSTExampleClient {

  @PostMapping(value = "/postRequest",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<ServerResponseBody>> postRequest(@RequestBody ServerRequestBody requestBody);

  @PostMapping(value = "/postRequest-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<ServerResponseBody>> postRequestMultipart(
      @RequestPart("file") FilePart file);

  @PostMapping(value = "/postRequest-multipart-reactive",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartReactive(
      @RequestPart("file") Flux<FilePart> file);

  @PostMapping(value = "/postRequest-multipart-reactive", consumes =
      MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartReactiveMono(
      @RequestPart("file") Mono<FilePart> file);

  @PostMapping(value = "/postRequest-applicationForm",
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<ServerResponseBody>> postRequestApplicationForm(
      @RequestBody MultiValueMap<String, String> requestBody);

}
