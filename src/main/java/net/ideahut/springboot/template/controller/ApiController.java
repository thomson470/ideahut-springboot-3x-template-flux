package net.ideahut.springboot.template.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.api.ApiAccess;
import net.ideahut.springboot.api.ApiRequest;
import net.ideahut.springboot.api.WebFluxApiService;
import net.ideahut.springboot.template.service.AuthService;

/*
 * Komunikasi antar Service
 * - Menddapatkan token access untuk consumer (Api Provider)
 * - Mendapatkan object Api Access (jika service ini yang menerbitkan token user)
 */
@Public
@ComponentScan
@RestController
@RequestMapping("/api")
class ApiController {
	
	private final WebFluxApiService apiService;
	private final AuthService authService;
	
	@Autowired
	ApiController(
		WebFluxApiService apiService,
		AuthService authService
	) {
		this.apiService = apiService;
		this.authService = authService;
	}
	
	
	/*
	 * Membuat token untuk API Consumer
	 */
	@RequestMapping(path = "/token", method = { RequestMethod.GET, RequestMethod.POST })
	String token(
		ServerHttpRequest httpRequest
	) {
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		return authService.createConsumerToken(apiRequest);
	}
	
	/*
	 * Mendapatkan ApiAccess Object berdasarkan token API user
	 */
	@RequestMapping(path = "/access", method = { RequestMethod.GET, RequestMethod.POST })
	ApiAccess access(
		ServerHttpRequest httpRequest
	) {
		ApiRequest apiRequest = apiService.getApiRequest(httpRequest, true);
		return authService.getApiAccessForExternal(apiRequest);
	}
	
}
