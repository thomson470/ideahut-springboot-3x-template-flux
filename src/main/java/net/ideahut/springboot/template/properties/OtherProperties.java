package net.ideahut.springboot.template.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.entity.DatabaseProperties;
import net.ideahut.springboot.template.properties.AppProperties.Audit;

@Configuration
@ConfigurationProperties(prefix = "other")
@Setter
@Getter
public class OtherProperties {
	
	private TrxManager trxManager = new TrxManager();
	
	@Setter
	@Getter
	public static class TrxManager {
		private TrxManagerCfg second = new TrxManagerCfg();
	}
	
	@Setter
	@Getter
	public static class TrxManagerCfg extends DatabaseProperties {
		private Audit audit = new Audit();
	}
	
}
