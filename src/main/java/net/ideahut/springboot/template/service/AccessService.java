package net.ideahut.springboot.template.service;

import org.springframework.http.server.reactive.ServerHttpRequest;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;
import net.ideahut.springboot.api.ApiParameter;

public interface AccessService {

	ApiAuth login(ServerHttpRequest httpRequest, String username, String password) throws Exception;
	
	ApiAccess logout();
	
	ApiAccess info(ServerHttpRequest httpRequest, ApiParameter apiParameter);
	
	// consumer token
	String token(ServerHttpRequest httpRequest);
	
}
