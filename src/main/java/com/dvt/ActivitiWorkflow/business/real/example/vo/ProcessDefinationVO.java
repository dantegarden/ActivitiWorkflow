package com.dvt.ActivitiWorkflow.business.real.example.vo;

import com.dvt.ActivitiWorkflow.commons.entity.BaseBean;

public class ProcessDefinationVO extends BaseBean{
	private Integer id;
	private String processDefinationId;
	private String filePath;
	private String processData;
	private String processForm;
	private String processJudge;
	private String processDefinationName;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProcessDefinationName() {
		return processDefinationName;
	}
	public void setProcessDefinationName(String processDefinationName) {
		this.processDefinationName = processDefinationName;
	}
	public String getProcessDefinationId() {
		return processDefinationId;
	}
	public void setProcessDefinationId(String processDefinationId) {
		this.processDefinationId = processDefinationId;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getProcessData() {
		return processData;
	}
	public void setProcessData(String processData) {
		this.processData = processData;
	}
	public String getProcessForm() {
		return processForm;
	}
	public void setProcessForm(String processForm) {
		this.processForm = processForm;
	}
	public String getProcessJudge() {
		return processJudge;
	}
	public void setProcessJudge(String processJudge) {
		this.processJudge = processJudge;
	}
	
	
	
}
