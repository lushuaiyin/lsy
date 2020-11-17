package com.example.gate.init;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.gate.bean.InitServiceConfig;
import com.example.gate.init.business.InitDataService;
import com.example.gate.limit.self.RateLimiterConfig;
import com.example.gate.service.DynamicRouteService;
//CommandLineRunner ApplicationRunner


@Component
public class InitRouteRunner implements ApplicationRunner {
        private static final Logger logger = LoggerFactory.getLogger(InitRouteRunner.class);

        
        @Value("${spring.cloud.consul.host}")
    	private String consulip;
        
        @Value("${spring.cloud.consul.port}")
    	private String consulport;
        
    	@Value("${spring.cloud.client.ip-address}")
    	private String ip;
    
    	@Value("${spring.application.name}")
    	private String servername;
    
    	@Value("${server.port}")
    	private String port;
    	
    	@Value("${my.consul.check.register:false}")
    	private String registerFlag;//检查服务在consul列表不存在，或者consul访问不通，就不停的注册自己。保证consul因为网络不稳定或者重启，服务不正常。（微服务只有在启动是会注册自己，这样做是为了不重启服务）

    	@Value("${my.consul.check.interval:10}")
    	private String interval;//检查服务在consul中是否正常的频率，每interval秒检查一次
    	
    	private long intervalNum=10;//interval转成long类型
    	
        public long getIntervalNum() {
        	if(interval!=null) {
        		try {
        			intervalNum = Long.valueOf(interval.trim());
				} catch (NumberFormatException e) {
					intervalNum=10;
					e.printStackTrace();
				}
        	}
			return intervalNum;
		}
		public void setIntervalNum(long intervalNum) {
			this.intervalNum = intervalNum;
		}
    	
    	
        public String getRegisterFlag() {
        	//保证只有2个值
        	if(registerFlag!=null && registerFlag.trim().equals("true")) {
        		return "true";
        	}
			return "false";
		}


		public void setRegisterFlag(String registerFlag) {
			this.registerFlag = registerFlag;
		}


		@Autowired
    	private RestTemplate restTemplate;
//        @Autowired
//        private DbRouteDefinitionRepository dbRouteDefinitionRepository;

        public RestTemplate getRestTemplate() {
			return restTemplate;
		}


		public void setRestTemplate(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		}


		@Autowired
        private DynamicRouteService dynamicRouteService;

		@Autowired
		RateLimiterConfig rateLimiterConf;
		
		
		@Value("${spring.cloud.gateway.httpclient.pool.maxConnections}")
    	private String maxConnections;
    
    	@Value("${spring.cloud.gateway.httpclient.pool.acquireTimeout}")
    	private String acquireTimeout;
    	
    	
    	//测试yml映射boolean和long类型
    	@Value("${my.booflag:false}")
    	private String booflag;//
    	
    	public String getBooflag() {
			return booflag;
		}
		public void setBooflag(String booflag) {
			this.booflag = booflag;
		}
		public boolean getBooflagReal() {
        	//保证只有2个值
        	if(booflag!=null && booflag.trim().equals("true")) {
        		return true;
        	}
			return false;
		}
		
		public String getLongflag() {
			return longflag;
		}
		public void setLongflag(String longflag) {
			this.longflag = longflag;
		}


		//测试yml映射boolean和long类型
    	@Value("${my.longflag:123}")
    	private String longflag;//
    	
    	
    	
        @Override
        public void run(ApplicationArguments args) throws Exception {
        	System.out.println("InitRouteRunner初始化==============maxConnections==="+maxConnections+",acquireTimeout==="+acquireTimeout);
        	
        	System.out.println("InitRouteRunner初始化=============="
        			+ "booflag==="+booflag+",longflag==="+longflag);
        	
        	
        	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        	String dateStr=LocalDateTime.now().format(df);
//        	dynamicRouteService.reloadRoutes();
//        	dynamicRouteService.watchRoutes();
        	InMemoryRouteDefinitionRepository  bb=null;
        	System.out.println("InitRouteRunner初始化。。。");
        	if(rateLimiterConf!=null) {
        		System.out.println("InitRouteRunner初始化。。。rateLimiterConf==="+rateLimiterConf.toString());
        	}else {
        		System.out.println("InitRouteRunner初始化。。。rateLimiterConf===null..");
        	}
        	
        	
        	initDataAfterStart();//初始化系统数据
        	
        	
        	
        	
        	/////////////////////////
        	String serverInfo="["+servername+":"+ip+":"+port+"]";
//          LocalDateTime startTime = LocalDateTime.now();
    		String message=serverInfo;
    		
    		//gate-192-168-124-17-8888
    		String defalutConsulId=servername+"-"+ip.replace(".", "-")+"-"+port;
    		
        	ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        	scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
        	    @Override
        	    public void run() {
        	    	if(getRegisterFlag().equals("false")) {
        	    		logger.debug("InitRouteRunner consul检查纠正服务开关未开启，不执行检查纠错服务。。。。。");
        	    		return;
        	    	}
        	    	try {
        	    		logger.info("InitRouteRunner 执行consul检查纠正服务"+serverInfo+"。。。delay "+getIntervalNum()+" seconds...");
            	    	
            	    	//查询consul的已经注册的服务列表
            	    	String consulQueryUrl="http://"+consulip+":"+consulport+"/v1/agent/services";
            	    	String consulRegisterUrl="http://"+consulip+":"+consulport+"/v1/agent/service/register";
            	    	
//            	    	getRestTemplate().getForObject(url, responseType, uriVariables)
            	        HttpHeaders headers = new HttpHeaders();
            	        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            	        HttpEntity<String> entity = new HttpEntity<String>(headers);
//            	        String strbody=restTemplate.exchange(consulUrl, HttpMethod.GET, entity,String.class).getBody();
//            	        Map responseData=restTemplate.exchange(consulUrl, HttpMethod.GET, entity,Map.class).getBody();
            	        
            	        ResponseEntity<Map> responseEntity=restTemplate.exchange(consulQueryUrl, HttpMethod.GET, entity,Map.class);
            	        Map responseData = responseEntity.getBody();
            	        
            	        boolean isExistSelf=false;//是否能从consul查到自己的信息。查不到说明自己没注册上或者被注销了，需要主动注册。
            	        if(responseData!=null) {
            	        	logger.info("InitRouteRunner 执行consul检查纠正服务。。。返回 ：\r\n"+responseData.toString());
            	        	
            	        	Iterator it= responseData.keySet().iterator();
            	        	while(it.hasNext()) {
            	        		String key= (String) it.next();
            	        		Map instance = (Map) responseData.get(key);
            	        		if(instance!=null) {
                	        		String Service =(String) instance.get("Service");
                	        		String Address =(String) instance.get("Address");
                	        		String Port =null;
                	        		if(instance.get("Port")!=null) {
                	        			Port =""+ instance.get("Port");
                	        		}
                	        		String ID =(String) instance.get("ID");
                	        		
                	        		if(Service.equalsIgnoreCase(servername) && Address.equals(ip) && Port.equals(port)) {
                	        			logger.info("InitRouteRunner 执行consul检查纠正服务，找到本实例的ID==="+ID);
                	        			isExistSelf=true;
                	        		}
                	        	}
            	        		
            	        	}
            	        	logger.info("InitRouteRunner 执行consul检查纠正服务。。。遍历完毕\r\n");
            	        }else {
            	        	logger.info("InitRouteRunner 执行consul检查纠正服务。。。返回null");
            	        }
            	        
            	        //判断自己是否正常注册在consul.不正常就再注册一次自己
            	        if(!isExistSelf) {
            	        	logger.info("InitRouteRunner 执行consul检查纠正服务。。。没有查到自己在consul的信息，准备进行注册。。。。。。defalutConsulId=="+defalutConsulId);
            	        	
            	        	HttpHeaders headers2 = new HttpHeaders();
            	        	headers2.setContentType(MediaType.APPLICATION_JSON_UTF8);
            	        	String reqBodyJson=getConsulregisterInfoJson();
                	        HttpEntity<String> entity2 = new HttpEntity<String>(reqBodyJson,headers2);
                	        
            	        	ResponseEntity<String> responseEntity2=restTemplate.exchange(consulRegisterUrl, HttpMethod.PUT, entity2,String.class);
            	        	String responseData2 = responseEntity2.getBody();
                	        int statusCode = responseEntity2.getStatusCode().value();
                	        logger.info("InitRouteRunner 执行consul检查纠正服务。。。没有查到自己在consul的信息，准备进行注册。。。。。。defalutConsulId=="
                	        +defalutConsulId+",请求返回码statusCode==="+statusCode);
            	        }
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        	    	
        	    	
        	    }//end run 
        	}, 5, getIntervalNum(), TimeUnit.SECONDS);
        	
        	
        }

        /**
         * 拼装consul注册请求的requestbody。
         * 注意是PUT请求
         * @return
         */
        public String getConsulregisterInfoJson() {
        	String defalutConsulId=servername+"-"+ip.replace(".", "-")+"-"+port;
        	
        	String template="{\r\n" + 
        			"    \"id\": \""+defalutConsulId+"\",\r\n" + 
        			"    \"name\": \""+servername+"\",\r\n" + 
        			"    \"address\": \""+ip+"\",\r\n" + 
        			"    \"port\": "+port+",\r\n" + 
        			"    \"tags\": [\r\n" + 
        			"        \"secure=false\"\r\n" + 
        			"    ],\r\n" + 
        			"    \"checks\": [\r\n" + 
        			"        {\r\n" + 
        			"        	\"deregister_critical_service_after\": \"10s\",\r\n" + 
        			"            \"http\": \"http://"+ip+":"+port+"/actuator/health\",\r\n" + 
        			"            \"interval\": \"5s\"\r\n" + 
        			"        }\r\n" + 
        			"    ]\r\n" + 
        			"}";
        	
        	//正确样例
//        	String template="{\r\n" + 
//        			"    \"id\": \"gate-192-168-124-17-8888\",\r\n" + 
//        			"    \"name\": \"gate\",\r\n" + 
//        			"    \"address\": \"192.168.124.17\",\r\n" + 
//        			"    \"port\": 8888,\r\n" + 
//        			"    \"tags\": [\r\n" + 
//        			"        \"secure=false\"\r\n" + 
//        			"    ],\r\n" + 
//        			"    \"checks\": [\r\n" + 
//        			"        {\r\n" + 
//        			"        	\"deregister_critical_service_after\": \"10s\",\r\n" + 
//        			"            \"http\": \"http://192.168.124.17:8888/actuator/health\",\r\n" + 
//        			"            \"interval\": \"5s\"\r\n" + 
//        			"        }\r\n" + 
//        			"    ]\r\n" + 
//        			"}";
        	
        	return template;
        }
        
        @Autowired
        InitServiceConfig initServiceConfig;
        
        @Autowired
        ApplicationContext applicationContext;
        
        
        /**
         * 按顺序执行InitDataService的initData方法，在系统启动后初始化一些数据
         */
        public void initDataAfterStart() {
        	if(applicationContext==null) {
        		System.out.println("error applicationContext is null.");
        		return;
        	}
        	System.out.println("initDataAfterStart开始执行预定义的初始化服务。。。");
        	if(initServiceConfig.getServicesMap()!=null) {
    			Iterator it = initServiceConfig.getServicesMap().keySet().iterator();
    			while(it.hasNext()) {
    				String key = (String)it.next();
    				String value = (String)this.initServiceConfig.getServicesMap().get(key);//
    				if(value!=null && !value.trim().equals("")) {
    					try {
							if(applicationContext.getBean(value.trim())!=null) {
								InitDataService initDataService = (InitDataService)applicationContext.getBean(value.trim());
								boolean res = initDataService.initData();
								if(res) {
									System.out.println(value.trim()+"初始化数据成功~~~");
								}else {
									System.out.println(value.trim()+"初始化数据失败！！！");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println(value.trim()+"初始化数据失败！！！");
							continue;
						}
    				}
    				System.out.println("加载缓存顺序"+key+"==="+value);
    			}//end while
    		}else {
    			System.out.println("加载缓存servicesMap配置为空。。。。");
    		}
        }
    	
}

/*
get
http://127.0.0.1:8500/v1/agent/services

{
    "gate-192-168-124-17-8888": {
        "ID": "gate-192-168-124-17-8888",
        "Service": "gate",
        "Tags": [
            "secure=false"
        ],
        "Meta": {},
        "Port": 8888,
        "Address": "192.168.124.17",
        "Weights": {
            "Passing": 1,
            "Warning": 1
        },
        "EnableTagOverride": false
    },
    "one-192-168-124-17-8901": {
        "ID": "one-192-168-124-17-8901",
        "Service": "one",
        "Tags": [
            "secure=false"
        ],
        "Meta": {},
        "Port": 8901,
        "Address": "192.168.124.17",
        "Weights": {
            "Passing": 1,
            "Warning": 1
        },
        "EnableTagOverride": false
    }
}
 * 
*/
