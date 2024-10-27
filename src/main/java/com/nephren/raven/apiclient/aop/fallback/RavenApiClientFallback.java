package com.nephren.raven.apiclient.aop.fallback;

import lombok.Builder;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

@Builder
public class RavenApiClientFallback {
  private Object fallback;
  private FallbackMetadata fallbackMetadata;

  public boolean isAvailable() {
    return fallback != null;
  }

  public Mono invoke(Method method, Object[] arguments, Throwable throwable) {
    return Mono.just(throwable)
        .flatMap(exception -> {
          if (method.getDeclaringClass().isAssignableFrom(fallback.getClass())) {
            return (Mono) ReflectionUtils.invokeMethod(method, fallback, arguments);
          }

          Method methodWithException = fallbackMetadata.exceptionMethods().get(method);
          if (methodWithException != null) {
            Object[] target = getArgumentsWithException(arguments, exception);
            return (Mono) ReflectionUtils.invokeMethod(methodWithException, fallback, target);
          }

          Method fallbackMethod = fallbackMetadata.methods().get(method);
          if (fallbackMethod != null) {
            return (Mono) ReflectionUtils.invokeMethod(fallbackMethod, fallback, arguments);
          }

          return Mono.error(exception);
        });
  }

  private Object[] getArgumentsWithException(Object[] arguments, Throwable exception) {
    Object[] target = new Object[arguments.length + 1];
    System.arraycopy(arguments, 0, target, 0, arguments.length);
    target[target.length - 1] = exception;
    return target;
  }

}
