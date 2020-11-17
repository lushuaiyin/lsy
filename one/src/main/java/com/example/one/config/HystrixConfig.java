package com.example.one.config;

import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;


/**
 * 熔断配置
 * 
 * groupKey表示所属的group，一个group共用线程池
 */
@Configuration
@DefaultProperties(groupKey = "default", 
					commandProperties = {
							@HystrixProperty(name=HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value="THREAD"),
					        @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "2000"),
					        
					        @HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS, value = "8000"),
							
					        @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "3"),
					        @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "5000"),
					        @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50")
					},
					threadPoolProperties = {
					        @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "30"),
					        @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "2"),
					        @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "10")
					}, 
                   threadPoolKey = "default")
public class HystrixConfig {

//	@Bean
//    public HystrixCommandAspect hystrixAspect() {
//        return new HystrixCommandAspect();
//    }

//	//配置监控路径，此配置同yml
//    @Bean
//    public ServletRegistrationBean getServlet() {
//        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
//        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
//        registrationBean.setLoadOnStartup(1);
//        registrationBean.addUrlMappings("/actuator/hystrix.stream");
//        registrationBean.setName("HystrixMetricsStreamServlet");
//        return registrationBean;
//    }
}
