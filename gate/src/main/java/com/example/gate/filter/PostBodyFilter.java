package com.example.gate.filter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

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
 * 
 * 
 * 这个filter主要目的就是处理 post请求且Content-Type是application/json 的请求，从中获取请求体的json。
 * 这个json后面的filter需要用。
 * 除此之外，其他类型的请求不做任何处理，直接放行给后面。
 * 
 * 
 * @author lsy
 *
 */
public class PostBodyFilter implements GlobalFilter, Ordered {

	Logger logger = LoggerFactory.getLogger(PostBodyFilter.class);

	public static final String PostBodyData = "PostBodyData";
	public static final String PostBodyByteData = "PostBodyByteData";

	@Override
	public int getOrder() {
		return 0;
	}
	
//	@Autowired     
//	private RedisTemplate redisTemplate;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		try {
			
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
			
			// mediaType先判断请求类型和媒体类型，我们只对post请求且Content-Type是application/json的请求做特殊处理，请他的都放行
			MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
			if("POST".equals(exchange.getRequest().getMethodValue()) && mediaType==null) {//post请求mediaType不能为空.非法的请求，直接返回错误提示给前端
				//设置status和body
			    return Mono.defer(() -> {
			        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);//设置status
			        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");//设置返回类型
			        final ServerHttpResponse response = exchange.getResponse();
			        byte[] bytes = "{\"code\":\"99999\",\"message\":\"非法访问,Content-Type不合法~~~~~~\"}".getBytes(StandardCharsets.UTF_8);
			        
			        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
			        response.getHeaders().set("aaa", "bbb");//设置header
			        logger.info("PostBodyFilter3拦截非法请求，没有检测到token............");
			        
			        return response.writeWith(Flux.just(buffer));//设置body
			    });
			}
			if(mediaType!=null && mediaType.getType()!=null) {
				logger.info("PostBodyFilter3.....getType==="+mediaType.getType());
			}
			
			if ("POST".equals(exchange.getRequest().getMethodValue()) &&
					(MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)
							|| MediaType.APPLICATION_JSON_UTF8.isCompatibleWith(mediaType))) {
				
				//post请求且Content-Type是application/json的继续走下面的modifiedBody（修改请求体）
			}else {//其他请求例如post请求（Content-Type不是application/json），input,get,delete请求，不做特殊处理，直接放行。让后面的filter拿到原始数据
				
				logger.info("PostBodyFilter3.....非post + [application/json]请求，不做特殊处理，直接放行~~~~~~");
				return chain.filter(exchange);//请求放行
			}
			
			
			// 修改请求体 read & modify body
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
		} catch (Exception e) {
			e.printStackTrace();
			return chain.filter(exchange);
		}
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
