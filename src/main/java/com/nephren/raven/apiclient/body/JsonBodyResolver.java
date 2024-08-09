package com.nephren.raven.apiclient.body;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

public class JsonBodyResolver implements ApiBodyResolver {

  private final ObjectMapper objectMapper;

  public JsonBodyResolver(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public boolean canResolve(String contentType) {
    return MediaType.APPLICATION_JSON_VALUE.equals(contentType);
  }

  @Override
  public BodyInserter<?, ? super ClientHttpRequest> resolve(Method method, Object[] arguments) {
    Parameter[] parameters = method.getParameters();
    for (int i = 0;
         i < parameters.length;
         i++) {
      Parameter parameter = parameters[i];
      RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
      if (requestBody != null) {
        return BodyInserters.fromValue(arguments[i]);
      }
    }
    return null;
  }

}
