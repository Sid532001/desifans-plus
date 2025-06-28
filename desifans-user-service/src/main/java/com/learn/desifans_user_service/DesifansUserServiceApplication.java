package com.learn.desifans_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableDiscoveryClient
@EnableMongoAuditing
@EnableCaching
public class DesifansUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesifansUserServiceApplication.class, args);
	}

}
