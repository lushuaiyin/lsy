package com.example.gate.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 定义乱七八糟的bean
 * 
 * @author lsy
 *
 */
@Configuration
public class BeanConfig {
	

	@Value("${jasypt.encryptor.password}")
	private String password;//秘钥
	
	
	/* jasypt3.0.2版本的默认值如下：

表头Key	                                    Required    Default Value

jasypt.encryptor.password	                True	    -秘钥自己定义
jasypt.encryptor.algorithm	                False	    PBEWITHHMACSHA512ANDAES_256
jasypt.encryptor.key-obtention-iterations	False	    1000
jasypt.encryptor.pool-size	                False	    1
jasypt.encryptor.provider-name	            False	    SunJCE
jasypt.encryptor.provider-class-name	    False	    null
jasypt.encryptor.salt-generator-classname	False	    org.jasypt.salt.RandomSaltGenerator
jasypt.encryptor.iv-generator-classname	    False	    org.jasypt.iv.RandomIvGenerator
jasypt.encryptor.string-output-type	        False	    base64
jasypt.encryptor.proxy-property-sources	    False	    false
jasypt.encryptor.skip-property-sources	    False	    empty list
	 */
	
	
	@Bean("stringEncryptor")
    public StringEncryptor stringEncryptor() {
		//3.0.2版本的默认值
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);//定义在yml中的秘钥
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }
	
	
	
}
