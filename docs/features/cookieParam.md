---
layout: default
title: Cookie Param
nav_order: 6
parent: Features
---

# Cookie Param

For defining a cookie parameter in the request, you can use the `@CookieValue` annotation.

#### Example

```java
    @GetMapping(value = "/getRequest-withCookieParam",
        produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<String>> getRequestWithCookieParam(@CookieValue("testCookie") String cookie);
```