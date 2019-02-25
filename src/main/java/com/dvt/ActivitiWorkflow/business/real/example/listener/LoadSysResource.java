package com.dvt.ActivitiWorkflow.business.real.example.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.dvt.ActivitiWorkflow.business.real.example.service.InitService;

@Component
public class LoadSysResource implements ApplicationListener<ContextRefreshedEvent>{
	@Autowired
	private InitService initService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
		if(event.getApplicationContext().getParent() != null){
			initService.init();
		}
	}
	

	
}
