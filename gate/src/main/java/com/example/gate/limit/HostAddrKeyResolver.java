package com.example.gate.limit;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 根据host限流
 * @author lsy
 *
 *RequestRateLimiter 即
 *RequestRateLimiterGatewayFilterFactory
 */
public class HostAddrKeyResolver implements KeyResolver {
	@Override
	public Mono<String> resolve(ServerWebExchange exchange) {
		return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
	}
}
