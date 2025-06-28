package com.example.desifanseurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DesifansEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesifansEurekaServerApplication.class, args);
    }

}
