package com.dvt.ActivitiWorkflow.business.real.example.dto;

public class WorkflowDTO {
	public String processDefinitionKey;
	public String processInstanceId;
	public String taskId;
	public String userId;
	public String roleId;
	public String judge;
	public String nextUserId;
	public String nextRoleId;
	public String destTaskKey;
	public String variables;//要设置在流程之始的全局变量
	public String signResult;//单人的会签结果 同意不同意 布尔
	public String participantUsers;
	public String participantRoles;
	public String academy;//学院
		
	
	public WorkflowDTO(String processDefinitionKey, String processInstanceId,
			String taskId, String userId, String roleId, String judge,
			String nextUserId, String nextRoleId, String destTaskKey,
			String variables, String signResult, String participantUsers,
			String participantRoles, String academy) {
		super();
		this.processDefinitionKey = processDefinitionKey;
		this.processInstanceId = processInstanceId;
		this.taskId = taskId;
		this.userId = userId;
		this.roleId = roleId;
		this.judge = judge;
		this.nextUserId = nextUserId;
		this.nextRoleId = nextRoleId;
		this.destTaskKey = destTaskKey;
		this.variables = variables;
		this.signResult = signResult;
		this.participantUsers = participantUsers;
		this.participantRoles = participantRoles;
		this.academy = academy;
	}
	public WorkflowDTO() {
		super();
	}
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}
	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getJudge() {
		return judge;
	}
	public void setJudge(String judge) {
		this.judge = judge;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getDestTaskKey() {
		return destTaskKey;
	}
	public void setDestTaskKey(String destTaskKey) {
		this.destTaskKey = destTaskKey;
	}
	public String getNextUserId() {
		return nextUserId;
	}
	public void setNextUserId(String nextUserId) {
		this.nextUserId = nextUserId;
	}
	public String getVariables() {
		return variables;
	}
	public void setVariables(String variables) {
		this.variables = variables;
	}
	public String getParticipantUsers() {
		return participantUsers;
	}
	public void setParticipantUsers(String participantUsers) {
		this.participantUsers = participantUsers;
	}
	public String getParticipantRoles() {
		return participantRoles;
	}
	public void setParticipantRoles(String participantRoles) {
		this.participantRoles = participantRoles;
	}
	public String getSignResult() {
		return signResult;
	}
	public void setSignResult(String signResult) {
		this.signResult = signResult;
	}
	public String getAcademy() {
		return academy;
	}
	public void setAcademy(String academy) {
		this.academy = academy;
	}
	public String getNextRoleId() {
		return nextRoleId;
	}
	public void setNextRoleId(String nextRoleId) {
		this.nextRoleId = nextRoleId;
	}
	
	
}
