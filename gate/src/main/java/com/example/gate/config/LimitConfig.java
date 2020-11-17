package com.example.gate.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.validation.Validator;

import com.example.gate.limit.self.ChannelRedisRateLimiter;
import com.example.gate.limit.self.IPRedisRateLimiter;
import com.example.gate.limit.self.UrlAndChannelJedisRedisRateLimiter;
import com.example.gate.limit.self.UrlAndChannelRedisRateLimiter;

import reactor.core.publisher.Mono;

/**
 * 限流的配置
 * 
 * 
 *RequestRateLimiter 即
 *RequestRateLimiterGatewayFilterFactory
 *关键类 RedisRateLimiter
 *
 *他的配置需要
 *KeyResolver  （限流的key，或者说限流的维度，按什么限流）
 *RateLimiter  （限流的2个参数：（replenishRate=50，burstCapacity=100） ）
 *
 *
 *org.springframework.cloud.gateway.config.GatewayAutoConfiguration
 *
 *
 *org.springframework.cloud.gateway.config.GatewayRedisAutoConfiguration
 *@Bean
	@ConditionalOnMissingBean
	public RedisRateLimiter redisRateLimiter
	
	
 *HTTP 429 - Too Many Requests
 *
 *
 *RequestRateLimiterGatewayFilterFactory；
KeyResolver：PrincipalNameKeyResolver；
RateLimiter：RedisRateLimiter；
RedisScript ：META-INF/scripts/request_rate_limiter.lua
ReferenceCountUtil
 * @author lsy
 *
 */
@Configuration
public class LimitConfig {

	Logger logger = LoggerFactory.getLogger(LimitConfig.class);

	
	@Bean
	public DefaultRedisScript selfRateLimitRedisScript() {
		DefaultRedisScript<List> redisScript = new DefaultRedisScript<List>();
    	redisScript.setResultType(List.class);
    	redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("request_rate_limiter.lua")));
    	
    	return redisScript;
	}
	
	
	//---------渠道自定义限流
	@Bean
	public KeyResolver selfChannelKeyResolver() {//限流维度：header中的channel，由前面的filter处理
		//这里注意KeyResolver的返回值不能为空。为空的话，请求发不通。所以这里做了空判断，保证返回一个字符串。
		return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("channel")==null?"default":exchange.getRequest().getHeaders().getFirst("channel"));
	}

//	@Bean
////	@Primary
//	ChannelRedisRateLimiter channelRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
//			@Qualifier(ChannelRedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> script, Validator validator) {
//		return new ChannelRedisRateLimiter(redisTemplate, script, validator);// 使用自己定义的限流类
//	}
	
	
	//-----------IP自定义限流
	@Bean
	public KeyResolver selfIPKeyResolver() {//限流维度：header中的IP-Address，由前面的filter处理
		//这里注意KeyResolver的返回值不能为空。为空的话，请求发不通。所以这里做了空判断，保证返回一个字符串。
		return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("IP-Address")==null?"default":exchange.getRequest().getHeaders().getFirst("IP-Address"));
	}

//	@Bean(name="iPRedisRateLimiter")
//	// 使用自己定义的限流类
//	IPRedisRateLimiter iPRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
//			@Qualifier(IPRedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> script, Validator validator) {
//		return new IPRedisRateLimiter(redisTemplate, script, validator);
//	}
	
	
	
	//---------稍微复杂一点的按照url加渠道进行自定义限流
	@Bean
	public KeyResolver selfUrlAndChannelKeyResolver() {//限流维度：header中的urlandchannel，由前面的filter处理
		//这里注意KeyResolver的返回值不能为空。为空的话，请求发不通。所以这里做了空判断，保证返回一个字符串。
		return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("urlandchannel")==null?"default":exchange.getRequest().getHeaders().getFirst("urlandchannel"));
	}

//	@Bean(name="urlAndChannelRedisRateLimiter")
//	@Primary
//	UrlAndChannelRedisRateLimiter urlAndChannelRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
//			@Qualifier(UrlAndChannelRedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> script, Validator validator) {
//		return new UrlAndChannelRedisRateLimiter(redisTemplate, script, validator);// 使用自己定义的限流类
//	}
	
//	@Bean(name="urlAndChannelJedisRedisRateLimiter")
//	@Primary
//	UrlAndChannelJedisRedisRateLimiter urlAndChannelJedisRedisRateLimiter(StringRedisTemplate redisTemplate,
//			@Qualifier(UrlAndChannelJedisRedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> script, Validator validator) {
//		return new UrlAndChannelJedisRedisRateLimiter(redisTemplate, script, validator);// 使用自己定义的限流类
//	}
	
   //////////////////////////////////////////////////////////////////////////////////
   
   
   
   
//	@Bean
//	public UrlRequestRateLimiterGatewayFilterFactory urlRequestRateLimiterGatewayFilterFactory() {
//		return new UrlRequestRateLimiterGatewayFilterFactory(myRateLimiter(),uriKeyResolverMy());
//	}
	
	
//	@ConditionalOnMissingBean
//	@Primary
//	@Bean
//	public KeyResolver principalNameKeyResolver() {
//		return new PrincipalNameKeyResolver();
//	}
	
//	@Bean
//    public RedisRateLimiter myRateLimiter() {
//        return new RedisRateLimiter(2, 3);
//    }
//	
//	@Bean
//    public Map<String,String> limitmap() {
//		Map<String,String> hm=new HashMap<String,String>();
//		hm.put("/hello", "1,5");
//		hm.put("/hello2", "2,6");
//		
//        return hm;
//    }
	
	/**
	 * 根据uri限流
	 * 
	 * /one/hello
	 * 
	 * @return
	 */
//	@Primary
//	@Bean
//	public KeyResolver uriKeyResolverMy() {
//		return exchange -> Mono.just(exchange.getRequest().getURI().getPath());
//	}
	
	
	
//	/**
//	 * 根据uri限流的另一种写法。
//	 * 写一个类UriKeyResolver继承接口KeyResolver
//	 * 
//	@Override
//	public Mono<String> resolve(ServerWebExchange exchange) {
//		return Mono.just(exchange.getRequest().getURI().getPath());
//	}
//	 * @return
//	 */
//	@Bean
//	public UriKeyResolver uriKeyResolver() {
//		return new UriKeyResolver();
//	}
	
	
	
	
	

	
	/**
	 * 根据host限流
	 * 
	 * 0:0:0:0:0:0:0:1
	 * 
	 * @return
	 */
//	@Bean
//	public KeyResolver hostAddrKeyResolver2() {
//		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
//	}
	
	/**
	 * 根据IP限流
	 * 
	 * 0:0:0:0:0:0:0:1
	 * 
	 * @return
	 */
//	@Bean
//	public KeyResolver ipKeyResolver2() {
//		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostString());
//	}
	
//	/**
//	 * 针对来源IP的限流
//	 * 
//	 * @return
//	 */
//	@Bean
//	public KeyResolver ipKeyResolver() {
//		return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst("X-Forwarded-For")==null?"default":exchange.getRequest().getHeaders().getFirst("X-Forwarded-For"));
//	}
	
	

	/**
	 * 按照Path限流
	 *
	 *DefaultRequestPath[fullPath='[path='/one/hello']', contextPath='', pathWithinApplication='/one/hello']
	 *
	 * @return key
	 */
//	@Bean
//	public KeyResolver pathKeyResolver() {
//		return exchange -> Mono.just(exchange.getRequest().getPath().toString());
//	}

	/**
	 * 根据请求参数进行限流
	 * 
	 * 这里可以改成具体的业务逻辑。比如，根据应用来限流，那么这里你只要获取到应用名就可以了。
	 * 如果要根据模块限流，那么这里就写怎么获取你的模块的名字。
	 * 
	 * 这些获取必须都是从请求里获取，因为归根到底，限流是基于拦截器的。
	 * 再具体点就是拦截器中截获header。你定义的这些key是写在header中的。
	 * 同一个key就是限流的一个维度。比如，按url，IP，参数，应用（名称），自定义等等。
	 * 一个key就是一个维度。
	 * 
	 * 但是在一个应用里怎么多维度进行限流，还没搞懂。
	 * 多维度是指，我既按照IP限流（replenishRate=100，burstCapacity=200）
	 * 又要按照应用限流（replenishRate=50，burstCapacity=100）
	 * 
	 * 注意，多维度不能有交集。
	 * 
	 * 一个请求过来要按照哪种方式限流只能选一个。如果多个限流都起作用，那就很复杂了额。
	 * 
	 * 
	 * 
	 * @return
	 */
//	@Primary
//	@Bean
//	public KeyResolver tokenKeyResolver() {
//		System.out.println("限流。。。。。tokenKeyResolver。。。。");
//		//这里注意KeyResolver的返回值不能为空。为空的话，请求发不通。所以这里做了空判断，保证返回一个字符串。
//		return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("token")==null?"default":exchange.getRequest().getQueryParams().getFirst("token"));
//	}

}
