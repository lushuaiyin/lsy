package com.example.one.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;

//import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
//import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 
 将HTTP请求重定向到HTTPS（可选）
不能同时在application.yml中同时配置两个connector，
所以要以编程的方式配置HTTP connector，然后重定向到HTTPS connector


springboot1.x这样配置：

@Bean
  public EmbeddedServletContainerFactory servletContainer(){
      TomcatEmbeddedServletContainerFactory tomcat=new TomcatEmbeddedServletContainerFactory(){
          @Override
          protected void postProcessContext(Context context) {
              SecurityConstraint securityConstraint=new SecurityConstraint();
              securityConstraint.setUserConstraint("CONFIDENTIAL");//confidential
              SecurityCollection collection=new SecurityCollection();
              collection.addPattern("/*");
              securityConstraint.addCollection(collection);
              context.addConstraint(securityConstraint);
          }
      };
      tomcat.addAdditionalTomcatConnectors(httpConnector());
      return tomcat;
  }
  
 * @author lsy
 *
 */
//@Configuration
public class HttpsConfig {

//	@Value("${server.port}")
//	private String port;//配置的是https端口8443
//	
//	@Bean
//	public TomcatServletWebServerFactory servletContainer() {
//		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//			@Override
//			protected void postProcessContext(Context context) {
// 
//				SecurityConstraint securityConstraint = new SecurityConstraint();
//				securityConstraint.setUserConstraint("CONFIDENTIAL");
//				SecurityCollection collection = new SecurityCollection();
//				collection.addPattern("/*");
//				securityConstraint.addCollection(collection);
//				context.addConstraint(securityConstraint);
//			}
//		};
//		tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
//		return tomcat;
//	}
// 
//	private Connector initiateHttpConnector() {
//		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//		connector.setScheme("http");
//		connector.setPort(8901);//http端口
//		connector.setSecure(false);
//		connector.setRedirectPort(Integer.valueOf(port));//转发到https端口8443
//		
//		
//		Http11NioProtocol protocol = (Http11NioProtocol)connector.getProtocolHandler();  
//        //设置最大连接数  
//        protocol.setMaxConnections(2000);  
//        //设置最大线程数  
//        protocol.setMaxThreads(2000);  
//        protocol.setConnectionTimeout(30000);  
////        protocol.setKeepAliveTimeout(keepAliveTimeout);
//		return connector;
//	}

}
