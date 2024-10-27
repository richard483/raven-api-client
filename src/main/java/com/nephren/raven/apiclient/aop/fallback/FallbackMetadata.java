package com.nephren.raven.apiclient.aop.fallback;

import java.lang.reflect.Method;
import java.util.Map;

public record FallbackMetadata(Map<Method, Method> methods, Map<Method, Method> exceptionMethods) {
}
