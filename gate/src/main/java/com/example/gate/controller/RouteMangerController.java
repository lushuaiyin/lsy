package com.example.gate.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.gate.service.DynamicRouteService;
import com.example.gate.util.JsonUtil;

import reactor.core.publisher.Flux;

@RestController
//@RequestMapping("/beat")
public class RouteMangerController {

	@Resource
	private DynamicRouteService dynamicRouteService;
	@Autowired 
	private RouteDefinitionLocator routeDefinitionLocator;
	
	@Value("${spring.cloud.client.ip-address}")
	private String ip;
	
	@Value("${spring.application.name}")
	private String servername;
	
	@Value("${server.port}")
	private String port;
	

//	@GetMapping(value="/query")
//	public String query() {
//		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		List<RouteDefinition> res = dynamicRouteService.watchRoutes();
//		return "查看路由信息："+LocalDateTime.now().format(df)+"\r\n"+JsonUtil.objectToString(res);
//	}
	
	

    //获取网关所有的路由信息
    @RequestMapping("/query2")
    public Flux<RouteDefinition> getRouteDefinitions(){
        return routeDefinitionLocator.getRouteDefinitions();
    }
	
	@GetMapping(value="/reload")
	public String reload() {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		dynamicRouteService.reloadRoutes();
		return "完成重载路由信息："+LocalDateTime.now().format(df);
	}
}