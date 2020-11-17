package com.example.one.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;

/**
 * 一个普通的controller，用于测试Hystrix熔断。 
 * 
 * 
 * @author lsy
 *
 */
@RestController
//@Controller
//@RequestMapping("/beat")
//@DefaultProperties(defaultFallback = "fallbackResponse")
public class TestHystrixController {

	public static final Logger logger = LoggerFactory.getLogger(TestHystrixController.class);
	
	@Value("${spring.cloud.client.ip-address}")
	private String ip;

	@Value("${spring.application.name}")
	private String servername;

	@Value("${server.port}")
	private String port;

	//RequestMapping千万别叫这个名字 waitFail ，有问题！！！！
	
	
	@Autowired
	private RestTemplate restTemplate;

    public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	/**
     * 该方法是对接口调用超时的处理方法
     * 注意fallback方法必须和业务方法的参数列表相同，否则找不到fallback方法
     */
//  @GetMapping(value = "/fallbackResponse") 
	@RequestMapping(value = "/fallbackResponse",method = RequestMethod.GET)
	@ResponseBody
    public String fallbackResponse(String param) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
        return "fallbackResponse: hystrix here..."+ LocalDateTime.now().format(df);
    }

    
	@RequestMapping(value = "/testhy0",method = RequestMethod.GET)
	@ResponseBody
	public String testhy0() {
		try {
			// 直接正常返回，用于填充熔断采样请求的百分比
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
			String info="【testhy0】0000000 直接返回===  hello !  I am   [" + servername + ":" + ip + ":" + port + "]" + "..."
					+ LocalDateTime.now().format(df);
			System.out.println(info);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return "异常testhy0";
		}
	}
	
    
	@RequestMapping(value = "/testhy1",method = RequestMethod.GET)
	@ResponseBody
//	@HystrixCommand(fallbackMethod = "fallbackResponse", 
//					commandProperties = {
//							@HystrixProperty(name=HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value="THREAD"),
////				            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "2000"),
//				            
//				            @HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS, value = "8000"),
//							
//				            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "3"),
//				            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "5000"),
//				            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50")
//				    },
//		            threadPoolProperties = {
//		                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "3"),
//		                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "2"),
//		                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "1")
//		            })
	public String testhy1(@RequestParam(required=false) String time) throws Exception {
		int timeout=3;//默认3秒
		
		if(time!=null && time.trim().equals("error")) {
			throw new Exception("error主动异常！");
		}
		
		try {
			try {
				if(time!=null && !time.trim().equals("")) {
					timeout = Integer.valueOf(time.trim());
				}
			} catch (Exception e) {
				timeout=3;
				e.printStackTrace();
			}
			Thread.sleep(timeout*1000);//转化成毫秒
			
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
			String info="【testhy1】 ===timeout==["+timeout+"]秒  hello !  I am   [" + servername + ":" + ip + ":" + port + "]" + "..."
					+ LocalDateTime.now().format(df);
			System.out.println(info);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return "异常testhy1";
		}
	}
    
	@RequestMapping(value = "/testquery",method = RequestMethod.GET)
	@ResponseBody
	@HystrixCommand(fallbackMethod = "fallbackResponse", 
					commandProperties = {
							@HystrixProperty(name=HystrixPropertiesManager.EXECUTION_ISOLATION_STRATEGY, value="THREAD"),
				            @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "7000"),
				            
				            @HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS, value = "8000"),
							
				            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "3"),
				            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_SLEEP_WINDOW_IN_MILLISECONDS, value = "5000"),
				            @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50")
				    },
		            threadPoolProperties = {
		                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "3"),
		                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "2"),
		                    @HystrixProperty(name = HystrixPropertiesManager.QUEUE_SIZE_REJECTION_THRESHOLD, value = "1")
		            })
	public String testquery(@RequestParam(required=false) String time) throws Exception {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
		String starttime=LocalDateTime.now().format(df);
		logger.info("testquery--start---"+starttime);
//		try {
	    	String queryUrl="http://127.0.0.1:8902/testhy1?time="+time;
	    	
	    	//设置访问参数
	        HashMap<String, Object> params = new HashMap<>();
	        params.put("aaa", "aaaaaa");
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
	        HttpEntity entity = new HttpEntity<>(params,headers);
	        String responseData=restTemplate.exchange(queryUrl, HttpMethod.GET, entity,String.class).getBody();
//	        Map responseData=restTemplate.exchange(queryUrl, HttpMethod.GET, entity,Map.class).getBody();
//	        ResponseEntity<Map> responseEntity=restTemplate.exchange(queryUrl, HttpMethod.GET, entity,Map.class);
//	        Map responseData = responseEntity.getBody();
	        
	        String endtime=LocalDateTime.now().format(df);
//	        if(responseData!=null) {
//	        	logger.info("testquery方法访问 two服务的testhy1接口。。开始时间点==["+starttime+"],结束时间点==["+endtime+"]。返回 ：\r\n"+responseData.toString());
//	        }
			
			String info="【testquery方法访问 two服务的testhy1接口】 ===time==["+time+"]秒  hello !  I am   [" + servername + ":" + ip + ":" + port + "]" + "..."
					+ "开始时间点==["+starttime+"],结束时间点==["+endtime+"]"+"被调用方返回：<br>"+responseData;
			System.out.println(info);
			return info;
//		} catch (Exception e) {
			
		/* 如果有try-catch，则不会返回fallback。所以必须注释掉 */
			
//			e.printStackTrace();
//			return "异常testhy1";
//		}
	}
	
}