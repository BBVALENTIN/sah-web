package com.sah;

import jakarta.persistence.EntityManager;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@SpringBootApplication
@EntityScan("com.sah.entity")
@EnableJpaRepositories("com.sah.repository")public class SahApplication {
	public static void main(String[] args) {
		SpringApplication.run(SahApplication.class, args);
	}

}
