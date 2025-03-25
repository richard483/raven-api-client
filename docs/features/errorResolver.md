---
layout: default
title: Error Resolver
nav_order: 7
parent: Features
---

# Error Resolver

The Error Resolver is a feature that allows you to set a custom error response when the API returns an error response (4xx or 5xx response).

## Creating Error Resolver

For creating error resolver class, we need to create a Bean class that implementing ApiErrorResolver interface.

```java
@Component
public class GetExampleClientErrorResolver implements ApiErrorResolver {

  @Override
  public Mono<Object> resolve(Throwable throwable, Class<?> type, Method method,
      Object[] arguments) {
    return Mono.error(throwable);
  }
}
```

If you see above example, the resolve method would return the throwable object as the response (not recommended) and having `@Component` annotation that implying the class is a Bean component. You can customize the response as you want.

Then, we just need to register the error resolver class to the api client interface.

```java
@RavenApiClient(name = "getExampleClient", errorResolver = GetExampleClientErrorResolver.class)
public interface GETExampleClient {

  @RequestMapping(value = "/getRequest/{name}",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
  Mono<ResponseEntity<String>> getRequest(@PathVariable("name") String name);
}
```