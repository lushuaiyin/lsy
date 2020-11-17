package com.example.one.config.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 映射redis的哨兵配置配置
 * yml:
 
  #redis哨兵.一主2从192.168.10.1:6379,192.168.10.2:6379,192.168.10.3:6379。三哨兵192.168.10.1:26379,192.168.10.2:26379,192.168.10.3:26379
spring:  
  redis3:
    timeout: 6000ms
    database: 0 
    host: 192.168.10.1 #主节点的master
    password: ha@123 #redis密码
    port: 6379 #主节点的master端口
    lettuce:
      pool: 
        max-active: 1000 #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 #连接池中的最大空闲连接
        min-idle: 3 #连接池中的最小空闲连接
        max-wait: -1 #连接池最大阻塞等待时间（使用负值表示没有限制）
    sentinel: 
      master: mymaster 
      nodes: 
        - 192.168.10.1:26379
        - 192.168.10.2:26379
        - 192.168.10.3:26379
      
 * 
 * @author lsy
 *
 */
//@Data
//@ConfigurationProperties(prefix="spring.redis3.sentinel")
public class RedisConfigThree {
/*
    @Value("${spring.redis3.host:127.0.0.1}")
    private String host;
    @Value("${spring.redis3.port:6379}")
    private int port;
    @Value("${spring.redis3.password:redis123}")
    private String password;
    @Value("${spring.redis3.timeout:6000}")
    private int timeout;
    @Value("${spring.redis3.database:0}")
    private int database;
    
    //pool映射
    @Value("${spring.redis3.lettuce.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis3.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis3.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis3.lettuce.pool.max-wait}")
    private long maxWait;
    
    //setinel映射
    List<String> nodes;//@ConfigurationProperties(prefix="spring.redis3.sentinel")映射
    
    @Value("${spring.redis3.sentinel.master:mymaster}")
    private String master;

    
    
    
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

	public List<String> getNodes() {
		return nodes;
	}

	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}
    
    */
}
