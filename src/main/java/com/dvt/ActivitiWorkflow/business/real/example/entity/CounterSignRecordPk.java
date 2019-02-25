package com.dvt.ActivitiWorkflow.business.real.example.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class CounterSignRecordPk implements Serializable{
	private String processdefinitionid;
	private String processinstanceid;
	private String taskid;
	private String taskdefinitionkey;
	
	@Column(name = "PROCESSDEFINITIONID", length = 255)
	public String getProcessdefinitionid() {
		return processdefinitionid;
	}
	public void setProcessdefinitionid(String processdefinitionid) {
		this.processdefinitionid = processdefinitionid;
	}
	@Column(name = "PROCESSINSTANCEID", length = 255)
	public String getProcessinstanceid() {
		return processinstanceid;
	}
	public void setProcessinstanceid(String processinstanceid) {
		this.processinstanceid = processinstanceid;
	}
	@Column(name = "TASKID", length = 255)
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	@Column(name = "TASKDEFINITIONKEY", length = 255)
	public String getTaskdefinitionkey() {
		return taskdefinitionkey;
	}
	public void setTaskdefinitionkey(String taskdefinitionkey) {
		this.taskdefinitionkey = taskdefinitionkey;
	}
	
	public CounterSignRecordPk(String processdefinitionid,
			String processinstanceid, String taskid, String taskdefinitionkey) {
		super();
		this.processdefinitionid = processdefinitionid;
		this.processinstanceid = processinstanceid;
		this.taskid = taskid;
		this.taskdefinitionkey = taskdefinitionkey;
	}
	public CounterSignRecordPk() {
		super();
	}
	
	
}
