package com.example.gate.webflux.route;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class HiController {

	public Mono<ServerResponse> sayHi(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
				.header("Content-Type", "text/plain; charset=utf-8")//设置编码，防止中文乱码
				.body(BodyInserters.fromObject("Hi Man, dude here !"));

	}
}
