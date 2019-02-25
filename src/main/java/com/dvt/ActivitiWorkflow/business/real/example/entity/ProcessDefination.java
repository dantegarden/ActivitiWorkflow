package com.dvt.ActivitiWorkflow.business.real.example.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.dvt.ActivitiWorkflow.commons.entity.BaseEntity;
@Entity
@Table(name = "T_PROCESS_DEFINATION")
public class ProcessDefination extends BaseEntity{
	private Integer id;
	private String processDefinationId;
	private String processDefinationName;
	private String filePath;
	private String processData;
	private String processForm;
	private String processJudge;
	private String md5;
	private String deploymentId;
	
	public ProcessDefination(String deploymentId, String filePath, String md5) {
		super();
		this.deploymentId = deploymentId;
		this.filePath = filePath;
		this.md5 = md5;
	}

	public ProcessDefination(Integer id, String processDefinationId,
			String processDefinationName, String filePath, String processData,
			String processForm, String processJudge) {
		super();
		this.id = id;
		this.processDefinationId = processDefinationId;
		this.processDefinationName = processDefinationName;
		this.filePath = filePath;
		this.processData = processData;
		this.processForm = processForm;
		this.processJudge = processJudge;
	}
	
	public ProcessDefination(Integer id, String processDefinationId,
			String processDefinationName, String filePath, String processData,
			String processForm, String processJudge, String md5) {
		super();
		this.id = id;
		this.processDefinationId = processDefinationId;
		this.processDefinationName = processDefinationName;
		this.filePath = filePath;
		this.processData = processData;
		this.processForm = processForm;
		this.processJudge = processJudge;
		this.md5 = md5;
	}

	public ProcessDefination() {
		super();
	}
	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false, precision = 12, scale = 0)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "PROCESS_DEFINATION_ID", length = 500)
	public String getProcessDefinationId() {
		return processDefinationId;
	}
	public void setProcessDefinationId(String processDefinationId) {
		this.processDefinationId = processDefinationId;
	}
	@Column(name = "PROCESS_DEFINATION_NAME", length = 500)
	public String getProcessDefinationName() {
		return processDefinationName;
	}
	public void setProcessDefinationName(String processDefinationName) {
		this.processDefinationName = processDefinationName;
	}
	@Column(name = "FILE_PATH", length = 500)
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	@Column(name="PROCESS_DATA")
	public String getProcessData() {
		return processData;
	}
	public void setProcessData(String processData) {
		this.processData = processData;
	}
	@Column(name="PROCESS_FORM")
	public String getProcessForm() {
		return processForm;
	}
	public void setProcessForm(String processForm) {
		this.processForm = processForm;
	}
	@Column(name="PROCESS_JUDGE")
	public String getProcessJudge() {
		return processJudge;
	}
	public void setProcessJudge(String processJudge) {
		this.processJudge = processJudge;
	}
	@Column(name = "MD5", length = 500)
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	@Column(name = "DEPLOYMENT_ID", length = 500)
	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	
}
