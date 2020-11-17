package com.example.gate.log;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * logback动态修改日志级别。
 * 为了更清楚的看清引入的是什么类，我写了类的全名
 * 
 * 
 * --------------------------------分割-------------------------
 * 下面通过actuator来查看和动态修改日志级别。
 * 
 * 
 * 在yml里暴露健康检查中的logger，就可以通过actuator接口查看所有logger的日志级别
 * 
 * 
 management:
  endpoints:
    web:
      exposure:
        include: 'loggers'

 * 访问：http://localhost:8888/actuator/loggers
 * 
 * 返回json：
 * 
 * {
 * "levels":["OFF","ERROR","WARN","INFO","DEBUG","TRACE"],
 * "loggers":
 * {
 * "ROOT":{"configuredLevel":"INFO","effectiveLevel":"INFO"},
 * "com":{"configuredLevel":null,"effectiveLevel":"INFO"},
 * "com.example":{"configuredLevel":null,"effectiveLevel":"INFO"},
 * "com.example.gate":{"configuredLevel":null,"effectiveLevel":"INFO"},
 * "com.example.gate.GateApplication":{"configuredLevel":null,"effectiveLevel":"INFO"},
 * 。。。。略。。。。。。。。。。
 * "reactor.util.Loggers$LoggerFactory":{"configuredLevel":null,"effectiveLevel":"INFO"}
 * }
 * }
 * 
 * 每个包到每个类都有一个日志级别。
 * 
 * 
 * 我想查看具体一个包的日志级别，不想查看全部，打印出来太多了
 * http://localhost:8888/actuator/loggers/com.example.gate
 * 返回：
 * {"configuredLevel":null,"effectiveLevel":"INFO"}
 * 
 * 
 * 
 * 看具体一个类的级别：
 * http://localhost:8888/actuator/loggers/com.example.gate.GateApplication
 * 返回：
 * {"configuredLevel":null,"effectiveLevel":"INFO"}
 * 
 * -------------------------------------分割---------------------------------
 * 
 * actuator既然有查看日志级别的能力，必然有修改级别的能力。
 * actuator使用了rest风格的接口，修改的接口还是上面按个地址，只不过请求改成了POST请求。
 * 用postman发post请求，请求体是：
 * {"configuredLevel":"ERROR"}
 * 
 * http://localhost:8888/actuator/loggers/com.example.gate
 * 
 * 再执行下查询接口，返回：
 * {
    "configuredLevel": "ERROR",
    "effectiveLevel": "ERROR"
}
 * 修改成功！！！
 * 
 * 
 * 上面是actuator提供的接口，我在这个类中写的是logback提供的修改接口。
 * 
 * 
 * 
 * @author lsy
 *
 */
@RestController
public class ChangeLogLevelController {

	org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ChangeLogLevelController.class);

	@RequestMapping(value = "logLevel/{logLevel}")
	public String changeLogLevel(@PathVariable("logLevel") String logLevel) {
		//logback动态修改日志级别
		try {
			ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext) org.slf4j.LoggerFactory
					.getILoggerFactory();
			loggerContext.getLogger("org.mybatis").setLevel(ch.qos.logback.classic.Level.valueOf(logLevel));
			loggerContext.getLogger("org.springframework").setLevel(ch.qos.logback.classic.Level.valueOf(logLevel));
		} catch (Exception e) {
			logger.error("动态修改日志级别出错", e);
			return "fail";
		}
		return "success";
	}
	
}
