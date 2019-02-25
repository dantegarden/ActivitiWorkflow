package com.dvt.ActivitiWorkflow.business.real.example.dto;

import java.util.Date;
import java.util.List;

import com.dvt.ActivitiWorkflow.commons.utils.CommonHelper;

public class TaskDTO {
	private String taskId;
	private String taskKey;
	private String taskName;
	private String taskAssigner;
	private String taskRoler;
	private String createTime;
	private String endTime;
	private String processInstanceId;
	private String processDefinitionId;
	private boolean isWorkflowFinished;
	private List<NextTaskDTO> nextTask;
	public TaskDTO() {
		super();
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getTaskKey() {
		return taskKey;
	}
	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = CommonHelper.date2Str(createTime, CommonHelper.DF_DATE_TIME);
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = CommonHelper.date2Str(endTime, CommonHelper.DF_DATE_TIME);
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public TaskDTO(String taskKey, String taskName, String processDefinitionId){
		super();
		this.taskKey = taskKey;
		this.taskName = taskName;
		this.processDefinitionId = processDefinitionId;
	}
	public TaskDTO(String taskId, String taskKey, String taskName,String taskAssigner,
			Date createTime, String processInstanceId, String processDefinitionId) {
		super();
		this.taskId = taskId;
		this.taskKey = taskKey;
		this.taskName = taskName;
		this.taskAssigner = taskAssigner;
		this.createTime = CommonHelper.date2Str(createTime, CommonHelper.DF_DATE_TIME);
		this.processInstanceId = processInstanceId;
		this.processDefinitionId = processDefinitionId;
	}
	public TaskDTO(String taskId, String taskKey, String taskName,String taskAssigner,String taskRoler,
			Date createTime,Date endTime, String processInstanceId, String processDefinitionId) {
		super();
		this.taskId = taskId;
		this.taskKey = taskKey;
		this.taskName = taskName;
		this.taskAssigner = taskAssigner;
		this.taskRoler = taskRoler;
		this.createTime = CommonHelper.date2Str(createTime, CommonHelper.DF_DATE_TIME);
		this.endTime = CommonHelper.date2Str(endTime, CommonHelper.DF_DATE_TIME);
		this.processInstanceId = processInstanceId;
		this.processDefinitionId = processDefinitionId;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public boolean isWorkflowFinished() {
		return isWorkflowFinished;
	}
	public void setWorkflowFinished(boolean isWorkflowFinished) {
		this.isWorkflowFinished = isWorkflowFinished;
	}
	public String getTaskAssigner() {
		return taskAssigner;
	}
	public void setTaskAssigner(String taskAssigner) {
		this.taskAssigner = taskAssigner;
	}
	public List<NextTaskDTO> getNextTask() {
		return nextTask;
	}
	public void setNextTask(List<NextTaskDTO> nextTask) {
		this.nextTask = nextTask;
	}
	public String getTaskRoler() {
		return taskRoler;
	}
	public void setTaskRoler(String taskRoler) {
		this.taskRoler = taskRoler;
	}
	
	
	
	
}
