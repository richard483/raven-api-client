package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.reactor.helper.SchedulerHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWebTestClient
class RavenApiClientSchedulerTests {
  private final SchedulerHelper schedulerHelper;

  @Autowired
  public RavenApiClientSchedulerTests(SchedulerHelper schedulerHelper) {
    this.schedulerHelper = schedulerHelper;
  }

  @Test
  void schedulerTest() {
    assertNotNull(schedulerHelper.of("SINGLE"));
    assertNotNull(schedulerHelper.of("PARALLEL"));
    assertNotNull(schedulerHelper.of("IMMEDIATE"));
    assertNotNull(schedulerHelper.of("NEW_SINGLE"));
    assertNotNull(schedulerHelper.of("NEW_PARALLEL"));
    assertNotNull(schedulerHelper.of("NEW_BOUNDED_ELASTIC"));
    assertNotNull(schedulerHelper.of("EXECUTOR_SINGLE_THREAD_POOL"));
    assertNotNull(schedulerHelper.of("EXECUTOR_WORK_STEALING_POOL"));
    assertNotNull(schedulerHelper.of("EXECUTOR_CACHED_THREAD_POOL"));
    assertNotNull(schedulerHelper.of("EXECUTOR_FIXED_THREAD_POOL"));
    assertNotNull(schedulerHelper.of("THREAD_POOL"));
  }

}