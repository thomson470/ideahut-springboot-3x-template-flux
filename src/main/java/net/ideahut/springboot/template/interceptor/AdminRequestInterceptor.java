package net.ideahut.springboot.template.interceptor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.WebFluxAdminSecurity;
import net.ideahut.springboot.audit.AuditInfo;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.helper.WebFluxHelper;
import net.ideahut.springboot.interceptor.WebFluxHandlerInterceptor;
import net.ideahut.springboot.object.MapStringObject;
import net.ideahut.springboot.object.StringSet;
import net.ideahut.springboot.security.SecurityCredential;
import net.ideahut.springboot.security.SecurityUser;
import net.ideahut.springboot.security.WebFluxSecurity;
import net.ideahut.springboot.template.Application;
import net.ideahut.springboot.template.app.AppConstants;
import reactor.core.publisher.Mono;

@Component
public class AdminRequestInterceptor implements WebFluxHandlerInterceptor, InitializingBean {

	private StringSet allowPaths;
	private StringSet skipPaths;

	private final AdminHandler adminHandler;
	private final WebFluxSecurity adminSecurity;
	private final SecurityCredential adminCredential;
	
	@Autowired
	AdminRequestInterceptor(
		AdminHandler adminHandler,
		@Qualifier(AppConstants.Bean.Security.ADMIN) 
		WebFluxSecurity adminSecurity,
		@Qualifier(AppConstants.Bean.Credential.ADMIN) 
		SecurityCredential adminCredential
	) {
		this.adminHandler = adminHandler;
		this.adminSecurity = adminSecurity;
		this.adminCredential = adminCredential;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		allowPaths = new StringSet();
		allowPaths.add(adminHandler.getApiPath() + "/**");
		if (adminHandler.isWebEnabled()) {
			allowPaths.add(adminHandler.getWebPath() + "/**");
		}
		skipPaths = new StringSet();
	}
	
	@Override
	public StringSet allowPaths() {
		return allowPaths;
	}

	@Override
	public StringSet skipPaths() {
		return skipPaths;
	}

	@Override
	public Mono<Void> preHandle(ServerWebExchange exchange, Object handler) {
		if (!Application.isReady()) {
			exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
			return null;
		}
		Mono<Void> mono = adminSecurity.isRequestAuthorized(exchange);
		if (mono != null) {
			return mono;
		}
		if (handler == null) {
			ServerHttpRequest request = exchange.getRequest();
			String path = request.getPath().pathWithinApplication().value();
			Map<String, List<String>> parameters = WebFluxHelper.getRequestParameters(request);
			String redirect = adminHandler.getRedirect(adminCredential, path, parameters, null);
			if (redirect != null) {
				ServerHttpResponse response = exchange.getResponse();
				response.getHeaders().set(HttpHeaders.LOCATION, redirect);
				response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
				return Mono.empty();
			}
		} else if (ObjectHelper.isInstance(HandlerMethod.class, handler)) {
			String key = 
				ObjectHelper.isInstance(WebFluxAdminSecurity.class, adminSecurity) ? 
				((WebFluxAdminSecurity) adminSecurity).getHeaderKey() : HttpHeaders.AUTHORIZATION;
			SecurityUser user = adminCredential.getSecurityUser(
				new MapStringObject().setValue(
					SecurityUser.Parameter.AUTHORIZATION, 
					exchange.getRequest().getHeaders().getFirst(key)
				)
			);
			if (user != null) {
				AuditInfo.fromContext().setAuditor("ADMIN::" + user.getUsername());
			}
		}
		return null;
	}

}
