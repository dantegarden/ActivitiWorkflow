package com.dvt.ActivitiWorkflow.commons.utils;

import java.util.List;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dvt.ActivitiWorkflow.business.real.example.service.impl.ProcessCoreServiceImpl;

public class ActivitiUtils {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ActivitiUtils.class);
	/**
	 * 打印输出流程定义
	 * **/
	public static void printProcessDefinition(ProcessDefinition pd){
		if(pd!=null){
			logger.info("流程定义ID:" + pd.getId());// 流程定义的key+版本+随机生成数  
            logger.info("流程定义的名称:" + pd.getName());// 对应xxx.bpmn文件中的name属性值  
            logger.info("流程定义的key:" + pd.getKey());// 对应xxx.bpmn文件中的id属性值  
            logger.info("流程定义的版本:" + pd.getVersion());// 当流程定义的key值相同的相同下，版本升级，默认1  
            logger.info("资源名称bpmn文件:" + pd.getResourceName());  
            logger.info("资源名称png文件:" + pd.getDiagramResourceName());  
            logger.info("部署对象ID：" + pd.getDeploymentId());  
            logger.info("#########################################################");  
		}
	}
	/**
	 * 打印输出流程定义列表
	 * **/
	public static void printProcessDefinitionList(List<ProcessDefinition> list){
		if(CollectionUtils.isNotEmpty(list)){
			for (ProcessDefinition pd : list) {  
	            logger.info("流程定义ID:" + pd.getId());// 流程定义的key+版本+随机生成数  
	            logger.info("流程定义的名称:" + pd.getName());// 对应xxx.bpmn文件中的name属性值  
	            logger.info("流程定义的key:" + pd.getKey());// 对应xxx.bpmn文件中的id属性值  
	            logger.info("流程定义的版本:" + pd.getVersion());// 当流程定义的key值相同的相同下，版本升级，默认1  
	            logger.info("资源名称bpmn文件:" + pd.getResourceName());  
	            logger.info("资源名称png文件:" + pd.getDiagramResourceName());  
	            logger.info("部署对象ID：" + pd.getDeploymentId());  
	            logger.info("#########################################################");  
	        }  
		}
	}
	/**
	 * 打印输出任务列表
	 * **/
	public static void printTaskList(List<Task> list){
		if(CollectionUtils.isNotEmpty(list)){
			for (Task task : list) {  
	            logger.info("任务ID:" + task.getId());  
	            logger.info("任务名称:" + task.getName());  
	            logger.info("任务的创建时间:" + CommonHelper.date2Str(task.getCreateTime(), CommonHelper.DF_DATE_TIME));  
	            logger.info("任务的办理人:" + task.getAssignee());  
	            logger.info("流程实例ID:" + task.getProcessInstanceId());  
	            logger.info("执行对象ID:" + task.getExecutionId());  
	            logger.info("流程定义ID:" + task.getProcessDefinitionId());  
	            logger.info("##################################################");  
	        } 
		}
	}
	
	/**
	 * 打印输出历史任务列表
	 * **/
	public static void printHistoryTaskList(List<HistoricTaskInstance> list){
		if(CollectionUtils.isNotEmpty(list)){
			for (HistoricTaskInstance hti : list) {  
				logger.info("历史任务id:" + hti.getId());
				logger.info("任务名称:" + hti.getName());
				logger.info("任务的创建时间:" + CommonHelper.date2Str(hti.getCreateTime(), CommonHelper.DF_DATE_TIME));  
				logger.info("任务完成时间:" + CommonHelper.date2Str(hti.getEndTime(), CommonHelper.DF_DATE_TIME));
				logger.info("耗时:" + hti.getDurationInMillis());
	            logger.info("任务的办理人:" + hti.getAssignee());
	            logger.info("流程实例ID:" + hti.getProcessInstanceId());  
	            logger.info("执行对象ID:" + hti.getExecutionId());  
	            logger.info("流程定义ID:" + hti.getProcessDefinitionId());  
	            logger.info("################################"); 
			}
		}
	}
}	
