package com.nephren.raven.apiclient.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nephren.raven.apiclient.body.FormBodyResolver;
import com.nephren.raven.apiclient.body.JsonBodyResolver;
import com.nephren.raven.apiclient.body.MultipartBodyResolver;
import com.nephren.raven.apiclient.properties.RavenApiClientProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RavenApiClientRegistrar.class)
@EnableConfigurationProperties({
    RavenApiClientProperties.class
})
public class RavenApiConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public FormBodyResolver formBodyResolver() {
    return new FormBodyResolver();
  }

  @Bean
  @ConditionalOnMissingBean
  public MultipartBodyResolver multipartBodyResolver() {
    return new MultipartBodyResolver();
  }

  @Bean
  @ConditionalOnMissingBean
  public JsonBodyResolver jsonBodyResolver(ObjectMapper objectMapper) {
    return new JsonBodyResolver(objectMapper);
  }

}
