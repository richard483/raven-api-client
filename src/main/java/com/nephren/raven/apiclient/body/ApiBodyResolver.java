package com.nephren.raven.apiclient.body;

import java.lang.reflect.Method;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.reactive.function.BodyInserter;
import reactor.core.publisher.Mono;

public interface
ApiBodyResolver {

  boolean canResolve(String contentType);

  Mono<BodyInserter<?, ? super ClientHttpRequest>> resolve(Method method, Object[] arguments);

}
