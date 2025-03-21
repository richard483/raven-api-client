---
layout: default
title: Basics API Call
nav_order: 1
parent: Features
---

# Basics API Call

Below is an example of how to implement a simple GET request using Raven API Client.

## Client Interface

```java
@RavenApiClient(name = "getExampleClient")
public interface GETExampleClient {
  
  @GetMapping(value = "/getRequest",
      produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<ResponseEntity<String>> getRequest();

}
```

## Service call

```java
@Service
public class GETClientService {

  private final GETExampleClient getExampleClient;

  private final ExampleClientWithFallback exampleClientWithFallback;

  private final ExampleClientWithOtherFallback exampleClientWithOtherFallback;

  private final EmptyExampleClient emptyExampleClient;

  @Autowired
  public GETClientService(GETExampleClient getExampleClient) {
    this.getExampleClient = getExampleClient;
  }

  public Mono<ResponseEntity<String>> getRequest() {
    return getExampleClient.getRequest();
  }
}
```

## Controller Implementation

```java
@RestController
@RequestMapping("/api")
public class ExampleController {

  private final GETClientService getClientService;

  @Autowired
  public ExampleController(GETClientService getClientService) {
    this.getClientService = getClientService;
  }

  @GetMapping("/getRequest")
  public Mono<ResponseEntity<String>> getRequest() {
    return getClientService.getRequest();
  }
}
```
