package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.serviceExample.model.ServerResponseBody;
import com.nephren.raven.apiclient.serviceExample.service.GETClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

/**
 * Verifies that {@code Mono<List<POJO>>} and {@code Mono<ResponseEntity<List<POJO>>>} return
 * shapes deserialize properly into typed POJO elements rather than degrading to
 * {@code List<LinkedHashMap>} — the regression fixed in tier 2 B3.
 *
 * <p>Before the fix the interceptor passed only the raw type ({@code List.class}) or only the
 * inner element type ({@code POJO.class}) to {@link
 * org.springframework.core.ParameterizedTypeReference#forType}, erasing the generic parameter
 * Jackson needs to drive POJO deserialization. With the existing {@code List<String>} tests
 * this was masked because Jackson maps JSON strings to {@link String} natively; POJO elements
 * surface the bug.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientGenericResponseTests {

  private final GETClientService getClientService;

  @Autowired
  public RavenApiClientGenericResponseTests(GETClientService getClientService) {
    this.getClientService = getClientService;
  }

  @Test
  void monoListPojo_deserializesIntoTypedPojoElements() {
    StepVerifier.create(getClientService.getRequestListPojoNoEntity())
        .assertNext(list -> {
          org.assertj.core.api.Assertions.assertThat(list).hasSize(2);
          org.assertj.core.api.Assertions.assertThat(list.get(0))
              .isInstanceOf(ServerResponseBody.class)
              .extracting(ServerResponseBody::getMessage).isEqualTo("first");
          org.assertj.core.api.Assertions.assertThat(list.get(1))
              .extracting(ServerResponseBody::getMessage).isEqualTo("second");
        })
        .verifyComplete();
  }

  @Test
  void monoResponseEntityListPojo_deserializesIntoTypedPojoElements() {
    StepVerifier.create(getClientService.getRequestListPojo())
        .assertNext(entity -> {
          org.assertj.core.api.Assertions.assertThat(entity.getStatusCode().is2xxSuccessful())
              .isTrue();
          org.assertj.core.api.Assertions.assertThat(entity.getBody()).hasSize(2);
          org.assertj.core.api.Assertions.assertThat(entity.getBody().get(0))
              .isInstanceOf(ServerResponseBody.class)
              .extracting(ServerResponseBody::getMessage).isEqualTo("first");
          org.assertj.core.api.Assertions.assertThat(entity.getBody().get(1))
              .extracting(ServerResponseBody::getMessage).isEqualTo("second");
        })
        .verifyComplete();
  }
}
