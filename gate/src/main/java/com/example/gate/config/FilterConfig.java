package com.example.gate.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.gate.filter.HeaderDealFilter;
import com.example.gate.filter.PostBodyFilter;
import com.example.gate.filter.PrintFilter;
import com.example.gate.filter.SelfLimitFilter;

/**
 * 这个是配置类，只配置Filter相关的内容
 * 所有的filter中filter中的代码一定要用try catch包起来！
 * 因为只要有一个filter异常了，那么请求链走不下去了！
 * 如果你还用了hystrix，那么看不到任何错误！莫名其妙的就熔断了！很坑！
 * 所以一定要把自己的代码用try catch包起来，保证异常发生时，请求继续往下走！
 * 
 * 
 * @author lsy
 *
 */
@Configuration
public class FilterConfig {

	Logger logger = LoggerFactory.getLogger(FilterConfig.class);
	
	/*filter按顺序写，这样方便阅读*/
	
	@Bean
	public GlobalFilter postBodyFilter() {//获取post请求体，放入Attributes中，方便后面的拦截器和微服务使用postbody数据
	    return new PostBodyFilter();
	}
	
	@Bean
	public GlobalFilter headerDealFilter() {//处理请求中跟header相关的逻辑
	    return new HeaderDealFilter();
	}
	
//	@Bean
//	public GlobalFilter tokenFilter() {//一个校验token的简单例子
//	    return new TokenFilter();
//	}
	
//	@Bean
//	public GlobalFilter printFilter() {//打印之前filter的内容，用于验证
//	    return new PrintFilter();
//	}
	
	
	//用自己写的GlobalFilter执行限流脚本实现限流
//	@Bean
//	public GlobalFilter selfLimitFilter() {//执行redis的lua脚本（限流脚本）
//	    return new SelfLimitFilter();
//	}
	
	//跟yml配置一样效果
//	@Bean
//    public RouteLocator urlFilterRouteLocator(RouteLocatorBuilder builder) {
//		logger.info("FilterConfig---urlFilterRouteLocator---");
//        return builder.routes()
//                .route(r -> r.path("/one/**")
//                        .filters(f -> f.stripPrefix (1).filter(new UrlFilter())
//                                .addResponseHeader("urlFilterFlag", "pass"))
//                        .uri("lb://one")
//                        .order(0)
//                        .id("one")
//                )
//                .build();
//    }
	
	
	
}
