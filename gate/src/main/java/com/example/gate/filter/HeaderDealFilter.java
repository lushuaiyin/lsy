package com.example.gate.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 实现业务处理逻辑，最终往header中放一个渠道标识channel和请求的IP地址
 * 
 * @author lsy
 *
 */
public class HeaderDealFilter implements GlobalFilter, Ordered {

	Logger logger = LoggerFactory.getLogger(HeaderDealFilter.class);

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 2;//顺序不能改！！！
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		
		//        ServerHttpRequest serverHttpRequestNew = exchange.getRequest().mutate().header("channel", channelParam).build();//另一种写法，添加单个值
		        //将现在的request 变成 change对象 
		ServerWebExchange changeNew=null;
		try {
			logger.info("HeaderDealFilter开始............");
			String channelParam = exchange.getRequest().getQueryParams().getFirst("channelParam");//这里为了方便测试，改成从Params取
			String IPParam = exchange.getRequest().getQueryParams().getFirst("IPParam");//这里为了方便测试，改成从Params取
			logger.info("HeaderDealFilter channelParam=="+channelParam+",IPParam=="+IPParam);
			
			String urlPath = exchange.getRequest().getURI().getPath();
			logger.info("HeaderDealFilter urlPath=="+urlPath);
			
			String routeid= getRouteId(exchange);
			urlPath = "/"+routeid+urlPath;
			logger.info("HeaderDealFilter加上路由id后 urlPath=="+urlPath);
			
			String urlPath2=urlPath.replace("/", "_");// /one/hello 改成 _one_hello
			String urlandchannel = urlPath2+"@"+channelParam;
			//下面的写法是不行的
//		exchange.getRequest().getHeaders().add("channel", channelParam);
			
			Consumer<HttpHeaders> httpHeadersNew = httpHeader -> {
				httpHeader.set("channel", channelParam);
			    httpHeader.set("IP-Address", IPParam);
			    httpHeader.set("urlandchannel", urlandchannel);// _one_hello@channelA
			};
			
			ServerHttpRequest serverHttpRequestNew = exchange.getRequest().mutate().headers(httpHeadersNew).build();//构建header
            changeNew = exchange.mutate().request(serverHttpRequestNew).build();

			logger.info("HeaderDealFilter header放入渠道标识:"+channelParam);
			logger.info("HeaderDealFilter header放入IP标识:"+IPParam);
			logger.info("HeaderDealFilter header放入url加渠道标识:"+urlandchannel);
		} catch (Exception e) {
			e.printStackTrace();
			return chain.filter(exchange);
		}
		
		
//		return chain.filter(exchange);
		return chain.filter(changeNew);
	}
	
	
	
	/**
	 * 获取路由id
	 * 
	 * @param exchange
	 * @return
	 */
	private String getRouteId(ServerWebExchange exchange) {
		logger.info("PrintFilter getRouteId开始............");
		if(exchange==null) {
			return null;
		}
		String routeId=null;
		Object routeObj = exchange.getAttribute(
				org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
		
		if(routeObj!=null) {
			Route route = (Route)routeObj;
			logger.info("PrintFilter开始.........路由所有信息==="+route.toString());
			routeId = route.getId();
		}
		
		return routeId;
	}

}
