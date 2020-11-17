package com.example.gate.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.gate.lua.LuaUtil;
import com.example.gate.util.EnvPropertyUtil;

/**
 * 查看环境变量测试 
 * 
 * @author lsy
 *
 */
@RestController
@RequestMapping("/env")
public class EnvQueryController {

	@Value("${spring.cloud.client.ip-address}")
	private String ip;
	
	@Value("${spring.application.name}")
	private String servername;
	
	@Value("${server.port}")
	private String port;
	
	@RequestMapping(value = "/getEnv",method = RequestMethod.GET)
    @ResponseBody
    public String getEnv(@RequestParam(required=false) String param){
		System.out.println("getEnv接收请求参数为==="+param); 
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr=LocalDateTime.now().format(df);
        
        String denv=EnvPropertyUtil.getEnvFromD(param);
        String exportenv=EnvPropertyUtil.getEnvFromExport(param);
        String mess="getEnv ! 请求参数["+param+"] 的 -D 值是["+denv+"],export 值是["+exportenv+"]。response form  ["+servername+":"+ip+":"+port+"]"+"..."+dateStr;
        return mess;
        
    }
	
}
