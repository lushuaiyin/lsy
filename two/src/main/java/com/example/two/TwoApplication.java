package com.example.two;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

//@EnableAutoConfiguration
@ComponentScan(basePackages="com.example")
@EnableDiscoveryClient
@SpringBootApplication
public class TwoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwoApplication.class, args);
	}

}
