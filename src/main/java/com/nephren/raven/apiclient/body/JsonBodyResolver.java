package com.nephren.raven.apiclient.body;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

@NoArgsConstructor
public class JsonBodyResolver implements ApiBodyResolver {

  @Override
  public boolean canResolve(String contentType) {
    return MediaType.APPLICATION_JSON_VALUE.equals(contentType);
  }

  @Override
  public BodyInserter<?, ? super ClientHttpRequest> resolve(Method method, Object[] arguments) {
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < parameters.length; i++) {
      Parameter parameter = parameters[i];
      RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
      if (requestBody != null) {
        return BodyInserters.fromValue(arguments[i]);
      }
    }
    return null;
  }

}
