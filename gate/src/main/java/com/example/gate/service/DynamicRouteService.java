package com.example.gate.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
//import org.springframework.cloud.gateway.actuate.GatewayControllerEndpoint;//新
//import org.springframework.cloud.gateway.actuate.GatewayWebfluxEndpoint;//旧
//import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
@Service
//public class DynamicRouteService implements ApplicationEventPublisherAware {
public class DynamicRouteService  {

//	@Override
//	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
//		this.publisher = applicationEventPublisher;
//	}
	
//  @Resource
//  private StringRedisTemplate redisTemplate;

	@Resource
	private DbService dbService;
	@Resource
//	private DbRouteDefinitionRepository dbRouteDefinitionRepository;//继承RouteDefinitionLocator，RouteDefinitionWriter
	
	
	@Autowired 
	private RouteDefinitionLocator routeDefinitionLocator;//内存中的路由
	@Resource
	private RouteDefinitionWriter routeDefinitionWriter;//内存中的路由
	
	@Resource
	private RouteDefinitionRepository routeDefinitionRepository;//内存中的路由
	

	private ApplicationEventPublisher publisher;

	/**
	 * 实现RouteDefinitionRepository接口，当网关启动或者接受到RefreshRoutesEvent（一种AppliacationEvent）事件，
	 * 就会触发getRouteDefinitions的方法.
	 * 
	 * 路由默认是可以从注册中心获取的，不需要在yml（或者代码编写）配置。一个默认的配置如下：
#      routes:
#      - id: one
#        # lb代表从注册中心获取服务，且已负载均衡方式转发
#        uri: lb://one #目标服务地址
#        predicates: #路由条件
#          - Path=/one/**
#        #过滤规则。 StripPrefix=1标识去掉url的第一个前缀，用后面的内容拼接到uri后面
#        filters:
#          - StripPrefix=1
     * 
     * 只有需要配置复杂的路由规则时，才需要在yml配置（或者代码编写），这才引出一个问题，如果路由规则因业务变更要投产，
     * 就必须重启网关了（无论是yml里配置还是代码编写）。
     * 而实际情况基本不允许重启网关！所以才有了动态路由，通过rest接口的方式去改写路由规则。（整个场景就是重载的场景。即不重启服务实现修改JVM数据。）
     * 
     * 所以，如果实际场景不需要配置复杂的路由规则，没必要用动态路由
     * 
	 */
	private void notifyChanged() {
		this.publisher.publishEvent(new RefreshRoutesEvent(this));
	}

	
	
	/**
	 * 重载路由
	 *
	 */
	public String reloadRoutes() {
		List<RouteDefinition> dbList = dbService.getRouteList2();//修改后的数据库信息
//		routeDefinitionLocator.getRouteDefinitions();
//		dbRouteDefinitionRepository.getRouteDefinitions();
		
		
		if(dbList!=null ) {
			//遍历删除路由
			Flux<RouteDefinition> routeNeicun = routeDefinitionRepository.getRouteDefinitions();
			Iterator it = routeNeicun.toIterable().iterator();
			while(it.hasNext()) {
				RouteDefinition rout= (RouteDefinition)it.next();
				if(rout!=null) {
					routeDefinitionWriter.delete(Mono.just(rout.getId()));
					System.out.println("delte遍历删除路由=="+rout.getId());
					notifyChanged();
				}
			}
			//遍历增加路由
			for(RouteDefinition df : dbList) {
				if(df!=null) {
					routeDefinitionWriter.save(Mono.just(df)).subscribe();
					System.out.println("add遍历增加路由=="+df.getId());
					notifyChanged();
				}
			}
			
			dbService.syn1And2(dbList);
		}
		return "success";
	}
	
	
	/**
	 *查看内存路由信息
	 *
	 */
	public List<RouteDefinition> watchRoutes() {
		List<RouteDefinition> res=new ArrayList<RouteDefinition>();
		//遍历删除路由
		Flux<RouteDefinition> routeNeicun = routeDefinitionRepository.getRouteDefinitions();
		Iterator it = routeNeicun.toIterable().iterator();
		while(it.hasNext()) {
			RouteDefinition rout= (RouteDefinition)it.next();
			if(rout!=null) {
				System.out.println("遍历查看路由=="+rout.toString());
				res.add(rout);
			}
		}
		return res;
	}
	
	
	/**
	 * 增加路由
	 *
	 */
	public String add(RouteDefinition definition) {
		routeDefinitionWriter.save(Mono.just(definition)).subscribe();
		notifyChanged();
		return "success";
	}

	/**
	 * 更新路由
	 */
	public String update(RouteDefinition definition) {
		try {
			this.routeDefinitionWriter.delete(Mono.just(definition.getId()));
		} catch (Exception e) {
			return "update fail,not find route  routeId: " + definition.getId();
		}
		try {
			routeDefinitionWriter.save(Mono.just(definition)).subscribe();
			notifyChanged();
			return "success";
		} catch (Exception e) {
			return "update route  fail";
		}

	}

	/**
	 * 删除路由
	 *
	 */
	public String delete(String id) {
		try {
			this.routeDefinitionWriter.delete(Mono.just(id));

			notifyChanged();
			return "delete success";
		} catch (Exception e) {
			e.printStackTrace();
			return "delete fail";
		}

	}

	



	
}