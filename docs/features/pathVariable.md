---
layout: default
title: Path Variable
nav_order: 5
parent: Features
---

# Path Variable

Like the usual Spring Web annotations, you can use the `@PathVariable` annotation to specify the path variables to be used in the request.

#### Example

```java
    @GetMapping(value = "/getRequest-withPathVariable/{testPathVariable}",
        produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<String>> getRequestWithPathVariable(@PathVariable("testPathVariable") String pathVariable);
```

Note that the path variable name in the annotation should match the path variable name in the path. In the above example, the path variable name in the annotation is `testPathVariable` and the path variable in the path is `{testPathVariable}`. The path variable name in the annotation is used to bind the path variable value to the method parameter.