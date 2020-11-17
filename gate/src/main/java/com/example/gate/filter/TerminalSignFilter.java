package com.example.gate.filter;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author tengdj
 * @date 2019/8/13 11:08
 * 设备接口验签，解密
 **/
//@Slf4j
public class TerminalSignFilter implements GatewayFilter, Ordered {
	Logger logger = LoggerFactory.getLogger(TerminalSignFilter.class);
    private static final String AES_SECURTY = "XXX";
    private static final String MD5_SALT = "XXX";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put("startTime", System.currentTimeMillis());
        if (exchange.getRequest().getMethod().equals(HttpMethod.POST)) {
        	//重新构造request，参考ModifyRequestBodyGatewayFilterFactory
            ServerRequest serverRequest = ServerRequest.create(exchange, HandlerStrategies.withDefaults().messageReaders());
            MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
            //重点
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
            	//因为约定了终端传参的格式，所以只考虑json的情况，如果是表单传参，请自行发挥
                if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType) || MediaType.APPLICATION_JSON_UTF8.isCompatibleWith(mediaType)) {
//                    JSONObject jsonObject = JSONUtil.toJO(body);
//                    String paramStr = jsonObject.getString("param");
                	String paramStr="";//xxxxx
                    String newBody;
                    try{
                        newBody = verifySignature(paramStr);
                    }catch (Exception e){
                        return processError(e.getMessage());
                    }
                    return Mono.just(newBody);
                }
                return Mono.empty();
            });
            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(exchange.getRequest().getHeaders());
            //猜测这个就是之前报400错误的元凶，之前修改了body但是没有重新写content length
            headers.remove("Content-Length");
            //MyCachedBodyOutputMessage 这个类完全就是CachedBodyOutputMessage，只不过CachedBodyOutputMessage不是公共的
//            MyCachedBodyOutputMessage outputMessage = new MyCachedBodyOutputMessage(exchange, headers);
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
            
            return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
                ServerHttpRequest decorator = this.decorate(exchange, headers, outputMessage);
                return returnMono(chain, exchange.mutate().request(decorator).build());
            }));
        } else {
            //GET 验签 
            MultiValueMap<String, String> map = exchange.getRequest().getQueryParams();
            if (!CollectionUtils.isEmpty(map)) {
                String paramStr = map.getFirst("param");
                try{
                    verifySignature(paramStr);
                }catch (Exception e){
                    return processError(e.getMessage());
                }
            }
            return returnMono(chain, exchange);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }


    private Mono<Void> returnMono(GatewayFilterChain chain,ServerWebExchange exchange){
        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            Long startTime = exchange.getAttribute("startTime");
            if (startTime != null){
                long executeTime = (System.currentTimeMillis() - startTime);
                logger.info("耗时：{}ms" , executeTime);
                logger.info("状态码：{}" , Objects.requireNonNull(exchange.getResponse().getStatusCode()).value());
            }
        }));
    }


    private Mono processError(String message) {
            /*exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();*/
        logger.error(message);
        return Mono.error(new Exception(message));
    }

    //CachedBodyOutputMessage  MyCachedBodyOutputMessage
    ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0L) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set("Transfer-Encoding", "chunked");
                }
                return httpHeaders;
            }
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    
    
    private String verifySignature(String paramStr) throws Exception{
//      logger.info("密文{}", paramStr);
//      String dParamStr;
//      try{
//          dParamStr = AESUtil.decrypt(paramStr, AES_SECURTY);
//      }catch (Exception e){
//          throw new Exception("解密失败！");
//      }
//      logger.info("解密得到字符串{}", dParamStr);
//      String signature = SignUtil.sign(dParamStr, MD5_SALT);
//      logger.info("重新加密得到签名{}", signature);
//      JSONObject jsonObject1 = JSONUtil.toJO(dParamStr);
//      if (!jsonObject1.getString("signature").equals(signature)) {
//          throw new Exception("签名不匹配！");
//      }
//      return jsonObject1.toJSONString();
      return paramStr;
  }
}
//————————————————
//版权声明：本文为CSDN博主「seantdj」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
//原文链接：https://blog.csdn.net/seantdj/article/details/100546713
