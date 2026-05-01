package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.body.FormBodyResolver;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.test.StepVerifier;

class FormBodyResolverTest {

  private final FormBodyResolver resolver = new FormBodyResolver();

  interface Sample {
    void valid(@RequestBody MultiValueMap<String, String> body);

    void wrongType(@RequestBody String body);
  }

  @Test
  void canResolve_onlyMatchesFormUrlEncoded() {
    org.assertj.core.api.Assertions.assertThat(
            resolver.canResolve(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .isTrue();
    org.assertj.core.api.Assertions.assertThat(
            resolver.canResolve(MediaType.APPLICATION_JSON_VALUE))
        .isFalse();
  }

  @Test
  void resolve_withMultiValueMap_emitsBodyInserter() throws Exception {
    Method method = Sample.class.getMethod("valid", MultiValueMap.class);
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("k", "v");

    StepVerifier.create(resolver.resolve(method, new Object[]{form}))
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void resolve_withWrongArgumentType_emitsIllegalArgumentException() throws Exception {
    Method method = Sample.class.getMethod("wrongType", String.class);

    StepVerifier.create(resolver.resolve(method, new Object[]{"not-a-map"}))
        .expectError(IllegalArgumentException.class)
        .verify();
  }
}
