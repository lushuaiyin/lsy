package com.example.gate.service;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 动态路由
 * 采用数据介质
 * 
 * 
 * 如果没有RouteDefinitionRepository的实例，则默认用InMemoryRouteDefinitionRepository。
 * 
 * 
 * @author lsy
 *
 */
//@Component
//public class DbRouteDefinitionRepository implements RouteDefinitionRepository {
public class DbRouteDefinitionRepository  {

	
//	private final Map<String, RouteDefinition> routes = java.util.Collections.synchronizedMap(new LinkedHashMap<String, RouteDefinition>());
//	private boolean init_flag = false;//是否已初始化
//	
//	@Resource
//	private DbService dbService;
//	
//	@Override
//	public Flux<RouteDefinition> getRouteDefinitions() {
//		System.out.println("遍历路由==getRouteDefinitions");
//		if(!init_flag) {
//			try {
////				Map<String, RouteDefinition> routesMap = new LinkedHashMap<String, RouteDefinition>();
////				List<RouteDefinition> routeDefinitions = new LinkedList<>();
//				List<RouteDefinition> dbList = dbService.getRouteList();//数据库路由信息
//				if(dbList!=null) {
//					routes.clear();
//					for(RouteDefinition rd : dbList) {
//						routes.put(rd.getId(), rd);
//					}
//					init_flag=true;
//				}
//				return Flux.fromIterable(routes.values());
//			} catch (Exception e) {
//				e.printStackTrace();
//		        return Flux.empty();
//			}
//		}else {
//			return Flux.fromIterable(routes.values());
//		}
//		
//	}
//	
//	@Override
//    public Mono<Void> save(Mono<RouteDefinition> route) {
//        return route.flatMap(r -> {
//        	routes.put(r.getId(), r);
//        	dbService.addOrEditRoute(r);//数据库保存
//            return Mono.empty();
//        });
//
//    }
//
//
//	@Override
//	public Mono<Void> delete(Mono<String> routeId) {
//		return routeId.flatMap(id -> {
//			routes.remove(id);
//			dbService.delRoute(id);//数据库删除
//			return Mono.empty();
//        });
//	}

}
