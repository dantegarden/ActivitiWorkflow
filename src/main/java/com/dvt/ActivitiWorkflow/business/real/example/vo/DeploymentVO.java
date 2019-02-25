package com.dvt.ActivitiWorkflow.business.real.example.vo;

import java.sql.Timestamp;
import java.util.Date;

import com.dvt.ActivitiWorkflow.commons.utils.CommonHelper;

public class DeploymentVO {
	private String deploymentId;
	private String name;
	private String deploymentTime;
	
	public DeploymentVO(String deploymentId, String name, Date deploymentTime) {
		super();
		this.deploymentId = deploymentId;
		this.name = name;
		this.deploymentTime = CommonHelper.date2Str(deploymentTime, CommonHelper.DF_DATE_TIME);
	}
	public DeploymentVO() {
		super();
	}
	
	public DeploymentVO(Object[] input) {
		this.deploymentId = (String)input[0];
		this.name = (String)input[1];
		this.deploymentTime =  CommonHelper.date2Str((Timestamp)input[2], CommonHelper.DF_DATE_TIME);
	}
	
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDeploymentTime() {
		return deploymentTime;
	}
	public void setDeploymentTime(Date deploymentTime) {
		this.deploymentTime = CommonHelper.date2Str(deploymentTime, CommonHelper.DF_DATE_TIME);
	}
	
	
}
