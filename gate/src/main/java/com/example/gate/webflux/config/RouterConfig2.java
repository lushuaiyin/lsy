package com.example.gate.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;



/**
 * webflux定义路由的功能。
 * 类似springmvc的controller。
 * 
 * 
 * @author lsy
 *
 */
@Configuration
public class RouterConfig2 {
	@Bean
	public RouterFunction<ServerResponse> helloXttblog() {
		return RouterFunctions
				.route(RequestPredicates.GET("/xxxxxbbbbb").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), request -> {
					System.out.println("url: " + request.attribute("url"));
					return ServerResponse.ok().body(BodyInserters.fromObject("Hello www.xttblog"));
				}).filter((serverRequest, handlerFunction) -> {
					// 针对/hello 的请求进行过滤，然后在响应中添加一个Content-Type属性
					return ServerResponse.status(HttpStatus.OK).header("Content-Type", "text/plain; charset=utf-8")
							.body(BodyInserters.fromObject("Hello www.xttblog.com"));
				});
	}

	@Bean
	public RouterFunction<ServerResponse> xttblog() {
		return RouterFunctions.route(
				RequestPredicates.GET("/xttblog").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), request -> {
					System.out.println("url: " + request.attribute("url"));
					return ServerResponse.ok().body(BodyInserters.fromObject("Hello www.xttblog"));
				}).filter((serverRequest, handlerFunction) -> {
					// 这里可以判断是否登录，进行拦截。我这里写个业余草，打个广告
					if ("业余草".equals(serverRequest.path())) {
						return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
					}

					// 在request 中放一个url参数
					serverRequest.attributes().put("url", "www.xttblog.com");
					// 针对/hello 的请求进行过滤，然后在响应中添加一个Content-Type属性
					return handlerFunction.handle(serverRequest);
				});
	}

	@Bean
	public RouterFunction<ServerResponse> xttblog_com() {
		return RouterFunctions
				.route(RequestPredicates.GET("/xttblogWebFlux").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
						request -> {
							System.out.println(
									"url: " + request.attribute("url") + ", wexin: " + request.attribute("wexin"));
							return ServerResponse.ok()
									.body(BodyInserters.fromObject("Hello www.xttblog.com xttblogWebFlux"));
						})
				.filter((serverRequest, handlerFunction) -> {
					// 这里可以判断是否登录，进行拦截。我这里写个业余草，打个广告
					if ("业余草".equals(serverRequest.path())) {
						return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
					}

					// 在request 中放一个url参数
					serverRequest.attributes().put("url", "www.xttblog.com");
					// 针对/hello 的请求进行过滤，然后在响应中添加一个Content-Type属性
					return handlerFunction.handle(serverRequest);
				}).filter((serverRequest, handlerFunction) -> {
					// 这里可以判断是否登录，进行拦截。我这里写个业余草，打个广告
					if ("业余草".equals(serverRequest.path())) {
						return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
					}

					// 在request 中放一个url参数
					serverRequest.attributes().put("wexin", "公众号：业余草[yyucao]");
					// 针对/hello 的请求进行过滤，然后在响应中添加一个Content-Type属性
					return handlerFunction.handle(serverRequest);
				});
	}
	
}
