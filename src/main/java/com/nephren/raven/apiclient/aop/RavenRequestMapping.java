package com.nephren.raven.apiclient.aop;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMethod;

@Builder
@RequiredArgsConstructor
public class RavenRequestMapping {

  @Builder.Default
  private final String[] value = new String[0];
  @Builder.Default
  private final String[] path = new String[0];
  @Builder.Default
  private final RequestMethod[] method = new RequestMethod[0];
  @Builder.Default
  private final String[] headers = new String[0];
  @Builder.Default
  private final String[] consumes = new String[0];
  @Builder.Default
  private final String[] produces = new String[0];

  public String[] value() {
    return this.value;
  }

  public String[] path() {
    return this.path;
  }

  public RequestMethod[] method() {
    return this.method;
  }

  public String[] headers() {
    return this.headers;
  }

  public String[] consumes() {
    return this.consumes;
  }

  public String[] produces() {
    return this.produces;
  }

}
