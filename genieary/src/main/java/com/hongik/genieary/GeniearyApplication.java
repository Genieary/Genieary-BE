package com.hongik.genieary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GeniearyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeniearyApplication.class, args);
	}

}
