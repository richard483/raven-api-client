package com.nephren.raven.apiclient.aop;

import com.nephren.raven.apiclient.properties.PropertiesHelper;
import com.nephren.raven.apiclient.properties.RavenApiClientProperties;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
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

@Slf4j
public class RequestMappingMetadataBuilder {

  private static final Class[] mappingAnnotation = {
      GetMapping.class,
      PutMapping.class,
      PostMapping.class,
      PatchMapping.class,
      DeleteMapping.class,
      RequestMapping.class
  };

  private final Map<String, MultiValueMap<String, String>> headers = new HashMap<>();
  private final Map<String, Integer> apiUrlPositions = new HashMap<>();
  private final Map<String, Map<String, Integer>> queryParamPositions = new HashMap<>();
  private final Map<String, Map<String, Integer>> headerParamPositions = new HashMap<>();
  private final Map<String, Map<String, Integer>> cookieParamPositions = new HashMap<>();
  private final Map<String, Map<String, Integer>> pathVariablePositions = new HashMap<>();
  private final Map<String, RequestMethod> requestMethods = new HashMap<>();
  private final Map<String, String> paths = new HashMap<>();
  private final Map<String, Type> responseBodyClasses = new HashMap<>();
  private final Map<String, String> contentTypes = new HashMap<>();
  private final ApplicationContext applicationContext;
  private final Class<?> type;
  private final String name;
  private Map<String, Method> methods;
  private RavenApiClientProperties.ApiClientConfigProperties properties;

  public RequestMappingMetadataBuilder(
      ApplicationContext applicationContext,
      Class<?> type,
      String name) {
    this.applicationContext = applicationContext;
    this.type = type;
    this.name = name;
  }

  public RequestMappingMetadata build() {
    prepareProperties();
    prepareMethods();
    prepareHeaders();
    prepareHeaderParams();
    prepareQueryParams();
    preparePathVariables();
    prepareCookieParams();
    prepareResponseBodyClasses();
    prepareRequestMethods();
    preparePaths();

    prepareContentTypes();
    return RequestMappingMetadata.builder()
        .properties(properties)
        .methods(methods)
        .headerParamPositions(headerParamPositions)
        .headers(headers)
        .queryParamPositions(queryParamPositions)
        .pathVariablePositions(pathVariablePositions)
        .responseBodyClasses(responseBodyClasses)
        .requestMethods(requestMethods)
        .paths(paths)
        .cookieParamPositions(cookieParamPositions)
        .contentTypes(contentTypes)
        .apiUrlPositions(apiUrlPositions)
        .build();
  }

  private void prepareProperties() {
    RavenApiClientProperties apiClientProperties =
        applicationContext.getBean(RavenApiClientProperties.class);
    properties = mergeApiClientConfigProperties(
        apiClientProperties.getConfigs().get(RavenApiClientProperties.DEFAULT),
        apiClientProperties.getConfigs().get(name)
    );
  }

  private RavenApiClientProperties.ApiClientConfigProperties mergeApiClientConfigProperties(
      RavenApiClientProperties.ApiClientConfigProperties defaultProperties,
      RavenApiClientProperties.ApiClientConfigProperties properties) {
    RavenApiClientProperties.ApiClientConfigProperties configProperties =
        new RavenApiClientProperties.ApiClientConfigProperties();

    PropertiesHelper.copyConfigPropertiesFromSourceToTarget(defaultProperties, configProperties);
    PropertiesHelper.copyConfigPropertiesFromSourceToTarget(properties, configProperties);

    return configProperties;
  }

  private void prepareMethods() {
    methods = Arrays.stream(ReflectionUtils.getAllDeclaredMethods(type))
        .filter(method -> getRequestMappingAnnotation(method) != null)
        .collect(Collectors.toMap(java.lang.reflect.Method::getName, method -> method));
    // TODO:
    // check if this is correct, because it different from the reference code
  }

  private void prepareHeaders() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = getRequestMappingAnnotation(method);
      if (requestMapping != null) {
        HttpHeaders httpHeaders = new HttpHeaders();

        String[] consumes = requestMapping.consumes();
        if (consumes.length > 0) {
          httpHeaders.addAll(HttpHeaders.CONTENT_TYPE, Arrays.asList(consumes));
        }

        String[] produces = requestMapping.produces();
        if (produces.length > 0) {
          httpHeaders.addAll(HttpHeaders.ACCEPT, Arrays.asList(produces));
        }

        String[] requestHeaders = requestMapping.headers();
        injectRequestHeaders(requestHeaders, httpHeaders);

        headers.put(methodName, httpHeaders);
      }
    });
  }

  private void injectRequestHeaders(String[] requestHeaders, HttpHeaders httpHeaders) {
    for (String header : requestHeaders) {
      String[] split = header.split("=");
      if (split.length > 1) {
        httpHeaders.add(split[0], split[1]);
      } else {
        httpHeaders.add(split[0], "");
      }
    }
  }

  private void prepareHeaderParams() {
    methods.forEach((methodName, method) -> {
      if (getRequestMappingAnnotation(method) != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> headerParamPosition = parametersToMap(parameters, RequestHeader.class);
        headerParamPositions.put(methodName, headerParamPosition);
      }
    });
  }

  private void prepareQueryParams() {
    methods.forEach((methodName, method) -> {
      if (getRequestMappingAnnotation(method) != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> queryParamPosition = parametersToMap(parameters, RequestParam.class);
        queryParamPositions.put(methodName, queryParamPosition);
      }
    });
  }

  private void preparePathVariables() {
    methods.forEach((methodName, method) -> {
      if (getRequestMappingAnnotation(method) != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> pathVariablePosition = parametersToMap(parameters, PathVariable.class);
        pathVariablePositions.put(methodName, pathVariablePosition);
      }
    });
  }

  private void prepareCookieParams() {
    methods.forEach((methodName, method) -> {
      if (getRequestMappingAnnotation(method) != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> cookieParamPosition = parametersToMap(parameters, CookieValue.class);
        cookieParamPositions.put(methodName, cookieParamPosition);
      }
    });
  }

  private <T extends Annotation> Map<String, Integer> parametersToMap(
      Parameter[] parameters,
      Class<T> parameterAnnotationClass) {
    Map<String, Integer> parameterPosition = new HashMap<>();
    for (int i = 0;
         i < parameters.length;
         i++) {
      Parameter parameter = parameters[i];
      T annotation = parameter.getAnnotation(parameterAnnotationClass);
      if (annotation != null) {
        String paramName = getParamName(annotation);
        if (!paramName.isEmpty()) {
          parameterPosition.put(paramName, i);
        }
      }
    }
    return parameterPosition;
  }

  private <T extends Annotation> String getParamName(T annotation) {
    String paramName = "", paramValue = "";
    try {
      paramName = annotation.getClass().getMethod("name").invoke(annotation).toString();
      paramValue =
          annotation.getClass().getMethod("value").invoke(annotation).toString();
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      log.warn("#RavenApiClient RequestMappingMetadataBuilder mapping parameter to map got "
          + "error trace: ");
      e.printStackTrace();
    }
    return paramName.isEmpty() ? paramValue : paramName;
  }

  private void prepareResponseBodyClasses() {
    methods.forEach((methodName, method) -> {
      ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
      if (!parameterizedType.getRawType().getTypeName().equals(Mono.class.getName())) {
        throw new BeanCreationException(
            String.format("#RavenApiClient method must return reactive, %s is not reactive",
                methodName));
      }

      Type[] typeArguments = parameterizedType.getActualTypeArguments();
      if (typeArguments.length != 1) {
        throw new BeanCreationException(
            String.format("#RavenApiClient method must return 1 generic type, %s generic type is "
                    + "not 1",
                methodName));
      }

      responseBodyClasses.put(methodName, typeArguments[0]);
    });
  }

  private void prepareRequestMethods() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = getRequestMappingAnnotation(method);

      if (requestMapping != null && requestMapping.method().length > 0) {
        RequestMethod[] methods = requestMapping.method();
        requestMethods.put(methodName, methods[0]);
      } else {
        requestMethods.put(methodName, RequestMethod.GET);
      }
    });
  }

  private void preparePaths() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = getRequestMappingAnnotation(method);
      if (requestMapping != null) {
        String[] pathValues =
            requestMapping.path().length > 0 ? requestMapping.path() : requestMapping.value();
        if (pathValues.length > 0) {
          paths.put(methodName, pathValues[0]);
        } else {
          paths.put(methodName, "");
        }
      }
    });
  }

  private void prepareContentTypes() {
    String defaultContentType = getDefaultContentType();
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = getRequestMappingAnnotation(method);
      if (requestMapping != null) {
        String[] consumes = requestMapping.consumes();
        if (consumes.length > 0) {
          contentTypes.put(methodName, consumes[0]);
        } else {
          contentTypes.put(methodName, defaultContentType);
        }
      }
    });
  }

  private RavenRequestMapping getRequestMappingAnnotation(Method method) {
    for (Class annotation : mappingAnnotation) {
      if (method.getAnnotation(annotation) != null) {
        return getAnnotation(method, annotation);
      }
    }
    return null;
  }

  private <T extends java.lang.annotation.Annotation> RavenRequestMapping getAnnotation(
      Method method, Class<T> annotationType) {
    try {
      T annotation = method.getAnnotation(annotationType);
      String[] consumes =
          (String[]) annotation.getClass().getMethod("consumes").invoke(annotation);
      String[] produces =
          (String[]) annotation.getClass().getMethod("produces").invoke(annotation);
      String[] headers = (String[]) annotation.getClass().getMethod("headers").invoke(annotation);
      String[] path = (String[]) annotation.getClass().getMethod("path").invoke(annotation);
      String[] value = (String[]) annotation.getClass().getMethod("value").invoke(annotation);
      RequestMethod[] methodValue = new RequestMethod[] {getRequestMethod(annotationType)};
      return RavenRequestMapping.builder()
          .consumes(consumes)
          .produces(produces)
          .headers(headers)
          .path(path)
          .value(value)
          .method(methodValue)
          .build();
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      log.error("#RavenApiClient getAnnotation got error trace: ");
      e.printStackTrace();
    }
    return null;

  }

  private RequestMethod getRequestMethod(Class clazz) {
    return switch (clazz.getSimpleName()) {
      case "PutMapping" -> RequestMethod.PUT;
      case "PostMapping" -> RequestMethod.POST;
      case "PatchMapping" -> RequestMethod.PATCH;
      case "DeleteMapping" -> RequestMethod.DELETE;
      default -> RequestMethod.GET;
    };
  }

  private String getDefaultContentType() {
    String defaultContentType = null;
    for (Map.Entry<String, String> entry : properties.getHeaders().entrySet()) {
      if (HttpHeaders.CONTENT_TYPE.equals(entry.getKey())) {
        defaultContentType = entry.getValue();
      }
    }
    return defaultContentType;
  }

}
