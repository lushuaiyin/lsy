package com.example.gate.webflux.filter;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * webflux后置拦截器，在请求执行完后执行。
 * 这个还有问题，@Configuration放开后，注册中心的健康检查都有问题了，注册不上去！
 * 
 * @author lsy
 *
 */
//@Configuration
public class PostFilter implements WebFilter{

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		/* 其他几种
         * or 1: 建议尽量采用链式的fluent连贯写法
         *   Mono<Void>  completeMono = chain.filter(exchange);
         *   return completeMono.doFinally(signal -> System.out.println("signal="+signal.toString()));
         */
        //or 2: return chain.filter(exchange).thenEmpty(other);
        //or 3: return chain.filter(exchange).thenMany(other).map(..)....then();
		
		
		/*
		 我们在 WebFilterChain 的 filter 方法后面加入了部分逻辑处理。
		 filter 方法后面可以接doFinally、then、thenEmpty、thenMany、map、flatMap等用法。
		 只要保证这些链式用法后面最终返回 Mono<Void> 即可 
		 */
		return chain.filter(exchange).doFinally(signal -> System.out.println("signal="+signal.toString()));
	}

}
