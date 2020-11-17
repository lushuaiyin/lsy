package com.example.gate.config;

//import java.util.ArrayList;
//import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;


/**
 * jedis多数据源配置redis
 * 
 * @author lsy
 *
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfigGateway {
	
	//redisConnectionFactory配置信息由yml控制
	
//	@Bean
//	@ConditionalOnMissingBean
//	public RedisTemplate<String, Object> redisTemplate (LettuceConnectionFactory redisConnectionFactory) {
//		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
//		//配置序列化器
//		template.setKeySerializer(new StringRedisSerializer());
//		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//		template.setHashKeySerializer(new StringRedisSerializer());
//		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//		template.setConnectionFactory(redisConnectionFactory);
//		
////		template.afterPropertiesSet();//断言，判断redisConnectionFactory是否不为空,实现InitializingBean接口会自动执行，所以这里不用写这行代码。
//		return template;
//	}
	
	//从Lettuce改成jedis后，多数据源的配置样例如下-------------------------------------------------------
	
//	@Autowired
//	RedisConfigOne redisConfigOne;//集群的配置，读取yml文件    //本地没环境，没做实际测试。在其他环境写类似的代码做过了测试
	
	@Autowired
	RedisConfigTwo redisConfigTwo;//单实例的配置，读取yml文件
	
	
//	@Autowired
//	RedisConfigThree redisConfigThree;//哨兵的配置，读取yml文件  //本地没环境，没做实际测试。在其他环境写类似的代码做过了测试
	
	/************集群的配置--start*******************/
	//读取pool配置
	
//	@Bean
//    public JedisPoolConfig jedisPool() {
//		JedisPoolConfig config = new JedisPoolConfig();
//		config.setMinIdle(redisConfigOne.getMaxIdle());
//		config.setMaxIdle(redisConfigOne.getMaxIdle());
//		config.setMaxTotal(redisConfigOne.getMaxActive());
//		config.setMaxWaitMillis(redisConfigOne.getMaxWait());
//        return config;
//    }
//	
//	@Bean
//    public RedisClusterConfiguration redisConfig1() {//集群配置类
//		RedisClusterConfiguration redisConfig = new RedisClusterConfiguration(redisConfigOne.getNodes());
//		redisConfig.setMaxRedirects(redisConfigOne.getMaxRedirects());
//		redisConfig.setPassword(RedisPassword.of(redisConfigOne.getPassword()));
//        return redisConfig;
//    }
//	@Bean("factory")
//    @Primary
//    public JedisConnectionFactory factory() {//注意传入的对象名和类型RedisClusterConfiguration
//        JedisConnectionFactory factory= new JedisConnectionFactory(redisConfig1(), jedisPool());
//        factory.setTimeout(redisConfigOne.getTimeout());
//        return factory;
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
//    public RedisTemplate<String, Object> redisTemplate() {//注意传入的对象名
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(factory());
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
	
	
	
  /************单实例的配置--start*******************/
	//读取pool配置
	@Bean
  public GenericObjectPoolConfig redisPool() {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(redisConfigTwo.getMaxIdle());
		config.setMaxIdle(redisConfigTwo.getMaxIdle());
		config.setMaxTotal(redisConfigTwo.getMaxActive());
		config.setMaxWaitMillis(redisConfigTwo.getMaxWait());
      return config;
  }
	
	@Bean
    public JedisPoolConfig jedisPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMinIdle(redisConfigTwo.getMaxIdle());
		config.setMaxIdle(redisConfigTwo.getMaxIdle());
		config.setMaxTotal(redisConfigTwo.getMaxActive());
		config.setMaxWaitMillis(redisConfigTwo.getMaxWait());
        return config;
    }
	
	@Bean
  public RedisStandaloneConfiguration redisConfig1() {
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisConfigTwo.getHost(),redisConfigTwo.getPort());
		redisConfig.setPassword(RedisPassword.of(redisConfigTwo.getPassword()));
		return redisConfig;
  }
	
	@Bean("factory")
  public JedisConnectionFactory factory() {//注意传入的对象名和类型RedisStandaloneConfiguration
		JedisConnectionFactory factory= new JedisConnectionFactory(redisConfig1());
		factory.setPoolConfig(jedisPool());
        factory.setTimeout(redisConfigTwo.getTimeout());
        
        return factory;
  }
	
	
	/**
	 * 单实例redis数据源
	 * 
	 * @param connectionFactory
	 * @return
	 */
	@Bean("redisTemplate")
  public RedisTemplate<String, Object> redisTemplate() {//注意传入的对象名
      RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
      redisTemplate.setConnectionFactory(factory());

      StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
      GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();

      //设置序列化器
      redisTemplate.setKeySerializer(stringRedisSerializer);
      redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
      redisTemplate.setHashKeySerializer(stringRedisSerializer);
      redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
      redisTemplate.afterPropertiesSet();
      
      return redisTemplate;
  }
	/************单实例的配置--end*******************/
	
	
	//本地没环境，没做实际测试。在其他环境写类似的代码做过了测试
//  /************哨兵的配置--start*******************/
//	//读取pool配置
//	@Bean
//    public JedisPoolConfig jedisPool3() {
//		JedisPoolConfig config = new JedisPoolConfig();
//		config.setMinIdle(redisConfigThree.getMaxIdle());
//		config.setMaxIdle(redisConfigThree.getMaxIdle());
//		config.setMaxTotal(redisConfigThree.getMaxActive());
//		config.setMaxWaitMillis(redisConfigThree.getMaxWait());
//        return config;
//    }
//	@Bean
//  public RedisSentinelConfiguration redisConfig3() {//哨兵的节点要写代码组装到配置对象中
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
//  }
//	@Bean("factory3")
//  public JedisConnectionFactory factory3() {//注意传入的对象名和类型RedisSentinelConfiguration
//		JedisConnectionFactory factory= new JedisConnectionFactory(redisConfig3());
//		factory.setPoolConfig(jedisPool3());
//        factory.setTimeout(redisConfigThree.getTimeout());
//        return factory;
//  }
//	
//	/**
//	 * 哨兵redis数据源
//	 * 
//	 * @param connectionFactory
//	 * @return
//	 */
//	@Bean("redisTemplate3")
//  public RedisTemplate<String, Object> redisTemplate3() {//注意传入的对象名
//      RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//      redisTemplate.setConnectionFactory(factory3());
//
//      StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//      GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//
//      //设置序列化器
//      redisTemplate.setKeySerializer(stringRedisSerializer);
//      redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
//      redisTemplate.setHashKeySerializer(stringRedisSerializer);
//      redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
//      redisTemplate.afterPropertiesSet();
//      
//      return redisTemplate;
//  }
//	/************哨兵的配置--end*******************/
}
