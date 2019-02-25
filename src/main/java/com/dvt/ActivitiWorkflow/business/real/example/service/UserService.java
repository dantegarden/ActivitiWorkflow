package com.dvt.ActivitiWorkflow.business.real.example.service;

import java.util.List;

import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

public interface UserService {
	public String findLeader(String userId);
	public String findDepartLeader(String userId);
	public String findDepartGroup(String userId);
	
	public String getAssginerRoles(String userId);
	//获取参与会签的角色
	public String getLeaderApprovalRoles(String variables);
	
	public List<String> getSignUser(ActivityExecution execution);
	
	/**会签完成条件 所有人都需要投票*/
	public boolean isComplete(ActivityExecution execution);
	/**会签完成条件 一票否决，有否决时立刻结束会签*/
	public boolean oneDeny(ActivityExecution execution);
}
