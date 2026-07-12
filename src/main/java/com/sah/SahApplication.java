package com.sah;

import com.sah.FileUpload.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.sah.entity")
@EnableConfigurationProperties(StorageProperties.class)
@EnableJpaRepositories("com.sah.repository")
public class SahApplication {
	public static void main(String[] args) {
		SpringApplication.run(SahApplication.class, args);
	}
}