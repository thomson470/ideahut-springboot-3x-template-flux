package net.ideahut.springboot.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import net.ideahut.springboot.filter.WebFluxRequestFilter;
import net.ideahut.springboot.template.interceptor.AdminRequestInterceptor;
import net.ideahut.springboot.template.interceptor.RootRequestInterceptor;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * Konfigurasi Filter
 */
@Configuration
class FilterConfig {
	
	@Bean
	protected WebFluxRequestFilter defaultRequestFilter(
		AppProperties appProperties,
		RequestMappingHandlerMapping requestMappingHandlerMapping,
		RootRequestInterceptor rootRequestInterceptor,
		AdminRequestInterceptor adminRequestInterceptor
	) {
		return new WebFluxRequestFilter()
		.setCORSHeaders(appProperties.getCors())
		.setTraceEnable(true)
		.setAllowPaths("/**")
		.setHandlerMapping(requestMappingHandlerMapping)
		.setInterceptors(rootRequestInterceptor, adminRequestInterceptor);
	}
	
}
