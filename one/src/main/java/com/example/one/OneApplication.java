package com.example.one;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.context.annotation.ComponentScan;

import com.example.one.event.TestListener;
import com.example.one.event.springevent.Test5SpringListener;
/**
 * 
hystrix监控
启动工程后，访问路径：http://127.0.0.1:8901/hystrix

先随便访问一个hystrix拦截的请求，再访问http://127.0.0.1:8901/hystrix
在第一个输入框输入需要监控的链接，如：http://127.0.0.1:8901/actuator/hystrix.stream
点击按钮Monitor Stream
(直接访问需要监控的链接返回的是json数据，而不是页面)

仪表盘说明：
曲线中间有个圆，圆的大小表示流量，流量越大，圆越大；颜色越偏向红色，代表这个服务越不健康。
图中testHystrixCommand5下来的百分比是失败率，旁边的六个数字根据颜色不同分别对应右上角
那里的Success | Short-Circuited | Bad Request | Timeout | Rejected | Failure 的颜色，
Host就是请求的频率
Circuit就是熔断的状态，open就是熔断打开，closed就是关闭，还有半开half open
曲线代表一段时间内，流量的相对变化



 * @author lsy
 *
 */
//@EnableAutoConfiguration
@ComponentScan(basePackages="com.example")
@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableTurbine
public class OneApplication {

	public static void main(String[] args) {
//		SpringApplication.run(OneApplication.class, args);
		
		//注册监听的第3种方式。spring自带的监听只能用这种方式。自定义监听这种方式可选。
		SpringApplication app =new SpringApplication(OneApplication.class);
//        app.addListeners(new TestListener());
//        app.addListeners(new Test5SpringListener());
        app.run(args);
		
	}

}
