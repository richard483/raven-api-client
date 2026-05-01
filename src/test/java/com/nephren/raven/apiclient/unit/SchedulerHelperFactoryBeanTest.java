package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.properties.SchedulerProperties;
import com.nephren.raven.apiclient.reactor.factory.SchedulerHelperFactoryBean;
import com.nephren.raven.apiclient.reactor.helper.SchedulerHelper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class SchedulerHelperFactoryBeanTest {

  @Test
  void getObjectType_returnsSchedulerHelperClass_satisfyingFactoryBeanContract() {
    SchedulerHelperFactoryBean factoryBean = new SchedulerHelperFactoryBean();

    Assertions.assertThat(factoryBean.getObjectType()).isEqualTo(SchedulerHelper.class);
  }

  @Test
  void getObject_buildsHelperFromConfiguredSchedulers() {
    SchedulerHelperFactoryBean factoryBean = new SchedulerHelperFactoryBean();
    SchedulerProperties properties = new SchedulerProperties();
    SchedulerProperties.SchedulerItemProperties item =
        new SchedulerProperties.SchedulerItemProperties();
    item.setType(SchedulerProperties.SchedulerType.IMMEDIATE);
    properties.getConfigs().put("ANY", item);
    factoryBean.setSchedulerProperties(properties);

    SchedulerHelper helper = factoryBean.getObject();

    Assertions.assertThat(helper).isNotNull();
    Assertions.assertThat(helper.of("ANY")).isNotNull();
    Assertions.assertThat(helper.of("UNKNOWN")).isNotNull();
  }
}
