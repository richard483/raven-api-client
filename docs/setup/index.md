---
layout: default
title: Setup
nav_order: 2
has_children: false
---

# Setup

## Add new repository to your pom.xml

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

## Add new dependency to your pom.xml

```xml
<dependency>
    <groupId>com.github.richard483</groupId>
    <artifactId>raven-api-client</artifactId>
    <version>${version-tag}</version>
</dependency>
```

## Initial Configuration

## Api Client Properties

```
# where your api client interfaces are located
nephren.raven.apiclient.packages=com.example.apiclient

# not required, base url for the api client, default is localhost
nephren.raven.apiclient.configs.<apiClient-name>.url=localhost:8080

# not required, fallback class for the api client, default is null
nephren.raven.apiclient.configs.<apiClient-name>.fallback=com.nephren.raven.apiclient.
serviceExample.client.fallback.ExampleClientWithFallbackOtherFallback

# not required, read timeout for the api client, default is 2000
nephren.raven.apiclient.configs.<apiClient-name>.read-timeout=5000

# not required, connect timeout for the api client, default is 2000
nephren.raven.apiclient.configs.<apiClient-name>.connect-timeout=5000

# not required, write timeout for the api client, default is 2000
nephren.raven.apiclient.configs.<apiClient-name>.write-timeout=5000

# not required, headers for the api client, default is empty
nephren.raven.apiclient.configs.<apiClient-name>.headers.<headers-key>=application/json

# not required, error resolver for the api client, default is DefaultErrorResolver
nephren.raven.apiclient.configs.<apiClient-name>.error-resolver=com.nephren.raven.apiclient.
serviceExample.client.errorresolver.DefaultErrorResolver
```

## Api Scheduler

```
# not required, customize scheduler flavors, default is immediate
nephren.raven.reactor.helper.configs.<apiClient-name>.type=single

# based on the configured flavors, there were some other settings that could be customized, but all of the is not required
nephren.raven.reactor.helper.configs.SINGLE.type=single
nephren.raven.reactor.helper.configs.PARALLEL.type=parallel
nephren.raven.reactor.helper.configs.IMMEDIATE.type=immediate
nephren.raven.reactor.helper.configs.NEW_SINGLE.type=new_single
nephren.raven.reactor.helper.configs.NEW_SINGLE.new-single.name=New Single
nephren.raven.reactor.helper.configs.NEW_SINGLE.new-single.daemon=true
nephren.raven.reactor.helper.configs.NEW_PARALLEL.type=new_parallel
nephren.raven.reactor.helper.configs.NEW_PARALLEL.new-parallel.daemon=true
nephren.raven.reactor.helper.configs.NEW_PARALLEL.new-parallel.name=New Parallel
nephren.raven.reactor.helper.configs.NEW_PARALLEL.new-parallel.parallelism=4
nephren.raven.reactor.helper.configs.NEW_BOUNDED_ELASTICS.type=new_bounded_elastic
nephren.raven.reactor.helper.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.thread-size=100
nephren.raven.reactor.helper.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.queue-size=1000
nephren.raven.reactor.helper.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.name=newBoundedElastic
nephren.raven.reactor.helper.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.ttl=60s
nephren.raven.reactor.helper.configs.NEW_BOUNDED_ELASTICS.new-bounded-elastic.daemon=true
nephren.raven.reactor.helper.configs.EXECUTOR_SINGLE_THREAD_POOL.type=executor
nephren.raven.reactor.helper.configs.EXECUTOR_SINGLE_THREAD_POOL.executor.type=single_thread_pool
nephren.raven.reactor.helper.configs.EXECUTOR_WORK_STEALING_POOL.type=executor
nephren.raven.reactor.helper.configs.EXECUTOR_WORK_STEALING_POOL.executor.type=work_stealing_pool
nephren.raven.reactor.helper.configs.EXECUTOR_WORK_STEALING_POOL.executor.parallelism=5
nephren.raven.reactor.helper.configs.EXECUTOR_CACHED_THREAD_POOL.type=executor
nephren.raven.reactor.helper.configs.EXECUTOR_CACHED_THREAD_POOL.executor.type=cached_thread_pool
nephren.raven.reactor.helper.configs.EXECUTOR_FIXED_THREAD_POOL.type=executor
nephren.raven.reactor.helper.configs.EXECUTOR_FIXED_THREAD_POOL.executor.type=fixed_thread_pool
nephren.raven.reactor.helper.configs.EXECUTOR_FIXED_THREAD_POOL.executor.number-of-thread=100
nephren.raven.reactor.helper.configs.THREAD_POOL.type=thread_pool
nephren.raven.reactor.helper.configs.THREAD_POOL.thread-pool.ttl=10s
nephren.raven.reactor.helper.configs.THREAD_POOL.thread-pool.core-pool-size=100
nephren.raven.reactor.helper.configs.THREAD_POOL.thread-pool.maximum-pool-size=1000
nephren.raven.reactor.helper.configs.THREAD_POOL.thread-pool.queue-type=linked
nephren.raven.reactor.helper.configs.THREAD_POOL.thread-pool.queue-size=100
nephren.raven.reactor.helper.configs.THREAD_POOL.thread-pool.allow-core-thread-time-out=true
```