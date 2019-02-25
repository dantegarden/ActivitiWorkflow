package com.dvt.ActivitiWorkflow.business.real.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dvt.ActivitiWorkflow.business.real.example.entity.ProcessDefination;
import com.dvt.ActivitiWorkflow.commons.GlobalConstants;

@Controller
@RequestMapping("/test")
public class TestController {
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@RequestMapping
	public String init() {
		return GlobalConstants.INIT_ACT;
	}
}
