package com.nephren.raven.apiclient.serviceExample.server;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class POSTServerController {

  private static final Logger log = LoggerFactory.getLogger(POSTServerController.class);

  @PostMapping(path = "/postRequest")
  public Mono<ResponseEntity<ServerResponseBody>> postRequest(
      @RequestBody ServerRequestBody requestBody) {
    String message = "Hello, " + requestBody.getName() + "!";
    return Mono.just(ResponseEntity.ok(ServerResponseBody.builder().message(message).build()));
  }

  @PostMapping(path = "/postRequest-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipart(@RequestPart("file") FilePart filePart) {
    log.info("#Ricat Received file: {}", filePart);

    return Mono.just(ResponseEntity.ok(ServerResponseBody.builder()
        .message("Hello, World! Your file content is: Towa Sama Maji Tenshi").build()));
  }

}
