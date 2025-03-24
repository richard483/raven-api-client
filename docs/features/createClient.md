---
layout: default
title: Creating Client
nav_order: 1
parent: Features
---

# Creating API Client Interface

Like on the Feign library, API client interface is the interface that defines the methods that 
will be 
used to make the requests to the API. The interface can be annotated with the `@RavenApiClient` annotation to specify the name of the client. The client name is used to identify the client when creating the client instance.

## Example

```java
@RavenApiClient(name = "exampleClient")
public interface ExampleClient {

  @GetMapping(value = "/getRequest",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequest();

  @PostMapping(value = "/postRequest",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> postRequest(@RequestBody String requestBody);
}
```

In the above example, the `ExampleClient` interface is annotated with `@RavenApiClient` with the `name` attribute set to `exampleClient`. The interface defines two methods `getRequest` and `postRequest` that will be used to make GET and POST requests to the `/getRequest` and `/postRequest` endpoints respectively.
