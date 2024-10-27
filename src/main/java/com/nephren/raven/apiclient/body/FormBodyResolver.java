package com.nephren.raven.apiclient.body;

import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class FormBodyResolver implements ApiBodyResolver {

  @Override
  public boolean canResolve(String contentType) {
    return MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType);
  }

  @Override
  public Mono<BodyInserter<?, ? super ClientHttpRequest>> resolve(
      Method method, Object[] arguments) {
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
      if (requestBody != null && arguments[i] != null) {
        return Mono.just(BodyInserters.fromFormData((MultiValueMap<String, String>) arguments[i]));
      }
    }
    return Mono.empty();
  }

}