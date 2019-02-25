package com.dvt.ActivitiWorkflow.business.real.example.service.impl;

import java.util.List;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvt.ActivitiWorkflow.business.real.example.service.RoleService;
import com.dvt.ActivitiWorkflow.commons.query.DynamicQuery;
@Transactional
@Service("roleService")
public class RoleServiceImpl implements RoleService{
	
	@Autowired
	private DynamicQuery dynamicQuery;
	@Autowired
	private IdentityService identityService;
	
	@Override
	public List<Group> findGroups() {
		return identityService.createGroupQuery().list();
	}

	@Override
	public Group findGroupById(String groupId) {
		return identityService.createGroupQuery().groupId(groupId).singleResult();
	}

	@Override
	public void saveGroup(Group group) {
		identityService.saveGroup(group);
	}

}
