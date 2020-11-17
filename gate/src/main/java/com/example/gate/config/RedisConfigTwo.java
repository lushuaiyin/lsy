package com.example.gate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 映射redis的单实例配置配置
 * yml:

   #redis单实例。单实例比较简单，ip，端口，密码，连接池就ok
spring: 
  redis2:
    timeout: 6000ms
    database: 0 
    host: localhost #单实例redis用这个配置
    password: #单实例redis用这个配置
    port: 6379 #单实例redis用这个配置
    lettuce:
      pool: 
        max-active: 1000 #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 #连接池中的最大空闲连接
        min-idle: 3 #连接池中的最小空闲连接
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）
 
 * @author lsy
 *
 */
//@Data
@Configuration
@ConfigurationProperties(prefix="spring.redis2.cluster")
public class RedisConfigTwo {


    @Value("${spring.redis2.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis2.port:6379}")
    private int port;
    @Value("${spring.redis2.password:redis123}")
    private String password;
    @Value("${spring.redis2.timeout:6000}")
    private int timeout;
    @Value("${spring.redis2.database:0}")
    private int database;
    
    //pool映射
    @Value("${spring.redis2.lettuce.pool.max-active:5000}")
    private int maxActive;
    @Value("${spring.redis2.lettuce.pool.max-idle:100}")
    private int maxIdle;
    @Value("${spring.redis2.lettuce.pool.min-idle:5}")
    private int minIdle;
    @Value("${spring.redis2.lettuce.pool.max-wait:5000}")
    private long maxWait;
    
    
    
    
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getDatabase() {
		return database;
	}
	public void setDatabase(int database) {
		this.database = database;
	}
	public int getMaxActive() {
		return maxActive;
	}
	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	public int getMaxIdle() {
		return maxIdle;
	}
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}
	public int getMinIdle() {
		return minIdle;
	}
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}
	public long getMaxWait() {
		return maxWait;
	}
	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}
    
 
    
}
