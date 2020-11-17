package com.example.one.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolUtil {
	private static PoolUtil pool=null;
	static {
		pool=new PoolUtil();
	}
	public static PoolUtil getInstance() {
		if(pool==null) {
			pool=new PoolUtil();
		}
		return pool;
	}

	public ExecutorService executorService = Executors.newFixedThreadPool(3);
	private ExecutorService executorService2 = new ThreadPoolExecutor(3, 10, 5L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
	
	private void executeTask(MyJob job) {
		this.executorService.submit(job);
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PoolUtil pool=PoolUtil.getInstance();
		
		
		MyJob job1=new MyJob("任务1");
		MyJob job2=new MyJob("任务2");
		MyJob job3=new MyJob("任务3");
		
		pool.executeTask(job1);
		pool.executeTask(job2);
		pool.executeTask(job3);
		
		MyJob job999=new MyJob("任务999");
//		pool.executorService2.submit(job999);
		
		
		System.out.println("线程池是否关闭pool.executorService---"+pool.executorService.isShutdown());
		System.out.println("线程池是否关闭pool.executorService2---"+pool.executorService2.isShutdown());
		System.out.println("程序关闭线程池1");
		pool.executorService.shutdown();
//		pool.executorService2.shutdown();
		
		
		
		try {
			System.out.println("模拟主程序执行一段时间。-------。。10秒");
			Thread.sleep(1000*10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("线程池是否关闭pool.executorService---"+pool.executorService.isShutdown());
		System.out.println("线程池是否关闭pool.executorService2---"+pool.executorService2.isShutdown());
		
	}

}
