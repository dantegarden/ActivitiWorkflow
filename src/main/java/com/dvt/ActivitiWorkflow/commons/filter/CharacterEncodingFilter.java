package com.dvt.ActivitiWorkflow.commons.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharacterEncodingFilter implements Filter  {
	
	private String characterEncoding; // 编码方式，配置在web.xml中
	
	private boolean enabled; // 是否开启用该Filter,配置在web.xml中
	
	public void init(FilterConfig config) throws ServletException { // 初始化时加载参数
		characterEncoding = config.getInitParameter("characterEncoding");
		// 编码方式
		enabled = "true".equalsIgnoreCase(config.getInitParameter("enabled").trim());
		// 启用
	}
	
	public void destroy() {
		characterEncoding = null; // 销毁时清空资源
		
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (enabled || characterEncoding != null) { // 如果启用该Filter
			request.setCharacterEncoding(characterEncoding); // 设置request编码
		}
		if (enabled || characterEncoding != null) { // 如果启用该Filter
			response.setCharacterEncoding(characterEncoding); // 设置response编码
		}
		chain.doFilter(request, response);
	}
}
