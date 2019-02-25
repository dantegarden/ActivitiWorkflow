package com.dvt.ActivitiWorkflow.commons.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class Utils {
	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * 获取文件
	 * @author lij
	 * @param folder 路径 
	 * @param fileName 文件名
	 * **/
	public static File getFilePath(String folder, String fileName) {
		return new File(folder + File.separator + fileName);
	}
	
	/**
	 * 获取properties文件
	 * @author lij
	 * @param path 路径 
	 * **/
	public static Properties loadPropertiesFileFromClassPath(String path) {
		Properties config = new Properties();
		try {
			config.load(new ClassPathResource(path).getInputStream());
		} catch (IOException e) {
			logger.error("load properties file error: {}", e.getMessage());
		}
		return config;
	}
	
	/**
	 * 获取xml文件
	 * @author lij
	 * @param path 路径 
	 * **/
	public static Document loadXmlFileFromClassPath(String path) {
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(new ClassPathResource(path).getInputStream());
		} catch (DocumentException e) {
			logger.error("load xml file error: {}", e.getMessage());
		} catch (IOException e) {
			logger.error("load xml file error: {}", e.getMessage());
		}
		return document;
	}
}
