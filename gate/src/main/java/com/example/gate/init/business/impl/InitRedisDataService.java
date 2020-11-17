package com.example.gate.init.business.impl;

import org.springframework.stereotype.Service;

import com.example.gate.init.business.InitDataService;
@Service
public class InitRedisDataService implements InitDataService{

	@Override
	public boolean initData() {
		System.out.println("初始化redis数据完毕。。");
		return true;
	}

}
