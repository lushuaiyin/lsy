package com.example.gate.bean;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 一个yml映射map的例子，我做了排序。
 * 
 * 应用场景：系统启动后要初始化一些业务数据，每个初始化的功能都写在一个规定好的service接口中。
 * 拿到这个map中的bean名称，去spring容器中找到这个bean，调用他的方法。
 * 排序是为了应对某些数据的初始化有前后依赖关系。
 * 
 * @author lsy
 *
 */
@Component
@ConfigurationProperties(prefix = "initservicelist")
public class InitServiceConfig {

//	private Map<String , String> servicesMap = new ConcurrentHashMap<String , String>(){};
	
	private Map<String, String> servicesMap = new TreeMap<String , String>(new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {
			String key1=(o1==null)?"":(String)o1;
			String key2=(o2==null)?"":(String)o2;
			int num1 = Integer.MAX_VALUE;
			int num2 = Integer.MAX_VALUE;
			try {
				num1=Integer.valueOf(key1);
			} catch (NumberFormatException e) {
				num1 = Integer.MAX_VALUE;
			}
			try {
				num2=Integer.valueOf(key2);
			} catch (NumberFormatException e) {
				num1 = Integer.MAX_VALUE;
			}
			
			return num1 - num2;
		}
	}){};

	public Map<String, String> getServicesMap() {
		return servicesMap;
	}

	public void setServicesMap(Map<String, String> servicesMap) {
		this.servicesMap = servicesMap;
	}

	@Override
	public String toString() {
		return "InitServiceConfig [servicesMap=" + servicesMap + "]";
	}

	@PostConstruct
    public void init() {
		System.out.println("InitServiceConfig servicesMap内容:"+servicesMap.toString());
		if(servicesMap!=null) {
			Iterator it = servicesMap.keySet().iterator();
//			int inx = 1;
			while(it.hasNext()) {
				String key = (String)it.next();
				String value = (String)this.servicesMap.get(key);//
				System.out.println("加载缓存顺序"+key+"==="+value);
			}
		}else {
			System.out.println("加载缓存servicesMap配置为空。。。。");
		}
		
	}
}
