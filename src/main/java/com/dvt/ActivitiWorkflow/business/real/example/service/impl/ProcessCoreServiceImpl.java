package com.dvt.ActivitiWorkflow.business.real.example.service.impl;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.RuntimeServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvt.ActivitiWorkflow.business.real.example.dao.CounterSignRecordDao;
import com.dvt.ActivitiWorkflow.business.real.example.dao.TaskDefinationDao;
import com.dvt.ActivitiWorkflow.business.real.example.dto.RoleDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.UserDTO;
import com.dvt.ActivitiWorkflow.business.real.example.entity.CounterSignRecord;
import com.dvt.ActivitiWorkflow.business.real.example.entity.CounterSignRecordPk;
import com.dvt.ActivitiWorkflow.business.real.example.service.ProcessCoreService;
import com.dvt.ActivitiWorkflow.business.real.example.vo.DeploymentVO;
import com.dvt.ActivitiWorkflow.commons.GlobalConstants;
import com.dvt.ActivitiWorkflow.commons.query.DynamicQuery;
import com.dvt.ActivitiWorkflow.commons.utils.ActivitiUtils;
import com.dvt.ActivitiWorkflow.commons.utils.CommonHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
@Transactional
@Service
public class ProcessCoreServiceImpl implements ProcessCoreService {

	private static final Logger logger = LoggerFactory
			.getLogger(ProcessCoreServiceImpl.class);

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
	private IdentityService identityService;
	@Autowired
	private FormService formService;
	@Autowired
	private CounterSignRecordDao  counterSignRecordDao;
	@Autowired
	private DynamicQuery dynamicQuery;
	@Autowired
	private TaskDefinationDao taskDefinationDao;
	
	
	@Override
	public ProcessInstance startWorkflow(String processDefinitionKey,String variables) {
		if (StringUtils.isNotBlank(processDefinitionKey)) {
			
			ProcessInstance pi = null;
			//挂载流程变量
			if(StringUtils.isNotBlank(variables)){
				identityService.setAuthenticatedUserId(variables);
			}
			// 使用流程定义的key启动流程实例,key对应xml中的流程id 
			pi = runtimeService.startProcessInstanceByKey(processDefinitionKey);
			
			logger.info("=========流程启动");
			logger.info("=========流程实例ID:{}", pi.getId());
			logger.info("=========流程定义ID:{}", pi.getProcessDefinitionId());
			return pi;
		}
		return null;
	}
	
	@Override
	public ProcessInstance findProcessInstance(String processInstanceId) {
		if(StringUtils.isNotBlank(processInstanceId)){
			ProcessInstance pi = runtimeService
					.createProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			logger.info("=========查询流程ID:{}",pi.getId());
			logger.info("=========activityId:{}",pi.getActivityId());
			return pi;
		}
		return null;
	}
	
	@Override
	public HistoricProcessInstance findHistoryProcessInstance(String processInstanceId) {
		if(StringUtils.isNotBlank(processInstanceId)){
			HistoricProcessInstance hpi = historyService
					.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			logger.info("=========查询历史流程ID:{}",hpi.getId());
			logger.info("=========历史流程开始时间:{}",CommonHelper.date2Str(hpi.getStartTime(), CommonHelper.DF_DATE_TIME));
			return hpi;
		}
		return null;
	}

	
	@Override
	public List<Task> findTasksByUser(String userid) {
		List<Task> list = taskService.createTaskQuery() // 创建任务查询对象
				.taskAssignee(userid) // 指定个人任务查询，指定办理人 
				// .taskCandidateGroup("")//组任务的办理人查询  
				// .processDefinitionId("")//使用流程定义ID查询
				// .processInstanceId("")//使用流程实例ID查询
				// .executionId(executionId)//使用执行对象ID查询
				.orderByTaskCreateTime().asc() // 使用优先级的升序排列
				.list();
		List<Task> groupTask = taskService.createTaskQuery().taskCandidateUser(userid).list();
		if(CollectionUtils.isNotEmpty(groupTask)){
			list.addAll(groupTask);
		}
		ActivitiUtils.printTaskList(list);
		return list;
	}

	@Override
	public List<Task> findTasksByUserAndProcess(String userid, String processId) {
		List<Task> list = taskService.createTaskQuery() // 创建任务查询对象
				.taskAssignee(userid) // 指定个人任务查询，指定办理人 
				.processInstanceId(processId)//使用流程实例ID查询
				// .taskCandidateGroup("")//组任务的办理人查询  
				// .processDefinitionId("")//使用流程定义ID查询
				// .executionId(executionId)//使用执行对象ID查询
				.orderByTaskPriority().asc() // 使用优先级的升序排列
				.list();
		ActivitiUtils.printTaskList(list);
		return list;
	}

	@Override
	public void completeTask(String taskId) {
		if(StringUtils.isNotBlank(taskId)){
			queryLock(taskId);//查询锁，解锁继续
			taskService.complete(taskId);
			logger.info("=========完成任务:任务ID:{}",taskId);
		}
	}
	
	@Override
	public boolean assigneeTask(String taskId, String userId) {
		
		if(StringUtils.isNotBlank(taskId)&&StringUtils.isNotBlank(userId)){
			try {
				//更新任务记录的委办人
				Task curTask = taskService.createTaskQuery().taskId(taskId).singleResult();
				if(StringUtils.isNotBlank(curTask.getAssignee())){
					logger.info("=========任务{}已被{}认领过了",taskId,curTask.getAssignee());
					logger.info("=========任务{}被转让给{}",taskId,userId);
					updateSignTaskAssigneer(taskId,userId);
					taskService.setAssignee(taskId, userId);
				}else{
					updateSignTaskAssigneer(taskId,userId);
					taskService.claim(taskId, userId);
					logger.info("=========用户"+userId+"认领任务:任务ID:{}",taskId);
				}
				return Boolean.TRUE;
			} catch (ActivitiTaskAlreadyClaimedException  e) {
				//taskService.setAssignee(taskId, userId);
				//
				return Boolean.FALSE;
			}
		}
		return Boolean.FALSE;
	}
	
	@Override
	public List<Task> findTasksByProcessInstanceId(String processInstanceId) {
		List<Task> list = taskService
				.createTaskQuery()
				.processInstanceId(processInstanceId)
				.list();
		ActivitiUtils.printTaskList(list);
		return list;
	}
	@Override
	public Task findNextTaskByProcessInstanceId(String processInstanceId) {
		Task task = taskService
		.createTaskQuery()
		.processInstanceId(processInstanceId)
		.active().singleResult();
		return task;
	}
	@Override
	public List<Task> findNextTasksByProcessInstanceId(String processInstanceId) {
		List<Task> tasks = taskService
		.createTaskQuery()
		.processInstanceId(processInstanceId)
		.active().list();
		return tasks;
	}
	@Override
	public List<HistoricTaskInstance> findHistoryTasksByProcessInstanceId(
			String processInstanceId) {
		List<HistoricTaskInstance> list = historyService//与历史数据（历史表）相关的service  
	            .createHistoricTaskInstanceQuery()//创建历史任务实例查询  
	            .processInstanceId(processInstanceId)  
	            //.taskAssignee(taskAssignee)//指定历史任务的办理人  
	            .list();
		List<HistoricTaskInstance> results = Lists.newArrayList();
		for (HistoricTaskInstance task : list) {
			if(!task.getTaskDefinitionKey().contains("-pass")){//过滤会签后的临时任务
					results.add(task);
			}
		}
		ActivitiUtils.printHistoryTaskList(results);
		return results;
	}

	@Override
	public List<HistoricTaskInstance> findHistoryTasksByUserIdandProcessInstanceId(
			String userid, String processInstanceId) {
		List<HistoricTaskInstance> list = historyService//与历史数据（历史表）相关的service  
	            .createHistoricTaskInstanceQuery()//创建历史任务实例查询  
	            .processInstanceId(processInstanceId)  
	            .taskAssignee(userid)//指定历史任务的办理人  
	            .list();
		List<HistoricTaskInstance> results = Lists.newArrayList();
		for (HistoricTaskInstance task : list) {
			if(!task.getTaskDefinitionKey().contains("-pass")){//过滤会签后的临时任务
				results.add(task);
			}
		}
		ActivitiUtils.printHistoryTaskList(results);
		return results;
	}
	
	@Override
	public List<HistoricTaskInstance> findActiveHistoryTasksByProcessInstanceId(
			String processInstanceId) {
		List<HistoricTaskInstance> list = historyService//与历史数据（历史表）相关的service  
	            .createHistoricTaskInstanceQuery()//创建历史任务实例查询  
	            .processInstanceId(processInstanceId)  
	            //.taskAssignee(taskAssignee)//指定历史任务的办理人  
	            .list();
		List<HistoricTaskInstance> results = Lists.newArrayList();
		for (HistoricTaskInstance task : list) {
			if(!task.getTaskDefinitionKey().contains("-pass")){//过滤会签后的临时任务
				if(hasMultiInstanceCharacteristics(task.getProcessDefinitionId(), task.getTaskDefinitionKey())){//会签节点
					if(task.getEndTime()!=null){//已完成的会签任务
						String status = findCounterSignStatusByUser(task.getId(),task.getAssignee());//该次会签驳回和未审批的人员列表
						if(StringUtils.isNotBlank(status)){
							results.add(task);
						}
					}else{//未完成的会签任务
						results.add(task);
					}
					
				}else{//普通节点
					results.add(task);
				}
			}
		}
		ActivitiUtils.printHistoryTaskList(results);
		return results;
	}

	@Override
	public List<HistoricTaskInstance> findActiveHistoryTasksByUserIdandProcessInstanceId(
			String userid, String processInstanceId) {
		List<HistoricTaskInstance> list = historyService//与历史数据（历史表）相关的service  
	            .createHistoricTaskInstanceQuery()//创建历史任务实例查询  
	            .processInstanceId(processInstanceId)  
	            .taskAssignee(userid)//指定历史任务的办理人  
	            .list();
		List<HistoricTaskInstance> results = Lists.newArrayList();
		for (HistoricTaskInstance task : list) {
			if(!task.getTaskDefinitionKey().contains("-pass")){//过滤会签后的临时任务
				if(hasMultiInstanceCharacteristics(task.getProcessDefinitionId(), task.getTaskDefinitionKey())){//会签节点
					if(task.getEndTime()!=null){//已完成的会签任务
						String status = findCounterSignStatusByUser(task.getId(),task.getAssignee());//该次会签驳回和未审批的人员列表
						if(StringUtils.isNotBlank(status)){
							results.add(task);
						}
					}else{//未完成的会签任务
						results.add(task);
					}
				}else{//普通节点
					results.add(task);
				}
			}
		}
		ActivitiUtils.printHistoryTaskList(results);
		return results;
	}
	
	@Override
	public boolean isProcessEnd(String processInstanceId) {
		if(StringUtils.isNotBlank(processInstanceId)){
			ProcessInstance pi = runtimeService
					.createProcessInstanceQuery() //创建流程实例查询
					.processInstanceId(processInstanceId) //使用流程实例ID查询 
					.singleResult();
			if(pi==null){
				return Boolean.TRUE;
			}else{
				return Boolean.FALSE;
			}
		}
		return false;
	}

	@Override
	public void deoploymentProcessDefinitionByClasspath(List<String> bpmns, String name) {
		Deployment deployment = repositoryService
				.createDeployment() // 创建一个部署对象
				.name(name) // 添加部署的名称
				.addClasspathResource(bpmns.get(0))// 从classpath的资源中加载，一次只能加载一个文件  
	            .addClasspathResource(bpmns.get(1))// 从classpath的资源中加载，一次只能加载一个文件  
	            .deploy();// 完成部署  
		logger.info("=========部署流程定义完成");
		logger.info("=========部署ID:{}", deployment.getId());
		logger.info("=========部署名称:{}", deployment.getName());
	}

	@Override
	public void deploymentProcessDefinitionByZip(String bpmnsZip, String name) {
		InputStream in = this.getClass().getClassLoader()  
	            .getResourceAsStream(bpmnsZip); 
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deployment = repositoryService// 与流程定义和部署对象相关的service  
	            .createDeployment()// 创建一个部署对象  
	            .name(name)// 添加部署  
	            .addZipInputStream(zipInputStream)// 指定zip格式的文件完成部署  
	            .deploy();// 完成部署 
		logger.info("=========部署流程定义完成");
		logger.info("=========部署ID:{}", deployment.getId());
		logger.info("=========部署名称:{}", deployment.getName());
	}

	@Override
	public List<ProcessDefinition> findProcessDefinition(String processDefinitionId) {
		List<ProcessDefinition> list = repositoryService
				.createProcessDefinitionQuery() // 创建一个流程定义的查询
				/** 指定查询条件，where条件 */  
				// .deploymentId(deploymentId) //使用部署对象ID查询  
				// .processDefinitionNameLike(processDefinitionNameLike)//使用流程定义的名称模糊查询  
				.processDefinitionId(processDefinitionId)//使用流程定义ID查询 
	            .orderByProcessDefinitionVersion().desc() //版本由新到旧
	            .list();
		ActivitiUtils.printProcessDefinitionList(list);
		return list;
	}

	@Override
	public ProcessDefinition findLastVersionProcessDefinition(String processDefinitionId) {
		List<ProcessDefinition> list = repositoryService 
	            .createProcessDefinitionQuery()
	            .processDefinitionId(processDefinitionId)//使用流程定义ID查询 
	            .orderByProcessDefinitionVersion().desc() // 使用流程定义的版本升序排列  
	            .list(); 
		ProcessDefinition pd =  CollectionUtils.isNotEmpty(list)?list.get(0):null;
		ActivitiUtils.printProcessDefinition(pd);
		return pd;
	}

	@Override
	public void deleteProcessDefinitionByKey(String processDefinitionKey) {
		// 先使用流程定义的key查询流程定义，查询出所有的版本  
	    List<ProcessDefinition> list = repositoryService 
	            .createProcessDefinitionQuery()  
	            .processDefinitionKey(processDefinitionKey.split(":")[0])// 使用流程定义的key查询  
	            .list();
	    // 遍历，获取每个流程定义的部署ID  
	    if (CollectionUtils.isNotEmpty(list)) {  
	        for (ProcessDefinition pd : list) {  
	            // 获取部署ID  
	            String deploymentId = pd.getDeploymentId();  
	            // 不带级联的删除， 只能删除没有启动的流程，如果流程启动，就会抛出异常  
	            //repositoryService.deleteDeployment(deploymentId);  
	              
	            //级联删除 不管流程是否启动，都可以删除  
	            repositoryService.deleteDeployment(  
	                    deploymentId, true); 
	            logger.info("=========部署删除 ID:{}",deploymentId);
	        }  
	    }
	}
	@Override
	public List<Task> findTasksByUserGroup(String group) {
		List<Task> list = taskService
				.createTaskQuery()
				.taskCandidateGroup(group)
				.orderByTaskCreateTime().desc()
				.list();
		return list;
	}
	@Override
	public List<Task> findTasksByUserGroupAndProcessId(String group,
			String processId) {
		List<Task> list = taskService
				.createTaskQuery()
				.taskCandidateGroup(group)
				.processInstanceId(processId)
				.list();
		return list;
	}

	@Override
	public void multipolarCompleteTask(String taskId, String polarValue) {
		if(StringUtils.isNotBlank(taskId)&&StringUtils.isNotBlank(polarValue)){
			queryLock(taskId);//查询锁，解锁继续
			Map<String,String> variables =new HashMap<String,String>();
			variables.put("judge", polarValue);
			processEngine.getFormService().submitTaskFormData(taskId, variables);
			logger.info("=========岔路选择 任务ID:{} 选择值:{}",taskId,polarValue);
		}else if(StringUtils.isNotBlank(taskId)){
			this.completeTask(taskId);
		}
	}
	
	@Override
	public List<ActivityImpl> getActivitiesByProcessId(String procInstId) {
		//获得当前任务的对应实例 
		List<Task> curTask = taskService.createTaskQuery().processInstanceId(procInstId).list();
		String taskDefKey = curTask.get(0).getTaskDefinitionKey(); 
		ProcessDefinitionEntity  processDefinition  =(ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService)  
			      .getDeployedProcessDefinition(curTask.get(0).getProcessDefinitionId());
		List<ActivityImpl> activitilist = processDefinition.getActivities(); 
		return activitilist;
	}

	@Override
	public void deleteProcessInstance(String processId) {
		if(!isProcessEnd(processId)){
			runtimeService.deleteProcessInstance(processId, "强制终止");
		}
	}
	
	@Override
	public void deleteTaskDefination(String deploymentId) {
		if(StringUtils.isNotBlank(deploymentId)){
			taskDefinationDao.deleteByDeploymentId(deploymentId);
		}
	}
	
	@Override
	public ActivityImpl nextTaskDefinition(String procInstId,String judge){
        //流程标示
        String processDefinitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult().getProcessDefinitionId();
           
        ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(processDefinitionId);
        //执行实例
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        //当前实例的执行到哪个节点
        String activitiId = execution.getActivityId();
        //获得当前任务的所有节点
        List<ActivityImpl> activitiList = def.getActivities();
        String id = null;
        for(ActivityImpl activityImpl:activitiList){
            id = activityImpl.getId();
            if(activitiId.equals(id)){
                logger.info("当前任务：{}", activityImpl.getProperty("name"));
                String judgeExpression = StringUtils.isNotBlank(judge)?"${judge=="+judge+"}":null;
                return nextTaskDefinition(activityImpl, activityImpl.getId(),judgeExpression);
//              System.out.println(taskDefinition.getCandidateGroupIdExpressions().toArray()[0]);
//              return taskDefinition;
            }
        }
        return null;
    }

	@Override
	public ActivityImpl nextTaskDefinition(ActivityImpl activityImpl,
			String activityId, String elString) {
		if("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())){
            return activityImpl;
        }else{
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for(PvmTransition tr:outTransitions){
                PvmActivity ac = tr.getDestination(); //获取线路的终点节点
                if("exclusiveGateway".equals(ac.getProperty("type"))){
                    outTransitionsTemp = ac.getOutgoingTransitions();
                    if(outTransitionsTemp.size() == 1){
                        return nextTaskDefinition((ActivityImpl)outTransitionsTemp.get(0).getDestination(), activityId, elString);
                    }else if(outTransitionsTemp.size() > 1){
                        for(PvmTransition tr1 : outTransitionsTemp){
                            Object s = tr1.getProperty("conditionText");
                            if(elString.equals(StringUtils.trim(s.toString()))){
                                return nextTaskDefinition((ActivityImpl)tr1.getDestination(), activityId, elString);
                            }
                        }
                    }
                }else if("userTask".equals(ac.getProperty("type"))){//单出
                	return  ((ActivityImpl)ac);
                }else{
                    logger.debug((String) ac.getProperty("type"));
                }
            }
        return null;
	   }
	}

	@Override
	public synchronized void rejectTask(String proInstId, String destTaskKey) {
		//先获取当前任务
		Task taskEntity =  taskService.createTaskQuery().processInstanceId(proInstId).singleResult();
		//当前任务的key
		String taskDefKey = taskEntity.getTaskDefinitionKey(); 
		//获得当前流程的定义模型  
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService)  
			      .getDeployedProcessDefinition(taskEntity.getProcessDefinitionId()); 
		String processDefinitionId = processDefinition.getId();
		GlobalConstants.lockWorkFlowMap.put(processDefinitionId,Boolean.TRUE);//静态变量 加锁
		//获得当前流程定义模型的所有任务节点
		List<ActivityImpl> activitilist = processDefinition.getActivities();
		//找到当前活动节点和驳回的目标节点
		ActivityImpl currActiviti = null;//当前活动节点  
		ActivityImpl destActiviti = null;//驳回目标节点  
		for(ActivityImpl activityImpl : activitilist){  
			//确定当前活动activiti节点  
			if(taskDefKey.equals(activityImpl.getId())){  
				currActiviti = activityImpl; 
			}else if(destTaskKey.equals(activityImpl.getId())){  
			    destActiviti = activityImpl;   
			}
		}     
		if(currActiviti!=null&&destActiviti!=null){
			logger.info("======当前任务节点:{} ", currActiviti.getId());  
			logger.info("======目标任务节点:{} ", destActiviti.getId());
			//保存当前活动节点的流出方向参数  
		    List<PvmTransition> hisPvmTransitionList = Lists.newArrayList();  
			for(PvmTransition pvmTransition:currActiviti.getOutgoingTransitions()){  
			   hisPvmTransitionList.add(pvmTransition);  
			}
			//清空当前活动节点的所有流出项
			currActiviti.getOutgoingTransitions().clear();
			logger.info("=====当前任务节点流出方向:{}", currActiviti.getOutgoingTransitions().size());
			//为当前节点动态创建新的流出项 
			TransitionImpl newTransitionImpl = currActiviti.createOutgoingTransition();  
			//为当前活动节点新的流出目标指定流程目标  
			newTransitionImpl.setDestination(destActiviti); 
			//保存驳回意见  
			taskEntity.setDescription("强制驳回");//设置驳回意见  
			taskService.saveTask(taskEntity);
			//执行当前任务驳回到目标任务draft  
			taskService.complete(taskEntity.getId());
			
			//清除目标节点的新流入项  
			destActiviti.getIncomingTransitions().remove(newTransitionImpl);
			//清除原活动节点的临时流出项  
		    currActiviti.getOutgoingTransitions().clear(); 
		    //还原原活动节点流出项
		    currActiviti.getOutgoingTransitions().addAll(hisPvmTransitionList);  
		}  
		GlobalConstants.lockWorkFlowMap.put(processDefinitionId,Boolean.FALSE);//静态变量 解锁
	}
	
	/**
	 * 获取当前未完成流程的流程定义Id
	 * @param proInstId 流程Id
	 * */
	private String getProcessDefinitionId(String taskId){
		Task task = this.taskService.createTaskQuery().taskId(taskId).singleResult();
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(task.getProcessDefinitionId());
		return processDefinition==null?null:processDefinition.getId();
	}
	
	private void queryLock(String taskId){
		String pde_id = this.getProcessDefinitionId(taskId);
		while(true){
			logger.info(pde_id+"查询锁");
			Boolean lock = GlobalConstants.lockWorkFlowMap.get(pde_id);
			if(lock==null||lock==false){
				logger.info(pde_id+"的定义解锁");
				break;
			}
		}
	}

	@Override
	public void setCounterSignParticipants(String taskId, String participantUsers) {
		if(StringUtils.contains(participantUsers, ",")){
			String[] participants = participantUsers.split(",");
			Map<String,Object> variables = Maps.newHashMap();
			variables.put("pers", Arrays.asList(participants));
			taskService.setVariables(taskId,variables);
		}else{
			Map<String,Object> variables = Maps.newHashMap();
			variables.put("pers", Arrays.asList(participantUsers));
			taskService.setVariables(taskId,variables);
		}
	}

	@Override
	public void bundleTask2Role(String processInstanceId,
			String participantRoles) {
		List<String> roles = null;
		if(StringUtils.isNotBlank(participantRoles)){
			if(StringUtils.contains(participantRoles, ",")){
				roles = Arrays.asList(participantRoles.split(","));
			}else{
				roles = ImmutableList.of(participantRoles); 
			}
		}
		//ProcessInstance pi = findProcessInstance(processInstanceId);
		List<Task> taskList = findNextTasksByProcessInstanceId(processInstanceId);
		if(CollectionUtils.isNotEmpty(taskList)
				&&taskList.size()==roles.size()){
			int counter = 0;
			for (Task task : taskList) {
				taskService.addCandidateGroup(task.getId(), roles.get(counter));
				counter++;
				//taskService.getIdentityLinksForTask(task.getId());
			}
			logger.error("绑定成功！！");
		}else{
			logger.error("任务与角色数量不匹配，绑定失败！！");
		}
	}

	@Override
	public void insertCounterSignRecord(String taskId, String lastTaskId,
			String taskDefinitionKey, String processDefinitionId,
			String processInstanceId) {
		String nowStr = CommonHelper.getNowStr(CommonHelper.DF_DATE_TIME);
		Task curTask = taskService.createTaskQuery().taskId(taskId).singleResult();
		CounterSignRecordPk pk = new CounterSignRecordPk(processDefinitionId, processInstanceId, taskId, taskDefinitionKey);
		CounterSignRecord csr = new CounterSignRecord(pk, curTask.getAssignee(), null, nowStr, nowStr, lastTaskId, null);
		counterSignRecordDao.save(csr);
	}

	@Override
	public int updateCounterSignRecord(String taskId, String processDefinitionId,
			String processInstanceId, String taskDefinitionKey,String assigneer, String signResult) {
		String nowStr = CommonHelper.getNowStr(CommonHelper.DF_DATE_TIME);
		CounterSignRecord csr = counterSignRecordDao.findByPk(processDefinitionId, processInstanceId, taskId, taskDefinitionKey);
		csr.setAssigneer(assigneer);
		csr.setValue(signResult);
		csr.setUpdatetime(nowStr);
		counterSignRecordDao.save(csr);
		
		List<String> objs = counterSignRecordDao.findValueByPkAndLasttaskid(processDefinitionId, processInstanceId, csr.getLasttaskid());
		
		boolean isUNFINISHED = Boolean.FALSE;
		boolean isONEDENY = Boolean.FALSE;
		if(CollectionUtils.isNotEmpty(objs)){
			for (String str : objs) {
				if(str==null||StringUtils.isBlank(str)||"null".equals(str)){
					isUNFINISHED = Boolean.TRUE;
				}else if(StringUtils.equals("false", str)){
					isONEDENY = Boolean.TRUE;
				}
			}
			
			if(isUNFINISHED){
				return GlobalConstants.UNFINISHED;
			}else if(!isUNFINISHED&&isONEDENY){
				return GlobalConstants.ONE_DENY;
			}else{
				return GlobalConstants.ALL_PASS;
			}
			
		}	
			
		return GlobalConstants.OTHER;
	}

	@Override
	public int updateCounterSignRecordOneDeny(String taskId, String processDefinitionId,
			String processInstanceId, String taskDefinitionKey,String assigneer, String signResult) {
		String nowStr = CommonHelper.getNowStr(CommonHelper.DF_DATE_TIME);
		CounterSignRecord csr = counterSignRecordDao.findByPk(processDefinitionId, processInstanceId, taskId, taskDefinitionKey);
		csr.setAssigneer(assigneer);
		csr.setValue(signResult);
		csr.setUpdatetime(nowStr);
		counterSignRecordDao.save(csr);
		
		List<String> objs = counterSignRecordDao.findValueByPkAndLasttaskid(processDefinitionId, processInstanceId, csr.getLasttaskid());
		
		boolean isUNFINISHED = Boolean.FALSE;
		boolean isONEDENY = Boolean.FALSE;
		if(CollectionUtils.isNotEmpty(objs)){
			for (String str : objs) {
				if(StringUtils.equals("false", str)){
					isONEDENY = Boolean.TRUE;
				}else if(str==null||StringUtils.isBlank(str)||"null".equals(str)){
					isUNFINISHED = Boolean.TRUE;
				}
			}
			
			if(isONEDENY){
				return GlobalConstants.ONE_DENY;
			}else if(isUNFINISHED){
				return GlobalConstants.UNFINISHED;
			}else{
				return GlobalConstants.ALL_PASS;
			}
			
		}
		
		return GlobalConstants.OTHER;
	}

	
	@Override
	public List<String> findRolesByUserId(String userId) {
		String nativeSql = "select group_id_ roleid from ACT_ID_MEMBERSHIP me left join ACT_ID_USER u "
				+ "on u.ID_ = me.USER_ID_ where  u.ID_ = '"+userId+"' ";
		List<String> list = dynamicQuery.nativeQuery(String.class, nativeSql);
		return list;
	}

	@Override
	public ActivityImpl findTaskActivity(String taskId) {
		HistoricTaskInstance curTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService)  
			      .getDeployedProcessDefinition(curTask.getProcessDefinitionId()); 
		//获得当前流程定义模型的所有任务节点
		List<ActivityImpl> activitilist = processDefinition.getActivities();
		for (ActivityImpl activityImpl : activitilist) {
			if(StringUtils.equals("userTask", (String)activityImpl.getProperty("type"))
					&&StringUtils.equals(activityImpl.getId(), curTask.getTaskDefinitionKey())){
				return activityImpl;
			}
		}
		return null;
	}

	@Override
	public ActivityImpl findTaskActivity(String processDefinitionId, String taskKey) {
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl)repositoryService)  
			      .getDeployedProcessDefinition(processDefinitionId); 
		//获得当前流程定义模型的所有任务节点
		List<ActivityImpl> activitilist = processDefinition.getActivities();
		for (ActivityImpl activityImpl : activitilist) {
			if(StringUtils.equals("userTask", (String)activityImpl.getProperty("type"))
					&&StringUtils.equals(activityImpl.getId(), taskKey)){
				return activityImpl;
			}
		}
		return null;
	}
	
	@Override
	public boolean IsPerviousCounterSign(String proInsId,String judge) {
		//当前任务
		Task curTask = findNextTaskByProcessInstanceId(proInsId);
		String processDefinitionId = curTask.getProcessDefinitionId();
		List<HistoricTaskInstance> hisTasks = findHistoryTasksByProcessInstanceId(proInsId);
		if(CollectionUtils.isNotEmpty(hisTasks)){
			if(hisTasks.size()==1){
				//正在进行全流程第一个任务
				return Boolean.FALSE;
			}else{
				//从历史任务里找倒数第二个任务
				HistoricTaskInstance perviousTask = hisTasks.get(hisTasks.size()-2);
				ActivityImpl perviousTaskActivity = findTaskActivity(perviousTask.getId());
				//上一步是不是会签任务
				if(hasMultiInstanceCharacteristics(processDefinitionId,perviousTaskActivity.getId())){
					ActivityImpl nextTaskActivity = nextTaskDefinition(proInsId, judge);
					//下一步是不是会签任务
					if(hasMultiInstanceCharacteristics(processDefinitionId, nextTaskActivity.getId())){
						//是不是同一个任务
						if(StringUtils.equals(perviousTaskActivity.getId(), nextTaskActivity.getId())){
							return Boolean.TRUE;
						}
					}
				}
				
			}
		}
		return Boolean.FALSE;
		
	}
	
	public boolean hasMultiInstanceCharacteristics(String processDefinitionId,String taskKey){
		BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel(processDefinitionId); 
		org.activiti.bpmn.model.Process mainProcess = bpmnModel.getMainProcess();  
		Collection<FlowElement> flowElements = mainProcess.getFlowElements();
		for (FlowElement flowElement : flowElements) {
			
            if (flowElement.getId().equals(taskKey)) { 
            	UserTask userTask= (UserTask)flowElement;
            	MultiInstanceLoopCharacteristics loopCharacteristics = userTask.getLoopCharacteristics();
            	return loopCharacteristics!=null;
            }  
        }
		return Boolean.FALSE;
	}

	
	@Override
	public String findPerviousDenyParticipants(String processInstanceId) {
		List<HistoricTaskInstance> hisTasks = findHistoryTasksByProcessInstanceId(processInstanceId);
		if(CollectionUtils.isNotEmpty(hisTasks)&&hisTasks.size()>1){
			HistoricTaskInstance perviousTask = hisTasks.get(hisTasks.size()-2);
			CounterSignRecord csr = counterSignRecordDao.findByTaskid(perviousTask.getId());
			List<CounterSignRecord> lastCsrs = counterSignRecordDao.findByLasttaskid(csr.getLasttaskid());
			List<String> assigneers = Lists.newArrayList();
			for (CounterSignRecord lastCsr : lastCsrs) {
				if("false".equals(lastCsr.getValue())){
					assigneers.add(lastCsr.getAssigneer());
				}
			}
			
			if(CollectionUtils.isNotEmpty(assigneers)){
				String _assigneers = "";
				for (String assigneer : assigneers) {
					_assigneers+=","+assigneer;
				}
				return _assigneers.substring(1);
			}
		}
		return StringUtils.EMPTY;
	}

	@Override
	public List<String> findCounterSignStatus(String taskId) {
		CounterSignRecord csr = counterSignRecordDao.findByTaskid(taskId);
		return counterSignRecordDao.findValueByPkAndLasttaskid(csr.getPk().getProcessdefinitionid(), csr.getPk().getProcessinstanceid(), csr.getLasttaskid());
	}
	
	@Override
	public List<String> findCounterSignDenyUsers(String taskId) {
		CounterSignRecord csr = counterSignRecordDao.findByTaskid(taskId);
		List<CounterSignRecord> lastCsrs = counterSignRecordDao.findByLasttaskid(csr.getLasttaskid());
		List<String> assigneers = Lists.newArrayList();
		for (CounterSignRecord lastCsr : lastCsrs) {
			if(StringUtils.isBlank(lastCsr.getAssigneer()) || "false".equals(lastCsr.getAssigneer())){
				assigneers.add(lastCsr.getAssigneer());
			}
		}
		return assigneers;
	}
	
	@Override
	public String findCounterSignStatusByUser(String taskId,String userId) {
		CounterSignRecord csr = counterSignRecordDao.findByTaskid(taskId);
		//List<CounterSignRecord> lastCsrs = counterSignRecordDao.findByLasttaskid(csr.getLasttaskid());
		String status = csr.getStatus();
		if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(status)){
			if(GlobalConstants.TASK_STATUS_COMPLETED.equals(status)){
				status = csr.getStatus();
			}else if(GlobalConstants.TASK_STATUS_WORKING.equals(status)){
				status = "wait";
			}
		}
//		for (CounterSignRecord lastCsr : lastCsrs) {
//			if(userId.equals(lastCsr.getAssigneer())){
//				if(GlobalConstants.TASK_STATUS_COMPLETED.equals(lastCsr.getStatus())){
//					status = lastCsr.getStatus();
//				}else if(GlobalConstants.TASK_STATUS_WORKING.equals(lastCsr.getStatus())){
//					status = "wait";
//				}
//			}
//		}
		
		if(StringUtils.isNotEmpty(status)){
			return status;
		}else{return StringUtils.EMPTY;}
		
	}
	
	
	private void updateSignTaskAssigneer(String taskId,String assigneer){
		String nowStr = CommonHelper.getNowStr(CommonHelper.DF_DATE_TIME);
		Task curTask = taskService.createTaskQuery().taskId(taskId).singleResult();
		CounterSignRecord csr = counterSignRecordDao.findByPk(curTask.getProcessDefinitionId(), curTask.getProcessInstanceId(), taskId, curTask.getTaskDefinitionKey());
		if(csr!=null){
			csr.setAssigneer(assigneer);
			csr.setUpdatetime(nowStr);
			counterSignRecordDao.save(csr);
		}
	}
	
	@Override
	public void updateCounterSignTaskStatus(String taskId,String status){
		//Task curTask = taskService.createTaskQuery().taskId(taskId).singleResult()
		String nowStr = CommonHelper.getNowStr(CommonHelper.DF_DATE_TIME);
		CounterSignRecord csr = counterSignRecordDao.findByTaskid(taskId);
		List<CounterSignRecord> lastCsrs = counterSignRecordDao.findByLasttaskid(csr.getLasttaskid());
		for (CounterSignRecord lastCsr : lastCsrs) {
			lastCsr.setStatus(status);
			lastCsr.setUpdatetime(nowStr);
			counterSignRecordDao.save(lastCsr);
		}
	}

	@Override
	public void updateTaskStatus(String taskId, String judge, String status) {
		String nowStr = CommonHelper.getNowStr(CommonHelper.DF_DATE_TIME);
		Task curTask = taskService.createTaskQuery().taskId(taskId).singleResult();
		CounterSignRecord csr = counterSignRecordDao.findByTaskid(taskId);
		csr.setStatus(status);
		csr.setValue(judge);
		csr.setAssigneer(curTask.getAssignee());
		csr.setUpdatetime(nowStr);
		counterSignRecordDao.save(csr);
	}

	@Override
	public boolean isSingleOutTask(String taskId) {
		ActivityImpl curAct = this.findTaskActivity(taskId);
		int outWays = CollectionUtils.isNotEmpty(curAct.getOutgoingTransitions())?curAct.getOutgoingTransitions().size():0;
		if(outWays==1){
			PvmTransition pvm = curAct.getOutgoingTransitions().get(0);
			PvmActivity pa = pvm.getDestination();
			if("exclusiveGateway".equals(pa.getProperty("type"))){
				//如果是网关 把流出项的数量修正成网关流出项
				outWays = CollectionUtils.isNotEmpty(pa.getOutgoingTransitions())?pa.getOutgoingTransitions().size():0;
			}
		}
		return outWays<=1?Boolean.TRUE:Boolean.FALSE;
	}

	@Override
	public void modWorkflowUser(List<UserDTO> users) {
		for (UserDTO userDTO : users) {
			User curUser = identityService.createUserQuery().userId(userDTO.getUserid()).singleResult();  
			if(curUser!=null){
				curUser.setFirstName(userDTO.getName());
				identityService.saveUser(curUser);
				
				//查询旧用户的多对多关系
				List<Group> groups = identityService.createGroupQuery().groupMember(curUser.getId()).list();
				if(CollectionUtils.isNotEmpty(groups)){
					for (Group group : groups) {
						//删除旧的一对多关系
						identityService.deleteMembership(curUser.getId(), group.getId());
					}
				}
				if(CollectionUtils.isNotEmpty(userDTO.getRoles())){
					for (RoleDTO roleDTO : userDTO.getRoles()) {
						Group oldGroup = identityService.createGroupQuery().groupId(roleDTO.getRoleid()).singleResult();
						if(oldGroup==null){
							Group newGroup = identityService.newGroup(roleDTO.getRoleid());
							newGroup.setName(roleDTO.getName());
							newGroup.setType("assignment");
							identityService.saveGroup(newGroup);
						}
						//增加新的一对多关系
						identityService.createMembership(curUser.getId(), roleDTO.getRoleid());
					}
				}
				
			}else{
				User newUser = identityService.newUser(userDTO.getUserid());
				newUser.setFirstName(userDTO.getName());
				newUser.setPassword(GlobalConstants.DEFAULT_PASSWORD);
				identityService.saveUser(newUser);
				
				if(CollectionUtils.isNotEmpty(userDTO.getRoles())){
					for (RoleDTO roleDTO : userDTO.getRoles()) {
						Group oldGroup = identityService.createGroupQuery().groupId(roleDTO.getRoleid()).singleResult();
						if(oldGroup==null){
							Group newGroup = identityService.newGroup(roleDTO.getRoleid());
							newGroup.setName(roleDTO.getName());
							newGroup.setType("assignment");
							identityService.saveGroup(newGroup);
						}
						//增加新的一对多关系
						identityService.createMembership(newUser.getId(), roleDTO.getRoleid());
					}
				}
			}
		}
	}

	@Override
	public void delWorkflowUser(List<UserDTO> users) {
		for (UserDTO userDTO : users) {
			List<Group> groups = identityService.createGroupQuery().groupMember(userDTO.getUserid()).list();
			if(CollectionUtils.isNotEmpty(groups)){
				for (Group group : groups) {
					identityService.deleteMembership(userDTO.getUserid(), group.getId());
				}
			}
			identityService.deleteUser(userDTO.getUserid());
		}
		
	}

	@Override
	public void setSignParticipants(String taskId, String participantUsers) {
		if(StringUtils.contains(participantUsers, ",")){
			String[] participants = participantUsers.split(",");
			for (int i = 0; i < participants.length; i++) {
				taskService.addCandidateUser(taskId, participants[i]);
			}
		}else{
			assigneeTask(taskId, participantUsers);//直接把任务派发给这个人
		}
	}

	@Override
	public void setSignParticipantsRoles(String taskId,
			String participantRoles) {
		if(StringUtils.contains(participantRoles, ",")){
			String[] participants = participantRoles.split(",");
			for (int i = 0; i < participants.length; i++) {
				taskService.addCandidateGroup(taskId, participants[i]);
			}
		}else{
			taskService.addCandidateGroup(taskId, participantRoles);
		}
	}

	@Override
	public String getCandidateGroupFromTask(String taskId) {
		String nativeSql = "select group_id_  from ACT_RU_IDENTITYLINK lk  where lk.task_id_ = '"+taskId+"' ";
		List<String> list = dynamicQuery.nativeQuery(String.class, nativeSql);
		String groupId = "";
		if(CollectionUtils.isNotEmpty(list)){
			for (String _groupId : list) {
				groupId += "," + _groupId;
			}
			return groupId.substring(1);
		}
		return null;
	}
	
	@Override
	public String getCandidateGroupFromHistoryTask(String taskId) {
		String nativeSql = "select group_id_  from ACT_HI_IDENTITYLINK lk  where lk.task_id_ = '"+taskId+"' ";
		List<String> list = dynamicQuery.nativeQuery(String.class, nativeSql);
		String groupId = "";
		if(CollectionUtils.isNotEmpty(list)){
			for (String _groupId : list) {
				groupId += "," + _groupId;
			}
			return groupId.substring(1);
		}
		return null;
	}
	
	@Override
	public List findDeployments(Integer firstNumber, Integer pageSize,
			List<Object> params) {
//		String jpql = "select ard from ActReDeployment ard order by deploy_time_ desc";
		List<Deployment> list = repositoryService.createDeploymentQuery().orderByDeploymenTime().desc().listPage(firstNumber, pageSize);
//		Page p = dynamicQuery.query(ActReDeployment.class, new PageRequest(pageNumber - 1,
//				pageSize), jpql, null);
		return list;
	}
	@Override
	public long countDeployments(){
		return repositoryService.createDeploymentQuery().count();
	}
	
	@Override
	public void dropDeployment(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId);
	}

	@Override
	public Page findDeployDetail(Integer pageNumber, Integer pageSize,
			List<Object> params) {
		String sql = "select name_,key_,id_,concat(RESOURCE_NAME_,'&&',DGRM_RESOURCE_NAME_) from ACT_RE_PROCDEF where DEPLOYMENT_ID_ = '"+params.get(0)+"'";
		return dynamicQuery.nativeQuery(DeploymentVO.class, new PageRequest(pageNumber - 1,
				pageSize), sql, null);
	}

	@Override
	public List<HistoricTaskInstance> findActiveHistoryTasksByUserId(
			String userId) {
		List<HistoricTaskInstance> list = historyService//与历史数据（历史表）相关的service  
	            .createHistoricTaskInstanceQuery()//创建历史任务实例查询  
	            .taskAssignee(userId)//指定历史任务的办理人  
	            .list();
		List<HistoricTaskInstance> results = Lists.newArrayList();
		for (HistoricTaskInstance task : list) {
			if(!task.getTaskDefinitionKey().contains("-pass")){//过滤会签后的临时任务
				if(hasMultiInstanceCharacteristics(task.getProcessDefinitionId(), task.getTaskDefinitionKey())){//会签节点
					if(task.getEndTime()!=null){//已完成的会签任务
						String status = findCounterSignStatusByUser(task.getId(),task.getAssignee());//该次会签驳回和未审批的人员列表
						if(StringUtils.isNotBlank(status)){
							results.add(task);
						}
					}else{//未完成的会签任务
						results.add(task);
					}
				}else{//普通节点
					results.add(task);
				}
			}
		}
		ActivitiUtils.printHistoryTaskList(results);
		return results;
	}

	@Override
	public Long countActiveProcessInstanceByDeploymentId(String deploymentId) {
		long count = runtimeService.createProcessInstanceQuery().deploymentId(deploymentId).active().count();
		return count;
	}

	@Override
	public List<ProcessInstance> findActiveProcessInstanceByDeploymentId(
			String deploymentId) {
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().deploymentId(deploymentId).active().list();
		return list;
	}
	@Override
	public User searchUser(String userId){
		return  identityService.createUserQuery().userId(userId).singleResult();
	}
	
	@Override
	public Group searchGroup(String roleId){
		return  identityService.createGroupQuery().groupId(roleId).singleResult();
	}

	@Override
	public void saveGroup(Group aig) {
		identityService.saveGroup(aig);
	}

	@Override
	public void modWorkflowRole(List<RoleDTO> roles) {
		for (RoleDTO roleDTO : roles) {
			Group group = this.searchGroup(roleDTO.getRoleid());
			if(group==null){
				Group newgroup = identityService.newGroup(roleDTO.getRoleid());
				newgroup.setName(roleDTO.getName());
				identityService.saveGroup(newgroup);
			}else{
				group.setName(roleDTO.getName());
				identityService.saveGroup(group);
			}
		}
	}
}
