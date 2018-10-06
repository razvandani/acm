package com.gcr.acm.registry;

import com.gcr.acm.common.logging.LoggerAspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication(scanBasePackages = "com.gcr.acm")
@EnableEurekaServer
@EnableTransactionManagement
@EntityScan(value = "com.gcr.acm")
public class RegistryApplication extends WebMvcConfigurerAdapter {

	@Value("${spring.profiles.active}")
	private String activeProfile;

	public static void main(String[] args) {
		LoggerAspect.initializeLogging();
		SpringApplication.run(RegistryApplication.class, args);
	}
}
