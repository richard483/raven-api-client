package com.nephren.raven.apiclient.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nephren.raven.apiclient.annotation.RavenApiClient;
import com.nephren.raven.apiclient.aop.RequestMappingMetadata;
import com.nephren.raven.apiclient.aop.RequestMappingMetadataBuilder;
import com.nephren.raven.apiclient.body.ApiBodyResolver;
import com.nephren.raven.apiclient.reactor.helper.SchedulerHelper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;

public class RavenApiClientMethodInterceptor implements InitializingBean, MethodInterceptor,
    ApplicationContextAware {

  private RequestMappingMetadata metadata;
  private List<ApiBodyResolver> bodyResolvers;
  private WebClient webClient;
  @Setter
  private AnnotationMetadata annotationMetadata;

  @Setter
  private Class<?> type;

  @Setter
  private String name;

  @Setter
  private ApplicationContext applicationContext;

  private Scheduler scheduler;

  @Override
  public void afterPropertiesSet() throws Exception {
    prepareAttribute();
    prepareBodyResolvers();
    prepareWebClient();
    prepareScheduler();

  }
  private void prepareScheduler() {
    SchedulerHelper schedulerHelper = applicationContext.getBean(SchedulerHelper.class);
    RavenApiClient ravenApiClient = type.getAnnotation(RavenApiClient.class);
    if (schedulerHelper.of(ravenApiClient.name()) != Schedulers.immediate()) {
      scheduler = schedulerHelper.of(ravenApiClient.name());
    }
  }

  private void prepareAttribute() {
    metadata = new RequestMappingMetadataBuilder(applicationContext, type, name).build();
  }

  private void prepareWebClient() {
    WebClient.Builder builder = applicationContext.getBean(WebClient.Builder.class)
        .exchangeStrategies(getExchangeStrategies()).baseUrl(metadata.getProperties().getUrl())
        .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
        .defaultHeaders(
            httpHeaders -> metadata.getProperties().getHeaders().forEach(httpHeaders::add));
    webClient = builder.build();

  }

  private void prepareBodyResolvers() {
    bodyResolvers =
        new ArrayList<>(applicationContext.getBeansOfType(ApiBodyResolver.class).values());
  }

  private ExchangeStrategies getExchangeStrategies() {
    ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
    return ExchangeStrategies.builder().codecs(clientCodecConfigurer -> {
      clientCodecConfigurer.defaultCodecs()
          .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
      clientCodecConfigurer.defaultCodecs()
          .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));

    }).build();
  }

  private HttpClient getHttpClient() {
    // TODO: could add more api client customizer
    return HttpClient.create().option(
            ChannelOption.CONNECT_TIMEOUT_MILLIS,
            (int) metadata.getProperties().getConnectTimeout().toMillis())
        .doOnConnected(connection -> connection
            .addHandlerLast(
                new ReadTimeoutHandler(metadata.getProperties().getReadTimeout().toMillis(),
                    TimeUnit.MILLISECONDS))
            .addHandlerLast(
                new WriteTimeoutHandler(metadata.getProperties().getWriteTimeout().toMillis(),
                    TimeUnit.MILLISECONDS))
        );
  }

  @Override
  public Object invoke(MethodInvocation invocation) {
    Method method = invocation.getMethod();
    String methodName = method.getName();
    Object[] args = invocation.getArguments();
    Mono mono = Mono.fromCallable(() -> webClient)
        .map(client -> doMethod(methodName))
        .map(client -> getUriBuilder(methodName, args, client))
        .map(client -> doHeader(client, methodName, args))
        .map(client -> doBody(client, method, methodName, args))
        .flatMap(client -> doResponse(client, methodName))
        .onErrorResume(throwable -> doFallback((Throwable) throwable, method, args));
    if (scheduler != null) {
      return mono.subscribeOn(scheduler);
    }
    return mono;
  }

  private WebClient.RequestHeadersUriSpec<?> doMethod(String methodName) {
    RequestMethod requestMethod = metadata.getRequestMethods().get(methodName);
    //    requestMethod = RequestMethod.GET; // TODO: need to remove later
    return switch (requestMethod) {
      case RequestMethod.GET -> webClient.get();
      case RequestMethod.POST -> webClient.post();
      case RequestMethod.PUT -> webClient.put();
      case RequestMethod.DELETE -> webClient.delete();
      case RequestMethod.HEAD -> webClient.head();
      case RequestMethod.OPTIONS -> webClient.options();
      case RequestMethod.PATCH -> webClient.patch();
      default -> throw new UnsupportedOperationException(
          "#RavenAPIClientMethodInterceptor Unsupported method: " + methodName);
    };
  }

  private WebClient.RequestHeadersSpec<?> getUriBuilder(
      String methodName, Object[] arguments, WebClient.RequestHeadersUriSpec<?> client) {
    if (metadata.getApiUrlPositions().containsKey(methodName)) {
      String baseUrl = (String) arguments[metadata.getApiUrlPositions().get(methodName)];
      return client.uri(baseUrl, uriBuilder -> getUri(uriBuilder, methodName, arguments));
    } else {
      return client.uri(uriBuilder -> getUri(uriBuilder, methodName, arguments));
    }
  }

  private URI getUri(UriBuilder builder, String methodName, Object[] arguments) {
    builder.path(metadata.getPaths().get(methodName));

    metadata.getQueryParamPositions().get(methodName).forEach((paramName, position) -> {
      if (arguments[position] instanceof Collection collection) {
        builder.queryParam(paramName, collection);
      } else {
        builder.queryParam(paramName, arguments[position]);
      }
    });

    Map<String, Object> uriVariables = new HashMap<>();
    metadata.getPathVariablePositions().get(methodName).forEach((paramName, position) -> {
      uriVariables.put(paramName, arguments[position]);
    });

    return builder.build(uriVariables);
  }

  private WebClient.RequestHeadersSpec<?> doHeader(
      WebClient.RequestHeadersSpec<?> spec, String methodName, Object[] arguments) {
    metadata.getHeaders().get(methodName).forEach((key, values) -> {
      spec.headers(httpHeaders -> httpHeaders.addAll(key, values));
    });

    metadata.getHeaderParamPositions().get(methodName).forEach((key, position) -> {
      spec.headers(httpHeaders -> httpHeaders.add(key, String.valueOf(arguments[position])));
    });

    metadata.getCookieParamPositions().get(methodName).forEach((key, position) -> {
      spec.cookies(cookies -> cookies.add(key, String.valueOf(arguments[position])));
    });

    return spec;
  }

  private WebClient.RequestHeadersSpec<?> doBody(
      WebClient.RequestHeadersSpec<?> client, Method method, String methodName,
      Object[] arguments) {
    if (client instanceof WebClient.RequestBodySpec bodySpec) {

      String contentType = metadata.getContentTypes().get(methodName);
      BodyInserter<?, ? super ClientHttpRequest> body = null;

      for (ApiBodyResolver bodyResolver : bodyResolvers) {
        if (bodyResolver.canResolve(contentType)) {
          body = bodyResolver.resolve(method, arguments);
        }
      }

      if (body != null) {
        return bodySpec.body(body);
      }
    }
    return client;
  }

  private Mono doResponse(WebClient.RequestHeadersSpec<?> client, String methodName) {
    Type type = metadata.getResponseBodyClasses().get(methodName);
    if (type instanceof ParameterizedType parameterizedType) {
      if (ResponseEntity.class.equals(parameterizedType.getRawType())) {
        WebClient.ResponseSpec responseEntitySpec =
            client.retrieve().onStatus(HttpStatusCode::isError,
                clientResponse -> Mono.empty());

        if (parameterizedType.getActualTypeArguments()[0] instanceof ParameterizedType actualTypeArgument) {
          if (List.class.equals(actualTypeArgument.getRawType())) {
            return responseEntitySpec.toEntityList(
                ParameterizedTypeReference.forType(actualTypeArgument.getActualTypeArguments()[0]));
          }
        }

        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
        if (Void.class.equals(actualTypeArgument)) {
          return responseEntitySpec.toBodilessEntity();
        } else {
          return responseEntitySpec.toEntity(
              ParameterizedTypeReference.forType(actualTypeArgument));
        }
      } else {
        return client.retrieve().bodyToMono(ParameterizedTypeReference.forType(parameterizedType));
      }
    } else {
      return client.retrieve().bodyToMono((Class) type);
    }
  }

  private Mono doFallback(Throwable throwable, Method method, Object[] arguments) {
    return Mono.error(throwable);
  }

}
