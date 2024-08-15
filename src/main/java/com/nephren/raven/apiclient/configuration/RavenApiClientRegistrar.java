package com.nephren.raven.apiclient.configuration;

import com.nephren.raven.apiclient.annotation.RavenApiClient;
import com.nephren.raven.apiclient.exception.RavenApiException;
import com.nephren.raven.apiclient.interceptor.RavenApiClientMethodInterceptor;
import com.nephren.raven.apiclient.reactor.ReactorConfiguration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Setter
@Configuration
@AutoConfigureAfter({ReactorConfiguration.class})
public class RavenApiClientRegistrar
    implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

  private static final String METHOD_INTERCEPTOR = "MethodInterceptor";

  private ResourceLoader resourceLoader;
  private Environment environment;

  @Override
  public void registerBeanDefinitions(
      AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
    ClassPathScanningCandidateComponentProvider scanner = getScanner();
    scanner.setResourceLoader(resourceLoader);

    AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RavenApiClient.class);
    scanner.addIncludeFilter(annotationTypeFilter);
    Set<String> basePackages = getBasePackages(metadata);
    for (String basePackage : basePackages) {
      Set<BeanDefinition> candidateComponentsComponents =
          scanner.findCandidateComponents(basePackage);
      for (BeanDefinition candidateComponent : candidateComponentsComponents) {
        if (candidateComponent instanceof AnnotatedBeanDefinition beanDefinition) {
          registerBean(beanDefinition, registry);
        }
      }
    }

  }

  private ClassPathScanningCandidateComponentProvider getScanner() {
    return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
      @Override
      protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata()
            .isAnnotation();

      }
    };
  }

  private Set<String> getBasePackages(AnnotationMetadata annotationMetadata) {
    Set<String> basePackage = new HashSet<>();
    String[] packages = environment.getProperty("nephren.raven.apiclient.packages", String[].class);
    if (packages != null) {
      basePackage.addAll(Arrays.asList(packages));
    }

    if (basePackage.isEmpty()) {
      basePackage.add(ClassUtils.getPackageName(annotationMetadata.getClassName()));
    }
    return basePackage;
  }

  private void registerBean(
      AnnotatedBeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
    if (!annotationMetadata.isInterface()) {
      throw new RavenApiException(
          "#RavenApiClientRegistrar - @RavenApiClient can only be specified on an interface",
          null);
    }

    Map<String, Object> attributes =
        annotationMetadata.getAnnotationAttributes(RavenApiClient.class.getCanonicalName());
    assert attributes != null && !attributes.isEmpty();
    String name = getBeanName(attributes);
    registerApiClientInterceptor(registry, name, annotationMetadata, attributes);
    registerApiClient(registry, name, annotationMetadata, attributes);
  }

  private String getBeanName(Map<String, Object> attributes) {
    String name = (String) attributes.get("name");
    if (StringUtils.hasText(name)) {
      return name;
    }
    throw new RavenApiException("'name' must be provided in @RavenApiClient", null);
  }

  private void registerApiClientInterceptor(
      BeanDefinitionRegistry registry, String name,
      AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
    String beanName = annotationMetadata.getClassName() + METHOD_INTERCEPTOR;
    String aliasName = name + METHOD_INTERCEPTOR;

    BeanDefinitionBuilder beanDefinitionBuilder =
        BeanDefinitionBuilder.genericBeanDefinition(RavenApiClientMethodInterceptor.class);
    beanDefinitionBuilder.addPropertyValue("annotationMetadata", annotationMetadata);
    try {
      beanDefinitionBuilder.addPropertyValue("type",
          Class.forName(annotationMetadata.getClassName()));
    } catch (ClassNotFoundException e) {
      throw new BeanCreationException(e.getMessage(), e);
    }

    beanDefinitionBuilder.addPropertyValue("name", name);
    beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

    AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
    boolean primary = (Boolean) attributes.get("primary");
    beanDefinition.setPrimary(primary);

    BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, beanName,
        new String[] {aliasName});
    BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
  }

  private void registerApiClient(
      BeanDefinitionRegistry registry, String name,
      AnnotationMetadata annotationMetadata, Map<String, Object> attributes) {
    String beanName = annotationMetadata.getClassName();
    BeanDefinitionBuilder definition =
        BeanDefinitionBuilder.genericBeanDefinition(ProxyFactoryBean.class);
    definition.addPropertyValue("interceptorNames", new String[] {name + METHOD_INTERCEPTOR});
    try {
      definition.addPropertyValue("proxyInterfaces", new Class[] {Class.forName(beanName)});
    } catch (ClassNotFoundException e) {
      throw new BeanCreationException(e.getMessage(), e);
    }
    definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

    AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
    boolean primary = (Boolean) attributes.get("primary");
    beanDefinition.setPrimary(primary);

    BeanDefinitionHolder holder =
        new BeanDefinitionHolder(beanDefinition, beanName, new String[] {name});
    BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
  }

}
