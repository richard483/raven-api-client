package com.nephren.raven.apiclient.serviceExample.client.errorresolver;

import com.nephren.raven.apiclient.error.ApiErrorResolver;
import java.lang.reflect.Method;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DeleteExampleClientErrorResolver implements ApiErrorResolver {

  @Override
  public Mono<Object> resolve(Throwable throwable, Class<?> type, Method method,
      Object[] arguments) {
    return Mono.error(throwable);
  }
}
