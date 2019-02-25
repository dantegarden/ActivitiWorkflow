package com.dvt.ActivitiWorkflow.business.real.example.dto;

public class ProcessInstanceDTO {
	private String deploymentId;
	private String processDefinitionId;
	private String processInstanceId;
	private String taskId;
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public ProcessInstanceDTO(String deploymentId, String processDefinitionId,
			String processInstanceId) {
		super();
		this.deploymentId = deploymentId;
		this.processDefinitionId = processDefinitionId;
		this.processInstanceId = processInstanceId;
	}
	public ProcessInstanceDTO(String processDefinitionId,
			String processInstanceId) {
		super();
		this.processDefinitionId = processDefinitionId;
		this.processInstanceId = processInstanceId;
	}
	public ProcessInstanceDTO() {
		super();
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	
}
