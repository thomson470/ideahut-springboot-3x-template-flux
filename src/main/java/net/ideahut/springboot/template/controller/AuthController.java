package net.ideahut.springboot.template.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiAuth;
import net.ideahut.springboot.api.ApiRequest;
import net.ideahut.springboot.api.WebFluxApiService;
import net.ideahut.springboot.template.service.AuthService;

@ComponentScan
@RestController
@RequestMapping("/auth")
class AuthController {
	
	private final AuthService authService;
	private final WebFluxApiService apiService;
	
	@Autowired
	AuthController(
		AuthService authService,
		WebFluxApiService apiService
	) {
		this.authService = authService;
		this.apiService = apiService;
	}
	
	@Public
	@PostMapping("/login")
	ApiAuth login(
		ServerHttpRequest httpRequest,
		@RequestParam("username") String username,
		@RequestParam("password") String password
	) throws Exception {
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		ApiAuth apiAuth = authService.login(apiRequest, username, password);
		return apiAuth != null ? apiAuth.setApiAccess(null).setApiKey(null) : null;
	}
	
	@Public
	@RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
	ApiAccess logout(
		ServerHttpRequest httpRequest
	) {
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		return authService.logout(apiRequest);
	}
	
	@Public
	@RequestMapping(value = "/info", method = {RequestMethod.GET, RequestMethod.POST})
	ApiAccess info(
		ServerHttpRequest httpRequest	
	) {
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		return authService.info(apiRequest);
	}
	
}
