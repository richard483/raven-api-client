package com.nephren.raven.apiclient.aop;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.web.bind.annotation.RequestMethod;

@Value
@Builder
@Getter
public class RavenRequestMapping {

  @Builder.Default
  String[] value = new String[0];
  @Builder.Default
  String[] path = new String[0];
  @Builder.Default
  RequestMethod[] method = new RequestMethod[0];
  @Builder.Default
  String[] headers = new String[0];
  @Builder.Default
  String[] consumes = new String[0];
  @Builder.Default
  String[] produces = new String[0];

}
