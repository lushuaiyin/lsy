package com.example.gate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * tomcat配置
 * ServerProperties
 * 
 * @author lsy
 *
 */
@Configuration
public class TomcatConfig {
	

	@Value("${jasypt.encryptor.password}")
	private String password;//秘钥
	
	
}
