package com.example.gate.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
public class TestHystrixController {

	@Value("${spring.cloud.client.ip-address}")
	private String ip;

	@Value("${spring.application.name}")
	private String servername;

	@Value("${server.port}")
	private String port;

	//RequestMapping千万别叫这个名字 waitFail ，有问题！！！！
	
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
	public String testhy1(@RequestParam(required=false) String time) throws Exception {
		int timeout=3000;
		
		if(time!=null && time.trim().equals("error")) {
			throw new Exception("error主动异常！");
		}
		
		try {
			try {
				if(time!=null && !time.trim().equals("")) {
					timeout = Integer.valueOf(time.trim());
				}
			} catch (Exception e) {
				timeout=3000;
				e.printStackTrace();
			}
			Thread.sleep(timeout*1000);//转化成毫秒
			
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
			String info="【testhy1】 ===timeout==["+timeout+"]  hello !  I am   [" + servername + ":" + ip + ":" + port + "]" + "..."
					+ LocalDateTime.now().format(df);
			System.out.println(info);
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return "异常testhy1";
		}
	}
	
}