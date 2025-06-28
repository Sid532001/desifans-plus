package com.learn.desifans_api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DesifansApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesifansApiGatewayApplication.class, args);
	}

}
