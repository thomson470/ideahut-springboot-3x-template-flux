package net.ideahut.springboot.template.controller;

import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.ideahut.springboot.annotation.Public;
import net.ideahut.springboot.helper.FrameworkHelper;
import net.ideahut.springboot.helper.StringHelper;
import net.ideahut.springboot.report.ReportHandler;
import net.ideahut.springboot.report.ReportInput;
import net.ideahut.springboot.report.ReportType;
import net.ideahut.springboot.stream.ByteArrayInOutStream;
import net.ideahut.springboot.template.object.ReportData;
import net.ideahut.springboot.template.properties.AppProperties;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import reactor.core.publisher.Mono;

/*
 * Contoh penggunaan ReportHandler
 */
@ComponentScan
@RestController
@RequestMapping("/report")
class ReportController implements InitializingBean {
	
	private final AppProperties appProperties;
	private final ReportHandler reportHandler;
	
	private byte[] template;
	private byte[] imageHeader;
	private byte[] imageDetail;
	
	
	@Autowired
	ReportController(
		AppProperties appProperties,
		ReportHandler reportHandler
	) {
		this.appProperties = appProperties;
		this.reportHandler = reportHandler;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String path = appProperties.getReportPath();
		Assert.hasLength(path, "AppProperties.reportPath is required");
		path = FrameworkHelper.replacePath(appProperties.getReportPath());
		path = StringHelper.removeEnd(path, "/");
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());
		template = FrameworkHelper.getResourceAsByteArray(resolver.getResource(path + "/sample.jasper"));
		imageHeader = FrameworkHelper.getResourceAsByteArray(resolver.getResource(path + "/tree1.png"));
		imageDetail = FrameworkHelper.getResourceAsByteArray(resolver.getResource(path + "/tree2.png"));
	}
	
	@Public
	@GetMapping
	ResponseEntity<Mono<Resource>> get(
		@RequestParam("name") String name
	) {
		ReportType type = getType(name);
		Mono<ByteArrayInputStream> mono = Mono.fromCallable(new Callable<ByteArrayInputStream>() {
			@Override
			public ByteArrayInputStream call() throws Exception {
				JasperReport report = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(template));
				ReportInput input = new ReportInput();
				input.setType(type);
				input.setReport(report);
				input.setParameter("MAIN_TITLE", "Contoh Report");
				input.setParameter("SUB_TITLE", type.name());
				input.setParameter("IMAGE_HEADER", new ByteArrayInputStream(imageHeader));
				input.setParameter("IMAGE_DETAIL", new ByteArrayInputStream(imageDetail));
				
				List<ReportData> datasource = new ArrayList<>();
				for (long i = 0; i < 100; i++) {
					ReportData data = new ReportData();
					data.setDescription("Deskripsi - " + System.nanoTime());
					data.setName("Name - " + System.nanoTime());
					data.setNumber(i);
					data.setUuid(UUID.randomUUID().toString());
					datasource.add(data);
				}
				input.setDatasource(datasource);
				
				ByteArrayInOutStream stream = new ByteArrayInOutStream();
				reportHandler.exportReport(input, stream);
	            OutputStreamWriter writer = new OutputStreamWriter(stream);
	            writer.flush();
	            return stream.getInputStream();
			}
		});
		return ResponseEntity.ok()
		.contentType(MediaType.valueOf(type.getContentType()))
        .body(mono.flatMap(x -> {
        	Resource resource = new InputStreamResource(x);
            return Mono.just(resource);
        }));
	}
	
	private static ReportType getType(String name) {
		try {
			return ReportType.valueOf(name.trim().toUpperCase());
		} catch (Exception e) {
			return ReportType.PDF;
		}
	}
	
}
