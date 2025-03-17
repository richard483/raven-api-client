package com.nephren.raven.apiclient.error;

import java.lang.reflect.Method;
import reactor.core.publisher.Mono;

public interface ApiErrorResolver {

  Mono<Object> resolve(Throwable throwable, Class<?> type,
      Method method, Object[] arguments);
}
