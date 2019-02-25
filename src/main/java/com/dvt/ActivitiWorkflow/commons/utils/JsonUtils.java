package com.dvt.ActivitiWorkflow.commons.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {
	private static Gson gson = new Gson();
	/**
     * JSON字符串转JavaBean
     * @param json
     * @param className
     */
	public static <T> T jsonToJavaBean(String json,Class<T> className) {
        T bean = gson.fromJson(json, className);//对于javabean直接给出class实例
        return bean;
    }
	
	public static String JavaBeanToJson(Object obj){
		return gson.toJson(obj);
	}
	
    /**
     * json字符串转List集合
     */
    public static <T> List<T> jsonToList(String json, Class<T> className) {
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();  
        for(final JsonElement elem : array){  
        	list.add(gson.fromJson(elem, className));  
        }  
        return list;
    }
    /**
     * json字符串转Map
     */
    public static Map<String,String> jsonToMap(String json) {
    	//Map<String,String> map = new HashMap<String,String>();
        Map<String, String> maps = gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
        return maps;
    }
}
