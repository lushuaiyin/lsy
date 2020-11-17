package com.example.gate.webflux.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;


/**
 * webflux定义filter。
 * 其实springcloudgateway中的filter也是建立在webflux之上的。
 * 
 * @Configuration表示该对象注入到spring容器。也可以换种写法。这里不加注解，在一个总的@Configuration类里用@Bean声明这个对象
 * @Order 注解是表示过滤器的执行顺序。数值越小，越先执行！没有@Order注解，过滤器的执行顺序将按实现类的全限定类名(含有包名)的字典序升序排列进行执行。
 * 
 * @author lsy
 *
 */
//@Configuration
//@Order(-1)
public class TestAuthFilter implements WebFilter {
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//		ServerHttpRequest request = exchange.getRequest();
//		ServerHttpResponse response = exchange.getResponse();
//
//		String token = request.getHeaders().getFirst("token");
//		if (null == token) {
//			/*
//			 * http头中没有appKey，修改请求的目标url为/auth/error request.mutate返回一个请求构建器(builder design
//			 * pattern)，path方法修改请求的url，build方法返回新的request
//			 */
//			ServerHttpRequest authErrorReq = request.mutate().path("/auth/error").build();
//			// erverWebExchange.mutate类似，构建一个新的ServerWebExchange
//			ServerWebExchange authErrorExchange = exchange.mutate().request(authErrorReq).build();
//			return chain.filter(authErrorExchange);
//		} else { // 业余草：www.xttblog.com
//			return chain.filter(exchange);
//		}
		
		return chain.filter(exchange);
	}
}
