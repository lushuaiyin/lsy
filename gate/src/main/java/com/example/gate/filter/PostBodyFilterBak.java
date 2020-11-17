package com.example.gate.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * Ordered 负责filter的顺序，数字越小越优先，越靠前。
 * 
 * GatewayFilter： 需要通过spring.cloud.routes.filters 配置在具体路由下，
 * 只作用在当前路由上或通过spring.cloud.default-filters配置在全局，作用在所有路由上。
 * 需要用代码的形式，配置一个RouteLocator，里面写路由的配置信息。
 * 
 * GlobalFilter：
 * 全局过滤器，不需要在配置文件中配置，作用在所有的路由上，最终通过GatewayFilterAdapter包装成GatewayFilterChain可识别的过滤器，
 * 它为请求业务以及路由的URI转换为真实业务服务的请求地址的核心过滤器，不需要配置，系统初始化时加载，并作用在每个路由上。
 * 代码配置需要声明一个GlobalFilter对象。
 * 
 * 
 * 对一个应用来说，GatewayFilter和GlobalFilter是等价的，order也会按照顺序进行拦截。所以两个order不要写一样！
 * 
 * 
 * post请求的传参获取相对比较麻烦一些，springcloudgateway采用了webflux的方式来封装的请求体。
 * 我们知道post常用的两种传参content-type是application/x-www-form-urlencoded和application/json，这两种方式还是有区别的。
 * 
 * 解决post请求体获取不到和获取不正确的关键是：
 * ModifyRequestBodyGatewayFilterFactory
 * 
 * 修改响应体的关键 ModifyResponseBodyGatewayFilterFactory
 * 
 * 
 * @author lsy
 *
 */
public class PostBodyFilterBak implements GlobalFilter, Ordered {

	Logger logger = LoggerFactory.getLogger(PostBodyFilterBak.class);

	public static final String PostBodyData = "PostBodyData";
	public static final String PostBodyByteData = "PostBodyByteData";

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		logger.info("\r\n\r\n");
		logger.info("PostBodyFilter开始............");
//		URI urii = exchange.getRequest().getURI();
//		logger.info("PostBodyFilter开始...........getURI.URI==="+urii.toString());
//		
//		String getPath = exchange.getRequest().getURI().getPath();
//		logger.info("PostBodyFilter开始.........getURI...getPath==="+getPath);
//		
//		HttpMethod httpMethod = exchange.getRequest().getMethod();
//		logger.info("PostBodyFilter开始............HttpMethod==="+httpMethod.toString());
//		
//		String methodValue= exchange.getRequest().getMethodValue();
//		logger.info("PostBodyFilter开始............getMethodValue==="+methodValue);
//		
//		
//		RequestPath getPath2 =exchange.getRequest().getPath();
//		logger.info("PostBodyFilter开始............getPath2==="+getPath2.toString());
//		
//		InetSocketAddress inetSocketAddress =exchange.getRequest().getRemoteAddress();
//		logger.info("PostBodyFilter开始............getRemoteAddress==="+inetSocketAddress.toString());
//		
//		String getHostString = exchange.getRequest().getRemoteAddress().getHostString();
//		logger.info("PostBodyFilter开始............getRemoteAddress-getHostString==="+getHostString);
//		
//		String getHostName = exchange.getRequest().getRemoteAddress().getHostName();
//		logger.info("PostBodyFilter开始............getRemoteAddress-getHostName==="+getHostName);
		
		ServerRequest serverRequest = new DefaultServerRequest(exchange);
		// mediaType
		MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
		if(mediaType!=null) {
			logger.info("PostBodyFilter3.....getType==="+mediaType.getType());
		}
		
		// read & modify body
		Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
			logger.info("PostBodyFilter3.....原始length==="+body.length()+",内容==="+body);
			
			String method = exchange.getRequest().getMethodValue();
	        if ("POST".equals(method)) {
//	        	if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
//
//					// origin body map
//					Map<String, Object> bodyMap = decodeBody(body);
//
//					//TODO
//
//					// new body map
//					Map<String, Object> newBodyMap = new HashMap<>();
//					return Mono.just(encodeBody(newBodyMap));
//				}
				//这里对application/json;charset=UTF-8的数据进行截获。
				if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)
						|| MediaType.APPLICATION_JSON_UTF8.isCompatibleWith(mediaType)) {
					String newBody;
					try {
						newBody = body;//可以修改请求体
					} catch (Exception e) {
						return processError(e.getMessage());
					}
					logger.info("PostBodyFilter3.....newBody长度==="+newBody.length()+",newBody内容====\r\n"+newBody);
					exchange.getAttributes().put(PostBodyData, newBody);//为了向后传递，放入exchange.getAttributes()中，后面直接取
					
					return Mono.just(newBody);
				}
	        }
	        
			logger.info("PostBodyFilter3.....empty or just haha===");
//			return Mono.empty();
			return Mono.just(body);

		});
		
		BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
		HttpHeaders headers = new HttpHeaders();
		headers.putAll(exchange.getRequest().getHeaders());
		
		// the new content type will be computed by bodyInserter
		// and then set in the request decorator
		headers.remove(HttpHeaders.CONTENT_LENGTH);

		CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
		return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
			
			ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(exchange.getRequest()) {

				public HttpHeaders getHeaders() {
					long contentLength = headers.getContentLength();
					HttpHeaders httpHeaders = new HttpHeaders();
					httpHeaders.putAll(super.getHeaders());
					if (contentLength > 0) {
						httpHeaders.setContentLength(contentLength);
					} else {
						httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
					}
					return httpHeaders;
				}

				public Flux<DataBuffer> getBody() {
					return outputMessage.getBody();
				}
			};
			
			return chain.filter(exchange.mutate().request(decorator).build());
		}));
	}

	private Map<String, Object> decodeBody(String body) {
		return Arrays.stream(body.split("&")).map(s -> s.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
	}

	private String encodeBody(Map<String, Object> map) {
		return map.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
	}
	
	private Mono processError(String message) {
		/*
		 * exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); return
		 * exchange.getResponse().setComplete();
		 */
		logger.error(message);
		return Mono.error(new Exception(message));
	}
	

}
