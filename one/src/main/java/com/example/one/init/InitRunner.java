package com.example.one.init;


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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
//CommandLineRunner ApplicationRunner


@Component
public class InitRunner implements ApplicationRunner {
        private static final Logger logger = LoggerFactory.getLogger(InitRunner.class);

        
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


        @Override
        public void run(ApplicationArguments args) throws Exception {
        	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        	String dateStr=LocalDateTime.now().format(df);
        	String serverInfo="["+servername+":"+ip+":"+port+"]";
        	
        	System.out.println(serverInfo+",InitRunner初始化逻辑。。。");
        	
        	/////////////////////////
        	
//          LocalDateTime startTime = LocalDateTime.now();
    		String message=serverInfo;
    		
    		//gate-192-168-124-17-8888
    		String defalutConsulId=servername+"-"+ip.replace(".", "-")+"-"+port;
    		
//    		long interVal=10;
        	ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        	scheduledThreadPool.scheduleWithFixedDelay(new Runnable() {
        	    @Override
        	    public void run() {
        	    	if(getRegisterFlag().equals("false")) {
        	    		logger.debug("InitRunner consul检查纠正服务开关未开启，不执行检查纠错服务。。。。。");
        	    		return;
        	    	}
        	    	try {
        	    		logger.info("InitRunner 执行consul检查纠正服务"+serverInfo+"。。。delay "+getIntervalNum()+" seconds...");
            	    	
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
            	        	logger.info("InitRunner 执行consul检查纠正服务。。。返回 ：\r\n"+responseData.toString());
            	        	
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
                	        			logger.info("InitRunner 执行consul检查纠正服务，找到本实例的ID==="+ID);
                	        			isExistSelf=true;
                	        		}
                	        	}
            	        		
            	        	}
            	        	logger.info("InitRunner 执行consul检查纠正服务。。。遍历完毕\r\n");
            	        }else {
            	        	logger.info("InitRunner 执行consul检查纠正服务。。。返回null");
            	        }
            	        
            	        //判断自己是否正常注册在consul.不正常就再注册一次自己
            	        if(!isExistSelf) {
            	        	logger.info("InitRunner 执行consul检查纠正服务。。。没有查到自己在consul的信息，准备进行注册。。。。。。defalutConsulId=="+defalutConsulId);
            	        	
            	        	HttpHeaders headers2 = new HttpHeaders();
            	        	headers2.setContentType(MediaType.APPLICATION_JSON_UTF8);
            	        	String reqBodyJson=getConsulregisterInfoJson();
                	        HttpEntity<String> entity2 = new HttpEntity<String>(reqBodyJson,headers2);
                	        
            	        	ResponseEntity<String> responseEntity2=restTemplate.exchange(consulRegisterUrl, HttpMethod.PUT, entity2,String.class);
            	        	String responseData2 = responseEntity2.getBody();
                	        int statusCode = responseEntity2.getStatusCode().value();
                	        logger.info("InitRunner 执行consul检查纠正服务。。。没有查到自己在consul的信息，准备进行注册。。。。。。defalutConsulId=="
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
    	
}

/*
  查询服务返回样例
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

