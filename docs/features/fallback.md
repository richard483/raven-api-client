---
layout: default
title: Fallback
nav_order: 8
parent: Features
---

# Fallback

The Fallback would prevent the unexpected response caused by the API when the API is down or the response is not as expected. The Fallback is a feature that allows you to set a default response when the server we tried to connect is offline or out of network.

## Implement Fallback

First, we need to create a class that implement the api client interface that we already made before.

For example, below is the api client interface we would use.

```java
@RavenApiClient(name = "getExampleClient")
public interface GETExampleClient {

  @RequestMapping(value = "/getRequest/{name}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
  Mono<ResponseEntity<String>> getRequest(@PathVariable("name") String name);
}
```

Then, we need to create a class that implements the api client interface.

```java
public class GETExampleClientFallback implements GETExampleClient {

  @Override
  public Mono<ResponseEntity<String>> getRequest(String name) {
    return Mono.just(ResponseEntity.ok("Oops! it seems the API is down or unreachable"));
  }
}
```

Don't forget to register the Fallback implementation to the api client interface.

```java
@RavenApiClient(name = "getExampleClient", fallback = GETExampleClientFallback.class)
public interface GETExampleClient {

  @RequestMapping(value = "/getRequest/{name}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
  Mono<ResponseEntity<String>> getRequest(@PathVariable("name") String name);
}
```

The fallback would be called when the API is down or unreachable, and the response would be the fallback response that we set before.