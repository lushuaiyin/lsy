package com.example.gate.init.business.impl;

import org.springframework.stereotype.Service;

import com.example.gate.init.business.InitDataService;

@Service
public class InitSysDataService implements InitDataService{

	@Override
	public boolean initData() {
		System.out.println("初始化系统数据完毕。。");
		return true;
	}

}
