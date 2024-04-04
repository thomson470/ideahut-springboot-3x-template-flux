package net.ideahut.springboot.template.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.resource.VersionResourceResolver;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminProperties;
import net.ideahut.springboot.config.BasicWebFluxConfig;
import net.ideahut.springboot.mapper.DataMapper;

@Configuration
@EnableWebFlux
class WebFluxConfig extends BasicWebFluxConfig {
	
	@Autowired
	private DataMapper dataMapper;
	@Autowired
	private AdminHandler adminHandler;
	
	@Override
	protected DataMapper dataMapper() {
		return dataMapper;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		AdminProperties.Resource resource = adminHandler.getProperties().getResource();
		registry
		.addResourceHandler(resource.getRequestPath() + "/**")
		.addResourceLocations(resource.getLocations())
		.setCacheControl(CacheControl.maxAge(60, TimeUnit.DAYS))
        .resourceChain(false)
        .addResolver(new VersionResourceResolver().addContentVersionStrategy(resource.getRequestPath() + "/**"));
		super.addResourceHandlers(registry);
	}
	
}
