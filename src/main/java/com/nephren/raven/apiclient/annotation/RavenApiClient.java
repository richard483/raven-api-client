package com.nephren.raven.apiclient.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RavenApiClient {
  //  String url();

  String name();

  Class<?> fallback() default Void.class;

  boolean primary() default true;

}