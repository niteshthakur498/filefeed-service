package com.nitesh.filefeed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableR2dbcRepositories
@EnableWebFlux
@SpringBootApplication
public class FilefeedApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilefeedApplication.class, args);
	}

}
