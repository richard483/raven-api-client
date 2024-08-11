package com.nephren.raven.apiclient.aop;

import java.lang.annotation.Annotation;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@NoArgsConstructor
public class RavenRequestMapping implements RequestMapping {

  private final String name = "";
  private final String[] value = new String[0];
  private final String[] path = new String[0];
  private final RequestMethod[] method = new RequestMethod[0];
  private final String[] params = new String[0];
  private final String[] headers = new String[0];
  private final String[] consumes = new String[0];
  private final String[] produces = new String[0];
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
