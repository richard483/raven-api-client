package com.nephren.raven.apiclient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RavenApiclientApplicationTests {

  @Autowired
  GolangClient golangClient;
  @Test
  void contextLoads() {
    golangClient.hello();
  }

}