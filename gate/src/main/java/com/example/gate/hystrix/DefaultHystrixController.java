package com.example.gate.hystrix;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

/**
 * HystrixGatewayFilterFactory
 * HystrixCommandProperties
 * 
 * Resilience4JCircuitBreakerFactory 
 * ReactiveResilience4JCircuitBreakerFactory 
 * 
 * 默认降级处理。
 * 当请求在一定时间内没有返回就出发网关的熔断，进行降级处理。
 * 即：请求被替换成这个熔断降级的controller，返回的内容就是这个controller的返回。
 * 这个写在网关里。
 * 
 */
@RestController
public class DefaultHystrixController {

	Logger logger = LoggerFactory.getLogger(DefaultHystrixController.class);
	
	@RequestMapping("/defaultfallback")
	@ResponseBody
	public Map<String, String> defaultfallback() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
		String dateNowStr=LocalDateTime.now().format(df);
        
		logger.info("DefaultHystrixController降级操作...defaultfallback");
		Map<String, String> map = new HashMap<>();
		map.put("resultCode", "fail");
		map.put("resultMessage", "服务繁忙，请稍后重试defaultfallback");
		map.put("time", dateNowStr);
		return map;
	}
	
	@RequestMapping("/defaultfallback1")
	@ResponseBody
	public Map<String, String> defaultfallback1() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
		String dateNowStr=LocalDateTime.now().format(df);
		logger.info("DefaultHystrixController降级操作...defaultfallback111111");
		Map<String, String> map = new HashMap<>();
		map.put("resultCode", "fail");
		map.put("resultMessage", "服务繁忙，请稍后重试defaultfallback111111");
		map.put("time", dateNowStr);
		return map;
	}
	
	@GetMapping(value="/defaultfallback2")
	public Map<String, String> defaultfallback2() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
		String dateNowStr=LocalDateTime.now().format(df);
		logger.info("DefaultHystrixController降级操作...defaultfallback222222");
		Map<String, String> map = new HashMap<>();
		map.put("resultCode", "fail");
		map.put("resultMessage", "服务繁忙，请稍后重试defaultfallback222222");
		map.put("time", dateNowStr);
		return map;
	}
	
	
	
//	@RequestMapping("/defaultfallback3")
//	public Mono<String> defaultfallback3() {
//		return Mono.just("defaultfallback3");
//	}
	
	
}
