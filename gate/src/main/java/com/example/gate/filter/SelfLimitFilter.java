package com.example.gate.filter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * RequestRateLimiterGatewayFilterFactory
 * RedisRateLimiter
 * GatewayRedisAutoConfiguration
 * GatewayAutoConfiguration
 * 
 * @author lsy
 *
 */
public class SelfLimitFilter implements GlobalFilter, Ordered {

	Logger logger = LoggerFactory.getLogger(SelfLimitFilter.class);


	@Override
	public int getOrder() {
		return 6;
	}
	
//	@Autowired     
//	private RedisTemplate redisTemplate;
	
	@Autowired
    private RedisTemplate<String,Object> redisTemplate;
 
	@Autowired
    private StringRedisTemplate stringRedisTemplate;//lua脚本执行必须要用string序列化器，否则传参到脚本中会有问题
	
	@Resource(name="selfRateLimitRedisScript")
    private DefaultRedisScript<List> redisScript;
    
    @PostConstruct
    public void init(){
//    	redisScript = new DefaultRedisScript<List>();
//    	redisScript.setResultType(List.class);
//    	redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("request_rate_limiter.lua")));
    	
//    	redisScript.setLocation(new ClassPathResource("test.lua"));//放在和application.yml 同层目录下
    }
	
    public static List<String> getKeys(String id) {
		// use `{}` around keys to use Redis Key hash tags
		// this allows for using redis cluster
	
		// Make a unique key per user.
		String prefix = "request_rate_limiter.{" + id;
	
		// You need two Redis keys for Token Bucket.
		String tokenKey = prefix + "}.tokens";
		String timestampKey = prefix + "}.timestamp";
		return Arrays.asList(tokenKey, timestampKey);
	}
    
    /**
     * 限流是否通过
     */
	public boolean isAllowed() {
	    int replenishRate = 1;
		int burstCapacity = 2;
		String id="trans";
	    List<String> keys = getKeys(id);
//	    //即
//	    List<String> keys = new ArrayList();
//        keys.add("request_rate_limiter.{" + id+"}.tokens");
//        keys.add("request_rate_limiter.{" + id+"}.timestamp");
        
	    List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "",
				Instant.now().getEpochSecond() + "", "1");
	    
	    //即
//	    List<String> scriptArgs = new ArrayList();
//        scriptArgs.add(replenishRate + "");
//        scriptArgs.add(burstCapacity + "");
//        scriptArgs.add(Instant.now().getEpochSecond() + "");
//        scriptArgs.add("1");
	    try {
		    List scriptResult = stringRedisTemplate.execute(redisScript, keys, 
		    		replenishRate + "", burstCapacity + "",
					Instant.now().getEpochSecond() + "", "1");
		    System.out.println("执行结果："+scriptResult);
	//	    ArrayList<Long>() res = redisTemplate.execute(redisScript, keys, scriptArgs);
		    
//		    scriptResult中返回2个数字。第一个是1则代表限流通过。第二个代表剩余的令牌桶个数
//		    boolean allowed = scriptResult.get(0) == 1L;
//		    Long tokensLeft = results.get(1);
		    //判断限流结果
		    if(scriptResult!=null&&scriptResult.size()==2) {
		    	Long allowedFlag=(Long) scriptResult.get(0);
		    	Long tokensLeft = (Long) scriptResult.get(1);
		    	if(allowedFlag!=null && allowedFlag==1L) {
		    		logger.info("限流允许通过.allowedFlag="+allowedFlag+",tokensLeft="+tokensLeft);
					return true;
		    	}else {
		    		logger.info("限流脚本拦截请求.allowedFlag="+allowedFlag+",tokensLeft="+tokensLeft);
		    		return false;
		    	}
		    }else {
		    	return false;
		    }
		    
	    } catch (Exception e) {
			e.printStackTrace();
			logger.info("isAllowed异常............此情况允许请求通过，不限流",e);
			return true;
		}
	    
	}

	

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		try {
			
			if(isAllowed()){
				logger.info("SelfLimitFilter限流通过。");
				return chain.filter(exchange);
			}else{
				logger.info("被限流。。。。limited by SelfLimitFilter!!!!!!!");
				//下面可以设置返回体的具体内容
				//设置status和body
		        return Mono.defer(() -> {
		            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);//设置status 401
//		            exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=utf-8");
		            exchange.getResponse().getHeaders().add("Content-Type", "text/html;charset=utf-8");
		            final ServerHttpResponse response = exchange.getResponse();
		            String failMess=this.getFailAuthorizedMessage("GATE901","limited by SelfLimitFilter!!!!!!!");
		            byte[] bytes = failMess.getBytes(StandardCharsets.UTF_8);
		            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
		            response.getHeaders().set("urlValid", "false");//设置header
		            return response.writeWith(Flux.just(buffer));//设置body
		        });
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("SelfLimitFilter异常............",e);
			return chain.filter(exchange);
		}
	}

	
	//组装返回体
		private String getFailAuthorizedMessage(String retCode,String param) {
			param=param==null?"":param;
			retCode=retCode==null?"GATE999":retCode;
			String message= "cebcc gateway rejected the request! [by UrlAuthCheckFilter], reason: "+param;
			String cebcc_message_format=""
					+ "{"+
					    "\"data\": {"+
//					       " \"xxxx\": \"\","+
//					        "\"xxxxxx\": \"\""+
					   " },"+
					   " \"header\": {"+
					       " \"greenMsg\": [],"+
					       " \"retCode\": \""+retCode+"\","+
					       " \"retMsg\": \"%s\""+
					   " }"+
					"}";
			return String.format(cebcc_message_format,message);
		}

}
