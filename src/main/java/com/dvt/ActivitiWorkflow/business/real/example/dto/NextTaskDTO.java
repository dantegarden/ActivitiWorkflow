package com.dvt.ActivitiWorkflow.business.real.example.dto;

import java.util.Date;

import com.dvt.ActivitiWorkflow.commons.utils.CommonHelper;

public class NextTaskDTO {
	private String nextTaskId;
	private String nextTaskKey;
	private String nextTaskName;
	private String nextaskAssigner;
	private String nextaskRole;
	private String processInstaceId;
	private String processDefinedId;
	private String startTime;
	public String getNextTaskId() {
		return nextTaskId;
	}
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	public String getNextTaskKey() {
		return nextTaskKey;
	}
	public void setNextTaskKey(String nextTaskKey) {
		this.nextTaskKey = nextTaskKey;
	}
	public String getNextTaskName() {
		return nextTaskName;
	}
	public void setNextTaskName(String nextTaskName) {
		this.nextTaskName = nextTaskName;
	}
	public String getNextaskAssigner() {
		return nextaskAssigner;
	}
	public void setNextaskAssigner(String nextaskAssigner) {
		this.nextaskAssigner = nextaskAssigner;
	}
	
	public NextTaskDTO(String nextTaskId, String nextTaskKey,
			String nextTaskName, String nextaskAssigner, String nextaskRole) {
		super();
		this.nextTaskId = nextTaskId;
		this.nextTaskKey = nextTaskKey;
		this.nextTaskName = nextTaskName;
		this.nextaskAssigner = nextaskAssigner;
		this.nextaskRole = nextaskRole;
	}
	
	public NextTaskDTO(String nextTaskId, String nextTaskKey,
			String nextTaskName, String nextaskAssigner, String nextaskRole,String processInstaceId,String processDefinedId, Date startTime) {
		super();
		this.nextTaskId = nextTaskId;
		this.nextTaskKey = nextTaskKey;
		this.nextTaskName = nextTaskName;
		this.nextaskAssigner = nextaskAssigner;
		this.nextaskRole = nextaskRole;
		this.processInstaceId = processInstaceId;
		this.processDefinedId = processDefinedId;
		this.startTime = CommonHelper.date2Str(startTime, CommonHelper.DF_DATE_TIME);
	}
	public NextTaskDTO() {
		super();
	}
	public String getNextaskRole() {
		return nextaskRole;
	}
	public void setNextaskRole(String nextaskRole) {
		this.nextaskRole = nextaskRole;
	}
	public String getStartTIme() {
		return startTime;
	}
	public void setStartTIme(Date startTime) {
		this.startTime = CommonHelper.date2Str(startTime, CommonHelper.DF_DATE_TIME);
	}
	public String getProcessInstaceId() {
		return processInstaceId;
	}
	public void setProcessInstaceId(String processInstaceId) {
		this.processInstaceId = processInstaceId;
	}
	public String getProcessDefinedId() {
		return processDefinedId;
	}
	public void setProcessDefinedId(String processDefinedId) {
		this.processDefinedId = processDefinedId;
	}
	
	
}
