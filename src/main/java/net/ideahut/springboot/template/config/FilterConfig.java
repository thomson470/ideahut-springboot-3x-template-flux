package net.ideahut.springboot.template.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import net.ideahut.springboot.filter.ReactiveRequestFilter;
import net.ideahut.springboot.template.interceptor.AdminRequestInterceptor;
import net.ideahut.springboot.template.interceptor.RootRequestInterceptor;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * Konfigurasi Filter
 */
@Configuration
class FilterConfig {
	
	@Autowired
	private AppProperties appProperties;
	@Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
	@Autowired
	private RootRequestInterceptor rootRequestInterceptor;
	@Autowired
	private AdminRequestInterceptor adminRequestInterceptor;
	
	@Bean
	protected ReactiveRequestFilter defaultRequestFilter() {
		return new ReactiveRequestFilter()
		.setCORSHeaders(appProperties.getCors())
		.setTraceEnable(true)
		.setAllowPaths("/**")
		.setHandlerMapping(requestMappingHandlerMapping)
		.setInterceptors(rootRequestInterceptor, adminRequestInterceptor);
	}
	
}
