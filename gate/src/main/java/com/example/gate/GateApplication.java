package com.example.gate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

/**
 * 计数器错误的类
 * io.netty.handler.codec.MessageToMessageEncoder      netty-codec-4.1.29.Final.jar
 * io.netty.buffer.AbstractReferenceCountedByteBuf     netty-buffer-4.1.29.Final.jar
 * 
 * io.netty.buffer.AbstractReferenceCountedByteBuf     netty-buffer-4.1.29.Final.jar
 * io.netty.util.AbstractReferenceCounted              netty-common-4.1.29.Final.jar
 * 
 * 
 * 
 * 
 * GatewayAutoConfiguration 
 * GatewayRedisAutoConfiguration 
 * ReactiveRedisConnectionFactory
 * @author lsy
 *
 */
//使用jasypt的第二种方式.如果你没用到@SpringBootApplication 或  @EnableAutoConfiguration就必须用下面2个注解，才能正常使用jasypt
//@Configuration
//@EnableEncryptableProperties

//具体指定配置文件的用法，其他配置文件不受jasypt影响
//@Configuration
//@EncryptablePropertySources({@EncryptablePropertySource("classpath:encrypted.properties"),
//                             @EncryptablePropertySource("classpath:encrypted2.properties")})

//@EnableCircuitBreaker
@ComponentScan(basePackages = "com.example")
@EnableDiscoveryClient
@SpringBootApplication
public class GateApplication {

	public static void main(String[] args) {
		System.setProperty("reactor.netty.ioWorkerCount", "100");
		System.setProperty("reactor.netty.ioSelectCount", "100");
		System.setProperty("reactor.netty.pool.maxConnections", "100");
		SpringApplication.run(GateApplication.class, args);
	}

	

}
