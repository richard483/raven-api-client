package com.nephren.raven.apiclient.serviceExample.server;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
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

  @PostMapping(path = "/postRequest-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipart(
      @RequestPart("file") Mono<FilePart> filePartMono) {

    return filePartMono
        .flatMap(filePart -> {
          Flux<DataBuffer> content = filePart.content();
          return DataBufferUtils.join(content)
              .map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                String fileContent = new String(bytes, StandardCharsets.UTF_8);
                return ResponseEntity.ok(
                    ServerResponseBody.builder()
                        .message("Hello, World! Your file content is: " + fileContent).build());
              });
        });

    //    return file.map(filePart -> {
    //      return filePart.content()
    //          .map(dataBuffer -> {
    //            // Process each DataBuffer from the file content
    //            // For example, read data, store it, etc.
    //            return "Received file: " + filePart.filename();
    //          })
    //          .then(Mono.just(
    //              ResponseEntity.ok(ServerResponseBody.builder().message("File received").build())))
    //          .block();
    //    });

    //    return file.flatMap(f -> {
    //      log.info("File name: {}", f.filename());
    //      return Mono.just(f);
    //    }).then(Mono.just(
    //        ResponseEntity.ok(ServerResponseBody.builder().message("File received").build())));

    //    return Mono.just(
    //        ResponseEntity.ok(ServerResponseBody.builder().message(file.content().toString()).build()));
  }

}
