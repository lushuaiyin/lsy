package com.example.gate.util;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @JsonIgnore 忽略此属性,比如password我并不想返回给前端，就可以添加此注解
 * @JsonFormat(pattern = "yy-MM-dd HH:mm:ss a", locale = "zh", timezone =
 *                     "GMT+8") 进行数据格式化
 * @JsonInclude(JsonInclude.Include.NON_NULL) 如果此字段为null不返回该字段数据。 @JsonProperty(value
 *                                            = "user_name") 指定序列化时的字段名，默认使用属性名
 * @JsonUnwrapped(prefix = "user_")
 * 
 * @JsonIgnore // 利用底层jackson注解 private String password;
 * 
 *             private Integer age;
 * 
 * @JsonFormat(pattern = "yy-MM-dd HH:mm:ss a", locale = "zh", timezone =
 *                     "GMT+8")//yy-MM-dd HH:mm:ss private Date birthday;
 * 
 * @JsonInclude(JsonInclude.Include.NON_NULL) private String desc;
 * 
 * 
 * @author lsy
 *
 */
public class JsonUtil {

	public static void main(String[] ss) {

		RouteDefinition definition = new RouteDefinition();
		definition.setId("id111");
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

		String json = JsonUtil.objectToString(definition);
		System.out.println("转json====" + json);

		RouteDefinition definition2 = (RouteDefinition)JsonUtil.stringToObject(json, RouteDefinition.class);
		System.out.println("转对象====" + definition2.getId());
		System.out.println("转json====" + JsonUtil.objectToString(definition2));
	}

	// 转json
	public static String objectToString(Object obj) {
		String json = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			json = objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return json;
	}

	// 转对象
	public static Object stringToObject(String json, Class cla) {
		Object obj = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			obj = objectMapper.readValue(json, cla);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return obj;
	}

}
