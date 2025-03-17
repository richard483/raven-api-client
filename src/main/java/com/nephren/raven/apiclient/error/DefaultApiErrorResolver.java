package com.nephren.raven.apiclient.error;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class DefaultApiErrorResolver implements ApiErrorResolver {

  @Override
  public Mono<Object> resolve(Throwable throwable, Class<?> type, Method method,
      Object[] arguments) {
    log.error(throwable.getMessage(), throwable);
    return Mono.empty();
  }
}
