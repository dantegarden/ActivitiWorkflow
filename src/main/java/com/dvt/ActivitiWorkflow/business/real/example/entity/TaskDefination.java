package com.dvt.ActivitiWorkflow.business.real.example.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dvt.ActivitiWorkflow.commons.entity.BaseEntity;

@Entity
@Table(name = "T_TASK_DEFINATION")
public class TaskDefination extends BaseEntity{
	private Integer id;
	private String taskName;
	private String processName;
	private Integer isHq = 0;//默认不是会签
	private String taskCode;
	private String roleCode;
	private String deploymentId;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false, precision = 12, scale = 0)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "TASK_NAME", length = 500)
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	@Column(name = "PROCESS_NAME", length = 500)
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	@Column(name = "IS_HQ", nullable = false, precision = 12, scale = 0)
	public Integer getIsHq() {
		return isHq;
	}
	public void setIsHq(Integer isHq) {
		this.isHq = isHq;
	}
	@Column(name = "TASK_CODE", length = 500)
	public String getTaskCode() {
		return taskCode;
	}
	public void setTaskCode(String taskCode) {
		this.taskCode = taskCode;
	}
	@Column(name = "ROLE_CODE", length = 500)
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	@Column(name = "DEPLOYMENT_ID", length = 500)
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	
	public TaskDefination(Integer id, String taskName, String processName,
			Integer isHq, String taskCode, String roleCode, String deploymentId) {
		super();
		this.id = id;
		this.taskName = taskName;
		this.processName = processName;
		this.isHq = isHq;
		this.taskCode = taskCode;
		this.roleCode = roleCode;
		this.deploymentId = deploymentId;
	}
	public TaskDefination(Integer id, String taskName,
			String processName, Integer isHq, String taskCode,
			String roleCode) {
		super();
		this.id = id;
		this.taskName = taskName;
		this.processName = processName;
		this.isHq = isHq;
		this.taskCode = taskCode;
		this.roleCode = roleCode;
	}
	public TaskDefination() {
		super();
	}
	public TaskDefination(String taskName, String processName,
			String taskCode, String roleCode) {
		super();
		this.taskName = taskName;
		this.processName = processName;
		this.taskCode = taskCode;
		this.roleCode = roleCode;
	}
	public TaskDefination(String taskName, String processName, String taskCode,
			String roleCode, String deploymentId) {
		super();
		this.taskName = taskName;
		this.processName = processName;
		this.taskCode = taskCode;
		this.roleCode = roleCode;
		this.deploymentId = deploymentId;
	}
	
	
	
}
