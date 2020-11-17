package com.example.one.config.redis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis多数据源配置类
 * 我用了3个数据源，分别是集群，单实例，哨兵。
 * 在工程中同时使用。如果你的数据源是多个，按照这里的例子，再配N套就行。
 * 
 * 第一个是集群，
 * 第二个是单实例
 * 第三个是哨兵。
 * 
 * @author lsy
 *
 */
//@Configuration
public class RedisConfig {

//	@Autowired
//	RedisConfigOne redisConfigOne;//集群的配置，读取yml文件
//	
//	@Autowired
//	RedisConfigTwo redisConfigTwo;//单实例的配置，读取yml文件
//	
//	@Autowired
//	RedisConfigThree redisConfigThree;//哨兵的配置，读取yml文件
//	
//	/************集群的配置--start*******************/
//	//读取pool配置
//	@Bean
//    public GenericObjectPoolConfig redisPool() {
//		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
//		config.setMinIdle(redisConfigOne.getMaxIdle());
//		config.setMaxIdle(redisConfigOne.getMaxIdle());
//		config.setMaxTotal(redisConfigOne.getMaxActive());
//		config.setMaxWaitMillis(redisConfigOne.getMaxWait());
//        return config;
//    }
//	@Bean
//    public RedisClusterConfiguration redisConfig() {//集群配置类
//		RedisClusterConfiguration redisConfig = new RedisClusterConfiguration(redisConfigOne.getNodes());
//		redisConfig.setMaxRedirects(redisConfigOne.getMaxRedirects());
//		redisConfig.setPassword(RedisPassword.of(redisConfigOne.getPassword()));
//        return redisConfig;
//    }
//	@Bean("factory")
//    @Primary
//    public LettuceConnectionFactory factory(@Qualifier("redisPool") GenericObjectPoolConfig config, 
//    		@Qualifier("redisConfig") RedisClusterConfiguration redisConfig) {//注意传入的对象名和类型RedisClusterConfiguration
//        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
//        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
//    }
//	
//	/**
//	 * 集群redis数据源
//	 * 
//	 * @param connectionFactory
//	 * @return
//	 */
//	@Bean("redisTemplate")
//    @Primary
//    public RedisTemplate<String, Object> redisTemplate(@Qualifier("factory")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//
//        //设置序列化器
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//        
//        redisTemplate.setKeySerializer(stringRedisSerializer);
//        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
//        redisTemplate.setHashKeySerializer(stringRedisSerializer);
//        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
//        redisTemplate.afterPropertiesSet();
//        
//        return redisTemplate;
//    }
//	/************集群的配置--end*******************/
//	
//	
//    /************单实例的配置--start*******************/
//	//读取pool配置
//	@Bean
//    public GenericObjectPoolConfig redisPool2() {
//		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
//		config.setMinIdle(redisConfigTwo.getMaxIdle());
//		config.setMaxIdle(redisConfigTwo.getMaxIdle());
//		config.setMaxTotal(redisConfigTwo.getMaxActive());
//		config.setMaxWaitMillis(redisConfigTwo.getMaxWait());
//        return config;
//    }
//	
//	@Bean
//    public RedisStandaloneConfiguration redisConfig2() {
//		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisConfigTwo.getHost(),redisConfigTwo.getPort());
//		redisConfig.setPassword(RedisPassword.of(redisConfigTwo.getPassword()));
//		return redisConfig;
//    }
//	
//	@Bean("factory2")
//    public LettuceConnectionFactory factory2(@Qualifier("redisPool2") GenericObjectPoolConfig config, 
//    		@Qualifier("redisConfig2") RedisStandaloneConfiguration redisConfig) {//注意传入的对象名和类型RedisStandaloneConfiguration
//        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
//        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
//    }
//	
//	
//	/**
//	 * 单实例redis数据源
//	 * 
//	 * @param connectionFactory
//	 * @return
//	 */
//	@Bean("redisTemplate2")
//    public RedisTemplate<String, Object> redisTemplate2(@Qualifier("factory2")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//
//        //设置序列化器
//        redisTemplate.setKeySerializer(stringRedisSerializer);
//        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
//        redisTemplate.setHashKeySerializer(stringRedisSerializer);
//        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
//        redisTemplate.afterPropertiesSet();
//        
//        return redisTemplate;
//    }
//	/************单实例的配置--end*******************/
//	
//	
//	
//    /************哨兵的配置--start*******************/
//	//读取pool配置
//	@Bean
//    public GenericObjectPoolConfig redisPool3() {
//		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
//		config.setMinIdle(redisConfigThree.getMaxIdle());
//		config.setMaxIdle(redisConfigThree.getMaxIdle());
//		config.setMaxTotal(redisConfigThree.getMaxActive());
//		config.setMaxWaitMillis(redisConfigThree.getMaxWait());
//        return config;
//    }
//	@Bean
//    public RedisSentinelConfiguration redisConfig3() {//哨兵的节点要写代码组装到配置对象中
//		RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
//		redisConfig.sentinel(redisConfigThree.getHost(), redisConfigThree.getPort());
//		redisConfig.setMaster(redisConfigThree.getMaster());
//		redisConfig.setPassword(RedisPassword.of(redisConfigThree.getPassword()));
//		if(redisConfigThree.getNodes()!=null) {
//			List<RedisNode> sentinelNode=new ArrayList<RedisNode>();
//			for(String sen : redisConfigThree.getNodes()) {
//				String[] arr = sen.split(":");
//				sentinelNode.add(new RedisNode(arr[0],Integer.parseInt(arr[1])));
//			}
//			redisConfig.setSentinels(sentinelNode);
//		}
//		return redisConfig;
//    }
//	@Bean("factory3")
//    public LettuceConnectionFactory factory3(@Qualifier("redisPool3") GenericObjectPoolConfig config, 
//    		@Qualifier("redisConfig3") RedisSentinelConfiguration redisConfig) {//注意传入的对象名和类型RedisSentinelConfiguration
//        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
//        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
//    }
//	
//	/**
//	 * 哨兵redis数据源
//	 * 
//	 * @param connectionFactory
//	 * @return
//	 */
//	@Bean("redisTemplate3")
//    public RedisTemplate<String, Object> redisTemplate3(@Qualifier("factory3")LettuceConnectionFactory connectionFactory) {//注意传入的对象名
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//
//        //设置序列化器
//        redisTemplate.setKeySerializer(stringRedisSerializer);
//        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
//        redisTemplate.setHashKeySerializer(stringRedisSerializer);
//        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
//        redisTemplate.afterPropertiesSet();
//        
//        return redisTemplate;
//    }
//	/************哨兵的配置--end*******************/
}
