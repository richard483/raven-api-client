package com.nephren.raven.apiclient.serviceExample.server;

import com.nephren.raven.apiclient.serviceExample.model.ServerRequestBody;
import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.nio.charset.StandardCharsets;

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
    return filePart.content().reduce(DataBuffer::write)  // Reduce the Flux<DataBuffer> into a single DataBuffer
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

  @PostMapping(path = "/postRequest-multipart-reactive", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ResponseEntity<ServerResponseBody>> postRequestMultipartReactive(@RequestPart("file") Flux<FilePart> filePart) {


    return filePart
        .flatMap(fileParts -> fileParts.content()  // Get the file content as Flux<DataBuffer>
                .reduce(DataBuffer::write)  // Combine multiple DataBuffers into one
                .map(dataBuffer -> {
                  try {
                    // Convert the DataBuffer to a byte array
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    // Release the buffer to prevent memory leaks
//                dataBuffer.release();
                    // Convert bytes to a string or process the content as needed
                    return new String(bytes, StandardCharsets.UTF_8);
                  } catch (Exception e) {
                    throw new RuntimeException("Error reading file content", e);
                  }
                })
        )
        .flatMap(fileContent -> {
          // Return a Mono with the file content in the response
          String message = "File content: " + fileContent;
          return Mono.just(ResponseEntity.ok(ServerResponseBody.builder()
              .message("Hello, World! Your file content is: " + message)
              .build()));
        }).next();

//    return filePart.map(part -> {
//      return ResponseEntity.ok(ServerResponseBody.builder()
//          .message("Hello, World! Your file content is: Towa Sama Maji Tenshis")
//          .build());
//    }).next();
  }

}
