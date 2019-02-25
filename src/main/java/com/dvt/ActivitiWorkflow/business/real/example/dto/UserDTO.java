package com.dvt.ActivitiWorkflow.business.real.example.dto;

import java.util.List;

public class UserDTO {
	public String userid;
	public String name;
	public List<RoleDTO> roles;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<RoleDTO> getRoles() {
		return roles;
	}
	public void setRoles(List<RoleDTO> roles) {
		this.roles = roles;
	}
	public UserDTO(String userid, String name, List<RoleDTO> roles) {
		super();
		this.userid = userid;
		this.name = name;
		this.roles = roles;
	}
	public UserDTO() {
		super();
	}
	
	
	
}
