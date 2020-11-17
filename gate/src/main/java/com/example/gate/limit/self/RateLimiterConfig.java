package com.example.gate.limit.self;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 读取yml配置的属性类
 * 
 * @author lsy
 *
 */
@Component
@ConfigurationProperties(prefix = "selfratelimiter")
public class RateLimiterConfig {
	
	public static final String SPILT_KEY="_";//限流key分隔符
	public static final String SPILT_SPEED="@";//限流参数分隔符
	
    //限流速率@令牌桶大小
    //按照业务渠道自定义限流
    private Map<String , String> rateLimitChannel = new ConcurrentHashMap<String , String>(){};
    
    //按照IP进行自定义限流(跟全局的统一配置不同，每个ip可配置限流参数)
    private Map<String , String> rateLimitIP = new ConcurrentHashMap<String , String>(){};

    //按照url和对应的业务渠道自定义限流
    private Map<String , String> rateLimitUrlAndChannel = new ConcurrentHashMap<String , String>(){};

    
	public Map<String, String> getRateLimitChannel() {
		return rateLimitChannel;
	}

	public void setRateLimitChannel(Map<String, String> rateLimitChannel) {
		this.rateLimitChannel = rateLimitChannel;
	}

	public Map<String, String> getRateLimitIP() {
		return rateLimitIP;
	}

	public void setRateLimitIP(Map<String, String> rateLimitIP) {
		this.rateLimitIP = rateLimitIP;
	}

	public Map<String, String> getRateLimitUrlAndChannel() {
		return rateLimitUrlAndChannel;
	}

	public void setRateLimitUrlAndChannel(Map<String, String> rateLimitUrlAndChannel) {
		this.rateLimitUrlAndChannel = rateLimitUrlAndChannel;
	}

	@Override
	public String toString() {
		return "RateLimiterConfig [rateLimitChannel=" + rateLimitChannel + ", rateLimitIP=" + rateLimitIP
				+ ", rateLimitUrlAndChannel=" + rateLimitUrlAndChannel + "]";
	}
	
	
	/**
	 * 因为yml里的数据结构不是我们代码需要的数据结构，所以要在bean初始化后进行数据结构重构。
	 * 
    yml的数据定义：
  rateLimitUrlAndChannel:
    default: 66@80
    url_1: /one/limit/sprint
    channel_url_1: channelA
    limit_url_1: 1@2
    
    我们实际需要放在map中的结构：
    default: 66@80
    _one_limit_sprint@channelA : 1@2
    
    因为url的斜杠放在map中有问题，所以，url的斜杠替换成_。
    这样限流的key的结构就是 ： 替换/为_的url@渠道  
   对应的限流参数还是用 @分割。
    
    我们定义了限流key的结构是_one_limit_sprint@channelA=1@2
    那么在定义KeyResolver对象时，也要对应这么取数据。
    在KeyResolver之前，还要在filter中村这样的数据。
 
	 */
	@PostConstruct
    public void init() {
		Map<String , String> resMap = new ConcurrentHashMap<String , String>(){};
		if(this.getRateLimitUrlAndChannel()!=null) {
			Iterator it = this.getRateLimitUrlAndChannel().keySet().iterator();
			while(it.hasNext()) {
				String key = (String)it.next();
				String value = (String)this.getRateLimitUrlAndChannel().get(key);//
				
				if(key!=null) {
					if(key.trim().equals("default")) {
						resMap.put(key, value);//default : 66@80
					}
					if(key.trim().startsWith("url_")) {//具体url加渠道限流 url_1
						String channelKey="channel_"+key;//channel_url_1
						String limitKey="limit_"+key;//limit_url_1
						
						String channelValue = (String)this.getRateLimitUrlAndChannel().get(channelKey);//channelA
						String limitValue = (String)this.getRateLimitUrlAndChannel().get(limitKey);//1@2
						
						String urlKey=value.replace("/", "_");///one/limit/sprint 转化 _one_limit_sprint
						String resKey=urlKey+"@"+channelValue;//_one_limit_sprint@channelA : 1@2
						
						resMap.put(resKey, limitValue);//_one_limit_sprint@channelA : 1@2
					}
					
					continue;
				}
			}//end while
			
			this.getRateLimitUrlAndChannel().clear();
			this.getRateLimitUrlAndChannel().putAll(resMap);
		}
		
        System.out.println("初始化rateLimitUrlAndChannel数据结构。。。"+this.toString());
    }

    
}