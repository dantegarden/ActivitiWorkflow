package com.dvt.ActivitiWorkflow.business.real.example.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.dvt.ActivitiWorkflow.commons.entity.BaseEntity;
@Entity
@Table(name = "COUNTER_SIGN_RECORD")
public class CounterSignRecord extends BaseEntity{
	@EmbeddedId
	private CounterSignRecordPk pk;
	
	private String assigneer;
	private String value;
	private String updatetime;
	private String createtime;
	private String lasttaskid;
	private String status;
	
	
	public CounterSignRecord() {
		super();
	}
	
	public CounterSignRecord(CounterSignRecordPk pk, String assigneer,
			String value, String updatetime, String createtime,
			String lasttaskid, String status) {
		super();
		this.pk = pk;
		this.assigneer = assigneer;
		this.value = value;
		this.updatetime = updatetime;
		this.createtime = createtime;
		this.lasttaskid = lasttaskid;
		this.status = status;
	}

	public CounterSignRecordPk getPk() {
		return pk;
	}

	public void setPk(CounterSignRecordPk pk) {
		this.pk = pk;
	}

	@Column(name = "ASSIGNEER", length = 255)
	public String getAssigneer() {
		return assigneer;
	}
	public void setAssigneer(String assigneer) {
		this.assigneer = assigneer;
	}
	@Column(name = "VALUE", length = 255)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Column(name = "UPDATETIME", length = 255)
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	@Column(name = "CREATETIME", length = 255)
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	@Column(name = "LASTTASKID", length = 255)
	public String getLasttaskid() {
		return lasttaskid;
	}
	public void setLasttaskid(String lasttaskid) {
		this.lasttaskid = lasttaskid;
	}
	@Column(name = "STATUS", length = 255)
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
