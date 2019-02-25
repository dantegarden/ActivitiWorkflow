package com.dvt.ActivitiWorkflow.business.real.example.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dvt.ActivitiWorkflow.commons.entity.DataTablesPage;
import com.dvt.ActivitiWorkflow.business.real.example.dto.NextTaskDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.ProcessInstanceDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.TaskDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.WorkflowDTO;
import com.dvt.ActivitiWorkflow.business.real.example.service.DesignService;
import com.dvt.ActivitiWorkflow.business.real.example.service.ProcessCoreService;
import com.dvt.ActivitiWorkflow.business.real.example.service.impl.ProcessCoreServiceImpl;
import com.dvt.ActivitiWorkflow.business.real.example.vo.DeployDetailVO;
import com.dvt.ActivitiWorkflow.business.real.example.vo.DeploymentVO;
import com.dvt.ActivitiWorkflow.business.real.example.webservice.WorkflowService;
import com.dvt.ActivitiWorkflow.business.real.example.webservice.impl.WorkflowServiceImpl;
import com.dvt.ActivitiWorkflow.commons.GlobalConstants;
import com.dvt.ActivitiWorkflow.commons.entity.Result;
import com.dvt.ActivitiWorkflow.commons.utils.CommonHelper;
import com.dvt.ActivitiWorkflow.commons.utils.JsonUtils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.bcel.internal.generic.IMUL;

@Controller
@RequestMapping("/process")
public class ActivitiController {
	
	private static final Logger logger = LoggerFactory.getLogger(ActivitiController.class);
	
	private static final Integer MULTI_INSTANCE_ROLE_TYPE = 1;
	private static final Integer MULTI_INSTANCE_USER_TYPE = 2;
	
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
	private DesignService designService;
	
	public String pid;
	
	@RequestMapping
	public String init() {
		System.out.println("process comming");
		/*monthtest2();
		developerAct();
		projectManagementAct();
		departmentManagementAct("false");
		developerAct();
		projectManagementAct();
		departmentManagementAct("true");
		monthtest3();*/
		return GlobalConstants.INIT_ACT;
	}
	
	@RequestMapping("/getDeployments")
	@ResponseBody
	public DataTablesPage getDeployments(@RequestParam("sEcho") Integer sEcho,
			@RequestParam("iDisplayStart") Integer firstIndex,
			@RequestParam("iDisplayLength") Integer pageSize,
			HttpServletRequest request){
		List<Object> params = new ArrayList<Object>();
		List<Deployment> queryList = processCoreService.findDeployments(
				firstIndex, pageSize, params);
		List<DeploymentVO> resultList = Lists.newArrayList(); 
		for (Deployment deployment : queryList) {
			resultList.add(new DeploymentVO(deployment.getId(), 
											deployment.getName(), 
											deployment.getDeploymentTime()));
		}
		Long record = processCoreService.countDeployments();
		//List<Object[]> resultList = queryList.getContent();
//		List<DeploymentVO> deployList = Lists.transform(resultList, new Function<Object[], DeploymentVO>() {
//			@Override
//			public DeploymentVO apply(Object[] arg0) {
//				return new DeploymentVO(arg0);
//			}
//		});
//		DataTablesPage page = new DataTablesPage(sEcho, deployList,queryList.getTotalElements(),1);
		DataTablesPage page = new DataTablesPage(sEcho, resultList,record);
		return page;
	}
	
	@RequestMapping("/getDeployDetail")
	@ResponseBody
	public DataTablesPage getDeployDetail(@RequestParam String id,
			@RequestParam("sEcho") Integer sEcho,
			@RequestParam("iDisplayStart") Integer firstIndex,
			@RequestParam("iDisplayLength") Integer pageSize,
			HttpServletRequest request){
		List<Object> params = new ArrayList<Object>();
		params.add(id);
		Page queryList = processCoreService.findDeployDetail(
				(firstIndex / pageSize) + 1, pageSize, params);
		List<Object[]> resultList = queryList.getContent();
		
		List<DeployDetailVO> deployList = Lists.transform(resultList, new Function<Object[], DeployDetailVO>() {
			@Override
			public DeployDetailVO apply(Object[] arg0) {
				return new DeployDetailVO(arg0);
			}
		});
		DataTablesPage page = new DataTablesPage(sEcho, deployList,queryList.getTotalElements(),1);
		return page;
	}
	
	
	
	@RequestMapping("/findActiveProcInstance")
	@ResponseBody
	public Result findActiveProcInstance(@RequestParam String deploymentId) {
		if(StringUtils.isNotBlank(deploymentId)){
			try {
				Long count = processCoreService.countActiveProcessInstanceByDeploymentId(deploymentId);
				return new Result(Boolean.TRUE,"查询成功",count);
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(Boolean.FALSE,e.getMessage(),null);
			}
		}
		return new Result(Boolean.FALSE,"未收到请求参数",null);
	}
	
	@RequestMapping("/dropDeployment")
	@ResponseBody
	public Result dropDeployment(@RequestParam String deploymentId){
		try {
			List<ProcessInstance> list = processCoreService.findActiveProcessInstanceByDeploymentId(deploymentId);
			if (CollectionUtils.isNotEmpty(list)) {
				for (ProcessInstance processInstance : list) {
					processCoreService.deleteProcessInstance(processInstance.getProcessInstanceId());
					processCoreService.deleteTaskDefination(deploymentId);
				}
			}
			processCoreService.dropDeployment(deploymentId);
			designService.deleteByDeploymentId(deploymentId);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Boolean.FALSE,"卸载失败",null);
		}
		return new Result(Boolean.TRUE,"卸载成功",null);
	}
	
	
	
	
	@RequestMapping("/query")
	public String initQuery() {
		System.out.println("process initQuery");
		return GlobalConstants.INIT_QUERY;
	}
	
	@RequestMapping("/query/currentTaskQuery")
	@ResponseBody
	public Result currentTaskQuery(@RequestParam String processInstanceId,@RequestParam String userId,@RequestParam String roleId){
		List<Task> taskList = null;
		if(!processCoreService.isProcessEnd(processInstanceId)){
			if(StringUtils.isNotBlank(processInstanceId)&&StringUtils.isNotBlank(userId)){
				taskList = processCoreService.findTasksByUserAndProcess(userId, processInstanceId);
			}else if(StringUtils.isNotBlank(processInstanceId)&&StringUtils.isNotBlank(roleId)){
				taskList = processCoreService.findTasksByUserGroupAndProcessId(roleId, processInstanceId);
			}else if(StringUtils.isNotBlank(processInstanceId)){
				taskList = processCoreService.findNextTasksByProcessInstanceId(processInstanceId);
			}else if(StringUtils.isNotBlank(userId)){
				taskList = processCoreService.findTasksByUser(userId);
			}else if(StringUtils.isNotBlank(roleId)){
				taskList = processCoreService.findTasksByUserGroup(roleId);
			}
		}else{
			return new Result(Boolean.FALSE,"该流程已结束，没有待办任务",null);
		}
		
			
		if(CollectionUtils.isNotEmpty(taskList)){
			List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
			for (Task nextTask : taskList) {
				String taskName = nextTask.getName();
				if(processCoreService.hasMultiInstanceCharacteristics(nextTask.getProcessDefinitionId(), nextTask.getTaskDefinitionKey())){
					//多实例节点
					String _roleId = processCoreService.getCandidateGroupFromTask(nextTask.getId());
					if(StringUtils.isNotBlank(_roleId)){//多实例节点 角色
						Group role = processCoreService.searchGroup(_roleId);
						taskName = taskName+"("+role.getName()+")";
					}else if(StringUtils.isNotBlank(nextTask.getAssignee())){
						User user = processCoreService.searchUser(nextTask.getAssignee());
						taskName = taskName+"("+user.getFirstName()+")";
					}
				}
				
				
				nextTaskDtos.add(new NextTaskDTO(nextTask.getId(), 
							nextTask.getTaskDefinitionKey(), 
							taskName, 
							nextTask.getAssignee(),
							processCoreService.getCandidateGroupFromTask(nextTask.getId()),
							nextTask.getProcessInstanceId(),
							nextTask.getProcessDefinitionId(),
							nextTask.getCreateTime()));
			}
			return new Result(Boolean.TRUE, "查询成功", nextTaskDtos);
		}	
		
		return new Result(Boolean.FALSE, "没有待办任务", null);
	}
	
	@RequestMapping("/query/histTaskQuery")
	@ResponseBody
	public Result histTaskQuery(@RequestParam String processInstanceId,@RequestParam String userId,@RequestParam String roleId,@RequestParam Integer queryType){
		List<HistoricTaskInstance> taskList = null;
		if(StringUtils.isNotBlank(processInstanceId)&&StringUtils.isNotBlank(userId)){
			taskList = processCoreService.findActiveHistoryTasksByUserIdandProcessInstanceId(userId, processInstanceId);
		}else if(StringUtils.isNotBlank(processInstanceId)&&StringUtils.isNotBlank(roleId)){
			return new Result(Boolean.FALSE, "不能通过角色查询已办任务", null);
		}else if(StringUtils.isNotBlank(processInstanceId)){
			taskList = processCoreService.findActiveHistoryTasksByProcessInstanceId(processInstanceId);
		}else if(StringUtils.isNotBlank(userId)){
			taskList = processCoreService.findActiveHistoryTasksByUserId(userId);
		}else if(StringUtils.isNotBlank(roleId)){
			return new Result(Boolean.FALSE, "不能通过角色查询已办任务", null);
		}
		
			
		if(CollectionUtils.isNotEmpty(taskList)){
			List<TaskDTO> dtolist = Lists.newArrayList();
			if(queryType==1){
				for (HistoricTaskInstance task : taskList) {
					dtolist.add(new TaskDTO(task.getId(), 
							task.getTaskDefinitionKey(), 
							task.getName(), 
							task.getAssignee(),
							processCoreService.getCandidateGroupFromHistoryTask(task.getId()),
							task.getCreateTime(),
							task.getEndTime(),
							task.getProcessInstanceId(),
							task.getProcessDefinitionId()));
				}
			}else if(queryType==2){
				Map<String,HistoricTaskInstance> map = new LinkedHashMap<String, HistoricTaskInstance>();
				for (HistoricTaskInstance task : taskList) {
					map.put(task.getTaskDefinitionKey(),task);
				}
				for (String taskKey : map.keySet()) {
					HistoricTaskInstance task = map.get(taskKey);
					dtolist.add(new TaskDTO(task.getId(), 
							task.getTaskDefinitionKey(), 
							task.getName(), 
							task.getAssignee(),
							processCoreService.getCandidateGroupFromHistoryTask(task.getId()),
							task.getCreateTime(),
							task.getEndTime(),
							task.getProcessInstanceId(),
							task.getProcessDefinitionId()));
				}
			}
			return new Result(Boolean.TRUE, "查询成功", dtolist);
		}	
		
		return new Result(Boolean.FALSE, "没有已办任务", null);
	}
	
	
	
	@RequestMapping("/begin")
	public String initBegin() {
		System.out.println("process initBegin");
		return GlobalConstants.INIT_BEGIN;
	}
	
	
	@RequestMapping("/begin/startWorkflow")
	@ResponseBody
	public Result startWorkflow(@ModelAttribute WorkflowDTO dto) {
		if(dto!=null){
			
			//检查参数信息是否齐全
			if(StringUtils.isBlank(dto.getUserId())){
				return new Result(Boolean.FALSE, "请求参数缺少当前委办人id", null);
			}
			
			/**1.开启新任务流程**/
			ProcessInstance pi = processCoreService.startWorkflow(dto.getProcessDefinitionKey(),dto.getUserId());
			if(pi!=null){
				
				//找到认领到的任务
				List<Task> tasklist = processCoreService.findTasksByUserAndProcess(dto.getUserId(), pi.getProcessInstanceId());
				if(CollectionUtils.isNotEmpty(tasklist)){
					
					/**1.自动完成第一个任务**/
					Task task = tasklist.get(0);
					
					//想在执行本次任务后发起会签 两种类型，一种指定到人，一种指定到角色
					boolean isNextCounterSign = Boolean.FALSE;//下一步是会签
					boolean isRoleCounterSign = Boolean.FALSE;//下一步是角色会签
					if(StringUtils.isNotBlank(dto.getParticipantUsers())){
						processCoreService.setCounterSignParticipants(task.getId(),dto.getParticipantUsers());
						isNextCounterSign = Boolean.TRUE;
					}else if(StringUtils.isNotBlank(dto.getParticipantRoles())){
						processCoreService.setCounterSignParticipants(task.getId(),dto.getParticipantRoles());
						isNextCounterSign = Boolean.TRUE;
						isRoleCounterSign = Boolean.TRUE;
					}
					
					//第一个任务是普通任务节点
					
					//先插一条待办记录
					processCoreService.insertCounterSignRecord(task.getId(), "StartPoint", task.getTaskDefinitionKey(), task.getProcessDefinitionId(), task.getProcessInstanceId());	
					processCoreService.assigneeTask(task.getId(), dto.getUserId());
					
					if(!processCoreService.isSingleOutTask(task.getId())){
						//改变本次任务的完成状态
						processCoreService.updateTaskStatus(task.getId(), dto.getJudge(), GlobalConstants.TASK_STATUS_COMPLETED);
						//多向
						processCoreService.multipolarCompleteTask(task.getId(),dto.getJudge());
					}else{
						//改变本次任务的完成状态
						processCoreService.updateTaskStatus(task.getId(), "one-way-pass", GlobalConstants.TASK_STATUS_COMPLETED);
						//单向
						processCoreService.completeTask(task.getId());
					}
						
					
					
					/**2.为下次任务做准备*/
					//本次所完成任务的信息
					TaskDTO taskdto = new TaskDTO(task.getId(), 
							task.getTaskDefinitionKey(), 
							task.getName(), 
							task.getAssignee(),
							task.getCreateTime(), 
							task.getProcessInstanceId(),
							task.getProcessDefinitionId());
					taskdto.setWorkflowFinished(processCoreService.isProcessEnd(dto.getProcessInstanceId()));
					//获得下个任务
					List<Task> nextTasks = processCoreService.findNextTasksByProcessInstanceId(pi.getProcessInstanceId());
					if(CollectionUtils.isNotEmpty(nextTasks)
							&&processCoreService.hasMultiInstanceCharacteristics(pi.getProcessDefinitionId(), nextTasks.get(0).getTaskDefinitionKey())){
						//下个任务是会签
						if(isRoleCounterSign){
							//如果是会签指定到角色的，需要手动绑定到角色
							processCoreService.bundleTask2Role(pi.getProcessInstanceId(), dto.getParticipantRoles());
						}
						if(!taskdto.isWorkflowFinished()){
							List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
							for (Task nextTask : nextTasks) {
								this.writeNextTaskDto(nextTaskDtos, nextTask);
								processCoreService.insertCounterSignRecord(nextTask.getId(),
																		   task.getId(),
																		   nextTask.getTaskDefinitionKey(),
																		   nextTask.getProcessDefinitionId(),
																		   nextTask.getProcessInstanceId());
							}
							taskdto.setNextTask(nextTaskDtos);
						}
					}else if(CollectionUtils.isNotEmpty(nextTasks)&&nextTasks.size()==1){
						//下个任务是普通任务
						List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
						Task nextTask = nextTasks.get(0);
						processCoreService.insertCounterSignRecord(nextTask.getId(),
																   task.getId(),
																   nextTask.getTaskDefinitionKey(),
																   nextTask.getProcessDefinitionId(),
																   nextTask.getProcessInstanceId());
						//如果指定了下一个委办人，就自动认领任务
						if(StringUtils.isNotBlank(dto.getNextUserId())){
							processCoreService.setSignParticipants(nextTask.getId(), dto.getNextUserId());
							nextTask.setAssignee(dto.getNextUserId());
						}else if(StringUtils.isNotBlank(dto.getNextRoleId())){
							processCoreService.setSignParticipantsRoles(nextTask.getId(), dto.getNextRoleId());
						}
						nextTaskDtos.add(new NextTaskDTO(nextTask.getId(), 
										 				 nextTask.getTaskDefinitionKey(), 
										 				 nextTask.getName(), 
										 				 nextTask.getAssignee(),
										 				 processCoreService.getCandidateGroupFromTask(nextTask.getId())));

						taskdto.setNextTask(nextTaskDtos);
					}
					
					return new Result(Boolean.TRUE, "工作流实例创建成功", taskdto);
				}else{
					return new Result(Boolean.TRUE, "工作流实例虽创建成功,但任务发起人没有可完成的任务", null);
				}
			}else{
				return new Result(Boolean.FALSE,"工作流实例创建失败",null);
			}
		}
		return new Result(Boolean.FALSE,"未收到请求参数",null);
	}
	
	
	
	@RequestMapping("/begin/dropWorkflow")
	@ResponseBody
	public Result dropWorkflow(@RequestParam String processInstanceId) {
		if(StringUtils.isNotBlank(processInstanceId)){
			try {
				processCoreService.deleteProcessInstance(processInstanceId);
				return new Result(Boolean.TRUE,"工作流实例删除成功",null);
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(Boolean.FALSE,e.getMessage(),null);
			}
		}
		return new Result(Boolean.FALSE,"未收到请求参数",null);
	}
	
	
	@RequestMapping("/execute")
	public String initExecute() {
		System.out.println("process initExecute");
		return GlobalConstants.INIT_EXEC;
	}
	
	
	@RequestMapping("/execute/assign")
	@ResponseBody
	public Result assign(@ModelAttribute WorkflowDTO dto) {
		if(dto!=null){
			boolean flag = processCoreService.assigneeTask(dto.getTaskId(), dto.getUserId());
			if(flag){
				return new Result(Boolean.TRUE, "认领成功", null); 
			}else{
				return new Result(Boolean.TRUE, "认领失败", null); 
			}
		}
		return new Result(Boolean.FALSE,"未收到请求参数",null);
	}
	
	
	@RequestMapping("/execute/complete")
	@ResponseBody
	public Result complete(@ModelAttribute WorkflowDTO dto) {
		if(dto!=null){
			
			//检查参数信息是否齐全
			if(StringUtils.isBlank(dto.getUserId())){
				return new Result(Boolean.FALSE, "请求参数缺少当前委办人id", null);
			}
			
			List<Task> tasklist = processCoreService.findTasksByUserAndProcess(dto.getUserId(), dto.getProcessInstanceId());
			if(CollectionUtils.isNotEmpty(tasklist)){
				for (Task task : tasklist) {
					if(task.getId().equals(dto.getTaskId())){
						
						//想在执行本次任务后发起会签 两种类型，一种指定到人，一种指定到角色
						boolean isNextCounterSign = Boolean.FALSE;//下一步是会签
						boolean isRoleCounterSign = Boolean.FALSE;//下一步是角色会签
						if(StringUtils.isNotBlank(dto.getParticipantUsers())){
							processCoreService.setCounterSignParticipants(task.getId(),dto.getParticipantUsers());
							isNextCounterSign = Boolean.TRUE;
						}else if(StringUtils.isNotBlank(dto.getParticipantRoles())){
							processCoreService.setCounterSignParticipants(task.getId(),dto.getParticipantRoles());
							isNextCounterSign = Boolean.TRUE;
							isRoleCounterSign = Boolean.TRUE;
						}
						
						/*if(isNextCounterSign){//下一环节是会签
							boolean isPerviousCounterSign = processCoreService.IsPerviousCounterSign(dto.getProcessInstanceId(),dto.getJudge());
							if(isPerviousCounterSign){
								System.out.println("上一步和下一步是同一会签节点");
								//去表里查上一步taskId的lastTaskId,找到lastTaskId相同的记录，找到里面value=false的userId
								String participants = processCoreService.findPerviousDenyParticipants(dto.getProcessInstanceId());
								
							}
						}*/
						
						/**1.完成本次任务*/
						
						//本次任务是会签中的一个任务 记录下会签结果是退回还是通过  
						if(processCoreService.hasMultiInstanceCharacteristics(task.getProcessDefinitionId(), task.getTaskDefinitionKey())){
							
							//先找到再更新操作，然后再判断是不是同一个lastTaskId出来的分支都结束了，如果都结束，就判断路由取向,在本次会签分支完成后，再向下推进一次任务
							boolean isRunCounterSignPass = Boolean.FALSE; 
							String counterSignJudge = null;
							if(StringUtils.isNotBlank(dto.getSignResult())){
								/* 所有人都审，全审完再决定过不过
								 * int status = processCoreService.updateCounterSignRecord(task.getId(),
																	   task.getProcessDefinitionId(),
																	   task.getProcessInstanceId(),
																	   task.getTaskDefinitionKey(),
																	   task.getAssignee(),
																	   dto.getSignResult());
								switch (status) {
								case GlobalConstants.ALL_PASS:{
									isRunCounterSignPass = Boolean.TRUE;
									counterSignJudge = dto.getJudge();//默认go是通过路线
									break;
								}
								case GlobalConstants.ONE_DENY:{
									isRunCounterSignPass = Boolean.TRUE;
									counterSignJudge = "back";//默认back是驳回路线
									break;
								}
								case GlobalConstants.UNFINISHED:{
									System.out.println("会签未全部完成！！");
									break;
								}
								default:
									System.out.println("报错~~~~~~~~~~~！！");
									break;
								}*/
								
								//--------------------------------------------------------
								
								//一有否决就马上结束会签
								int status = processCoreService.updateCounterSignRecordOneDeny(task.getId(),
										task.getProcessDefinitionId(),
										task.getProcessInstanceId(),
										task.getTaskDefinitionKey(),
										task.getAssignee(),
										dto.getSignResult());
								switch (status) {
								case GlobalConstants.ALL_PASS:{
									isRunCounterSignPass = Boolean.TRUE;
									counterSignJudge = dto.getJudge();//默认go是通过路线
									//结束会签时改变本次会签任务的完成状态
									processCoreService.updateCounterSignTaskStatus(task.getId(),GlobalConstants.TASK_STATUS_COMPLETED);
									break;
								}
								case GlobalConstants.ONE_DENY:{
									isRunCounterSignPass = Boolean.TRUE;
									counterSignJudge = "back";//默认back是驳回路线
									//结束会签时改变本次会签任务的完成状态
									processCoreService.updateCounterSignTaskStatus(task.getId(),GlobalConstants.TASK_STATUS_COMPLETED);
									break;
								}
								case GlobalConstants.UNFINISHED:{
									System.out.println("会签未全部完成！！");
									break;
								}
								default:
									System.out.println("报错~~~~~~~~~~~！！");
									break;
								}
							}
							
							//改变本次任务的完成状态
							processCoreService.updateTaskStatus(task.getId(), dto.getSignResult(), GlobalConstants.TASK_STATUS_COMPLETED);
							//完成任务
							processCoreService.completeTask(task.getId());
							if(isRunCounterSignPass){//会签完成，直接向下推进任务
								Task counterSignPass = processCoreService.findNextTaskByProcessInstanceId(dto.getProcessInstanceId());
								if(StringUtils.isNotBlank(dto.getDestTaskKey())&&counterSignJudge.equals("back")){//一票否决 的 允许会签结束后跳跃节点
									processCoreService.rejectTask(counterSignPass.getProcessInstanceId(), dto.getDestTaskKey());
								}else{
									processCoreService.multipolarCompleteTask(counterSignPass.getId(),counterSignJudge);
								}
							}
							
						}else{//普通任务节点
							
							if(!processCoreService.isSingleOutTask(task.getId())){
								//改变本次任务的完成状态
								processCoreService.updateTaskStatus(task.getId(), dto.getJudge(), GlobalConstants.TASK_STATUS_COMPLETED);
								//多向
								processCoreService.multipolarCompleteTask(task.getId(),dto.getJudge());
							}else{
								//改变本次任务的完成状态
								processCoreService.updateTaskStatus(task.getId(), "one-way-pass", GlobalConstants.TASK_STATUS_COMPLETED);
								//单向
								processCoreService.completeTask(task.getId());
							}
							
						}
						
						/**2.为下次任务做准备*/
						//本次所完成任务的信息
						TaskDTO taskdto = new TaskDTO(task.getId(), 
								task.getTaskDefinitionKey(), 
								task.getName(), 
								task.getAssignee(),
								task.getCreateTime(), 
								task.getProcessInstanceId(),
								task.getProcessDefinitionId());
						taskdto.setWorkflowFinished(processCoreService.isProcessEnd(dto.getProcessInstanceId()));
						//获得下个任务
						List<Task> nextTasks = processCoreService.findNextTasksByProcessInstanceId(dto.getProcessInstanceId());
						if(CollectionUtils.isNotEmpty(nextTasks)
								&&processCoreService.hasMultiInstanceCharacteristics(nextTasks.get(0).getProcessDefinitionId(), nextTasks.get(0).getTaskDefinitionKey())){
							//下个任务是会签
							if(isRoleCounterSign){
								//如果是会签指定到角色的，需要手动绑定到角色
								processCoreService.bundleTask2Role(dto.getProcessInstanceId(), dto.getParticipantRoles());
							}
							if(!taskdto.isWorkflowFinished()){
								List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
								for (Task nextTask : nextTasks) {
									
									this.writeNextTaskDto(nextTaskDtos, nextTask);
									
									if(isNextCounterSign){
										processCoreService.insertCounterSignRecord(nextTask.getId(),
												task.getId(),
												nextTask.getTaskDefinitionKey(),
												nextTask.getProcessDefinitionId(),
												nextTask.getProcessInstanceId());
									}
								}
								taskdto.setNextTask(nextTaskDtos);
							}
						}else if(CollectionUtils.isNotEmpty(nextTasks)&&nextTasks.size()==1){
							//下个任务是普通任务
							List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
							Task nextTask = nextTasks.get(0);
							processCoreService.insertCounterSignRecord(nextTask.getId(),
																	   task.getId(),
																	   nextTask.getTaskDefinitionKey(),
																	   nextTask.getProcessDefinitionId(),
																	   nextTask.getProcessInstanceId());
							//如果指定了下一个委办人，就自动认领任务
							if(StringUtils.isNotBlank(dto.getNextUserId())){
								processCoreService.setSignParticipants(nextTask.getId(), dto.getNextUserId());
								nextTask.setAssignee(dto.getNextUserId());
							}else if(StringUtils.isNotBlank(dto.getNextRoleId())){
								processCoreService.setSignParticipantsRoles(nextTask.getId(), dto.getNextRoleId());
							}
							nextTaskDtos.add(new NextTaskDTO(nextTask.getId(), 
											 				 nextTask.getTaskDefinitionKey(), 
											 				 nextTask.getName(), 
											 				 nextTask.getAssignee(),
											 				 processCoreService.getCandidateGroupFromTask(nextTask.getId())));

							taskdto.setNextTask(nextTaskDtos);
						}
						
						
						return new Result(Boolean.TRUE, "任务执行成功", taskdto); 
					}
				}
			}
			return new Result(Boolean.FALSE, "该用户没有这个任务，执行失败", null); 
		}
		return new Result(Boolean.FALSE,"未收到请求参数",null);
	}
	
	
	@RequestMapping("/execute/jump")
	@ResponseBody
	public Result jump(@ModelAttribute WorkflowDTO dto) {
		if(dto!=null){
			
			//检查参数信息是否齐全
			if(StringUtils.isBlank(dto.getUserId())){
				return new Result(Boolean.FALSE, "请求参数缺少当前委办人id", null);
			}
			
			try {
				List<Task> tasklist = processCoreService.findTasksByUserAndProcess(dto.getUserId(), dto.getProcessInstanceId());
				if(CollectionUtils.isNotEmpty(tasklist)){
					for (Task task : tasklist) {
						if(task.getId().equals(dto.getTaskId())){
							
							//想在执行本次任务后发起会签 两种类型，一种指定到人，一种指定到角色
							boolean isNextCounterSign = Boolean.FALSE;//下一步是会签
							boolean isRoleCounterSign = Boolean.FALSE;//下一步是角色会签
							if(StringUtils.isNotBlank(dto.getParticipantUsers())){
								processCoreService.setCounterSignParticipants(task.getId(),dto.getParticipantUsers());
								isNextCounterSign = Boolean.TRUE;
							}else if(StringUtils.isNotBlank(dto.getParticipantRoles())){
								processCoreService.setCounterSignParticipants(task.getId(),dto.getParticipantRoles());
								isNextCounterSign = Boolean.TRUE;
								isRoleCounterSign = Boolean.TRUE;
							}
							
							/**1.完成本次任务*/
							
							//本次任务是会签中的一个任务 记录下会签结果是退回还是通过  
							//普通任务节点
							
							//改变本次任务的完成状态
							processCoreService.updateTaskStatus(task.getId(), "jump", GlobalConstants.TASK_STATUS_COMPLETED);
							//节点跳跃
							processCoreService.rejectTask(dto.getProcessInstanceId(), dto.getDestTaskKey());
								
							
							
							/**2.为下次任务做准备*/
							//本次所完成任务的信息
							TaskDTO taskdto = new TaskDTO(task.getId(), 
									task.getTaskDefinitionKey(), 
									task.getName(), 
									task.getAssignee(),
									task.getCreateTime(), 
									task.getProcessInstanceId(),
									task.getProcessDefinitionId());
							taskdto.setWorkflowFinished(processCoreService.isProcessEnd(dto.getProcessInstanceId()));
							//获得下个任务
							List<Task> nextTasks = processCoreService.findNextTasksByProcessInstanceId(dto.getProcessInstanceId());
							if(CollectionUtils.isNotEmpty(nextTasks)
									&&processCoreService.hasMultiInstanceCharacteristics(nextTasks.get(0).getProcessDefinitionId(), nextTasks.get(0).getTaskDefinitionKey())){
								//下个任务是会签
								if(isRoleCounterSign){
									//如果是会签指定到角色的，需要手动绑定到角色
									processCoreService.bundleTask2Role(dto.getProcessInstanceId(), dto.getParticipantRoles());
								}
								if(!taskdto.isWorkflowFinished()){
									List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
									for (Task nextTask : nextTasks) {
										
										this.writeNextTaskDto(nextTaskDtos, nextTask);
										
										if(isNextCounterSign){
											processCoreService.insertCounterSignRecord(nextTask.getId(),
													task.getId(),
													nextTask.getTaskDefinitionKey(),
													nextTask.getProcessDefinitionId(),
													nextTask.getProcessInstanceId());
										}
										
									}
									taskdto.setNextTask(nextTaskDtos);
								}
							}else if(CollectionUtils.isNotEmpty(nextTasks)&&nextTasks.size()==1){
								//下个任务是普通任务
								List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
								Task nextTask = nextTasks.get(0);
								processCoreService.insertCounterSignRecord(nextTask.getId(),
																		   task.getId(),
																		   nextTask.getTaskDefinitionKey(),
																		   nextTask.getProcessDefinitionId(),
																		   nextTask.getProcessInstanceId());
								//如果指定了下一个委办人，就自动认领任务
								if(StringUtils.isNotBlank(dto.getNextUserId())){
									processCoreService.setSignParticipants(nextTask.getId(), dto.getNextUserId());
									nextTask.setAssignee(dto.getNextUserId());
								}else if(StringUtils.isNotBlank(dto.getNextRoleId())){
									processCoreService.setSignParticipantsRoles(nextTask.getId(), dto.getNextRoleId());
								}
								nextTaskDtos.add(new NextTaskDTO(nextTask.getId(), 
												 				 nextTask.getTaskDefinitionKey(), 
												 				 nextTask.getName(), 
												 				 nextTask.getAssignee(),
												 				 processCoreService.getCandidateGroupFromTask(nextTask.getId())));

								taskdto.setNextTask(nextTaskDtos);
							}
							
							
							return new Result(Boolean.TRUE, "跳转任务节点成功", taskdto); 
						}
					}
				}
				return new Result(Boolean.TRUE,"当前任务不存在",null);
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(Boolean.FALSE,e.getMessage(),null);
			}
		}
		return new Result(Boolean.FALSE,"未收到请求参数",null);
	}
	
	
	@RequestMapping("/execute/back")
	@ResponseBody
	public Result back(@ModelAttribute WorkflowDTO dto) {
		if(dto!=null){
					
			//检查参数信息是否齐全
			if(StringUtils.isBlank(dto.getUserId())){
				return new Result(Boolean.FALSE, "请求参数缺少当前委办人id", null);
			}
			
			try {
				List<Task> tasklist = processCoreService.findTasksByUserAndProcess(dto.getUserId(), dto.getProcessInstanceId());
				if(CollectionUtils.isNotEmpty(tasklist)){
					for (Task task : tasklist) {
						if(task.getId().equals(dto.getTaskId())){
							
							//想在执行本次任务后发起会签 两种类型，一种指定到人，一种指定到角色
							boolean isNextCounterSign = Boolean.FALSE;//下一步是会签
							boolean isRoleCounterSign = Boolean.FALSE;//下一步是角色会签
							if(StringUtils.isNotBlank(dto.getParticipantUsers())){
								processCoreService.setCounterSignParticipants(task.getId(),dto.getParticipantUsers());
								isNextCounterSign = Boolean.TRUE;
							}else if(StringUtils.isNotBlank(dto.getParticipantRoles())){
								processCoreService.setCounterSignParticipants(task.getId(),dto.getParticipantRoles());
								isNextCounterSign = Boolean.TRUE;
								isRoleCounterSign = Boolean.TRUE;
							}
							
							List<HistoricTaskInstance> hists = processCoreService.findHistoryTasksByProcessInstanceId(dto.getProcessInstanceId());
							if(CollectionUtils.isNotEmpty(hists)&&hists.size()>1){
								HistoricTaskInstance perviousTask = hists.get(hists.size()-2);
								
								/**1.完成本次任务*/
								
								//本次任务是会签中的一个任务 记录下会签结果是退回还是通过  
								//普通任务节点
								
								//改变本次任务的完成状态
								processCoreService.updateTaskStatus(task.getId(), "jump", GlobalConstants.TASK_STATUS_COMPLETED);
								//节点跳跃
								processCoreService.rejectTask(dto.getProcessInstanceId(), perviousTask.getTaskDefinitionKey());
							
								/**2.为下次任务做准备*/
								//本次所完成任务的信息
								TaskDTO taskdto = new TaskDTO(task.getId(), 
										task.getTaskDefinitionKey(), 
										task.getName(), 
										task.getAssignee(),
										task.getCreateTime(), 
										task.getProcessInstanceId(),
										task.getProcessDefinitionId());
								taskdto.setWorkflowFinished(processCoreService.isProcessEnd(dto.getProcessInstanceId()));
								//获得下个任务
								List<Task> nextTasks = processCoreService.findNextTasksByProcessInstanceId(dto.getProcessInstanceId());
								if(CollectionUtils.isNotEmpty(nextTasks)
										&&processCoreService.hasMultiInstanceCharacteristics(nextTasks.get(0).getProcessDefinitionId(), nextTasks.get(0).getTaskDefinitionKey())){
									//下个任务是会签
									if(isRoleCounterSign){
										//如果是会签指定到角色的，需要手动绑定到角色
										processCoreService.bundleTask2Role(dto.getProcessInstanceId(), dto.getParticipantRoles());
									}
									if(!taskdto.isWorkflowFinished()){
										List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
										for (Task nextTask : nextTasks) {
											
											this.writeNextTaskDto(nextTaskDtos, nextTask);
											
											if(isNextCounterSign){
												processCoreService.insertCounterSignRecord(nextTask.getId(),
														task.getId(),
														nextTask.getTaskDefinitionKey(),
														nextTask.getProcessDefinitionId(),
														nextTask.getProcessInstanceId());
											}
											
										}
										taskdto.setNextTask(nextTaskDtos);
									}
								}else if(CollectionUtils.isNotEmpty(nextTasks)&&nextTasks.size()==1){
									//下个任务是普通任务
									List<NextTaskDTO> nextTaskDtos = Lists.newArrayList();
									Task nextTask = nextTasks.get(0);
									processCoreService.insertCounterSignRecord(nextTask.getId(),
																			   task.getId(),
																			   nextTask.getTaskDefinitionKey(),
																			   nextTask.getProcessDefinitionId(),
																			   nextTask.getProcessInstanceId());
									//如果指定了下一个委办人，就自动认领任务
									String perviousTaskGroupIds = processCoreService.getCandidateGroupFromHistoryTask(perviousTask.getId());
									if(StringUtils.isNotBlank(perviousTask.getAssignee())){
										processCoreService.setSignParticipants(nextTask.getId(), perviousTask.getAssignee());
										nextTask.setAssignee(perviousTask.getAssignee());
									}
									if(StringUtils.isNotBlank(perviousTaskGroupIds)){
										processCoreService.setSignParticipantsRoles(nextTask.getId(), perviousTaskGroupIds);
									}
									nextTaskDtos.add(new NextTaskDTO(nextTask.getId(), 
													 				 nextTask.getTaskDefinitionKey(), 
													 				 nextTask.getName(), 
													 				 nextTask.getAssignee(),
													 				 processCoreService.getCandidateGroupFromTask(nextTask.getId())));

									taskdto.setNextTask(nextTaskDtos);
								}
								return new Result(Boolean.TRUE,"跳转任务节点成功",taskdto);
							}else{
								return new Result(Boolean.FALSE,"跳转失败，流程未进行够2个节点",null);
							}
							
						}
					}
				}
				return new Result(Boolean.TRUE,"当前任务不存在",null);
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(Boolean.FALSE,e.getMessage(),null);
			}
		}
		return new Result(Boolean.FALSE,"未收到请求参数",null);
	}
	
	private void writeNextTaskDto(List<NextTaskDTO> nextTaskDtos, Task nextTask){
		if(this.isMultiInstanceAnd2RolerTask(nextTask)==MULTI_INSTANCE_ROLE_TYPE){
			String roleId = processCoreService.getCandidateGroupFromTask(nextTask.getId());
			Group role = processCoreService.searchGroup(roleId);
			nextTaskDtos.add(new NextTaskDTO(nextTask.getId(), 
					nextTask.getTaskDefinitionKey(), 
					nextTask.getName()+"("+role.getName()+")", 
					nextTask.getAssignee(),
					roleId
			));
		}else if(this.isMultiInstanceAnd2RolerTask(nextTask)==MULTI_INSTANCE_USER_TYPE){
			User user = processCoreService.searchUser(nextTask.getAssignee());
			nextTaskDtos.add(new NextTaskDTO(nextTask.getId(), 
					nextTask.getTaskDefinitionKey(), 
					nextTask.getName()+"("+user.getFirstName()+")", 
					nextTask.getAssignee(),
					null
			));
		}else{
			nextTaskDtos.add(new NextTaskDTO(nextTask.getId(), 
					nextTask.getTaskDefinitionKey(), 
					nextTask.getName(), 
					nextTask.getAssignee(),
					null
			));
		}
	}
	
	private Integer isMultiInstanceAnd2RolerTask(Task task){
		Boolean b = processCoreService.hasMultiInstanceCharacteristics(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
		if(b){
			String roleId = processCoreService.getCandidateGroupFromTask(task.getId());
			return StringUtils.isNotBlank(roleId)?MULTI_INSTANCE_ROLE_TYPE:MULTI_INSTANCE_USER_TYPE;
		}
		return 0;
	}
	
	
	
	@RequestMapping("/designer")
	public String initDesigner() {
		System.out.println("process initDesigner");
		return GlobalConstants.INIT_DESIGNER;
	}
}
