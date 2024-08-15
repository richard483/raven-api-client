package com.nephren.raven.apiclient.reactor.factory;

import com.nephren.raven.apiclient.properties.SchedulerProperties;
import com.nephren.raven.apiclient.reactor.helper.SchedulerHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Setter
public class SchedulerHelperFactoryBean implements FactoryBean<SchedulerHelper> {

  private SchedulerProperties schedulerProperties;
  @Override
  public SchedulerHelper getObject() throws Exception {
    Map<String, Scheduler> schedulers = new HashMap<>();

    schedulerProperties.getConfigs().forEach((name, properties) -> {
      schedulers.put(name, createScheduler(properties));
    });

    return new SchedulerHelperImpl(schedulers);
  }
  @Override
  public Class<?> getObjectType() {
    return null;
  }
  private Scheduler createScheduler(SchedulerProperties.SchedulerItemProperties properties) {
    switch (properties.getType()) {
      case SINGLE:
        return Schedulers.single();
      case PARALLEL:
        return Schedulers.parallel();
      case NEW_PARALLEL:
        return newParallelScheduler(properties.getNewParallel());
      case NEW_SINGLE:
        return newSingleScheduler(properties.getNewSingle());
      case EXECUTOR:
        return newExecutorScheduler(properties.getExecutor());
      case THREAD_POOL:
        return newThreadPollScheduler(properties.getThreadPool());
      case NEW_BOUNDED_ELASTIC:
        return newBoundedElastic(properties.getNewBoundedElastic());
      case IMMEDIATE:
      default:
        return Schedulers.immediate();
    }
  }

  private Scheduler newBoundedElastic(
      SchedulerProperties.SchedulerNewBoundedElasticProperties properties) {
    return Schedulers.newBoundedElastic(
        properties.getThreadSize(),
        properties.getQueueSize(),
        properties.getName(),
        (int) properties.getTtl().getSeconds(),
        properties.getDaemon()
    );
  }

  private Scheduler newThreadPollScheduler(
      SchedulerProperties.SchedulerThreadPoolProperties properties) {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        properties.getCorePoolSize(),
        properties.getMaximumPoolSize(),
        properties.getTtl().toMillis(),
        TimeUnit.MILLISECONDS,
        createBlockingQueue(properties)
    );

    executor.allowCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
    return Schedulers.fromExecutor(executor);
  }

  private BlockingQueue<Runnable> createBlockingQueue(
      SchedulerProperties.SchedulerThreadPoolProperties properties) {
    switch (properties.getQueueType()) {
      case ARRAY:
        return new ArrayBlockingQueue<>(properties.getQueueSize());
      case LINKED:
      default:
        return new LinkedBlockingQueue<>(properties.getQueueSize());
    }
  }

  private Scheduler newExecutorScheduler(
      SchedulerProperties.SchedulerExecutorProperties properties) {
    switch (properties.getType()) {
      case WORK_STEALING_POOL:
        return Schedulers.fromExecutorService(
            Executors.newWorkStealingPool(properties.getParallelism()));
      case CACHED_THREAD_POOL:
        return Schedulers.fromExecutorService(Executors.newCachedThreadPool());
      case FIXED_THREAD_POOL:
        return Schedulers.fromExecutorService(
            Executors.newFixedThreadPool(properties.getNumberOfThread()));
      case SINGLE_THREAD_POOL:
      default:
        return Schedulers.fromExecutorService(Executors.newSingleThreadExecutor());
    }
  }

  private Scheduler newSingleScheduler(
      SchedulerProperties.SchedulerNewSingleProperties properties) {
    return Schedulers.newSingle(properties.getName(), properties.getDaemon());
  }

  private Scheduler newParallelScheduler(
      SchedulerProperties.SchedulerNewParallelProperties properties) {
    return Schedulers.newParallel(properties.getName(), properties.getParallelism(),
        properties.getDaemon());
  }

  @AllArgsConstructor
  private static class SchedulerHelperImpl implements SchedulerHelper {

    private final Map<String, Scheduler> schedulers;

    @Override
    public Scheduler of(String name) {
      return schedulers.getOrDefault(name, Schedulers.immediate());
    }

  }

}
