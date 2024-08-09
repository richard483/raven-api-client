package com.nephren.raven.apiclient.serviceExample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AsdService {
  @Autowired
  private Asd asd;

  public Mono<String> asd() {
    return asd.asd();
  }

}
