package net.ideahut.springboot.template.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.admin.AdminHandler;
import net.ideahut.springboot.admin.WebFluxAdminController;
import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.mapper.DataMapper;
import net.ideahut.springboot.security.WebFluxSecurity;

@ApiExclude
@ComponentScan
@RestController
@RequestMapping("/_/api")
class AdminController extends WebFluxAdminController {
	
	private final DataMapper dataMapper;
	private final AdminHandler adminHandler;
	private final WebFluxSecurity webFluxSecurity;
	
	@Autowired
	AdminController(
		DataMapper dataMapper,
		AdminHandler adminHandler,
		WebFluxSecurity webFluxSecurity
	) {
		this.dataMapper = dataMapper;
		this.adminHandler = adminHandler;
		this.webFluxSecurity = webFluxSecurity;
	}
	
	@Override
	protected DataMapper dataMapper() {
		return dataMapper;
	}
	
	@Override
	protected AdminHandler adminHandler() {
		return adminHandler;
	}

	@Override
	protected WebFluxSecurity webFluxSecurity() {
		return webFluxSecurity;
	}

}
