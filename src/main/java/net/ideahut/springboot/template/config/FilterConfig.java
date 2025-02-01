package net.ideahut.springboot.template.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import net.ideahut.springboot.definition.FilterDefinition;
import net.ideahut.springboot.filter.WebFluxRequestFilter;
import net.ideahut.springboot.helper.ObjectHelper;
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
		FilterDefinition filter = ObjectHelper.useOrDefault(
			appProperties.getFilter(), 
			FilterDefinition::new
		);
		return new WebFluxRequestFilter()
		.setCorsHeaders(filter.getCorsHeaders())
		.setEnableTimeResult(filter.getEnableTimeResult())
		.setTraceEnable(filter.getTraceEnable())
		.setTraceKey(filter.getTraceKey())
		.setAllowPaths("/**")
		.setHandlerMapping(requestMappingHandlerMapping)
		.setInterceptors(rootRequestInterceptor, adminRequestInterceptor);
	}
	
}
