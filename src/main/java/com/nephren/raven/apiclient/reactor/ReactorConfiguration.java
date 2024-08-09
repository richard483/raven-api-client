package com.nephren.raven.apiclient.reactor;

import com.nephren.raven.apiclient.reactor.factory.SchedulerHelperFactoryBean;
import com.nephren.raven.apiclient.reactor.properties.SchedulerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    SchedulerProperties.class
})
public class ReactorConfiguration {

  @Bean
  public SchedulerHelperFactoryBean schedulerHelper(SchedulerProperties schedulerProperties) {
    SchedulerHelperFactoryBean factoryBean = new SchedulerHelperFactoryBean();
    factoryBean.setSchedulerProperties(schedulerProperties);
    return factoryBean;
  }

}
