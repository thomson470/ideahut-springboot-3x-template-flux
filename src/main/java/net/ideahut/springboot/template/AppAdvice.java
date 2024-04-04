package net.ideahut.springboot.template;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import net.ideahut.springboot.exception.ResultException;
import net.ideahut.springboot.exception.ResultRuntimeException;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.properties.AppProperties;
import net.ideahut.springboot.util.ErrorUtil;

/*
 * 1. Untuk menghandle semua error yang terjadi di aplikasi
 *    - Method: handleAllException()
 *    
 * 2. Membuat semua response ke class net.ideahut.springboot.object.Result
 *    - Method: beforeBodyWrite()
 * 
 */

@Slf4j
@ControllerAdvice
public class AppAdvice {
	
	@Autowired
	private AppProperties appProperties;
	
	@ExceptionHandler
    @ResponseStatus(code = HttpStatus.OK)
    @ResponseBody
    public Result handleAllException(Throwable throwable) {
		if (Boolean.TRUE.equals(appProperties.getLoggingError())) {
    		log.error(AppAdvice.class.getSimpleName(), throwable);
    	}
    	Throwable ex = ErrorUtil.getCause(throwable);
    	Result result = null;
    	if (ex instanceof ResultException) {
    		result = ((ResultException)ex).getResult();
    	} else if (ex instanceof ResultRuntimeException) {
    		result = ((ResultRuntimeException)ex).getResult();
    	} else {
    		result = Result.error(ErrorUtil.getErrors(ex, true));
    	}
    	return result;
    }
    
}
