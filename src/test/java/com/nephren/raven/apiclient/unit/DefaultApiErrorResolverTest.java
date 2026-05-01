package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.errorresolver.DefaultApiErrorResolver;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class DefaultApiErrorResolverTest {

  private final DefaultApiErrorResolver resolver = new DefaultApiErrorResolver();

  @Test
  void resolve_returnsEmpty_signallingFallbackOrPropagation() {
    Mono<Object> result = resolver.resolve(
        new RuntimeException("boom"), Object.class, null, new Object[0]);

    StepVerifier.create(result).verifyComplete();
  }
}
