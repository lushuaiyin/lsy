package com.example.gate.filter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.synchronoss.cloud.nio.multipart.util.IOUtils;

import com.google.common.net.MediaType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TokenFilter implements GlobalFilter, Ordered {

	Logger logger = LoggerFactory.getLogger(TokenFilter.class);

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 20;
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		logger.info("TokenFilter开始............");
		
		getAllParamtersRequest(exchange.getRequest());//测试获取paramter数据
		getAllHeadersRequest(exchange.getRequest());//测试获取header数据
		getPostBodyData(exchange);//测试获取上个filter放入attribute中的post的bodydata
		
		
		
		//拦截的逻辑。根据具体业务逻辑做拦截。
		String token = exchange.getRequest().getQueryParams().getFirst("token");
		if (token == null || token.isEmpty()) {
			logger.info("token is empty...");
//			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//			return exchange.getResponse().setComplete();
			
			//设置status和body
	        return Mono.defer(() -> {
	            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);//设置status
	            exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");//设置返回类型
	            final ServerHttpResponse response = exchange.getResponse();
	            byte[] bytes = "{\"code\":\"99999\",\"message\":\"非法访问,没有检测到token~~~~~~\"}".getBytes(StandardCharsets.UTF_8);
	            
	            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
	            response.getHeaders().set("aaa", "bbb");//设置header
	            logger.info("TokenFilter拦截非法请求，没有检测到token............");
	            
	            return response.writeWith(Flux.just(buffer));//设置body
	        });
		}
		
		//没有被if条件拦截，就放行
		return chain.filter(exchange);
	}
	
	
	private Map getAllParamtersRequest(ServerHttpRequest request) {
		logger.info("getAllParamtersRequest开始............");
		Map map = new HashMap();
		MultiValueMap<String, String> paramNames = request.getQueryParams();
		Iterator it= paramNames.keySet().iterator();
		while (it.hasNext()) {
			String paramName = (String) it.next();
			
			List<String> paramValues = paramNames.get(paramName);
			if (paramValues.size() >= 1) {
				String paramValue = paramValues.get(0);
				logger.info("request参数："+paramName+",值："+paramValue);
				map.put(paramName, paramValue);
			}
		}
		return map;
	}
	
	private Map getAllHeadersRequest(ServerHttpRequest request) {
		logger.info("getAllHeadersRequest开始............");
		Map map = new HashMap();
		HttpHeaders hearders = request.getHeaders();
		Iterator it= hearders.keySet().iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			
			List<String> headValues = hearders.get(keyName);
			if (headValues.size() >= 1) {
				String kvalue = headValues.get(0);
				logger.info("request header的key："+keyName+",值："+kvalue);
				map.put(keyName, kvalue);
			}
		}
		return map;
	}
	
	
	/**
	 * 在之前的一个filter我们获取了post请求的body数据，放入了Attributes中
	 * exchange.getAttributes().put("PostBodyData", bodyStr);
	 * 所以在这里就可以直接取了。
	 * 
	 * 
	 * post请求的传参获取相对比较麻烦一些，gateway采用了webflux的方式来封装的请求体。  
	 * 我们知道post常用的两种传参content-type是application/x-www-form-urlencoded和application/json，这两种方式还是有区别的。
	 * 
	 * 
	 * @param exchange
	 * @return
	 */
	private Object getPostBodyData(ServerWebExchange exchange) {
		logger.info("getPostBodyData开始............");
		
		Object res=exchange.getAttributes().get("PostBodyData");
		if(res!=null) {
			logger.info("getPostBodyData获取前面filter放入Attributes中的数据为========\r\n"+res.toString());
		}else {
			logger.info("getPostBodyData获取前面filter放入Attributes中的数据为null");
		}
		
		
		return res;
	}
	
	
	
//	/**
//	 * 
//	 * @param request
//	 * @return
//	 */
//	private Map getAllParamtersRequest(HttpServletRequest request) {
//		logger.info("getAllParamtersRequest............");
//		Map map = new HashMap();
//		Enumeration paramNames = request.getParameterNames();
//		while (paramNames.hasMoreElements()) {
//			String paramName = (String) paramNames.nextElement();
//			String[] paramValues = request.getParameterValues(paramName);
//			if (paramValues.length >= 1) {
//				String paramValue = paramValues[0];
//				if (paramValue.length() != 0) {
//					logger.info("request参数："+paramName+",值："+paramValue);
//					map.put(paramName, paramValue);
//				}
//
//			}
//		}
//		return map;
//	}

}
