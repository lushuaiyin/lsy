package com.example.gate.limit.self;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;
import javax.validation.constraints.Min;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

import reactor.core.publisher.Mono;

/**
 * See https://stripe.com/blog/rate-limiters and
 * https://gist.github.com/ptarjan/e38f45f2dfe601419ca3af937fff574d#file-1-check_request_rate_limiter-rb-L11-L34
 *
 * @author lsy
 * 
 * 实现按照业务逻辑自定义限流
 * 
 * 底层实现是在spring-cloud-gateway-core-2.0.1.RELEASE.jar中
 * \META-INF\scripts\request_rate_limiter.lua
 * 这是个redis的lua脚本，实现了令牌桶的限流算法。
 * 
 * 
 * 
 * springcloudgateway默认使用Lettuce做限流，执行lua脚本是用ReactiveRedisTemplate这个对象。
 * 但是换成jedis框架后，没有这个ReactiveRedisTemplate。
 * 所以要想办法把ReactiveRedisTemplate换成StringRedisTemplate或者RedisTemplate
 * 然后去执行lua脚本做限流。
 * 这里我改写了执行lua脚本的代码，不再用ReactiveRedisTemplate
 * 
 */
//@ConfigurationProperties("spring.cloud.gateway.redis-rate-limiter")
public class UrlAndChannelJedisRedisRateLimiter extends AbstractRateLimiter<UrlAndChannelJedisRedisRateLimiter.Config> implements ApplicationContextAware {
//	@Deprecated
	public static final String REPLENISH_RATE_KEY = "replenishRate";//修改
//	@Deprecated
	public static final String BURST_CAPACITY_KEY = "burstCapacity";//修改

	public static final String CONFIGURATION_PROPERTY_NAME = "url-channel-redis-rate-limiter";//修改
	public static final String REDIS_SCRIPT_NAME = "redisRequestRateLimiterScript";
	public static final String REMAINING_HEADER = "X-RateLimit-Remaining";
	public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";
	public static final String BURST_CAPACITY_HEADER = "X-RateLimit-Burst-Capacity";

	private Log log = LogFactory.getLog(getClass());

	//这个ReactiveRedisTemplate只有Lettuce支持，所以换成jedis后，就无法使用springcloudgateway的限流功能了。我们要想办法把ReactiveRedisTemplate换成StringRedisTemplate
//	private ReactiveRedisTemplate<String, String> redisTemplate;
	
	private StringRedisTemplate redisTemplate;
	
	private RedisScript<List<Long>> script;
	private AtomicBoolean initialized = new AtomicBoolean(false);
	private Config defaultConfig;

	// configuration properties
	/** Whether or not to include headers containing rate limiter information, defaults to true. */
	private boolean includeHeaders = true;

	/** The name of the header that returns number of remaining requests during the current second. */
	private String remainingHeader = REMAINING_HEADER;

	/** The name of the header that returns the replenish rate configuration. */
	private String replenishRateHeader = REPLENISH_RATE_HEADER;

	/** The name of the header that returns the burst capacity configuration. */
	private String burstCapacityHeader = BURST_CAPACITY_HEADER;

//	public UrlAndChannelJedisRedisRateLimiter(ReactiveRedisTemplate<String, String> redisTemplate,
//							RedisScript<List<Long>> script, Validator validator) {
//		super(Config.class, CONFIGURATION_PROPERTY_NAME, validator);
//		this.redisTemplate = redisTemplate;
//		this.script = script;
//		initialized.compareAndSet(false, true);
//	}
	
	//把ReactiveRedisTemplate换成StringRedisTemplate
	public UrlAndChannelJedisRedisRateLimiter(StringRedisTemplate redisTemplate,
			RedisScript<List<Long>> script, Validator validator) {
		super(Config.class, CONFIGURATION_PROPERTY_NAME, validator);
		this.redisTemplate = redisTemplate;
		this.script = script;
		initialized.compareAndSet(false, true);
	}

	public UrlAndChannelJedisRedisRateLimiter(int defaultReplenishRate, int defaultBurstCapacity) {
		super(Config.class, CONFIGURATION_PROPERTY_NAME, null);
		this.defaultConfig = new Config()
				.setReplenishRate(defaultReplenishRate)
				.setBurstCapacity(defaultBurstCapacity);
	}

	public boolean isIncludeHeaders() {
		return includeHeaders;
	}

	public void setIncludeHeaders(boolean includeHeaders) {
		this.includeHeaders = includeHeaders;
	}

	public String getRemainingHeader() {
		return remainingHeader;
	}

	public void setRemainingHeader(String remainingHeader) {
		this.remainingHeader = remainingHeader;
	}

	public String getReplenishRateHeader() {
		return replenishRateHeader;
	}

	public void setReplenishRateHeader(String replenishRateHeader) {
		this.replenishRateHeader = replenishRateHeader;
	}

	public String getBurstCapacityHeader() {
		return burstCapacityHeader;
	}

	public void setBurstCapacityHeader(String burstCapacityHeader) {
		this.burstCapacityHeader = burstCapacityHeader;
	}

	private RateLimiterConfig rateLimiterConfig;//自定义限流的配置
	
	
//	@Override
//	@SuppressWarnings("unchecked")
//	public void setApplicationContext(ApplicationContext context) throws BeansException {
//		log.info("setApplicationContext自定义限流配置：RateLimiterConf=="+(rateLimiterConfig==null));
//        this.rateLimiterConfig = context.getBean(RateLimiterConfig.class);
//        
//		if (initialized.compareAndSet(false, true)) {
//			this.redisTemplate = context.getBean("stringReactiveRedisTemplate", ReactiveRedisTemplate.class);
//			this.script = context.getBean(REDIS_SCRIPT_NAME, RedisScript.class);
//			if (context.getBeanNamesForType(Validator.class).length > 0) {
//				this.setValidator(context.getBean(Validator.class));
//			}
//		}
//	}
	
	//把ReactiveRedisTemplate换成StringRedisTemplate
	@Override
	@SuppressWarnings("unchecked")
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		log.info("setApplicationContext自定义限流配置：RateLimiterConf=="+(rateLimiterConfig==null));
        this.rateLimiterConfig = context.getBean(RateLimiterConfig.class);
        
		if (initialized.compareAndSet(false, true)) {
			this.redisTemplate = context.getBean("stringRedisTemplate", StringRedisTemplate.class);
			this.script = context.getBean(REDIS_SCRIPT_NAME, RedisScript.class);
			if (context.getBeanNamesForType(Validator.class).length > 0) {
				this.setValidator(context.getBean(Validator.class));
			}
		}
	}
	
	
	

	/* for testing */ Config getDefaultConfig() {
		return defaultConfig;
	}

	/**
	 * 获取自定义限流的配置
	 * 
	 * @param routeId
	 * @param id
	 * @return
	 */
	public Map<String,Integer> getConfigFromMap(String routeId, String id){
		Map<String,Integer> res=new HashMap<String,Integer>();
		res.put("replenishRate", -1);
		res.put("burstCapacity", -1);
		
		
		int replenishRate = -1;
		int burstCapacity = -1;
		
		/*********************自定义限流start**************************/
		//限流的key是参数id。我们要向实现根据key的不同自定义限流，那么就要在这里实现根据参数id获取我们的配置，包括replenishRate（限流速率），burstCapacity（令牌桶大小）
		//这个类实现的是按照“渠道”限流，这是个业务上规定的字段，我们在获取渠道的时候逻辑写在前面的filter，传到KeyResolver，这里就能拿到这个值了。
		//自定义限流要做的就是具体不同的key有不同的replenishRate（限流速率），burstCapacity（令牌桶大小），在这里做个配置映射就实现了。
//		log.info("ChannelRedisRateLimiter routeId["+routeId+"],id["+id+"]"+"的初始配置：replenishRate=="+replenishRate+",burstCapacity=="+burstCapacity);
		if(rateLimiterConfig==null) {
			log.error("RedisRateLimiter rateLimiterConfig is not initialized");
			throw new IllegalStateException("RedisRateLimiter rateLimiterConfig is not initialized");
		}
		Map<String , String> rateLimitConfigMap = rateLimiterConfig.getRateLimitUrlAndChannel();
		if(rateLimitConfigMap==null) {
			log.error("RedisRateLimiter rateLimitConfigMap is not initialized");
			throw new IllegalStateException("RedisRateLimiter rateLimitConfigMap is not initialized");
		}
		int speed = -1;
		int capacity = -1;
//		String configKey=routeId+RateLimiterConfig.SPILT_KEY+id;
		String configKey=id;//这里因为我们定义的key变了，所以也要改一下
		
		//这里要做一个判断，如果id为空或者为“default”，那么说明我们在限流key在request没有获取到，那就要按照默认的配置（yml中default配置）进行限流
		if(id==null || id.trim().equals("")|| id.trim().equals("default")) {
			configKey = "default";
			log.error("RedisRateLimiter id is null or default,use the default value for configKey...");
		}
		String configValue=(String)rateLimitConfigMap.get(configKey);
		String configDefaultValue=(String)rateLimitConfigMap.get("default");
		
		if(configValue==null || configValue.trim().equals("")) {
			log.error("RedisRateLimiter configValue is not match ,use the default value...configDefaultValue=="+configDefaultValue);
			if(configDefaultValue==null || configDefaultValue.trim().equals("")) {
				log.error("RedisRateLimiter is not match ,and the default value is also null");
				throw new IllegalStateException("RedisRateLimiter is not match ,and the default value is also null");
			}else {
				configValue = configDefaultValue;//获取不到匹配的配置，就获取默认配置
			}
		}
		String[] defValues = configValue.split(RateLimiterConfig.SPILT_SPEED);
		if(defValues == null || defValues.length<2) {//配置内容不合法
			log.error("RedisRateLimiter rateLimitConfigMap defValues is not initialized");
			throw new IllegalStateException("RedisRateLimiter rateLimitConfigMap defValues is not initialized");
		}
		
		try {
//		    one_channelA: 2@3
			speed = Integer.valueOf(defValues[0].trim());
			capacity = Integer.valueOf(defValues[1].trim());
			
		} catch (Exception e) {
			log.error(e.getMessage()+defValues[0]+","+defValues[1]);
			throw new IllegalStateException("RedisRateLimiter rateLimitConfigMap defValues is not valided!");
		}
		replenishRate = speed;
		burstCapacity = capacity;
		log.info("UrlAndChannelRedisRateLimiter routeId["+routeId+"],id["+id+"]"+"的匹配后的配置：replenishRate=="+replenishRate+",burstCapacity=="+burstCapacity);
		/*********************自定义限流end**************************/
		//防止因为yml配置不正确导致报错，我们可以再加一层判断
		if(replenishRate==-1 || burstCapacity==-1) {
			replenishRate=Integer.MAX_VALUE;
			burstCapacity=Integer.MAX_VALUE;
		}
		res.put("replenishRate", replenishRate);
		res.put("burstCapacity", burstCapacity);
		return res;
	}
	
	
//	/**
//	 * This uses a basic token bucket algorithm and relies on the fact that Redis scripts
//	 * execute atomically. No other operations can run between fetching the count and
//	 * writing the new count.
//	 */
//	@Override
//	@SuppressWarnings("unchecked")
//	public Mono<Response> isAllowed(String routeId, String id) {
//		if (!this.initialized.get()) {
//			throw new IllegalStateException("RedisRateLimiter is not initialized");
//		}
//
//		
//		//修改:注释掉
////		Config routeConfig = getConfig().getOrDefault(routeId, defaultConfig);
////
////		if (routeConfig == null) {
////			throw new IllegalArgumentException("No Configuration found for route " + routeId);
////		}
////
////		// How many requests per second do you want a user to be allowed to do?
////		int replenishRate = routeConfig.getReplenishRate();
////
////		// How much bursting do you want to allow?
////		int burstCapacity = routeConfig.getBurstCapacity();
//		
//		//修改：获取自定义限流参数映射关系
//		Map<String,Integer> configMap = this.getConfigFromMap(routeId, id);
//		int replenishRate = configMap.get("replenishRate");
//		int burstCapacity = configMap.get("burstCapacity");
//		if(replenishRate==-1 || burstCapacity==-1) {
//			log.error("RedisRateLimiter rateLimiterConfig values is not valid...");
//			throw new IllegalStateException("RedisRateLimiter rateLimiterConfig values is not valid...");
//		}
//		
//		
//		try {
//			List<String> keys = getKeys(id);
//
//
//			// The arguments to the LUA script. time() returns unixtime in seconds.
//			List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "",
//					Instant.now().getEpochSecond() + "", "1");
//			
//			
//			//此方案在redis单实例和集群情况下都测试通过，没有问题。
//			
//			// allowed, tokens_left = redis.eval(SCRIPT, keys, args)
//			Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys, scriptArgs);
//					// .log("redisratelimiter", Level.FINER);
//			return flux.onErrorResume(throwable -> Flux.just(Arrays.asList(1L, -1L)))
//					.reduce(new ArrayList<Long>(), (longs, l) -> {
//						longs.addAll(l);
//						return longs;
//					}) .map(results -> {
//						boolean allowed = results.get(0) == 1L;
//						Long tokensLeft = results.get(1);
//						
////						Response response = new Response(allowed, getHeaders(routeConfig, tokensLeft));//修改:注释掉
//						RateLimiter.Response response = new RateLimiter.Response(allowed, getHeaders(replenishRate , burstCapacity , tokensLeft));//修改
//						
//						if (log.isDebugEnabled()) {
//							log.debug("response: " + response);
//						}
//						return response;
//					});
//		}
//		catch (Exception e) {
//			/*
//			 * We don't want a hard dependency on Redis to allow traffic. Make sure to set
//			 * an alert so you know if this is happening too much. Stripe's observed
//			 * failure rate is 0.01%.
//			 */
//			log.error("Error determining if user allowed from redis", e);
//		}
////		return Mono.just(new Response(true, getHeaders(routeConfig, -1L)));//修改:注释掉
//		return Mono.just(new RateLimiter.Response(true, getHeaders(replenishRate , burstCapacity , -1L)));//修改
//	}
	
	
	@Resource(name="selfRateLimitRedisScript")
    private DefaultRedisScript<List> redisScript;
	
	
	//把ReactiveRedisTemplate换成StringRedisTemplate
	@Override
	@SuppressWarnings("unchecked")
	public Mono<Response> isAllowed(String routeId, String id) {
		if (!this.initialized.get()) {
			throw new IllegalStateException("RedisRateLimiter is not initialized");
		}
		
		//修改：获取自定义限流参数映射关系
		Map<String,Integer> configMap = this.getConfigFromMap(routeId, id);
		int replenishRate = configMap.get("replenishRate");
		int burstCapacity = configMap.get("burstCapacity");
		if(replenishRate==-1 || burstCapacity==-1) {
			log.error("RedisRateLimiter rateLimiterConfig values is not valid...");
			throw new IllegalStateException("RedisRateLimiter rateLimiterConfig values is not valid...");
		}
		
		
		try {
			List<String> keys = getKeys(id);
			// The arguments to the LUA script. time() returns unixtime in seconds.
			
			List scriptResult = redisTemplate.execute(redisScript, keys, 
		    		replenishRate + "", burstCapacity + "",
					Instant.now().getEpochSecond() + "", "1");
			
			boolean allowed=true;//默认通过
			Long allowedFlag=1L;//默认通过
			Long tokensLeft=9999999999L;//默认剩余的令牌桶数量（尽量大些）
			//判断限流结果
		    if(scriptResult!=null&&scriptResult.size()==2) {
		    	allowedFlag=(Long) scriptResult.get(0);
		    	tokensLeft = (Long) scriptResult.get(1);
		    	if(allowedFlag!=null && allowedFlag==1L) {
		    		log.info("限流允许通过.allowedFlag="+allowedFlag+",tokensLeft="+tokensLeft);
		    		allowed = true;
		    	}else {
		    		log.info("限流脚本拦截请求.allowedFlag="+allowedFlag+",tokensLeft="+tokensLeft);
		    		allowed =  false;
		    	}
		    }else {
		    	log.info("限流脚本为获取到结果或结果size不正确，默认允许通过.scriptResult=="+scriptResult);
		    	allowed =  false;
		    }
		    RateLimiter.Response response = new RateLimiter.Response(allowed, getHeaders(replenishRate , burstCapacity , tokensLeft));//修改
		    if (log.isDebugEnabled()) {
				log.debug("response: " + response);
			}
		    return Mono.just(response);//返回response
		}
		catch (Exception e) {
			/*
			 * We don't want a hard dependency on Redis to allow traffic. Make sure to set
			 * an alert so you know if this is happening too much. Stripe's observed
			 * failure rate is 0.01%.
			 */
			log.error("Error determining if user allowed from redis", e);
		}
//		return Mono.just(new Response(true, getHeaders(routeConfig, -1L)));//修改:注释掉
		return Mono.just(new RateLimiter.Response(true, getHeaders(replenishRate , burstCapacity , -1L)));//修改
	}
	
	

	//修改:注释掉
//	@NotNull
//	public HashMap<String, String> getHeaders(Config config, Long tokensLeft) {
//		HashMap<String, String> headers = new HashMap<>();
//		headers.put(this.remainingHeader, tokensLeft.toString());
//		headers.put(this.replenishRateHeader, String.valueOf(config.getReplenishRate()));
//		headers.put(this.burstCapacityHeader, String.valueOf(config.getBurstCapacity()));
//		return headers;
//	}
	
	//修改
	public HashMap<String, String> getHeaders(Integer replenishRate, Integer burstCapacity , Long tokensLeft) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(this.remainingHeader, tokensLeft.toString());
        headers.put(this.replenishRateHeader, String.valueOf(replenishRate));
        headers.put(this.burstCapacityHeader, String.valueOf(burstCapacity));
        return headers;
    }

	static List<String> getKeys(String id) {
		// use `{}` around keys to use Redis Key hash tags
		// this allows for using redis cluster

		// Make a unique key per user.
//		String prefix = "request_rate_limiter.{" + id;
		String prefix = "request_url_channel_rate_limiter.{" + id;//修改

		// You need two Redis keys for Token Bucket.
		String tokenKey = prefix + "}.tokens";
		String timestampKey = prefix + "}.timestamp";
		return Arrays.asList(tokenKey, timestampKey);
	}

	@Validated
	public static class Config {
		@Min(1)
		private int replenishRate;

		@Min(1)
		private int burstCapacity = 1;

		public int getReplenishRate() {
			return replenishRate;
		}

		public Config setReplenishRate(int replenishRate) {
			this.replenishRate = replenishRate;
			return this;
		}

		public int getBurstCapacity() {
			return burstCapacity;
		}

		public Config setBurstCapacity(int burstCapacity) {
			this.burstCapacity = burstCapacity;
			return this;
		}

		@Override
		public String toString() {
			return "Config{" +
					"replenishRate=" + replenishRate +
					", burstCapacity=" + burstCapacity +
					'}';
		}
	}
}

