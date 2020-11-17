package com.example.one.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.example.one.bean.MyTestBean;

/**
 * 配置中心相关
 * 
 * 这类测试了一个注解的使用
 * 
 * @ConfigurationProperties
 * @Component
 * @EnableConfigurationProperties 
 * 这3个注解的关系
 * 
 * @EnableConfigurationProperties注解的作用是：使使用 @ConfigurationProperties 注解的类生效。
 * 定义一个配置类的时候，我们可以把这个类直接用@Component声明并注入spring容器，也可以不用@Component，然后再另一个@Configuration的类中
 * 显示的使用@Bean注入到spring容器中。
 * 
 * 还有另外一种方式：不使用@Component，也不在@Configuration的类中使用@Bean，
 * 而是在@Configuration的类中使用注解@EnableConfigurationProperties，指定那个类。
 * @EnableConfigurationProperties就是告诉spring容器需要生成这个bean。
 * 请看下面的例子
 * 
 * 
 * 
 * @author lsy
 *
 */
@Configuration
@EnableConfigurationProperties(MyTestBean.class) 
@ConditionalOnClass(MyTestBean.class)
public class ConfigCenterConfig {

}
