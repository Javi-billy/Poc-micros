package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.demo.infrastructure.adapter.out.feign")
public class ServicioNotificacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicioNotificacionesApplication.class, args);
	}

}
