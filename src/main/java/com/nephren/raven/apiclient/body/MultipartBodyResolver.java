package com.nephren.raven.apiclient.body;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      RequestPart annotation = parameter.getAnnotation(RequestPart.class);

      if (annotation != null) {
        String name = annotation.name().isEmpty() ? annotation.value() : annotation.name();

        if (arguments[i] instanceof Flux) {
          Flux<Object> filePart = (Flux<Object>) arguments[i];
          return filePart.collectList().map(files -> {
            for (Object file : files) {
              builder.part(name, file);
            }
            MultiValueMap<String, HttpEntity<?>> multiValueMap = builder.build();
            return BodyInserters.fromMultipartData(multiValueMap);
          });
        } else if (arguments[i] instanceof Mono) {
          Mono<Object> filePart = (Mono<Object>) arguments[i];
          return filePart.map(file -> {
            builder.part(name, file);
            MultiValueMap<String, HttpEntity<?>> multiValueMap = builder.build();
            return BodyInserters.fromMultipartData(multiValueMap);
          });
        } else {
          builder.part(name, arguments[i]);
          MultiValueMap<String, HttpEntity<?>> multiValueMap = builder.build();
          return Mono.just(BodyInserters.fromMultipartData(multiValueMap));
        }
      }
    }
    return Mono.empty();
  }

}