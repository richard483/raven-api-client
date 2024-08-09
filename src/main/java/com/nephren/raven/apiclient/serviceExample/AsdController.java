package com.nephren.raven.apiclient.serviceExample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AsdController {
  @Autowired
  private AsdService asdService;

  @GetMapping("/asd")
  public Mono<String> asd() {
    return asdService.asd();
  }

}
