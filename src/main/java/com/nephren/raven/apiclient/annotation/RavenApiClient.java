package com.nephren.raven.apiclient.annotation;

import com.nephren.raven.apiclient.errorresolver.ApiErrorResolver;
import com.nephren.raven.apiclient.errorresolver.DefaultApiErrorResolver;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as a Raven API client.
 *
 * <p>
 *
 * @param name          The name of the API client. This name will be used to find the client in the
 *                      context.
 * @param fallback      The fallback class to use when the client is not available.
 * @param errorResolver The error resolver class to use when an error occurs.
 * @param primary       Whether this client is the primary client. If there are multiple clients
 *                      with the same name, the primary client will be used.
 *                      </p>
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RavenApiClient {

  String name();

  Class<?> fallback() default Void.class;

  Class<? extends ApiErrorResolver> errorResolver() default DefaultApiErrorResolver.class;

  boolean primary() default true;

}