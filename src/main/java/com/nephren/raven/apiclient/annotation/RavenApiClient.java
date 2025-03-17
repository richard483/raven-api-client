package com.nephren.raven.apiclient.annotation;

import com.nephren.raven.apiclient.error.ApiErrorResolver;
import com.nephren.raven.apiclient.error.DefaultApiErrorResolver;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RavenApiClient {

  String name();

  Class<?> fallback() default Void.class;

  Class<? extends ApiErrorResolver> errorResolver() default DefaultApiErrorResolver.class;

  boolean primary() default true;

}