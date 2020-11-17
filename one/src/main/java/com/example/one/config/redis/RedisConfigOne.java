package com.example.one.config.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 映射redis的集群配置
 * yml:

  #默认用集群配置，3主3从共6节点的集群
spring: 
  redis:
    timeout: 6000ms
    database: 10 
    port: 6379 #单实例redis用这个配置
    password: Redis@123
    cluster: #集群用这个配置
      nodes:
        - 127.0.0.1:7011
        - 127.0.0.1:7012
        - 127.0.0.1:7013
        - 127.0.0.1:7014
        - 127.0.0.1:7015
        - 127.0.0.1:7016
      max-redirects: 2 #获取失败 最大重定向次数
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
//@ConfigurationProperties(prefix="spring.redis.cluster")
public class RedisConfigOne {
/*
    @Value("${spring.redis.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private int port;
    @Value("${spring.redis.password:redis123}")
    private String password;
    @Value("${spring.redis.timeout:6000}")
    private int timeout;
    @Value("${spring.redis.database:0}")
    private int database;
    
    //pool映射
    @Value("${spring.redis.lettuce.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.lettuce.pool.max-wait}")
    private int maxWait;
    
    //cluster映射
    List<String> nodes;//@ConfigurationProperties(prefix="spring.redis.cluster")映射
    
    @Value("${spring.redis.cluster.max-redirects:3}")
    private int maxRedirects;

    
    
    
    
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

	

	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	public int getMaxRedirects() {
		return maxRedirects;
	}

	public void setMaxRedirects(int maxRedirects) {
		this.maxRedirects = maxRedirects;
	}

    
    */
}
