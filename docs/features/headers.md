---
layout: default
title: HTTP headers
nav_order: 2
parent: Features
---

# HTTP Headers

There are several way to specify the headers to be used in the request.

## Using `@RequestHeader` annotation

The `@RequestHeader` annotation from `org.springframework.web.bind.annotation` can be used to specify the headers to be used in the request.

#### Example

```java
  @GetMapping(value = "/getRequest-withHeader",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestWithHeader(@RequestHeader("X-Test-Header") String header);
```

## Using `headers` attribute of `@RequestMapping` (or equivalent) annotation

The `headers` attribute of the `@RequestMapping` (or equivalent) annotation can be used to specify the headers to be used in the request. You need to specify the header name and value as a string in the format `HeaderName=HeaderValue`.

#### Example

```java
  @RequestMapping(value = "/getRequest-withHeader",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      headers = "X-Test-Header=Test-Value")
  Mono<ResponseEntity<String>> getRequestWithHeaderUsingRequestMapping();
```

## Using configuration file

You can specify the headers in the configuration (`application.properties` or `application.yml`) file. You need to specify the header name and value as a string in the format `nephren.raven.apiclient.configs.<apiClient-name>.headers.HeaderName=HeaderValue`.

#### Example

```
nephren.raven.apiclient.configs.<apiClient-name>.headers.Content-Type=application/json
``` 