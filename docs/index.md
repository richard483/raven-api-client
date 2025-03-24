---
layout: default
title: Home
nav_order: 1
has_children: false
---

# Getting Started
Raven Api Client is a Spring Boot Reactive Web Client library insired
by [Blibli Backend Framework's API Client](https://github.com/bliblidotcom/blibli-backend-framework/tree/master/blibli-backend-framework-api-client).

## Prerequisites

- Java 21+
- Spring 3.3+
- Maven 3.6.3+
## Import to your project
### Add new repository to your pom.xml

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

### Add new dependency to your pom.xml

```xml
<dependency>
    <groupId>com.github.richard483</groupId>
    <artifactId>raven-api-client</artifactId>
    <version>${version-tag}</version>
</dependency>
```

Latest & other versions:

[![](https://jitpack.io/v/richard483/raven-api-client.svg)](https://jitpack.io/#richard483/raven-api-client)