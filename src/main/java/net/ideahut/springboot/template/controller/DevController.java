package net.ideahut.springboot.template.controller;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.api.ApiConsumerRequest;
import net.ideahut.springboot.api.ApiConsumerService;
import net.ideahut.springboot.helper.ErrorHelper;
import net.ideahut.springboot.helper.ObjectHelper;
import net.ideahut.springboot.helper.WebFluxHelper;
import net.ideahut.springboot.object.Result;
import net.ideahut.springboot.rest.RestMethod;
import net.ideahut.springboot.rest.RestRequest;
import net.ideahut.springboot.rest.RestResponse;
import net.ideahut.springboot.sysparam.SysParamHandler;
import net.ideahut.springboot.sysparam.SysParamUpdater;
import net.ideahut.springboot.sysparam.dto.SysParamDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
 * Endpoint untuk kebutuhan DEVELOPMENT
 * Hapus / nonaktifkan jika tidak diperlukan
 */
@ComponentScan
@RestController
@RequestMapping("/dev")
class DevController {
	
	private static final String SYSTEM_CODE = "API_TOKEN";
	
	private final SysParamHandler sysParamHandler;
	private final ApiConsumerService apiConsumerService;
	
	@Autowired
	DevController(
		SysParamHandler sysParamHandler,
		ApiConsumerService apiConsumerService
	) {
		this.sysParamHandler = sysParamHandler;
		this.apiConsumerService = apiConsumerService;
	}
	
	/*
	 * Meminta token API consumer untuk melakukan request
	 * Token akan disimpan di SysParams, sysCode = "API_TOKEN", paramCode = <API_NAME>
	 */
	@Public
	@PostMapping("/api/token")
	void apiToken(
		@RequestParam("apiName") String apiName
	) {
		String token = apiConsumerService.token(apiName);
		if (token != null && !token.isEmpty() && ObjectHelper.isInstance(SysParamUpdater.class, sysParamHandler)) {
			((SysParamUpdater) sysParamHandler).updateSysParam(
				SysParamDto.create()
				.setSysCode(SYSTEM_CODE)
				.setParamCode(apiName)
				.setValue(token)
			);
		}
	}

	/*
	 * Proxy request ke Api Service lain
	 * - menggunakan API consumer token yang tersimpan di SysParam
	 * - semua http method diijinkan
	 * 
	 */
	@Public
	@RequestMapping(
		path = "/api/request/{apiName}/**", 
		method = { 
			RequestMethod.GET, 
			RequestMethod.POST, 
			RequestMethod.PUT, 
			RequestMethod.DELETE,
			RequestMethod.HEAD,
			RequestMethod.PATCH,
			RequestMethod.TRACE
		}
	)
	Mono<byte[]> apiRequest(
		ServerHttpRequest httpRequest,
		ServerHttpResponse httpResponse,
		@PathVariable("apiName") String apiName
	) {
		return WebFluxHelper
		.onRequestBody(httpRequest)
		.flatMap(requestBody -> {
			// replace prefix path, dan path sisanya akan diappend ke base url service yang dituju
			String path = httpRequest
			.getPath()
			.pathWithinApplication()
			.value()
			.replace("/dev/api/request/" + apiName, "");
			String token = sysParamHandler.getValue(String.class, SYSTEM_CODE, apiName, "");
			ErrorHelper.throwIf(token.isEmpty(), SYSTEM_CODE + " not found, api: " + apiName);
			RestRequest restRequest = new RestRequest()
			.setPath(path)
			.setMethod(RestMethod.valueOf(httpRequest.getMethod().name().toUpperCase()))
			.setQueries(httpRequest.getQueryParams());
			HttpHeaders httpHeaders = httpRequest.getHeaders();
			for (Entry<String, List<String>> entry : httpHeaders.entrySet()) {
				restRequest.setHeaderValues(entry.getKey(), entry.getValue());
			}
			if (!RestMethod.GET.equals(restRequest.getMethod())) {
				restRequest.setBody(requestBody);
			} else {
				restRequest.getHeaders().remove(HttpHeaders.CONTENT_LENGTH);
			}
			ApiConsumerRequest apiConsumerRequest = new ApiConsumerRequest()
			.setApiName(apiName)
			.setToken(token)
			.setRequest(restRequest);
			RestResponse restResponse = apiConsumerService.call(apiConsumerRequest);
			for (String restHeaderName : restResponse.getHeaderNames()) {
				httpResponse.getHeaders().put(restHeaderName, restResponse.getHeaderValues(restHeaderName));
			}
			byte[] body = restResponse.getBodyAsByteArray();
			return Mono.just(body);
		});
	}
	
	/*
	 * Contoh multipart
	 */
	@PostMapping(value = "/multipart")
	Flux<Result> receive(
		@RequestPart("name") String name,
		@RequestPart("file") FilePart file
	) {
		return file.content().flatMap(dataBuffer -> {
	        byte[] bytes = WebFluxHelper.getDataBufferAsBytes(dataBuffer);
	        Result result = Result.success()
	        .setInfo("name", name)
	        .setInfo("length", bytes.length)
	        .setInfo("filename", file.filename());
	        return Flux.just(result);
	    });
	}
	
}
