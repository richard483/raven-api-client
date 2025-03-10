package com.nephren.raven.apiclient;

import com.nephren.raven.apiclient.reactor.helper.SchedulerHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.test.StepVerifier;

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

    Scheduler singleScheduler = schedulerHelper.of("SINGLE");
    Mono<String> testMono = Mono.just("test").subscribeOn(singleScheduler);
    StepVerifier.create(testMono)
            .expectNext("test")
            .verifyComplete();

    Scheduler parallelScheduler = schedulerHelper.of("PARALLEL");
    Mono<String> testMono2 = Mono.just("test").subscribeOn(parallelScheduler);
    StepVerifier.create(testMono2)
            .expectNext("test")
            .verifyComplete();

    Scheduler immediateScheduler = schedulerHelper.of("IMMEDIATE");
    Mono<String> testMono3 = Mono.just("test").subscribeOn(immediateScheduler);
    StepVerifier.create(testMono3)
            .expectNext("test")
            .verifyComplete();

    Scheduler newSingleScheduler = schedulerHelper.of("NEW_SINGLE");
    Mono<String> testMono4 = Mono.just("test").subscribeOn(newSingleScheduler);
    StepVerifier.create(testMono4)
            .expectNext("test")
            .verifyComplete();

    Scheduler newParallelScheduler = schedulerHelper.of("NEW_PARALLEL");
    Mono<String> testMono5 = Mono.just("test").subscribeOn(newParallelScheduler);
    StepVerifier.create(testMono5)
            .expectNext("test")
            .verifyComplete();

    Scheduler newBoundedElasticScheduler = schedulerHelper.of("NEW_BOUNDED_ELASTIC");
    Mono<String> testMono6 = Mono.just("test").subscribeOn(newBoundedElasticScheduler);
    StepVerifier.create(testMono6)
            .expectNext("test")
            .verifyComplete();

    Scheduler executorSingleThreadPoolScheduler = schedulerHelper.of("EXECUTOR_SINGLE_THREAD_POOL");
    Mono<String> testMono7 = Mono.just("test").subscribeOn(executorSingleThreadPoolScheduler);
    StepVerifier.create(testMono7)
            .expectNext("test")
            .verifyComplete();

    Scheduler executorWorkStealingPoolScheduler = schedulerHelper.of("EXECUTOR_WORK_STEALING_POOL");
    Mono<String> testMono8 = Mono.just("test").subscribeOn(executorWorkStealingPoolScheduler);
    StepVerifier.create(testMono8)
            .expectNext("test")
            .verifyComplete();

    Scheduler executorCachedThreadPoolScheduler = schedulerHelper.of("EXECUTOR_CACHED_THREAD_POOL");
    Mono<String> testMono9 = Mono.just("test").subscribeOn(executorCachedThreadPoolScheduler);
    StepVerifier.create(testMono9)
            .expectNext("test")
            .verifyComplete();

    Scheduler executorFixedThreadPoolScheduler = schedulerHelper.of("EXECUTOR_FIXED_THREAD_POOL");
    Mono<String> testMono10 = Mono.just("test").subscribeOn(executorFixedThreadPoolScheduler);
    StepVerifier.create(testMono10)
            .expectNext("test")
            .verifyComplete();

    Scheduler threadPoolScheduler = schedulerHelper.of("THREAD_POOL");
    Mono<String> testMono11 = Mono.just("test").subscribeOn(threadPoolScheduler);
    StepVerifier.create(testMono11)
            .expectNext("test")
            .verifyComplete();
  }

}