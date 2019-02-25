package com.dvt.ActivitiWorkflow.business.real.example.vo;

import java.sql.Timestamp;

import com.dvt.ActivitiWorkflow.commons.utils.CommonHelper;

public class DeployDetailVO {
	private String key;
	private String name;
	private String prodefinedId;
	private String resource;
	
	public DeployDetailVO(Object[] input) {
		this.name = (String)input[0];
		this.key = (String)input[1];
		this.prodefinedId = (String)input[2];
		this.resource = (String)input[3];
	}
	
	public DeployDetailVO(String key, String name, String prodefinedId,
			String resource) {
		super();
		this.key = key;
		this.name = name;
		this.prodefinedId = prodefinedId;
		this.resource = resource;
	}
	public DeployDetailVO() {
		super();
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProdefinedId() {
		return prodefinedId;
	}
	public void setProdefinedId(String prodefinedId) {
		this.prodefinedId = prodefinedId;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	
}
