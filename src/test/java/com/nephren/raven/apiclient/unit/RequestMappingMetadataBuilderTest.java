package com.nephren.raven.apiclient.unit;

import com.nephren.raven.apiclient.aop.RequestMappingMetadata;
import com.nephren.raven.apiclient.aop.RequestMappingMetadataBuilder;
import com.nephren.raven.apiclient.properties.RavenApiClientProperties;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

/**
 * Locks in the {@link MergedAnnotations}-based metadata resolution introduced in tier 2 B2:
 * meta-annotated shortcuts ({@code @GetMapping} et al.) must yield the same merged
 * {@code @RequestMapping} attributes as a directly declared {@code @RequestMapping}, and
 * parameter annotations with {@code @AliasFor("name") value} must resolve via either alias.
 */
class RequestMappingMetadataBuilderTest {

  interface SampleClient {

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<ResponseEntity<String>> get(
        @PathVariable("id") String id,
        @RequestHeader("X-Trace-Id") String trace,
        @RequestParam("filter") String filter,
        @CookieValue("session") String session);

    @PostMapping(value = "/post", consumes = MediaType.APPLICATION_JSON_VALUE,
        headers = {"X-Static=value"})
    Mono<ResponseEntity<String>> post();

    @PutMapping("/put")
    Mono<ResponseEntity<String>> put();

    @PatchMapping("/patch")
    Mono<ResponseEntity<String>> patch();

    @DeleteMapping("/delete")
    Mono<ResponseEntity<String>> delete();

    @RequestMapping(value = "/explicit", method = RequestMethod.GET)
    Mono<ResponseEntity<String>> explicit();

    // RequestParam.value is @AliasFor("name") — verify either alias is read.
    @GetMapping("/alias")
    Mono<ResponseEntity<String>> alias(@RequestParam(value = "q") String q);

    // No mapping annotation — must be filtered out.
    Mono<ResponseEntity<String>> noMapping();
  }

  private RequestMappingMetadata metadata;

  @BeforeEach
  void buildMetadata() {
    ApplicationContext ctx = Mockito.mock(ApplicationContext.class);
    RavenApiClientProperties props = new RavenApiClientProperties();
    Map<String, RavenApiClientProperties.ApiClientConfigProperties> configs = new HashMap<>();
    configs.put("sample", new RavenApiClientProperties.ApiClientConfigProperties());
    props.setConfigs(configs);
    Mockito.when(ctx.getBean(RavenApiClientProperties.class)).thenReturn(props);

    metadata = new RequestMappingMetadataBuilder(ctx, SampleClient.class, "sample").build();
  }

  @Test
  void prepareMethods_includesAllAnnotated_andSkipsUnannotated() {
    Assertions.assertThat(metadata.getMethods()).containsKeys(
        "get", "post", "put", "patch", "delete", "explicit", "alias");
    Assertions.assertThat(metadata.getMethods()).doesNotContainKey("noMapping");
  }

  @Test
  void shortcutAnnotations_resolveToCorrectHttpMethod() {
    Assertions.assertThat(metadata.getRequestMethods())
        .containsEntry("get", RequestMethod.GET)
        .containsEntry("post", RequestMethod.POST)
        .containsEntry("put", RequestMethod.PUT)
        .containsEntry("patch", RequestMethod.PATCH)
        .containsEntry("delete", RequestMethod.DELETE)
        .containsEntry("explicit", RequestMethod.GET);
  }

  @Test
  void getMapping_path_isResolved() {
    Assertions.assertThat(metadata.getPaths()).containsEntry("get", "/get/{id}");
    Assertions.assertThat(metadata.getPaths()).containsEntry("explicit", "/explicit");
  }

  @Test
  void produces_isMappedTo_AcceptHeader() {
    Assertions.assertThat(metadata.getHeaders().get("get").getFirst(HttpHeaders.ACCEPT))
        .isEqualTo(MediaType.APPLICATION_JSON_VALUE);
  }

  @Test
  void consumes_isMappedTo_ContentTypeHeader_andContentTypes() {
    Assertions.assertThat(metadata.getHeaders().get("post").getFirst(HttpHeaders.CONTENT_TYPE))
        .isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    Assertions.assertThat(metadata.getContentTypes())
        .containsEntry("post", MediaType.APPLICATION_JSON_VALUE);
  }

  @Test
  void staticHeaderEntries_areInjected() {
    Assertions.assertThat(metadata.getHeaders().get("post").getFirst("X-Static"))
        .isEqualTo("value");
  }

  @Test
  void parameterAnnotations_resolveByName() {
    Assertions.assertThat(metadata.getPathVariablePositions().get("get"))
        .containsEntry("id", 0);
    Assertions.assertThat(metadata.getHeaderParamPositions().get("get"))
        .containsEntry("X-Trace-Id", 1);
    Assertions.assertThat(metadata.getQueryParamPositions().get("get"))
        .containsEntry("filter", 2);
    Assertions.assertThat(metadata.getCookieParamPositions().get("get"))
        .containsEntry("session", 3);
  }

  @Test
  void requestParamValueAlias_isResolvedAsName() {
    Assertions.assertThat(metadata.getQueryParamPositions().get("alias"))
        .containsEntry("q", 0);
  }
}
