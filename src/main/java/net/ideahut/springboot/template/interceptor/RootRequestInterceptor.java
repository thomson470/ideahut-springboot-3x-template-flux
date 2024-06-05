package net.ideahut.springboot.template.interceptor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.AdminProperties;
import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiHandler;
import net.ideahut.springboot.api.WebFluxApiValidator;
import net.ideahut.springboot.context.RequestContext;
import net.ideahut.springboot.interceptor.WebFluxHandlerInterceptor;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.AppConstants;
import net.ideahut.springboot.util.FrameworkUtil;
import net.ideahut.springboot.util.WebFluxUtil;
import reactor.core.publisher.Mono;

@Component
public class RootRequestInterceptor implements WebFluxHandlerInterceptor, InitializingBean {

	private Set<String> allowPaths;
	private Set<String> skipPaths;
	
	// set true jika ingin mengecek berdasarkan RequestPermission
	private boolean isCheckRequestEnabled = false;

	private final DataMapper dataMapper;
	private final AdminHandler adminHandler;
	private final ApiHandler apiHandler;
	private final WebFluxApiValidator apiValidator;
	
	@Autowired
	RootRequestInterceptor(
		DataMapper dataMapper,
		AdminHandler adminHandler,
		ApiHandler apiHandler,
		WebFluxApiValidator apiValidator
	) {
		this.dataMapper = dataMapper;
		this.adminHandler = adminHandler;
		this.apiHandler = apiHandler;
		this.apiValidator = apiValidator;
	}
	
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
		if (handler instanceof HandlerMethod hm) {
			try {
				Public anPublic = FrameworkUtil.getAnnotation(Public.class, hm);
				boolean isPublic = anPublic != null && anPublic.value();
				ApiAccess access = apiValidator.getApiAccess(exchange.getRequest(), isPublic);
				if (access == null) {
					access = new ApiAccess();
					access.setRole(AppConstants.Default.API_ROLE);
				}
				if (!isPublic && isCheckRequestEnabled) {
					boolean allowed = apiHandler.isRequestAllowed(access.getRole(), hm);
					if (!allowed) {
						throw FrameworkUtil.exception(Result.error("REQ-00", "Request is not allowed"));
					}
				}
				// implementasi untuk simpan access (bisa ke context atau redis), agar bisa dipanggil dari class lain.
				RequestContext.currentContext().setAttribute(ApiAccess.CONTEXT, access);
			} catch (Exception e) {
				Result result = FrameworkUtil.getErrorAsResult(e);
				return WebFluxUtil.sendToClient(dataMapper, exchange, result);
			}
		}
		return null;
	}

}
