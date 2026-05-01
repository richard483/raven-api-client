package com.nephren.raven.apiclient.errorresolver;

import java.lang.reflect.Method;
import reactor.core.publisher.Mono;

/**
 * ApiErrorResolver
 *
 * <p>Interface for resolving errors that occur during API calls using the RavenApiClient.
 *
 * <p>Contract for the returned {@link Mono}:
 * <ul>
 *   <li>{@code Mono.just(value)} — error is considered resolved; {@code value} is returned to
 *       the caller and any configured fallback is skipped.</li>
 *   <li>{@code Mono.empty()} — error is considered unresolved. If a fallback is configured it
 *       will be invoked; otherwise the original {@link Throwable} is propagated to the caller
 *       so it is never silently swallowed.</li>
 *   <li>{@code Mono.error(throwable)} — error is propagated as-is to the caller. Fallback is
 *       not invoked.</li>
 * </ul>
 */

public interface ApiErrorResolver {

  Mono<Object> resolve(Throwable throwable, Class<?> type,
      Method method, Object[] arguments);
}
