package com.nephren.raven.apiclient.aop.fallback;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FallbackMetadataBuilder {
  private final Map<Method, Method> exceptionMethods = new HashMap<>();
  private final Map<Method, Method> methods = new HashMap<>();
  private final Class<?> apiClient;
  private final Class<?> fallback;

  public FallbackMetadataBuilder(Class<?> apiClient, Class<?> fallback) {
    this.apiClient = apiClient;
    this.fallback = fallback;
  }

  private void prepareExceptionMethods() {
    Method[] apiClientMethods = apiClient.getDeclaredMethods();
    Method[] fallbackMethods = fallback.getDeclaredMethods();

    for (Method apiClientMethod : apiClientMethods) {
      for (Method fallbackMethod : fallbackMethods) {
        if (fallbackMethod.getName().equals(apiClientMethod.getName()) &&
            Arrays.equals(fallbackMethod.getParameterTypes(),
                getParameterClassesWithException(apiClientMethod))) {
          exceptionMethods.put(apiClientMethod, fallbackMethod);
        }
      }
    }
  }

  private Class<?>[] getParameterClassesWithException(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    Class<?>[] result = new Class<?>[parameterTypes.length + 1];
    System.arraycopy(parameterTypes, 0, result, 0, parameterTypes.length);
    result[result.length - 1] = Throwable.class;
    return result;
  }

  private void prepareMethod() {
    Method[] apiClientMethods = apiClient.getDeclaredMethods();
    Method[] fallbackMethods = fallback.getDeclaredMethods();

    for (Method apiClientMethod : apiClientMethods) {
      for (Method fallbackMethod : fallbackMethods) {
        if (fallbackMethod.getName().equals(apiClientMethod.getName()) &&
            Arrays.equals(fallbackMethod.getParameterTypes(),
                apiClientMethod.getParameterTypes())) {
          methods.put(apiClientMethod, fallbackMethod);
        }
      }
    }
  }

  public FallbackMetadata build() {
    prepareExceptionMethods();
    prepareMethod();
    return new FallbackMetadata(methods, exceptionMethods);
  }

}
