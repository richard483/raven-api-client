package com.nephren.raven.apiclient.serviceExample.client;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;

@RavenApiClient(name = "getExampleClient")
public interface GETExampleClient {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestNoPath();

  @GetMapping(value = "/getRequest",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequest();

  @RequestMapping(value = "/getRequest",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestWithRequestMapping();

  @GetMapping(value = "/getRequest-ISE",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestISE();

  @GetMapping(value = "/getRequest-withHeader",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestWithHeader(@RequestHeader("X-Test-Header") String header);

  @GetMapping(value = "/getRequest-withHeader",
      produces = MediaType.APPLICATION_JSON_VALUE, headers = {"X-Test-Header=Hola!"})
  Mono<ResponseEntity<String>> getRequestWithHeader2();

  @GetMapping(value = "/getRequest-withHeader",
      produces = MediaType.APPLICATION_JSON_VALUE, headers = {"X-Test-Header"})
  Mono<ResponseEntity<String>> getRequestWithHeader3();

  @GetMapping(value = "/getRequest-queryParam",
      produces = MediaType.APPLICATION_JSON_VALUE,
      params = {"name", "age"})
  Mono<ResponseEntity<String>> getRequestWithQueryParam(
      @RequestParam("name") String nameQueryParam, @RequestParam("age") String ageQueryParam);

  @GetMapping(value = "/getRequest-queryParam-collection",
      produces = MediaType.APPLICATION_JSON_VALUE,
      params = {"names"})
  Mono<ResponseEntity<String>> getRequestQueryParamCollection(
      @RequestParam("names") List<String> namesQueryParam);

  @GetMapping(path = "/getRequest-pathVariable/{variable}")
  Mono<ResponseEntity<String>> getRequestPathVariable(@PathVariable("variable") String variable);

  @GetMapping(value = "/getRequest-cookieParam",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestWithCookieParam(@CookieValue("username") String username);

}
