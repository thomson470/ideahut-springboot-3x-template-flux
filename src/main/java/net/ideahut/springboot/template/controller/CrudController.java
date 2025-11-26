package net.ideahut.springboot.template.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.crud.CrudAction;
import net.ideahut.springboot.crud.CrudHandler;
import net.ideahut.springboot.crud.CrudInput;
import net.ideahut.springboot.crud.CrudPermission;
import net.ideahut.springboot.helper.StringHelper;
import net.ideahut.springboot.helper.WebFluxHelper;
import net.ideahut.springboot.object.Page;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.task.TaskHandler;
import reactor.core.publisher.Mono;

@Public
@ComponentScan
@RestController
@RequestMapping("/crud")
class CrudController extends net.ideahut.springboot.crud.WebFluxCrudController {
	
	private final CrudHandler crudHandler;
	private final CrudPermission crudPermission;
	
	@Autowired
	CrudController(
		CrudHandler crudHandler, 
		CrudPermission crudPermission
	) {
		this.crudHandler = crudHandler;
		this.crudPermission = crudPermission;
	}
	
	@Override
	protected CrudHandler crudHandler() {
		return crudHandler;
	}
	
	@Override
	protected TaskHandler taskHandler() {
		return null;
	}

	/*
	 * Crud Permission bisa di level Handler ataupun di lever Controller.
	 * Untuk di level Handler akan berlaku disetiap penggunaan CrudHandler
	 * Untuk di level Controller hanya akan berlaku di setiap pemanggilan endpoint Crud
	 */
	@Override
	protected CrudPermission crudPermission() {
		return crudPermission;
	}
	
	/*
	 * INFO
	 * - Tipe Id: STANDARD, EMBEDDED, COMPOSITE
	 * - Logical: AND, OR
	 * - Condition: ANY_LIKE, LIKE, BETWEEN, dll
	 * - Match: EXACT (inner join), CONTAIN (left join)
	 * - CrudAction: SINGLE, PAGE, LIST, CREATE, UPDATE, DELETE, dll
	 */
	@Public
	@GetMapping(value = "/info/constant")
	Result infoConstant() {
		return super.constant();
	}
	
	/*
	 * BULK LIST
	 */
	@PostMapping(value = "/bulk/list")
	Mono<List<Result>> bulkList(
		ServerHttpRequest httpRequest
	) {
		return WebFluxHelper
		.onRequestBody(httpRequest)
		.flatMap(bytes -> {
			List<Result> result = super.bulkList(bytes);
			return Mono.just(result);
		});
	}
	
	/*
	 * BULK MAP
	 */
	@PostMapping(value = "/bulk/map")
	Mono<Map<String, Result>> bulkMap(
		ServerHttpRequest httpRequest
	) {
		return WebFluxHelper
		.onRequestBody(httpRequest)
		.flatMap(bytes -> {
			Map<String, Result> result = super.bulkMap(bytes);
			return Mono.just(result);
		});
	}
	
	/*
	 * BODY ACTION
	 */
	@PostMapping(value = "/action/{action}")
	Mono<Result> action(
		@PathVariable("action") String action,
		ServerHttpRequest httpRequest
	) {
		return WebFluxHelper
		.onRequestBody(httpRequest)
		.flatMap(bytes -> {
			Result result = super.body(CrudAction.valueOf(action.toUpperCase()), bytes);
			return Mono.just(result);
		});
	}
	
	/*
	 * PARAMETER
	 */
	@RequestMapping(
		value = "/parameter/{action}", 
		method = { 
			RequestMethod.GET, 
			RequestMethod.POST, 
			RequestMethod.PUT, 
			RequestMethod.DELETE 
		}
	)
	Result parameter(
		@PathVariable("action") String action,
		ServerHttpRequest httpRequest
	) {
		return super.parameter(CrudAction.valueOf(action.toUpperCase()), httpRequest);		
	}
	
	/*
	 * OBJECT (CrudAction.SINGLE)
	 */
	@GetMapping(value = "/rest/{name}/{id}")
	protected Result object(
		@PathVariable("name") String name, 
		@PathVariable("id") String id,
		@RequestParam(value = "manager", required = false) String manager
	) {
		CrudInput input = new CrudInput()
		.setManager(manager)
		.setName(name)
		.setId(id);
		return super.object(input);
	}
	
	/*
	 * COLLECTION (CrudAction.PAGE)
	 */
	@GetMapping(value = "/rest/{name}/{index}/{size}")
	Result collection(
		@PathVariable("name") String name, 
		@PathVariable("index") Integer index, 
		@PathVariable("size") Integer size,
		@RequestParam(value = "manager", required = false) String manager,
		@RequestParam(value = "count", required = false) String count,
		@RequestParam(value = "filters", required = false) String filters,
		@RequestParam(value = "orders", required = false) String orders,
		@RequestParam(value = "fields", required = false) String fields,
		@RequestParam(value = "excludes", required = false) String excludes,
		@RequestParam(value = "loads", required = false) String loads		
	) {
		Boolean pgcount = StringHelper.valueOf(Boolean.class, count, Boolean.FALSE);
		Page page = Page.of(index, size, pgcount);
		CrudInput input = new CrudInput()
		.setManager(manager)
		.setName(name)
		.setPage(page)
		.setFilters(filters)
		.setOrders(orders)
		.setFields(fields)
		.setExcludes(excludes)
		.setLoads(loads);
		return super.collection(input);
	}
	
	/*
	 * CREATE
	 */
	@PostMapping(value = "/rest/{name}")
	Mono<Result> create(
		@PathVariable("name") String name,
		@RequestParam(value = "manager", required = false) String manager,
		@RequestParam(value = "value", required = false) String value,
		ServerHttpRequest httpRequest
	) {
		return WebFluxHelper
		.onRequestBody(httpRequest)
		.flatMap(data -> {
			CrudInput input = new CrudInput()
			.setManager(manager)
			.setName(name)
			.setValue(value)
			.setData(data);
			Result result = super.create(input);
			return Mono.just(result);
		});
	}
	
	/*
	 * UPDATE
	 */
	@PutMapping(value = "/rest/{name}/{id}")
	Mono<Result> update(
		@PathVariable("name") String name,
		@PathVariable("id") String id,
		@RequestParam(value = "manager", required = false) String manager,
		@RequestParam(value = "value", required = false) String value,
		ServerHttpRequest httpRequest
	) {
		return WebFluxHelper
		.onRequestBody(httpRequest)
		.flatMap(data -> {
			CrudInput input = new CrudInput()
			.setManager(manager)
			.setName(name)
			.setId(id)
			.setValue(value)
			.setData(data);
			Result result = super.update(input);
			return Mono.just(result);
		});
	}
	
	/*
	 * DELETE 
	 */
	@DeleteMapping(value = "/rest/{name}/{id}")
	protected Result delete(
		@PathVariable("name") String name,
		@PathVariable("id") String id,
		@RequestParam(value = "manager", required = false) String manager
	) {
		CrudInput input = new CrudInput()
		.setManager(manager)
		.setName(name)
		.setId(id);
		return super.delete(input);
	}	
		
}
