package net.ideahut.springboot.template.service;

import org.springframework.http.server.reactive.ServerHttpRequest;

import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;

public interface AccessService {

	ApiAuth login(ServerHttpRequest request, String username, String password);
	
	void logout();
	
	ApiAccess getApiAccess(String key);
	
}
