package com.learning.desifans_config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class DesifansConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesifansConfigServerApplication.class, args);
	}

}
