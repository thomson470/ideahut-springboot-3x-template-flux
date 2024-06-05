package net.ideahut.springboot.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.util.FrameworkUtil;

/*
 * 1. Untuk menghandle semua error yang terjadi di aplikasi
 *    - Method: handleAllException()
 * 
 */

@Slf4j
@ControllerAdvice
class AppAdvice {
	
	private final AppProperties appProperties;
	
	@Autowired
	AppAdvice(
		AppProperties appProperties	
	) {
		this.appProperties = appProperties;
	}
	
	@ExceptionHandler
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    protected Result handleAllException(Throwable throwable) {
		if (Boolean.TRUE.equals(appProperties.getLoggingError())) {
    		log.error(AppAdvice.class.getSimpleName(), throwable);
    	}
    	return FrameworkUtil.getErrorAsResult(throwable);
    }
    
}
