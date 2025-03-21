---
layout: default
title: HTTP Methods
nav_order: 1
parent: Features
---

# Http Methods

The client interface can be annotated with the following annotations to specify the HTTP method to
be used for the
request:

- `@GetMapping`
- `@PostMapping`
- `@PutMapping`
- `@DeleteMapping`
- `@PatchMapping`
- `@RequestMapping` (with `method` attribute set to the desired HTTP method)

#### Example

```java

import org.springframework.web.bind.annotation.RequestMapping;

@RavenApiClient(name = "getExampleClient")
public interface GETExampleClient {

  @GetMapping(value = "/getRequest",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequest();

  @RequestMapping(value = "/getRequest",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequestWithRequestMapping();
}
```

In the above example, the `getRequest` method is annotated with `@GetMapping` and the `getRequestWithRequestMapping` method is annotated with `@RequestMapping` with the `method` attribute set to `RequestMethod.GET`. Both methods will make a GET request to the `/getRequest` endpoint.
