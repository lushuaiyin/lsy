package com.example.one.thread;

public class MyJob implements Runnable ,JobHandler {
	private String use;//任务处理需要的对象，比如你的任务是写数据库，那么就需要把数据库连接对象传入，这样具体的任务才能实现
	public MyJob(String use) {
		this.use=use;
	}

	@Override
	public void run() {
		System.out.println(use+"开始任务。。。。");
		String res="";
		if(this.use!=null) {
			res = this.handle();
		}else {
			res ="error-use为空，任务无法执行";
		}
		try {
			System.out.println(use+"模拟任务执行一段时间。。。2秒");
			Thread.sleep(1000*2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(use+"任务完成。结果=="+res);
	}

	@Override
	public String handle() {
		// TODO Auto-generated method stub
		System.out.println(use+"任务具体实现功能。。。。");
		return "success";
	}

}
