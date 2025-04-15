# Raven API Client

For more detailed documentation and examples, please visit the [github.io page](https://richard483.github.io/raven-api-client/)

This is a Spring Boot Reactive Web Client library inspired
by [Blibli Backend Framework's API Client](https://github.com/bliblidotcom/blibli-backend-framework/tree/master/blibli-backend-framework-api-client).

## How to implement Raven API Client in your Spring Boot project

Java : 21+

Spring : 3.3+

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

*version with "-s2" suffix is tend to use with Spring version 2.7, while the version without that suffix should used with Spring version 3.3
