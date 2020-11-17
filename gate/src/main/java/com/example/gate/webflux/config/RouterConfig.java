package com.example.gate.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.gate.webflux.route.HiController;

@Configuration
public class RouterConfig {

	@Bean
	public RouterFunction<ServerResponse> greet(HiController handler) {
		return RouterFunctions
				.route(RequestPredicates.GET("/hi").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), handler::sayHi)
				//过滤器给所有请求加header属性：设置编码为utf-8
				.filter((serverRequest, handlerFunction) -> {
					return ServerResponse.status(HttpStatus.OK).header("Content-Type", "text/plain; charset=utf-8")
							.build();
				});
	}

}