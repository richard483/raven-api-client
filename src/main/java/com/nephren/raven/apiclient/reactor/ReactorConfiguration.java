package com.nephren.raven.apiclient.reactor;

import com.nephren.raven.apiclient.properties.SchedulerProperties;
import com.nephren.raven.apiclient.reactor.factory.SchedulerHelperFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    SchedulerProperties.class
})
@ComponentScan(basePackages = {"com.nephren.raven.apiclient"})
public class ReactorConfiguration {

  @Bean
  public SchedulerHelperFactoryBean schedulerHelper(SchedulerProperties schedulerProperties) {
    SchedulerHelperFactoryBean factoryBean = new SchedulerHelperFactoryBean();
    factoryBean.setSchedulerProperties(schedulerProperties);
    return factoryBean;
  }

}
