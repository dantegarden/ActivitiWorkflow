package com.dvt.ActivitiWorkflow.business.real.example.task;

import java.util.Arrays;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class AssgineeMultiInstancePer implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		 System.out.println("设置会签环节的人员.");  
	     execution.setVariable("pers", Arrays.asList("AAAA", "BBBB", "CCCC")); 
	}

}
