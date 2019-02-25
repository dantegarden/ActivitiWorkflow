package com.dvt.ActivitiWorkflow.business.real.example.webservice.impl;

import java.net.IDN;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dvt.ActivitiWorkflow.business.real.example.dto.NextTaskDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.RoleDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.TaskDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.UserDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.WorkflowDTO;
import com.dvt.ActivitiWorkflow.business.real.example.service.ProcessCoreService;
import com.dvt.ActivitiWorkflow.business.real.example.webservice.WorkflowService;
import com.dvt.ActivitiWorkflow.commons.GlobalConstants;
import com.dvt.ActivitiWorkflow.commons.entity.Result;
import com.dvt.ActivitiWorkflow.commons.utils.JsonUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class WorkflowServiceImpl implements WorkflowService {
	
	private static final Integer MULTI_INSTANCE_ROLE_TYPE = 1;
	private static final Integer MULTI_INSTANCE_USER_TYPE = 2;
	
	@Autowired
	private ProcessCoreService processCoreService;
	

	@Override
	public String startWorkflow(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			
			//检查参数信息是否齐全
			if(StringUtils.isBlank(dto.getUserId())){
				return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE, "请求参数缺少当前委办人id", null));
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
					
					return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "工作流实例创建成功", taskdto));
				}else{
					return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "工作流实例虽创建成功,但任务发起人没有可完成的任务", null));
				}
			}else{
				return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"工作流实例创建失败",null));
			}
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
		
	}

	@Override
	public String dropWorkflow(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			try {
				processCoreService.deleteProcessInstance(dto.getProcessInstanceId());
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE,"工作流实例删除成功",null));
			} catch (Exception e) {
				e.printStackTrace();
				return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,e.getMessage(),null));
			}
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}

	@Override
	public String getUserTasks(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			List<Task> tasklist = null;
			if(StringUtils.isBlank(dto.getProcessInstanceId())){
				tasklist = processCoreService.findTasksByUser(dto.getUserId());
			}else{
				tasklist = processCoreService.findTasksByUserAndProcess(dto.getUserId(), dto.getProcessInstanceId());
			}
			
			if(CollectionUtils.isNotEmpty(tasklist)){
				List<TaskDTO> dtolist = Lists.newArrayList();
				for (Task task : tasklist) {
					dtolist.add(new TaskDTO(task.getId(), 
							task.getTaskDefinitionKey(), 
							task.getName(), 
							task.getAssignee(),
							task.getCreateTime(), 
							task.getProcessInstanceId(),
							task.getProcessDefinitionId()));
				}
				
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", dtolist)); 
			}
			return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", tasklist)); 
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}


	@Override
	public String getRoleTasks(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			List<Task> tasklist = null;
			if(StringUtils.isBlank(dto.getProcessInstanceId())){
				tasklist = processCoreService.findTasksByUserGroup(dto.getRoleId());
			}else{
				tasklist = processCoreService.findTasksByUserGroupAndProcessId(dto.getRoleId(), dto.getProcessInstanceId());
			}
			if(CollectionUtils.isNotEmpty(tasklist)){
				List<TaskDTO> dtolist = Lists.newArrayList();
				for (Task task : tasklist) {
					dtolist.add(new TaskDTO(task.getId(), 
							task.getTaskDefinitionKey(), 
							task.getName(), 
							task.getAssignee(),
							task.getCreateTime(), 
							task.getProcessInstanceId(),
							task.getProcessDefinitionId()));
				}
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", dtolist)); 
			}
			return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", tasklist)); 
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}

	@Override
	public String getUserRolesTasks(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			List<Task> tasklist = null;
			
			List<String> roleIds = processCoreService.findRolesByUserId(dto.getUserId());
			
			if(StringUtils.isBlank(dto.getProcessInstanceId())){
				tasklist = processCoreService.findTasksByUser(dto.getUserId());
				if(CollectionUtils.isNotEmpty(roleIds))
				for (String roleId : roleIds) {
					tasklist.addAll(processCoreService.findTasksByUserGroup(roleId));
				}
			}else{
				tasklist = processCoreService.findTasksByUserAndProcess(dto.getUserId(), dto.getProcessInstanceId());
				if(CollectionUtils.isNotEmpty(roleIds))
				for (String roleId : roleIds) {
					tasklist.addAll(processCoreService.findTasksByUserGroupAndProcessId(roleId, dto.getProcessInstanceId()));
				}
			}
			if(CollectionUtils.isNotEmpty(tasklist)){
				List<TaskDTO> dtolist = Lists.newArrayList();
				
				for (Task task : tasklist) {
					String taskName = task.getName();
					if(processCoreService.hasMultiInstanceCharacteristics(task.getProcessDefinitionId(), task.getTaskDefinitionKey())){
						//多实例节点
						String roleId = processCoreService.getCandidateGroupFromTask(task.getId());
						if(StringUtils.isNotBlank(roleId)){//多实例节点 角色
							Group role = processCoreService.searchGroup(roleId);
							taskName = taskName+"("+role.getName()+")";
						}else if(StringUtils.isNotBlank(task.getAssignee())){
							User user = processCoreService.searchUser(task.getAssignee());
							taskName = taskName+"("+user.getFirstName()+")";
						}
					}
					
					dtolist.add(new TaskDTO(task.getId(), 
							task.getTaskDefinitionKey(), 
							taskName,
							task.getAssignee(),
							task.getCreateTime(), 
							task.getProcessInstanceId(),
							task.getProcessDefinitionId()));
				}
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", dtolist)); 
			}
			return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", tasklist)); 
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}
	
	@Override
	public String claimTask(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			boolean flag = processCoreService.assigneeTask(dto.getTaskId(), dto.getUserId());
			if(flag){
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "认领成功", null)); 
			}else{
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "认领失败", null)); 
			}
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}
	
	@Override
	public String execTask(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			
			//检查参数信息是否齐全
			if(StringUtils.isBlank(dto.getUserId())){
				return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE, "请求参数缺少当前委办人id", null));
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
								processCoreService.multipolarCompleteTask(counterSignPass.getId(),counterSignJudge);
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
						
						
						return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "任务执行成功", taskdto)); 
					}
				}
			}
			return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE, "该用户没有这个任务，执行失败", null)); 
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}

	@Override
	public String getHistoryTasks(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			List<HistoricTaskInstance> tasklist = null;
			if(StringUtils.isBlank(dto.getProcessInstanceId())){
				tasklist = processCoreService.findActiveHistoryTasksByProcessInstanceId(dto.getProcessInstanceId());
			}else{
				tasklist = processCoreService.findActiveHistoryTasksByUserIdandProcessInstanceId(dto.getUserId(), dto.getProcessInstanceId());
			}
			if(CollectionUtils.isNotEmpty(tasklist)){
				List<TaskDTO> dtolist = Lists.newArrayList();
				Map<String,HistoricTaskInstance> map = new LinkedHashMap<String, HistoricTaskInstance>();
				for (HistoricTaskInstance task : tasklist) {
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
				
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", dtolist)); 
			}
			return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", tasklist)); 
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}
	
	@Override
	public String goBack(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			
			//检查参数信息是否齐全
			if(StringUtils.isBlank(dto.getUserId())){
				return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE, "请求参数缺少当前委办人id", null));
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
								return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "跳转任务节点成功", taskdto)); 
							}else{
								return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"跳转失败，流程未进行够2个节点",null));
							}
							
						}
					}
				}
				JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE,"当前任务不存在",null));
			} catch (Exception e) {
				e.printStackTrace();
				JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,e.getMessage(),null));
			}
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}
	
	@Override
	public String jumpTask(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			
			//检查参数信息是否齐全
			if(StringUtils.isBlank(dto.getUserId())){
				return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE, "请求参数缺少当前委办人id", null));
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
							
							
							return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "跳转任务节点成功", taskdto)); 
						}
					}
				}
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE,"当前任务不存在",null));
			} catch (Exception e) {
				e.printStackTrace();
				return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,e.getMessage(),null));
			}
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}

	@Override
	public String getProcessTasks(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			List<ActivityImpl> activities = processCoreService.getActivitiesByProcessId(dto.getProcessInstanceId());
			if(CollectionUtils.isNotEmpty(activities)){
				List<TaskDTO> dtolist = Lists.newArrayList();
				for (ActivityImpl actImpl : activities) {
					actImpl.getProperty("activiti:candidategroup");
					if(StringUtils.equals("userTask", (String)actImpl.getProperty("type"))
							&&!StringUtils.contains(actImpl.getId(), "-pass")){
						String[] _taskName = ((String)actImpl.getProperty("name")).split("\\$");
						String taskName = _taskName.length>1?_taskName[0]:(String)actImpl.getProperty("name");
						if(!processCoreService.hasMultiInstanceCharacteristics(actImpl.getProcessDefinition().getId(), actImpl.getId())){
							dtolist.add(new TaskDTO(actImpl.getId(), 
									taskName, 
									actImpl.getProcessDefinition().getId()));
						}
					}
				}
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", dtolist)); 
			}
			return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", activities)); 
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
	}

	@Override
	public String modWorkflowRole(String json) {
		List<RoleDTO> roles = JsonUtils.jsonToList(json, RoleDTO.class);
		try {
			processCoreService.modWorkflowRole(roles);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"同步失败 错误:"+e.getMessage(),null));
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE,"同步成功",null));
	}

	@Override
	public String modWorkflowUser(String json) {
		List<UserDTO> users = JsonUtils.jsonToList(json, UserDTO.class);
		try {
			processCoreService.modWorkflowUser(users);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"同步失败 错误:"+e.getMessage(),null));
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE,"同步成功",null));
	}

	@Override
	public String delWorkflowUser(String json) {
		List<UserDTO> users = JsonUtils.jsonToList(json, UserDTO.class);
		try {
			processCoreService.delWorkflowUser(users);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"同步失败 错误:"+e.getMessage(),null));
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE,"同步成功",null));
	}

	@Override
	public String getRecentHistoryTasks(String dtoJson) {
		if(StringUtils.isNotBlank(dtoJson)){
			WorkflowDTO dto = JsonUtils.jsonToJavaBean(dtoJson, WorkflowDTO.class);
			List<HistoricTaskInstance> tasklist = null;
			if(StringUtils.isBlank(dto.getProcessInstanceId())){
				tasklist = processCoreService.findActiveHistoryTasksByProcessInstanceId(dto.getProcessInstanceId());
			}else{
				tasklist = processCoreService.findActiveHistoryTasksByUserIdandProcessInstanceId(dto.getUserId(), dto.getProcessInstanceId());
			}
			if(CollectionUtils.isNotEmpty(tasklist)){
				String firstTaskKey = tasklist.get(0).getTaskDefinitionKey();
				List<TaskDTO> dtolist = Lists.newArrayList();
				
				//倒着找
				int listSize = tasklist.size();
				for (int i = 0; i < tasklist.size(); i++) {
					HistoricTaskInstance task  = tasklist.get(listSize-i-1);
					dtolist.add(new TaskDTO(task.getId(), 
							task.getTaskDefinitionKey(), 
							task.getName(), 
							task.getAssignee(),
							processCoreService.getCandidateGroupFromHistoryTask(task.getId()),
							task.getCreateTime(),
							task.getEndTime(),
							task.getProcessInstanceId(),
							task.getProcessDefinitionId()));
					if(StringUtils.equals(firstTaskKey, task.getTaskDefinitionKey())){
						break;
					}
				}
				
				return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", dtolist)); 
			}
			return JsonUtils.JavaBeanToJson(new Result(Boolean.TRUE, "查询成功", tasklist)); 
		}
		return JsonUtils.JavaBeanToJson(new Result(Boolean.FALSE,"未收到请求参数",null));
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
	

	
}
