package com.example.gate.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.gate.util.JsonUtil;

@Service
public class DbService {

	public static List<RouteDefinition> routeDefinitions = new ArrayList<>();
	
	
	// 数据库查询
	public List<RouteDefinition> getRouteList() {
		if(routeDefinitions!=null && routeDefinitions.size()>1) {
			return routeDefinitions;
		}
		for(int i=0;i<3;i++) {
			routeDefinitions.add(getTestRoute("testid-"+i));
		}
//		routeDefinitions.add(getTestRouteOne());
		return routeDefinitions;
	}
	
	// 数据库查询--测试2
	public List<RouteDefinition> getRouteList2() {
		List<RouteDefinition> routeDefinitions2 = new ArrayList<>();
		for(int i=0;i<2;i++) {
			routeDefinitions2.add(getTestRoute("new-testid-"+i));
		}
		return routeDefinitions2;
	}
	
	// 数据库查询--测试3
		public void syn1And2(List<RouteDefinition> param) {
			routeDefinitions=param;
		}
	
	//数据库增加
	public boolean addRoute(RouteDefinition rd) {
		if(rd!=null) {
			routeDefinitions.add(rd);
		}
		return true;
	}
	
	// 数据库删除
	public boolean delRoute(String rdId) {
		if (rdId != null) {
			for(RouteDefinition rd: routeDefinitions) {
				if(rd.getId().trim().equals(rdId.trim())) {
					routeDefinitions.remove(rd);
					break;
				}
			}
		}
		return true;
	}
	
	// 数据库修改
	public boolean editRoute(RouteDefinition param) {
		if (param != null) {
			for (RouteDefinition rd : routeDefinitions) {
				if (rd.getId().trim().equals(param.getId().trim())) {
					rd=param;
					break;
				}
			}
		}
		return true;
	}
	
	
	// 数据库增加 或 修改
	public boolean addOrEditRoute(RouteDefinition param) {
		if (param != null) {
			boolean isUpdate=false;
			for (RouteDefinition rd : routeDefinitions) {
				if (rd.getId().trim().equals(param.getId().trim())) {
					rd=param;
					isUpdate=true;
					break;
				}
			}
			
			if(!isUpdate) {
				this.addRoute(param);
			}
		}
		return true;
	}
		
	//测试数据
	public static RouteDefinition getTestRoute(String id) {

		RouteDefinition definition = new RouteDefinition();
		definition.setId("id111");
		if(id!=null) {
			definition.setId(id);
		}
		URI uri = UriComponentsBuilder.fromHttpUrl("http://127.0.0.1:8888/header").build().toUri();
		definition.setUri(uri);

		// 定义第一个断言
		PredicateDefinition predicate = new PredicateDefinition();
		predicate.setName("Path");

		Map<String, String> predicateParams = new HashMap<>(8);
		predicateParams.put("pattern", "/jd");
		predicate.setArgs(predicateParams);

		// 定义Filter
		FilterDefinition filter = new FilterDefinition();
		filter.setName("AddRequestHeader");
		Map<String, String> filterParams = new HashMap<>(8);
		// 该_genkey_前缀是固定的，见org.springframework.cloud.gateway.support.NameUtils类
		filterParams.put("_genkey_0", "header");
		filterParams.put("_genkey_1", "addHeader");
		filter.setArgs(filterParams);

		FilterDefinition filter1 = new FilterDefinition();
		filter1.setName("AddRequestParameter");
		Map<String, String> filter1Params = new HashMap<>(8);
		filter1Params.put("_genkey_0", "param");
		filter1Params.put("_genkey_1", "addParam");
		filter1.setArgs(filter1Params);

		definition.setFilters(Arrays.asList(filter, filter1));
		definition.setPredicates(Arrays.asList(predicate));

//		String json = JsonUtil.objectToString(definition);
//		System.out.println("转json====" + json);
//
//		RouteDefinition definition2 = (RouteDefinition)JsonUtil.stringToObject(json, RouteDefinition.class);
//		System.out.println("转对象====" + definition2.getId());
//		System.out.println("转json====" + JsonUtil.objectToString(definition2));
		
		return definition;
	}
	
	/**


routes:
#      - id: one
#        # lb代表从注册中心获取服务，且已负载均衡方式转发
#        uri: lb://one #目标服务地址
#        predicates: #路由条件
#          - Path=/one/**
#        #过滤规则。 StripPrefix=1标识去掉url的第一个前缀，用后面的内容拼接到uri后面
#        filters:
#          - StripPrefix=1

    
    //对应上面的配置(除了id不同)
    
{
        "id": "ReactiveCompositeDiscoveryClient_one",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "pattern": "/one/**"
                }
            }
        ],
        "filters": [
            {
                "name": "RewritePath",
                "args": {
                    "regexp": "/one/(?<remaining>.*)",
                    "replacement": "/${remaining}"
                }
            }
        ],
        "uri": "lb://one",
        "metadata": {},
        "order": 0
    }

	 */
	public static RouteDefinition getTestRouteOne() {

		RouteDefinition definition = new RouteDefinition();
		definition.setId("one");
//		URI uri = UriComponentsBuilder.fromHttpUrl("lb://one").build().toUri();
		URI uri=null;
		try {
			uri = new URI("lb://one");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		definition.setUri(uri);

		// 定义第一个断言
		PredicateDefinition predicate = new PredicateDefinition();
		predicate.setName("Path");

		Map<String, String> predicateParams = new HashMap<>(8);
		predicateParams.put("pattern", "/one/**");
		predicate.setArgs(predicateParams);

		// 定义Filter
		FilterDefinition filter = new FilterDefinition();
		filter.setName("RewritePath");
		Map<String, String> filterParams = new HashMap<>(8);
		filterParams.put("regexp", "/one/(?<remaining>.*)");
		filterParams.put("replacement", "/${remaining}");
		filter.setArgs(filterParams);


		definition.setFilters(Arrays.asList(filter));
		definition.setPredicates(Arrays.asList(predicate));

		
		return definition;
	}
}
