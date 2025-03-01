package net.ideahut.springboot.template.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.ApiExclude;
import net.ideahut.springboot.audit.AuditHandler;
import net.ideahut.springboot.audit.AuditRequest;
import net.ideahut.springboot.entity.EntityTrxManager;
import net.ideahut.springboot.entity.TrxManagerInfo;
import net.ideahut.springboot.exception.ResultRuntimeException;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.helper.WebFluxHelper;
import net.ideahut.springboot.object.Page;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.template.Application;
import reactor.core.publisher.Mono;

/*
 * API untuk melihat data audit
 */
@ApiExclude
@ComponentScan
@RestController
@RequestMapping("/audit")
class AuditController {
	
	private final EntityTrxManager entityTrxManager;
	private final AuditHandler auditHandler;
	
	@Autowired
	AuditController(
		EntityTrxManager entityTrxManager,
		AuditHandler auditHandler
	) {
		this.entityTrxManager = entityTrxManager;
		this.auditHandler = auditHandler;
	}
	
	
	@PostMapping(value = "/list")
	Mono<Result> list(ServerHttpRequest httpRequest) {
		return WebFluxHelper
		.onRequestBody(httpRequest)
		.flatMap(bytes -> {
			AuditRequest auditRequest = auditHandler.getRequest(bytes);
			TrxManagerInfo trxManagerInfo = FrameworkHelper.getTrxManagerInfo(entityTrxManager, auditRequest.getManager());
			if (trxManagerInfo == null) {
				throw new ResultRuntimeException(Result.error("AUDIT-01", "Unknown manager: " + auditRequest.getManager()));
			}
			String entity = auditRequest.getEntity();
			if (entity != null && !entity.isEmpty() && auditRequest.getClassOfEntity() == null) {
				try {
					Class<?> classOfEntity = ObjectHelper.classOf(entity);
					auditRequest.setClassOfEntity(classOfEntity);
				} catch(Exception e1) {
					try {
						Class<?> type = ObjectHelper.classOf(Application.Package.APPLICATION + ".entity." + entity);
						auditRequest.setClassOfEntity(type);	
					} catch (Exception e2) {
						throw new ResultRuntimeException(Result.error("AUDIT-02", "Entity is not found, for: " + entity));
					}
				}
			}			
			Page page = auditHandler.getList(auditRequest);
			return Mono.just(Result.success(page));
		});
	}
	
	
	@GetMapping(value = "/bytes")
	Result bytes(
		@RequestParam(name = "manager", required = false) String manager,
		@RequestParam(name = "id") String id
	) {
		byte[] bytes = auditHandler.getBytes(manager, id);
		return Result.success(bytes);
	}
	
	
}
