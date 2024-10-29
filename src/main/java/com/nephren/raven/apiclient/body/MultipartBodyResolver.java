package com.nephren.raven.apiclient.body;

import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
    Mono<MultipartBodyBuilder> builder = Mono.just(new MultipartBodyBuilder());

    return Flux.fromArray(parameters)
        .index()
        .flatMap(indexAndParameter -> collectNameObjectPair(indexAndParameter, arguments))
        .reduce(builder, (b, nameObjectPair) -> handleMultipart(nameObjectPair, b))
        .flatMap(b -> b.map(buildr -> BodyInserters.fromMultipartData(buildr.build())));
  }

  private Mono<NameObjectPair> collectNameObjectPair(
      Tuple2<Long, Parameter> indexAndParameter,
      Object[] arguments) {
    int index = indexAndParameter.getT1().intValue();
    Parameter parameter = indexAndParameter.getT2();
    RequestPart annotation = parameter.getAnnotation(RequestPart.class);

    if (annotation != null) {
      String name = annotation.name().isEmpty() ? annotation.value() : annotation.name();
      return Mono.just(new NameObjectPair(name, arguments[index]));
    }
    return Mono.empty();
  }

  private Mono<MultipartBodyBuilder> handleMultipart(
      NameObjectPair nameObjectPair, Mono<MultipartBodyBuilder> builder) {
    if (nameObjectPair.object() instanceof Flux) {
      Flux<Object> filePart = (Flux<Object>) nameObjectPair.object();
      return filePart.collectList().flatMap(files -> builder.map(b -> {
        for (Object file : files) {
          b.part(nameObjectPair.name(), file);
        }
        return b;
      }));
    } else if (nameObjectPair.object() instanceof Mono) {
      Mono<Object> filePart = (Mono<Object>) nameObjectPair.object();
      return filePart.flatMap(file -> builder.map(buildr -> {
        buildr.part(nameObjectPair.name(), file);
        return buildr;
      }));
    } else {
      return builder.map(b -> {
        b.part(nameObjectPair.name(), nameObjectPair.object());
        return b;
      });
    }

  }

  private record NameObjectPair(String name, Object object) {
  }

}