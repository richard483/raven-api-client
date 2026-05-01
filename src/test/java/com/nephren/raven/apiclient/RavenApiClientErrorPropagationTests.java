package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.serviceExample.service.GETClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

/**
 * Verifies that when no fallback is configured and the default error resolver is in use,
 * upstream errors propagate to the caller instead of being silently swallowed as
 * {@code Mono.empty()} — the regression fixed in this PR.
 *
 * <p>The {@code Mono<ResponseEntity<T>>} path short-circuits errors via
 * {@code onStatus(_, Mono.empty())} so it does not exercise the buggy branch; this test
 * uses the {@code Mono<T>} return shape instead, which lets the WebClient surface the
 * upstream {@link WebClientResponseException} that the interceptor must propagate.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientErrorPropagationTests {

  private final GETClientService getClientService;

  @Autowired
  public RavenApiClientErrorPropagationTests(GETClientService getClientService) {
    this.getClientService = getClientService;
  }

  @Test
  void monoReturn_noFallback_defaultResolver_propagatesUpstreamError() {
    StepVerifier.create(getClientService.getRequestISEWithoutResponseEntity())
        .expectError(WebClientResponseException.InternalServerError.class)
        .verify();
  }
}
