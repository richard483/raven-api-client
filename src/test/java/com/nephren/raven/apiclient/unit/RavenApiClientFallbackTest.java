package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.aop.fallback.FallbackMetadata;
import com.nephren.raven.apiclient.aop.fallback.RavenApiClientFallback;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class RavenApiClientFallbackTest {

  public interface Api {
    Mono<String> call();
  }

  public static class GoodFallback implements Api {
    @Override
    public Mono<String> call() {
      return Mono.just("fallback-value");
    }
  }

  public static class BadFallback {
    public String call() {
      return "not-a-mono";
    }
  }

  @Test
  void invoke_withDirectImplementingFallback_returnsFallbackMono() throws Exception {
    Method method = Api.class.getMethod("call");
    RavenApiClientFallback fallback = RavenApiClientFallback.builder()
        .fallback(new GoodFallback())
        .fallbackMetadata(new FallbackMetadata(Collections.emptyMap(), Collections.emptyMap()))
        .build();

    StepVerifier.create(fallback.invoke(method, new Object[0], new RuntimeException("boom")))
        .expectNext("fallback-value")
        .verifyComplete();
  }

  @Test
  void invoke_whenFallbackMethodReturnsNonMono_emitsIllegalStateException() throws Exception {
    Method apiMethod = Api.class.getMethod("call");
    Method badMethod = BadFallback.class.getMethod("call");
    Map<Method, Method> methods = new HashMap<>();
    methods.put(apiMethod, badMethod);
    RavenApiClientFallback fallback = RavenApiClientFallback.builder()
        .fallback(new BadFallback())
        .fallbackMetadata(new FallbackMetadata(methods, Collections.emptyMap()))
        .build();

    StepVerifier.create(fallback.invoke(apiMethod, new Object[0], new RuntimeException("boom")))
        .expectError(IllegalStateException.class)
        .verify();
  }

  @Test
  void invoke_whenNoFallbackMatches_propagatesOriginalThrowable() throws Exception {
    Method method = Api.class.getMethod("call");
    RuntimeException original = new RuntimeException("boom");
    RavenApiClientFallback fallback = RavenApiClientFallback.builder()
        .fallback(new BadFallback())
        .fallbackMetadata(new FallbackMetadata(Collections.emptyMap(), Collections.emptyMap()))
        .build();

    StepVerifier.create(fallback.invoke(method, new Object[0], original))
        .expectErrorMatches(t -> t == original)
        .verify();
  }
}
