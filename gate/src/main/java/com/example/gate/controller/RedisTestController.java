package com.example.gate.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.gate.lua.LuaUtil;

/**
 * RedisAutoConfiguration 
 * 
 * @author lsy
 *
 */
@RestController
public class RedisTestController {

//	@Resource(name="redisTemplateMy")
//	private RedisTemplate redisTemplateMy;
//	@Autowired     
//	private StringRedisTemplate  stringRedisTemplate;
	
	@Autowired     
	private RedisTemplate redisTemplate;
	
	
	@RequestMapping(value = "/helloRedis",method = RequestMethod.GET)
    @ResponseBody
    public String helloRedis(@RequestParam(required=false) String param){
		System.out.println("helloRedis接收请求体为===\r\n"+param); 
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr=LocalDateTime.now().format(df);
        
        String value=param+"-------"+dateStr;
        String key="testkey";
        this.redisTemplate.opsForValue().set(key, value);
        String res=(String) this.redisTemplate.opsForValue().get(key);
        String print="查询redis结果===key=="+key+",值=="+res;
		System.out.println(print);
//		String key22="testkey22";
//        this.stringRedisTemplate.opsForValue().set(key22, value+"22222");
//        String res222=(String) this.stringRedisTemplate.opsForValue().get(key22);
//        String print22="查询redis结果===key22=="+key22+",值=="+res222;
//		System.out.println(print22);
		
		////判断springboot帮我创建的对象和我自己声明的对象是否是同一个
//		System.out.println("判断是否相等===="+(redisTemplateMy.equals(redisTemplate)));
		
        return print;
        
    }
	
	@Autowired
	private LuaUtil luaUtil;
	
	@RequestMapping(value = "/lua1",method = RequestMethod.GET)
    @ResponseBody
    public String lua1(@RequestParam(required=false) String param){
		System.out.println("lua1接收请求体为===\r\n"+param); 
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr=LocalDateTime.now().format(df);
        
        luaUtil.evalLua();
        
        String print="lua1执行 evalLua"+dateStr;
		System.out.println(print);
        return print;
    }
	
	@RequestMapping(value = "/lua2",method = RequestMethod.GET)
    @ResponseBody
    public String lua2(@RequestParam(required=false) String param){
		System.out.println("lua2接收请求体为===\r\n"+param); 
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr=LocalDateTime.now().format(df);
        
        luaUtil.evalLua2();
        
        String print="lua2执行 evalLua"+dateStr;
		System.out.println(print);
        return print;
    }
	
	@RequestMapping(value = "/lua3",method = RequestMethod.GET)
    @ResponseBody
    public String lua3(@RequestParam(required=false) String param){
		System.out.println("lua3接收请求体为===\r\n"+param); 
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateStr=LocalDateTime.now().format(df);
        
        luaUtil.evalLua3();
        
        String print="lua3执行 evalLua"+dateStr;
		System.out.println(print);
        return print;
    }
	
}
