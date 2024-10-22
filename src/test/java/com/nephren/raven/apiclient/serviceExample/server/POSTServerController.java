package com.nephren.raven.apiclient.serviceExample.server;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
public class POSTServerController {

  @PostMapping(path = "/postRequest")
  public Mono<ResponseEntity<ServerResponseBody>> postRequest(
      @RequestBody ServerRequestBody requestBody) {
    String message = "Hello, " + requestBody.getName() + "!";
    return Mono.just(ResponseEntity.ok(ServerResponseBody.builder().message(message).build()));
  }

  @PostMapping(path = "/postRequest-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipart(
      @RequestPart("file") FilePart filePart) {
    return filePart.content()
        .reduce(DataBuffer::write)  // Reduce the Flux<DataBuffer> into a single DataBuffer
        .map(dataBuffer -> {
          byte[] bytes = new byte[dataBuffer.readableByteCount()];
          dataBuffer.read(bytes);
          return bytes;
        })
        .flatMap(bytes -> {
          String message = new String(bytes);
          return Mono.just(ResponseEntity.ok(
              ServerResponseBody.builder()
                  .message("Hello, World! Your file content is: " + message)
                  .build()
          ));
        });
  }

  @PostMapping(path = "/postRequest-multipart-reactive",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartReactive(
      @RequestPart("file") Flux<FilePart> filePart) {

    return filePart.publishOn(Schedulers.boundedElastic()).flatMap(fp -> fp.content().map(df -> {
          byte[] bytes = new byte[df.readableByteCount()];
          df.read(bytes);
          return bytes;
        })
        .reduce((a, b) -> {
          byte[] result = new byte[a.length + b.length];
          System.arraycopy(a, 0, result, 0, a.length);
          System.arraycopy(b, 0, result, a.length, b.length);
          return result;
        })
        .map(bytes -> {
          String message = new String(bytes, StandardCharsets.UTF_8);
          return ResponseEntity.ok(ServerResponseBody.builder()
              .message("Hello, World! Your file content is: " + message)
              .build());
        })).next();
  }

}
