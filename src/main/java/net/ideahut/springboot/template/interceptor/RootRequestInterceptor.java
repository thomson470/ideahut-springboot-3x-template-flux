package net.ideahut.springboot.template.interceptor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminProperties;
import net.ideahut.springboot.interceptor.ReactiveHandlerInterceptor;
import reactor.core.publisher.Mono;

@Component
public class RootRequestInterceptor implements ReactiveHandlerInterceptor, InitializingBean {

	private Set<String> allowPaths;
	private Set<String> skipPaths;

	@Autowired
	private AdminHandler adminHandler;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		allowPaths = new LinkedHashSet<>(Arrays.asList("/**"));
		AdminProperties props = adminHandler.getProperties();
		skipPaths = new LinkedHashSet<>(Arrays.asList(
			props.getApi().getRequestPath() + "/**",
			props.getResource().getRequestPath() + "/**"
		));
	}
	
	@Override
	public Set<String> allowPaths() {
		return allowPaths;
	}

	@Override
	public Set<String> skipPaths() {
		return skipPaths;
	}

	@Override
	public Mono<Void> preHandle(ServerWebExchange exchange, Object handler) {
		return null;
	}

}
