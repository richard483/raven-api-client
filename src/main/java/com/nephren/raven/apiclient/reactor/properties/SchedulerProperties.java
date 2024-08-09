package com.nephren.raven.apiclient.reactor.properties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import reactor.core.scheduler.Schedulers;

@Data
@ConfigurationProperties("nephren.raven.reactor.helper")
public class SchedulerProperties {

  private final Map<String, SchedulerItemProperties> configs = new HashMap<>();

  public enum ExecutorType {

    FIXED_THREAD_POOL,
    WORK_STEALING_POOL,
    SINGLE_THREAD_POOL,
    CACHED_THREAD_POOL

  }

  public enum QueueType {

    ARRAY,
    LINKED

  }

  public enum SchedulerType {

    PARALLEL,
    SINGLE,
    IMMEDIATE,
    NEW_PARALLEL,
    NEW_SINGLE,
    EXECUTOR,
    THREAD_POOL,
    NEW_BOUNDED_ELASTIC

  }

  @Data
  public static class SchedulerItemProperties {

    private SchedulerType type = SchedulerType.IMMEDIATE;

    private SchedulerNewParallelProperties newParallel = new SchedulerNewParallelProperties();

    private SchedulerNewSingleProperties newSingle = new SchedulerNewSingleProperties();

    private SchedulerExecutorProperties executor = new SchedulerExecutorProperties();

    private SchedulerThreadPoolProperties threadPool = new SchedulerThreadPoolProperties();

    private SchedulerNewBoundedElasticProperties newBoundedElastic =
        new SchedulerNewBoundedElasticProperties();

  }

  @Data
  public static class SchedulerNewParallelProperties {

    private String name;

    private Integer parallelism = Schedulers.DEFAULT_POOL_SIZE;

    private Boolean daemon = false;

  }

  @Data
  public static class SchedulerNewSingleProperties {

    private String name;

    private Boolean daemon = false;

  }

  @Data
  public static class SchedulerExecutorProperties {

    private ExecutorType type = ExecutorType.CACHED_THREAD_POOL;

    private Integer numberOfThread;

    private Integer parallelism = Runtime.getRuntime().availableProcessors();

  }

  @Data
  public static class SchedulerThreadPoolProperties {

    private Integer corePoolSize = 10;

    private Boolean allowCoreThreadTimeOut = false;

    private Integer maximumPoolSize = 50;

    private Duration ttl = Duration.ofSeconds(60);

    private QueueType queueType = QueueType.LINKED;

    private Integer queueSize = Integer.MAX_VALUE;

  }

  @Data
  public static class SchedulerNewBoundedElasticProperties {

    private Integer threadSize = Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE;

    private Integer queueSize = Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE;

    private String name = "boundedElastic";

    private Duration ttl = Duration.ofSeconds(60);

    private Boolean daemon = Boolean.TRUE;

  }

}
