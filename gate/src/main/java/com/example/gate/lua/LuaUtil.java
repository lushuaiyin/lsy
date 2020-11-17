package com.example.gate.lua;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

/**
 * org.springframework.cloud.gateway.config.GatewayRedisAutoConfiguration
 * 
 * @author lsy
 *
 */
@Component
public class LuaUtil {
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	
	/**
	 * 执行lua字符串脚本。
	 * 
	 */
	public void evalLua() {
        try {
            DefaultRedisScript<Object> script = new DefaultRedisScript<>();
            script.setResultType(Object.class);
            script.setScriptText("return {ARGV[1]}");
            script.getSha1();
            List<String> keys = new ArrayList<>();
            keys.add("key1");
            keys.add("key2");
            Object execute = redisTemplate.execute(script, keys, 10L);
            System.out.println(execute);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
	
	/**
	 * 执行jar包中lua。
	 * 测试序列化器对lua的影响
	 * 
	 */
	public void evalLua2() {
        try {
        	DefaultRedisScript redisScript = new DefaultRedisScript<>();
    		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
    		redisScript.setResultType(List.class);
            
//    		redisScript.getSha1();
            List<String> keys = getKeys("luatest2");
            
//            long replenishRate=2L;
//            long burstCapacity=3L;
            int replenishRate=2;
            int burstCapacity=3;
            List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "",
        			Instant.now().getEpochSecond() + "", "1");
            
            
        	redisTemplate.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        	redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        	
        	redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        	redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        	
//        	redisTemplate.setKeySerializer(new StringRedisSerializer());
//        	redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        	
//        	redisTemplate.setValueSerializer(new StringRedisSerializer());
//        	redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        	
            Object resObj = redisTemplate.execute(redisScript, keys, scriptArgs);
            System.out.println(resObj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
	
	/**
	 * 执行本地  lua（改了脚本内容，传入map参数）
	 */
	public void evalLua3() {
        try {
//        	redisTemplate.setKeySerializer(new GenericJackson2JsonRedisSerializer());
//        	redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        	
        	/*Key,HashKey的序列化器无所谓，主要是Value，HashValue的序列化器要使用json的*/
        	redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        	redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        	
        	/*Key,HashKey的序列化器无所谓，主要是Value，HashValue的序列化器要使用json的*/
        	redisTemplate.setKeySerializer(new StringRedisSerializer());
        	redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        	
        	//StringRedisSerializer有问题，不能用
//        	redisTemplate.setValueSerializer(new StringRedisSerializer());
//        	redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        	
        	
        	DefaultRedisScript redisScript = new DefaultRedisScript<>();
    		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("request_rate_limiter2.lua")));
    		redisScript.setResultType(List.class);
            
            List<String> keys = getKeys("luatest3");
            /**
             * 用Mpa设置Lua的ARGV[1]
             */
            Map<String,Object> scriptArgs = new HashMap<String,Object>();
            scriptArgs.put("rate","2");
            scriptArgs.put("capacity","3");
            scriptArgs.put("now",Instant.now().getEpochSecond()+"");
            scriptArgs.put("requested","1");
            
            Object resObj = redisTemplate.execute(redisScript, keys, scriptArgs);
            System.out.println(resObj);
            if(resObj!=null) {
            	List reslist = (List)resObj;
            	System.out.println("reslist==="+reslist.toString());
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
	
	
}
