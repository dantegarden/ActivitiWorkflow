package com.dvt.ActivitiWorkflow.business.test;

import java.security.MessageDigest;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class DevTest {
	
	public static String getCheckSum(String useKey, String nonce, String curTime){
		return encode("sha1", useKey+nonce+curTime);
	}
	
	private static String encode(String algorithm, String value){
		if (value == null){
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			messageDigest.update(value.getBytes());
			return getFormattedText(messageDigest.digest());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		
	}
	
	private static String getFormattedText(byte[] bytes){
		int len = bytes.length;
		StringBuffer buffer = new StringBuffer(len * 2);
		for (int j = 0; j < len; j++){
			buffer.append(HEX_DIGITS[(bytes[j] >> 4)  & 0x0f]);
			buffer.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buffer.toString();
	}
	
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	@Test
	public void test22() { 
		System.out.println(getCheckSum("333", "333", "333")); 
    } 
	
	private static final Logger logger = LoggerFactory.getLogger(DevTest.class);  
	
	public void initActiviti() {
		String resource = "activiti-context.xml";// 配置文件名称
		String beanName = "processEngineConfiguration";// 配置id值
		ProcessEngineConfiguration conf = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResource(resource,
						beanName);
		ProcessEngine processEngine = conf.buildProcessEngine();
	}
	
	//@Test
	public void testMethod(){
		Map<String,Boolean> aa = Maps.newHashMap();
		aa.put("11111", Boolean.TRUE);
		aa.put("11111", Boolean.FALSE);
		aa.get("11111");
	}
}
