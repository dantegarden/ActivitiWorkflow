package com.dvt.ActivitiWorkflow.commons.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

public class WebAppCtxHolder {
	
	public static WebApplicationContext getSpringWebAppCtx() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return RequestContextUtils.getWebApplicationContext(request);
	}
	
	public static <T> T getBean(Class<T> clazz) {
		return getSpringWebAppCtx().getBean(clazz);
	}
}