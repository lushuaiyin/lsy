package com.example.gate.config;

import java.nio.charset.Charset;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

//import com.example.gate.service.DbRouteDefinitionRepository;
//com.netflix.client.config.CommonClientConfigKey
//com.netflix.loadbalancer.RetryRule
// ClusterClientOptions

/**
 * 这个配置类是公共的配置。
 * 如果某一项配置信息比较多，就单独弄一个配置类。
 * 
 * 
 * @author lsy
 *
 */
@Configuration
public class GateConfig {
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
		RestTemplate restTemplate = new RestTemplate(factory);
		// 支持中文编码
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;

	}

	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();

		httpRequestFactory.setReadTimeout(5000);// 单位为ms
		httpRequestFactory.setConnectTimeout(5000);// 单位为ms
		return httpRequestFactory;
	}

//	@Bean
//    public RestTemplate restTemplate(RestTemplateBuilder builder){
//        return builder.build();
//    }

//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
//			.route("path_route", r -> r.path("/get")
//				.uri("http://httpbin.org"))
//			
//			.route("host_route", r -> r.host("*.myhost.org")
//				.uri("http://httpbin.org"))
//			
//			.route("rewrite_route", r -> r.host("*.rewrite.org")
//				.filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
//				.uri("http://httpbin.org"))
//			
//			.route("hystrix_route", r -> r.host("*.hystrix.org")
//				.filters(f -> f.hystrix(c -> c.setName("slowcmd")))
//				.uri("http://httpbin.org"))
//			
//			.route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
//				.filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
//				.uri("http://httpbin.org"))
//			
//			.route("limit_route", r -> r
//				.host("*.limited.org").and().path("/anything/**")
//				.filters(f -> f.requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())))
//				.uri("http://httpbin.org"))
//			
//			.build();
//	}

//	@Bean
//	public RouteDefinitionWriter routeDefinitionWriter() {
//		return new InMemoryRouteDefinitionRepository();
//	}
//
//	@Bean
//	public DbRouteDefinitionRepository dbRouteDefinitionRepository() {
//		return new DbRouteDefinitionRepository();
//	}
}
