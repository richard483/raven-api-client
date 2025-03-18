package com.nephren.raven.apiclient.serviceExample.client.errorresolver;

import com.nephren.raven.apiclient.errorresolver.ApiErrorResolver;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DefaultErrorResolver implements ApiErrorResolver {

  @Override
  public Mono<Object> resolve(Throwable throwable, Class<?> type, Method method,
      Object[] arguments) {
    log.error(throwable.getMessage(), throwable);
    return Mono.empty();
  }
}
