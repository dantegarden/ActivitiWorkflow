package com.dvt.ActivitiWorkflow.business.real.example.service.impl;

import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvt.ActivitiWorkflow.business.real.example.service.ProcessCoreService;
import com.dvt.ActivitiWorkflow.business.real.example.service.UserService;
import com.dvt.ActivitiWorkflow.commons.GlobalConstants;
import com.dvt.ActivitiWorkflow.commons.query.DynamicQuery;
import com.google.common.collect.ImmutableList;
import com.sun.org.apache.bcel.internal.generic.IMUL;
@Transactional
@Service("userService")
public class UserServiceImpl implements UserService{
	
	private static final Logger logger = LoggerFactory
			.getLogger(UserServiceImpl.class);

	@Autowired
	private ProcessEngine processEngine;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private HistoryService historyService;
	@Autowired
	private ManagementService managementService;
	@Autowired
	private ProcessCoreService processCoreService;
	@Autowired
	private DynamicQuery dynamicQuery;
	
	@Override
	public String getLeaderApprovalRoles(String variables) {
		String[] vars = variables.split(",");
		if(vars.length==2){
			return "project manager,department manager";
		}else if(vars.length==3){
			return "developer,project manager,department manager";
		}else{
			return "project manager";
		}
	}
	
	@Override
	public String getAssginerRoles(String userId) {
		String navtiveSql = "select ec.id_ from ACT_ID_GROUP ec where ec.id_ = ?1 ";
		List<Object[]> list = dynamicQuery.nativeQuery(navtiveSql, "project manager");
		Object obj = list.get(0);
		String name = obj.toString();
		return name;
	}

	
	@Override
	public String findLeader(String userId) {
		if(userId.equals("BBBB")){
			return "AAAA";
		}
		return "";
	}

	@Override
	public String findDepartLeader(String userId) {
		if(userId.equals("BBBB")){
			return "EEEE";
		}
		return "";
	}

	@Override
	public String findDepartGroup(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSignUser(ActivityExecution execution) {
		return ImmutableList.of("AAAA","BBBB");
	}

	@Override
	public boolean isComplete(ActivityExecution execution) {
		String nodeId = execution.getActivity().getId();  
		logger.info("executionId:{}",execution.getId());
		String nodeNome = (String) execution.getActivity().getProperty("name");  
	    String actInstId=execution.getProcessInstanceId(); 
	    ProcessInstance pi = processCoreService.findProcessInstance(actInstId); 
	    String pdid = pi.getProcessDefinitionId();
	    //已经完成的实例的数量
	    Integer completeCounter = (Integer)execution.getVariable("nrOfCompletedInstances");  
	    //当前活动的实例的数量，即还没有 完成的实例数量
	    Integer activeCounter = (Integer)execution.getVariable("nrOfActiveInstances");  
	    //总循环次数  
	    Integer instanceOfNumbers = (Integer)execution.getVariable("nrOfInstances"); 
	    
	    if(completeCounter==instanceOfNumbers){
	    	System.out.println("全票通过");
	    	return Boolean.TRUE;
	    }else{
	    	return Boolean.FALSE;
	    }
	    
	}

	@Override
	public boolean oneDeny(ActivityExecution execution) {
		String nodeId = execution.getActivity().getId();
		logger.info("executionId:{}",execution.getId());
		String nodeNome = (String) execution.getActivity().getProperty("name");  
	    String actInstId=execution.getProcessInstanceId(); 
	    ProcessInstance pi = processCoreService.findProcessInstance(actInstId); 
	    List<HistoricTaskInstance> hists = processCoreService.findHistoryTasksByProcessInstanceId(actInstId);
	    HistoricTaskInstance perviousTask = hists.get(hists.size()-2);
	    //已经完成的实例的数量
	    Integer completeCounter = (Integer)execution.getVariable("nrOfCompletedInstances");  
	    //当前活动的实例的数量，即还没有 完成的实例数量
	    Integer activeCounter = (Integer)execution.getVariable("nrOfActiveInstances");  
	    //总循环次数  
	    Integer instanceOfNumbers = (Integer)execution.getVariable("nrOfInstances"); 
	    
	    List<String> signStatus = processCoreService.findCounterSignStatus(perviousTask.getId());
	    if(CollectionUtils.isNotEmpty(signStatus)){
	    	int participantNum = signStatus.size();
	    	int unfinished = 0;
	    	int deny = 0;
	    	int pass = 0;
	    	for (String status : signStatus) {
				if(StringUtils.isBlank(status)||"null".equals(status)){
					unfinished++;
				}else if("false".equals(status)){
					deny++;
				}else{
					pass++;
				}
			}
	    	if(deny>0){
	    		logger.info("一票否决:"+ deny + "/" + participantNum );
	    		return Boolean.TRUE;
	    	}else if(pass==participantNum){
	    		logger.info("全票通过:"+ pass + "/" + participantNum );
	    		return Boolean.TRUE;
	    	}
	    }
	    return Boolean.FALSE;
	    
	    /*if(completeCounter==instanceOfNumbers){
	    	System.out.println("全票通过");
	    	return Boolean.TRUE;
	    }else{
	    	return Boolean.FALSE;
	    }*/
	    
	}


	

	
}
