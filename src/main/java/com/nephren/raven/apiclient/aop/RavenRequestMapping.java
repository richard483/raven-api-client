package com.nephren.raven.apiclient.aop;

import java.lang.annotation.Annotation;
import lombok.Builder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Builder
public class RavenRequestMapping implements RequestMapping {

  @Builder.Default
  private String name = "";
  @Builder.Default
  private String[] value = new String[0];
  @Builder.Default
  private String[] path = new String[0];
  @Builder.Default
  private RequestMethod[] method = new RequestMethod[0];
  @Builder.Default
  private String[] params = new String[0];
  @Builder.Default
  private String[] headers = new String[0];
  @Builder.Default
  private String[] consumes = new String[0];
  @Builder.Default
  private String[] produces = new String[0];
  @Builder.Default
  private Class<? extends Annotation> annotationType = null;

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
