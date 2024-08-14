package com.nephren.raven.apiclient.aop.fallback;

import java.lang.reflect.Method;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FallbackMetadata {
  private final Map<Method, Method> methods;
  private final Map<Method, Method> exceptionMethods;

}
