package com.nephren.raven.apiclient.properties;

import java.util.Objects;

public class PropertiesHelper {

  public static void copyConfigProperties(
      RavenApiClientProperties.ApiClientConfigProperties source,
      RavenApiClientProperties.ApiClientConfigProperties target) {
    if (source != null) {

      if (Objects.nonNull(source.getUrl())) {
        target.setUrl(source.getUrl());
      }
      //
      //      if (Objects.nonNull(source.getFallback())) {
      //        target.setFallback(source.getFallback());
      //      }

      if (Objects.nonNull(source.getReadTimeout())) {
        target.setReadTimeout(source.getReadTimeout());
      }

      if (Objects.nonNull(source.getConnectTimeout())) {
        target.setConnectTimeout(source.getConnectTimeout());
      }

      if (Objects.nonNull(source.getWriteTimeout())) {
        target.setWriteTimeout(source.getWriteTimeout());
      }

      source.getHeaders().forEach((key, value) -> target.getHeaders().put(key, value));
    }
  }

}
