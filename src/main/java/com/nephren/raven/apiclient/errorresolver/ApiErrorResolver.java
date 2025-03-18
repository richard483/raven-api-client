package com.nephren.raven.apiclient.errorresolver;

import java.lang.reflect.Method;
import reactor.core.publisher.Mono;

/**
 * ApiErrorResolver
 *
 * <p>
 * Interface for resolving errors that occur during API calls using the RavenApiClient.
 * </p>
 */

public interface ApiErrorResolver {

  Mono<Object> resolve(Throwable throwable, Class<?> type,
      Method method, Object[] arguments);
}
