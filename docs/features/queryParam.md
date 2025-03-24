---
layout: default
title: Query Parameter
nav_order: 4
parent: Features
---

# Query Parameter

Like the usual Spring Web annotations, you can use the `@RequestParam` annotation to specify the query parameters to be used in the request.

#### Example

```java
    @GetMapping(value = "/getRequest-withQueryParam",
        produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<String>> getRequestWithQueryParam(@RequestParam("testParam") String param);
```