package com.example.one.aspect.truth;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * Cglib动态代理
在实际开发中，可能需要对没有实现接口的类增强，用JDK动态代理的方式就没法实现。采用Cglib动态代理可以对没有实现接口的类产生代理，实际上是生成了目标类的子类来增强。

 * @author lsy
 *
 */
public class CglibProxyTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final LinkManDao linkManDao = new LinkManDao();
        // 创建cglib核心对象
        Enhancer enhancer = new Enhancer();
        // 设置父类
        enhancer.setSuperclass(linkManDao.getClass());
        // 设置回调
        enhancer.setCallback(new MethodInterceptor() {
            /**
             * 当你调用目标方法时，实质上是调用该方法
             * intercept四个参数：
             * proxy:代理对象
             * method:目标方法
             * args：目标方法的形参
             * methodProxy:代理方法
            */
            @Override
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy)
                    throws Throwable {
                System.out.println("cglib记录日志");
                 Object result = method.invoke(linkManDao, args);
                return result;
            }
        });
        // 创建代理对象
        LinkManDao proxy = (LinkManDao) enhancer.create();
        proxy.save();
	}

}
