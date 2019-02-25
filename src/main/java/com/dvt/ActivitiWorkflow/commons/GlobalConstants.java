package com.dvt.ActivitiWorkflow.commons;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class GlobalConstants {
	public static final String INIT_ACT = "manage/main";
	public static final String INIT_QUERY = "manage/query";
	public static final String INIT_BEGIN = "manage/begin";
	public static final String INIT_EXEC = "manage/exec";
	public static final String INIT_DESIGNER = "manage/designer";
	
	public static Map<String, Boolean> lockWorkFlowMap = Maps.newHashMap();
	
	public static Map<String, List<Boolean>> counterSignResultsMap = Maps.newHashMap();
	
	public static final int ALL_PASS = 0;
	public static final int ONE_DENY = 1;
	public static final int UNFINISHED = 2;
	public static final int OTHER = 3;
	public static final int ONEDENY_MODE = 4;
	
	public static final String TASK_STATUS_WORKING = "working";
	public static final String TASK_STATUS_COMPLETED = "done";
	
	public static final String DEFAULT_PASSWORD = "123456";
}
