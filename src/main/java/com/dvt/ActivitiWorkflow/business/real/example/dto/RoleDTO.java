package com.dvt.ActivitiWorkflow.business.real.example.dto;

public class RoleDTO {
	public String roleid;
	public String name;
	public String getRoleid() {
		return roleid;
	}
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RoleDTO(String roleid, String name) {
		super();
		this.roleid = roleid;
		this.name = name;
	}
	public RoleDTO() {
		super();
	}
	
	
}
