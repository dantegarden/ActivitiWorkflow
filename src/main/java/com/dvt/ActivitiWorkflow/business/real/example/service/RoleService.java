package com.dvt.ActivitiWorkflow.business.real.example.service;

import java.util.List;

import org.activiti.engine.identity.Group;

public interface RoleService {
	
	
	public List<Group> findGroups(); 
	
	public Group findGroupById(String groupId);
	
	public void saveGroup(Group group);
}
