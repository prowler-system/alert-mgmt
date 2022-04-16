package com.prowler.alertmgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories
public class AlertMgmtApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(AlertMgmtApplication.class, args);
	}

}
