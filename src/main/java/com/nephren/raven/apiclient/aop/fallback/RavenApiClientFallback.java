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
            return asMono(ReflectionUtils.invokeMethod(method, fallback, arguments), method);
          }

          Method methodWithException = fallbackMetadata.exceptionMethods().get(method);
          if (methodWithException != null) {
            Object[] target = getArgumentsWithException(arguments, exception);
            return asMono(
                ReflectionUtils.invokeMethod(methodWithException, fallback, target),
                methodWithException);
          }

          Method fallbackMethod = fallbackMetadata.methods().get(method);
          if (fallbackMethod != null) {
            return asMono(
                ReflectionUtils.invokeMethod(fallbackMethod, fallback, arguments), fallbackMethod);
          }

          return Mono.error(exception);
        });
  }

  private static Mono<?> asMono(Object result, Method invoked) {
    if (result instanceof Mono<?> mono) {
      return mono;
    }
    String got = result == null ? "null" : result.getClass().getName();
    return Mono.error(new IllegalStateException(
        "#RavenApiClientFallback fallback method '" + invoked.getName()
            + "' must return reactor.core.publisher.Mono, got " + got));
  }

  private Object[] getArgumentsWithException(Object[] arguments, Throwable exception) {
    Object[] target = new Object[arguments.length + 1];
    System.arraycopy(arguments, 0, target, 0, arguments.length);
    target[target.length - 1] = exception;
    return target;
  }

}
