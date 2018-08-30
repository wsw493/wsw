package com.vortex.cloud.ums.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.vortex.cloud.vfs.common.spring.SpringProperty;

@Configuration
public class PropertiesConfig {

	@Bean
	public Properties properties() throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		// 将加载多个绝对匹配的所有Resource
		// 将首先通过ClassLoader.getResource("META-INF")加载非模式路径部分
		// 然后进行遍历模式匹配
		Resource[] locations = resolver.getResources("classpath*:*.properties");

		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		// propertiesFactoryBean.setLocation(new
		// ClassPathResource("classpath*:/task.properties"));
		propertiesFactoryBean.setLocations(locations);
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}

	@Bean
	public SpringProperty property(Properties properties) {
		SpringProperty property = new SpringProperty();
		property.setProperties(properties);
		return property;
	}
}
