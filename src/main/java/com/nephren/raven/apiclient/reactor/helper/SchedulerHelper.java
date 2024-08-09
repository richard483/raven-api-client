package com.nephren.raven.apiclient.reactor.helper;

import reactor.core.scheduler.Scheduler;

public interface SchedulerHelper {
  Scheduler of(String name);

}
