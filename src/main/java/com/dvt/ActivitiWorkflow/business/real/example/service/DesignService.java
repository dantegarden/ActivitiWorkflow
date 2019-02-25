package com.dvt.ActivitiWorkflow.business.real.example.service;

import java.util.List;

import com.dvt.ActivitiWorkflow.business.real.example.entity.ProcessDefination;

public interface DesignService {
	public void createProcessDefination(String deploymentId, String filePath, String md5);
	
	public void createProcessDefination(String processDefinationId, String processDefinationName,String processData, String processForm, String processJudge, String filePath);

	public List<ProcessDefination> findProcessDefinations();
	
	public ProcessDefination findProcessDefinationById(Integer id);
	
	public void saveProcessDefination(ProcessDefination pd);
	
	public ProcessDefination findProcessDefinationByMd5(String md5);
	
	public int deleteByDeploymentId(String deploymentId);
}
