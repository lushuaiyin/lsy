package com.example.gate.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * GatewayFilter的优先级肯定比GlobalFilter小。
 * 
 * order可以这样分： GlobalFilter 从0开始 .  GatewayFilter 从100开始。
 * 
 * @author lsy
 *
 */
public class RequestTimeFilter implements GatewayFilter, Ordered {

//	private static final Log log = LogFactory.getLog(GatewayFilter.class);
	Logger logger = LoggerFactory.getLogger(RequestTimeFilter.class);
	private static final String REQUEST_TIME_BEGIN = "requestTimeBegin";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("RequestTimeFilter开始............");
		
		
		exchange.getAttributes().put(REQUEST_TIME_BEGIN, System.currentTimeMillis());
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			Long startTime = exchange.getAttribute(REQUEST_TIME_BEGIN);
			if (startTime != null) {
				logger.info("RequestTimeFilter===" + exchange.getRequest().getURI().getRawPath() + ": "
						+ (System.currentTimeMillis() - startTime) + "ms");
			}
		}));

	}

	@Override
	public int getOrder() {
		return 100;
	}
}