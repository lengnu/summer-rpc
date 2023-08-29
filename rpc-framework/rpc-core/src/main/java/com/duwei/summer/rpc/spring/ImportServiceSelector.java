package com.duwei.summer.rpc.spring;

import com.duwei.summer.rpc.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * <p>
 * 自定义注解扫描器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 19:29
 * @since: 1.0
 */
@Slf4j
public class ImportServiceSelector implements ImportBeanDefinitionRegistrar {
    private static final String BASE_PACKAGES = "basePackages";

    private static final String SERVICE_PUBLISH_PROCESSOR = "servicePublishProcessor";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        AnnotationAttributes rpcScanServiceAnnotation = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcServiceScan.class.getName()));
        if (rpcScanServiceAnnotation != null) {
            String[] basePackages = rpcScanServiceAnnotation.getStringArray(BASE_PACKAGES);
            if (basePackages.length != 0) {
                ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
                scanner.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));
                int scan = scanner.scan(basePackages);
                log.info("共扫描到{}符合条件的BeanDefinition",scan);
            }
        }
        // 将注册的postProcess注册进行
       registry.registerBeanDefinition(SERVICE_PUBLISH_PROCESSOR, BeanDefinitionBuilder.genericBeanDefinition(RpcServiceRegistryPostProcessor.class).getBeanDefinition());
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ImportBeanDefinitionRegistrar.super.registerBeanDefinitions(importingClassMetadata, registry);
    }
}
