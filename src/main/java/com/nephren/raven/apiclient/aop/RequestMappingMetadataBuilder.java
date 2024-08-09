package com.nephren.raven.apiclient.aop;

import com.nephren.raven.apiclient.properties.PropertiesHelper;
import com.nephren.raven.apiclient.properties.RavenApiClientProperties;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
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

public class RequestMappingMetadataBuilder {

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

  private void prepareProperties() {
    RavenApiClientProperties apiClientproperties =
        applicationContext.getBean(RavenApiClientProperties.class);
    properties = mergeApiClientConfigProperties(
        apiClientproperties.getConfigs().get(RavenApiClientProperties.DEFAULT),
        apiClientproperties.getConfigs().get(name)
    );
  }

  private RavenApiClientProperties.ApiClientConfigProperties mergeApiClientConfigProperties(
      RavenApiClientProperties.ApiClientConfigProperties defaultProperties,
      RavenApiClientProperties.ApiClientConfigProperties properties) {
    RavenApiClientProperties.ApiClientConfigProperties configProperties =
        new RavenApiClientProperties.ApiClientConfigProperties();

    PropertiesHelper.copyConfigProperties(defaultProperties, configProperties);
    PropertiesHelper.copyConfigProperties(properties, configProperties);

    return configProperties;
  }

  private void prepareMethods() {
    java.lang.reflect.Method[] declaredMethods = ReflectionUtils.getAllDeclaredMethods(type);
    methods = Arrays.stream(ReflectionUtils.getAllDeclaredMethods(type))
        .filter(method ->
                method.getAnnotation(RequestMapping.class) != null
            //                ||
            //                method.getAnnotation(GetMapping.class) != null ||
            //                method.getAnnotation(PutMapping.class) != null ||
            //                method.getAnnotation(PostMapping.class) != null ||
            //                method.getAnnotation(PatchMapping.class) != null ||
            //                method.getAnnotation(DeleteMapping.class) != null
        )
        .collect(Collectors.toMap(java.lang.reflect.Method::getName, method -> method)); // TODO:
    // check if this is correct, because it different from the reference code
  }

  private void preparePaths() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
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

  private void prepareRequestMethods() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      GetMapping getMapping = method.getAnnotation(GetMapping.class);
      PutMapping putMapping = method.getAnnotation(PutMapping.class);
      PostMapping postMapping = method.getAnnotation(PostMapping.class);
      PatchMapping patchMapping = method.getAnnotation(PatchMapping.class);
      DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);

      if (requestMapping != null && requestMapping.method().length > 0) {
        RequestMethod[] methods = requestMapping.method();
        requestMethods.put(methodName, methods[0]);
      } else if (getMapping != null) {
        requestMethods.put(methodName, RequestMethod.GET);
      } else if (putMapping != null) {
        requestMethods.put(methodName, RequestMethod.PUT);
      } else if (postMapping != null) {
        requestMethods.put(methodName, RequestMethod.POST);
      } else if (patchMapping != null) {
        requestMethods.put(methodName, RequestMethod.PATCH);
      } else if (deleteMapping != null) {
        requestMethods.put(methodName, RequestMethod.DELETE);
      } else {
        requestMethods.put(methodName, RequestMethod.GET);
      }
      // TODO: check if this is correct, because it different from the reference code

      //      if (requestMapping != null) {
      //        RequestMethod[] methods = requestMapping.method();
      //        if (methods.length > 0) {
      //          requestMethods.put(methodName, methods[0]);
      //        } else {
      //          requestMethods.put(methodName, RequestMethod.GET);
      //        }
      //      }
    });
  }

  private void prepareResponseBodyClasses() {
    methods.forEach((methodName, method) -> {
      ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
      if (!parameterizedType.getRawType().getTypeName().equals(Mono.class.getName())) {
        throw new BeanCreationException(
            String.format("ApiClient method must return reactive, %s is not reactive", methodName));
      }

      Type[] typeArguments = parameterizedType.getActualTypeArguments();
      if (typeArguments.length != 1) {
        throw new BeanCreationException(
            String.format("ApiClient method must return 1 generic type, %s generic type is not 1",
                methodName));
      }

      responseBodyClasses.put(methodName, typeArguments[0]);
    });
  }

  private void prepareQueryParams() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> queryParamPosition = new HashMap<>();
        queryParamPositions.put(methodName, queryParamPosition);

        if (parameters.length > 0) {
          for (int i = 0;
               i < parameters.length;
               i++) {
            Parameter parameter = parameters[i];
            RequestParam annotation = parameter.getAnnotation(RequestParam.class);
            if (annotation != null) {
              String name =
                  StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
              if (!StringUtils.isEmpty(name)) {
                queryParamPosition.put(name, i);
              }
            }
          }
        }
      }
    });
  }

  //  private void prepareApiUrl() {
  //    methods.forEach((methodName, method) -> {
  //      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
  //      if (requestMapping != null) {
  //        Parameter[] parameters = method.getParameters();
  //        if (parameters.length > 0) {
  //          for (int i = 0;
  //               i < parameters.length;
  //               i++) {
  //            Parameter parameter = parameters[i];
  //            ApiUrl annotation = parameter.getAnnotation(ApiUrl.class);
  //            if (annotation != null) {
  //              apiUrlPositions.put(methodName, i);
  //            }
  //          }
  //        }
  //      }
  //    });
  //  }

  private void prepareHeaderParams() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> headerParamPosition = new HashMap<>();
        headerParamPositions.put(methodName, headerParamPosition);

        if (parameters.length > 0) {
          for (int i = 0;
               i < parameters.length;
               i++) {
            Parameter parameter = parameters[i];
            RequestHeader annotation = parameter.getAnnotation(RequestHeader.class);
            if (annotation != null) {
              String name =
                  StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
              if (!StringUtils.isEmpty(name)) {
                headerParamPosition.put(name, i);
              }
            }
          }
        }
      }
    });
  }

  private void preparePathVariables() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> pathVariablePosition = new HashMap<>();
        pathVariablePositions.put(methodName, pathVariablePosition);

        if (parameters.length > 0) {
          for (int i = 0;
               i < parameters.length;
               i++) {
            Parameter parameter = parameters[i];
            PathVariable annotation = parameter.getAnnotation(PathVariable.class);
            if (annotation != null) {
              String name =
                  StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
              if (!StringUtils.isEmpty(name)) {
                pathVariablePosition.put(name, i);
              }
            }
          }
        }
      }
    });
  }

  private void prepareHeaders() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
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
        for (String header : requestHeaders) {
          String[] split = header.split("=");
          if (split.length > 1) {
            httpHeaders.add(split[0], split[1]);
          } else {
            httpHeaders.add(split[0], "");
          }
        }
        headers.put(methodName, httpHeaders);
      }
    });
  }

  private void prepareCookieParams() {
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
      if (requestMapping != null) {
        Parameter[] parameters = method.getParameters();
        Map<String, Integer> cookieParamPosition = new HashMap<>();
        cookieParamPositions.put(methodName, cookieParamPosition);

        if (parameters.length > 0) {
          for (int i = 0;
               i < parameters.length;
               i++) {
            Parameter parameter = parameters[i];
            CookieValue annotation = parameter.getAnnotation(CookieValue.class);
            if (annotation != null) {
              String name =
                  StringUtils.isEmpty(annotation.name()) ? annotation.value() : annotation.name();
              if (!StringUtils.isEmpty(name)) {
                cookieParamPosition.put(name, i);
              }
            }
          }
        }
      }
    });
  }

  private void prepareContentTypes() {
    String defaultContentType = getDefaultContentType();
    methods.forEach((methodName, method) -> {
      RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
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

  private String getDefaultContentType() {
    String defaultContentType = null;
    for (Map.Entry<String, String> entry : properties.getHeaders().entrySet()) {
      if (HttpHeaders.CONTENT_TYPE.equals(entry.getKey())) {
        defaultContentType = entry.getValue();
      }
    }
    return defaultContentType;
  }

  public RequestMappingMetadata build() {
    prepareProperties();
    prepareMethods();
    prepareHeaders();
    prepareQueryParams();
    prepareHeaderParams();
    preparePathVariables();
    prepareResponseBodyClasses();
    prepareRequestMethods();
    preparePaths();
    prepareCookieParams();
    prepareContentTypes();
    //    prepareApiUrl();

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

}
