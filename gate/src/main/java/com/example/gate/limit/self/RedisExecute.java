package com.example.gate.limit.self;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;

@Component
public class RedisExecute {

    
    @Autowired
    private StringRedisTemplate redisTemplate;

//	//////////////////////////  在UrlAndChannelRedisRateLimiter使用：  isAllowed 方法。。 注释掉this.redisTemplate.execute return...
//	Object resObj = redisExecute.executeScriptLua(script, keys.toArray(), scriptArgs.toArray());
//	System.out.println("executeScriptLua========="+resObj==null);
    
//    RedisExecute redisExecute;
    
//    this.redisExecute = context.getBean(RedisExecute.class);
//    log.info("setApplicationContext自定义限流配置：redisExecute=="+(redisExecute==null));
//	///////////////////////////
    
    public <K> Object executeScriptLua(RedisScript script, Object[] keys, Object[] args) {
    	
        return redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
            Object nativeConnection = redisConnection.getNativeConnection();
            Object resobj = null;
            ReturnType returnType = ReturnType.fromJavaType(script.getResultType());
            // lettuce连接包下 redis 单机模式
            if (nativeConnection instanceof RedisAsyncCommands) {
                RedisAsyncCommands connection = (RedisAsyncCommands) nativeConnection;
                
//                RedisCommands commands = connection.getStatefulConnection().sync();
//                resobj = commands.eval(script.getScriptAsString(), ScriptOutputType.MULTI, keys, args);
                
                
                resobj = connection.eval(script.getScriptAsString(), ScriptOutputType.MULTI, keys, args);
                if(resobj!=null) {
                	System.out.println("111=="+resobj.toString());
                }
                
            }
            
            
            // lettuce连接包下 redis 集群模式
            if (nativeConnection instanceof RedisAdvancedClusterAsyncCommands) {
                RedisAdvancedClusterAsyncCommands connection = (RedisAdvancedClusterAsyncCommands) nativeConnection;
                RedisAdvancedClusterCommands commands = connection.getStatefulConnection().sync();
                resobj = commands.eval(script.getScriptAsString(), ScriptOutputType.MULTI, keys, args);
                
                System.out.println("222=="+resobj==null);
            }

            return resobj;
        });
    }
}
