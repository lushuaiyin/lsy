/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.gate.limit;

import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * User Request Rate Limiter filter. See https://stripe.com/blog/rate-limiters and
 */
//@Component
public class UrlRequestRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<UrlRequestRateLimiterGatewayFilterFactory.Config> {

	public static final String KEY_RESOLVER_KEY = "keyResolver";

	private final RateLimiter defaultRateLimiter;
	private final KeyResolver defaultKeyResolver;

	public UrlRequestRateLimiterGatewayFilterFactory(RateLimiter defaultRateLimiter,
												  KeyResolver defaultKeyResolver) {
		super(Config.class);
		this.defaultRateLimiter = defaultRateLimiter;
		this.defaultKeyResolver = defaultKeyResolver;
	}

	public KeyResolver getDefaultKeyResolver() {
		return defaultKeyResolver;
	}

	public RateLimiter getDefaultRateLimiter() {
		return defaultRateLimiter;
	}

	@Autowired
	Map<String,String> limitmap;
	
	@SuppressWarnings("unchecked")
	@Override
	public GatewayFilter apply(Config config) {
		KeyResolver resolver = (config.keyResolver == null) ? defaultKeyResolver : config.keyResolver;
		RateLimiter<Object> limiter = (config.rateLimiter == null) ? defaultRateLimiter : config.rateLimiter;

		return (exchange, chain) -> {
			Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

			String routeid=route.getId();//one
			String reqPath = exchange.getRequest().getURI().getPath();// /one/hello
			
			
			System.out.println("UrlRequestRateLimiterGatewayFilterFactory。。。。。routeid=="+routeid+",reqPath=="+reqPath);
			System.out.println("UrlRequestRateLimiterGatewayFilterFactory。。。。。limiter.getConfig().toString()=="+limiter.getConfig().toString());
			
			if(limitmap!=null) {
				System.out.println("UrlRequestRateLimiterGatewayFilterFactory。。。。。limitmap=="+limitmap.toString());
//				//从配置数据limitmap根据key（url）获取对应的限流参数，把参数设置到config里。
//				config.setRateLimiter(new RedisRateLimiter(2,8));
//				System.out.println("UrlRequestRateLimiterGatewayFilterFactory。改变。改变。config.toString()=="+config.toString());
//				System.out.println("UrlRequestRateLimiterGatewayFilterFactory。改变。改变。limiter.getConfig().toString()=="+limiter.getConfig().toString());
			}
			
			return resolver.resolve(exchange).flatMap(key ->
			        
					// TODO: if key is empty?
					limiter.isAllowed(route.getId(), key).flatMap(response -> {
						System.out.println("UrlRequestRateLimiterGatewayFilterFactory。。。。。。"+route.getId()+"==="+key);//  one===/hello
						
						for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
							exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
						}

						if (response.isAllowed()) {
							return chain.filter(exchange);
						}

						exchange.getResponse().setStatusCode(config.getStatusCode());
						return exchange.getResponse().setComplete();
					}));
		};
	}

	public static class Config {
		private KeyResolver keyResolver;
		private RateLimiter rateLimiter;
		private HttpStatus statusCode = HttpStatus.TOO_MANY_REQUESTS;

		public KeyResolver getKeyResolver() {
			return keyResolver;
		}

		public Config setKeyResolver(KeyResolver keyResolver) {
			this.keyResolver = keyResolver;
			return this;
		}
		public RateLimiter getRateLimiter() {
			return rateLimiter;
		}

		public Config setRateLimiter(RateLimiter rateLimiter) {
			this.rateLimiter = rateLimiter;
			return this;
		}

		public HttpStatus getStatusCode() {
			return statusCode;
		}

		public Config setStatusCode(HttpStatus statusCode) {
			this.statusCode = statusCode;
			return this;
		}
	}

}
