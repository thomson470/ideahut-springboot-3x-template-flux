package net.ideahut.springboot.template.app;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.bean.BeanConfigure;
import net.ideahut.springboot.exception.ResultRuntimeException;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * 1. Untuk menghandle semua error yang terjadi di aplikasi
 *    - Method: handleAllException()
 * 
 */

@Slf4j
@ControllerAdvice
class AppAdvice implements BeanConfigure<AppAdvice> {
	
	private Set<String> handleAllExceptionSkipPaths = new LinkedHashSet<>();
	private boolean isLogAllError = true;
	
	@Override
	public Callable<AppAdvice> onConfigureBean(ApplicationContext applicationContext) {
		AppAdvice self = this;
		return () -> {
			AdminHandler adminHandler = applicationContext.getBean(AdminHandler.class);
			AppProperties appProperties = applicationContext.getBean(AppProperties.class);
			isLogAllError = Boolean.TRUE.equals(appProperties.getLogAllError());
			handleAllExceptionSkipPaths.add(adminHandler.getWebPath());
			return self;
		};
	}
	
	@ExceptionHandler
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public Result handleAllException(
    	ServerHttpRequest request,
    	Throwable throwable
    ) {
		String path = request.getPath().pathWithinApplication().value();
		boolean skip = isSkipPath(handleAllExceptionSkipPaths, path);
		return ObjectHelper.callIf(
			!skip, 
			() -> {
				ObjectHelper.runIf(isLogAllError, () -> log.error(AppAdvice.class.getSimpleName(), throwable));
				return ResultRuntimeException.of(throwable).getResult();
			}
		);
    }
	
	private boolean isSkipPath(Set<String> skipPaths, String path) {
		for (String skipPath : skipPaths) {
			if (path.startsWith(skipPath)) {
				return true;
			}
		}
		return false;
	}
    
}
