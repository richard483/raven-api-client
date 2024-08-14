package com.nephren.raven.apiclient.aop;

import java.lang.annotation.Annotation;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// TODO: we should not implements the request mapping, recheck the unused props later & remove
//  them (check the used props in the RequestMappingMetadataBuilder)
@Builder
@RequiredArgsConstructor
public class RavenRequestMapping implements RequestMapping {

  @Builder.Default
  private final String name = "";
  @Builder.Default
  private final String[] value = new String[0];
  @Builder.Default
  private final String[] path = new String[0];
  @Builder.Default
  private final RequestMethod[] method = new RequestMethod[0];
  @Builder.Default
  private final String[] params = new String[0];
  @Builder.Default
  private final String[] headers = new String[0];
  @Builder.Default
  private final String[] consumes = new String[0];
  @Builder.Default
  private final String[] produces = new String[0];
  @Builder.Default
  private final Class<? extends Annotation> annotationType = RequestMapping.class;

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String[] value() {
    return this.value;
  }

  @Override
  public String[] path() {
    return this.path;
  }

  @Override
  public RequestMethod[] method() {
    return this.method;
  }

  @Override
  public String[] params() {
    return this.params;
  }

  @Override
  public String[] headers() {
    return this.headers;
  }

  @Override
  public String[] consumes() {
    return this.consumes;
  }

  @Override
  public String[] produces() {
    return this.produces;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return this.annotationType;
  }

}
