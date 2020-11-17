package com.example.gate.filter;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * 前置拦截器的最后一个，用于打印之前的filter处理的一些数据
 * 
 * 
 * @author lsy
 *
 */
public class PrintFilter implements GlobalFilter, Ordered {

	Logger logger = LoggerFactory.getLogger(PrintFilter.class);

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 100;
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		try {
			logger.info("PrintFilter开始..........routeid==="+getRouteId(exchange));
			
			if(true) {
				if(exchange.getRequest().getHeaders().getContentType()!=null) {
					String contentType = exchange.getRequest().getHeaders().getContentType().toString();
					logger.info("PrintFilter开始...........contentType==="+contentType);
				}
				
				logger.info("PrintFilter开始...........MediaType.APPLICATION_JSON==="+MediaType.APPLICATION_JSON);
				logger.info("PrintFilter开始...........MediaType.APPLICATION_JSON_VALUE==="+MediaType.APPLICATION_JSON_VALUE);
				logger.info("PrintFilter开始...........MediaType.APPLICATION_JSON_UTF8==="+MediaType.APPLICATION_JSON_UTF8);
				logger.info("PrintFilter开始...........MediaType.APPLICATION_JSON_UTF8_VALUE==="+MediaType.APPLICATION_JSON_UTF8_VALUE);
				
				
				URI urii = exchange.getRequest().getURI();
				logger.info("PrintFilter开始...........getURI.URI==="+urii.toString());
				
				String getPath = exchange.getRequest().getURI().getPath();
				logger.info("PrintFilter开始.........getURI...getPath==="+getPath);
				
				HttpMethod httpMethod = exchange.getRequest().getMethod();
				logger.info("PrintFilter开始............HttpMethod==="+httpMethod.toString());
				
				String methodValue= exchange.getRequest().getMethodValue();
				logger.info("PrintFilter开始............getMethodValue==="+methodValue);
				
				
				RequestPath getPath2 =exchange.getRequest().getPath();
				logger.info("PrintFilter开始............getPath2==="+getPath2.toString());
				
				InetSocketAddress inetSocketAddress =exchange.getRequest().getRemoteAddress();
				logger.info("PrintFilter开始............getRemoteAddress==="+inetSocketAddress.toString());
				
				String getHostString = exchange.getRequest().getRemoteAddress().getHostString();
				logger.info("PrintFilter开始............getRemoteAddress-getHostString==="+getHostString);
				
				String getHostName = exchange.getRequest().getRemoteAddress().getHostName();
				logger.info("PrintFilter开始............getRemoteAddress-getHostName==="+getHostName);
			}
			
			logger.info("PrintFilter开始...........打印header所有内容。。。.");
			getAllHeadersRequest(exchange.getRequest());
			logger.info("");
			String channel = exchange.getRequest().getHeaders().getFirst("channel");
			String IPAddress = exchange.getRequest().getHeaders().getFirst("IP-Address");//这里为了方便测试，改成从Params取
			logger.info("PrintFilter打印header中...........channel=="+channel+",IP-Address=="+IPAddress);
		} catch (Exception e) {
			e.printStackTrace();
			return chain.filter(exchange);
		}
		
		//没有被if条件拦截，就放行
		return chain.filter(exchange);
	}
	
	
	private Map getAllParamtersRequest(ServerHttpRequest request) {
		logger.info("PrintFilter getAllParamtersRequest开始............");
		Map map = new HashMap();
		MultiValueMap<String, String> paramNames = request.getQueryParams();
		Iterator it= paramNames.keySet().iterator();
		while (it.hasNext()) {
			String paramName = (String) it.next();
			
			List<String> paramValues = paramNames.get(paramName);
			if (paramValues.size() >= 1) {
				String paramValue = paramValues.get(0);
				logger.info("request参数取第一个："+paramName+",值："+paramValue);
				map.put(paramName, paramValue);
				for(int i=0;i<paramValues.size();i++) {
					String paramValueTmp = paramValues.get(i);
					if(i>=1) {
						logger.info("request参数，size=="+paramValues.size()+"...key=="+paramName+",值："+paramValueTmp);
					}
				}
			}
		}
		return map;
	}
	
	private Map getAllHeadersRequest(ServerHttpRequest request) {
		logger.info("PrintFilter getAllHeadersRequest开始............");
		Map map = new HashMap();
		HttpHeaders hearders = request.getHeaders();
		Iterator it= hearders.keySet().iterator();
		while (it.hasNext()) {
			String keyName = (String) it.next();
			
			List<String> headValues = hearders.get(keyName);
			if (headValues.size() >= 1) {
				String kvalue = headValues.get(0);
				logger.info("request header取第一个key："+keyName+",值："+kvalue);
				map.put(keyName, kvalue);
				for(int i=0;i<headValues.size();i++) {
					String kvalueTmp = headValues.get(i);
					if(i>=1) {
						logger.info("request header size=="+headValues.size()+"...key："+keyName+",值："+kvalueTmp);
					}
				}
			}
		}
		return map;
	}
	
	
	/**
	 * 在之前的一个filter我们获取了post请求的body数据，放入了Attributes中
	 * exchange.getAttributes().put("PostBodyData", bodyStr);
	 * 所以在这里就可以直接取了。
	 * 
	 * 
	 * post请求的传参获取相对比较麻烦一些，gateway采用了webflux的方式来封装的请求体。  
	 * 我们知道post常用的两种传参content-type是application/x-www-form-urlencoded和application/json，这两种方式还是有区别的。
	 * 
	 * 
	 * @param exchange
	 * @return
	 */
	private Object getPostBodyData(ServerWebExchange exchange) {
		logger.info("PrintFilter getPostBodyData开始............");
		
		Object res=exchange.getAttributes().get("PostBodyData");
		if(res!=null) {
			logger.info("getPostBodyData获取前面filter放入Attributes中的数据为========\r\n"+res.toString());
		}else {
			logger.info("getPostBodyData获取前面filter放入Attributes中的数据为null");
		}
		
		
		return res;
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
