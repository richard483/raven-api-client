package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.exception.RavenApiException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RavenApiExceptionTest {

  @Test
  void messageOnlyConstructor_setsMessage_andHasNoCause() {
    RavenApiException ex = new RavenApiException("boom");

    Assertions.assertThat(ex.getMessage()).isEqualTo("boom");
    Assertions.assertThat(ex.getCause()).isNull();
  }

  @Test
  void messageAndCauseConstructor_chainsCause() {
    Throwable root = new IllegalStateException("root");
    RavenApiException ex = new RavenApiException("boom", root);

    Assertions.assertThat(ex.getMessage()).isEqualTo("boom");
    Assertions.assertThat(ex.getCause()).isSameAs(root);
  }
}
