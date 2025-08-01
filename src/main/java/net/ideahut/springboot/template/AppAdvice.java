package net.ideahut.springboot.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.properties.AppProperties;

/*
 * 1. Untuk menghandle semua error yang terjadi di aplikasi
 *    - Method: handleAllException()
 * 
 */

@Slf4j
@ControllerAdvice
class AppAdvice {
	
	private final AppProperties appProperties;
	private final AdminHandler adminHandler;
	
	@Autowired
	AppAdvice(
		AppProperties appProperties,
		AdminHandler adminHandler
	) {
		this.appProperties = appProperties;
		this.adminHandler = adminHandler;
	}
	
	@ExceptionHandler
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public Result handleAllException(
    	ServerHttpRequest request,
    	Throwable throwable
    ) {
		String path = request.getPath().pathWithinApplication().value();
		if (path.startsWith(adminHandler.getWebPath())) {
			// kasus: 
			// - Broken pipe, jika resource tidak tersedia
			// - MediaType unsupported, contoh untuk Accept: image/*
			return null;
		} else {
			if (Boolean.TRUE.equals(appProperties.getLogAllError())) {
	    		log.error(AppAdvice.class.getSimpleName(), throwable);
	    	}
			return FrameworkHelper.getErrorAsResult(throwable);
		}
    }
    
}
