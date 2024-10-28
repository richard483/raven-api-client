package com.nephren.raven.apiclient.body;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class MultipartBodyResolver implements ApiBodyResolver {

  @Override
  public boolean canResolve(String contentType) {
    return MediaType.MULTIPART_FORM_DATA_VALUE.equals(contentType);
  }

  @Override
  public Mono<BodyInserter<?, ? super ClientHttpRequest>> resolve(
      Method method, Object[] arguments) {
    Parameter[] parameters = method.getParameters();
    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    Mono<BodyInserter<?, ? super ClientHttpRequest>> bodyInserter = Mono.empty();
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      RequestPart annotation = parameter.getAnnotation(RequestPart.class);

      if (annotation != null) {
        String name = annotation.name().isEmpty() ? annotation.value() : annotation.name();
        bodyInserter = argumentToBodyInserter(arguments[i], name, builder);

      }
    }
    return bodyInserter;
  }

  private Mono<BodyInserter<?, ? super ClientHttpRequest>> argumentToBodyInserter(Object argument, String name, MultipartBodyBuilder builder) {
    if (argument instanceof Flux) {
      Flux<Object> filePart = (Flux<Object>) argument;
      return filePart.collectList().map(files -> {
        for (Object file : files) {
          builder.part(name, file);
        }
        return BodyInserters.fromMultipartData(builder.build());
      });
    } else if (argument instanceof Mono) {
      Mono<Object> filePart = (Mono<Object>) argument;
      return filePart.map(file -> {
        builder.part(name, file);
        return BodyInserters.fromMultipartData(builder.build());
      });
    } else if (argument != null) {
      builder.part(name, argument);
      return Mono.just(BodyInserters.fromMultipartData(builder.build()));
    }
    return Mono.empty();
  }

}