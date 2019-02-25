package com.dvt.ActivitiWorkflow.business.real.example.webservice;

import javax.jws.WebService;

import com.dvt.ActivitiWorkflow.business.real.example.dto.WorkflowDTO;

/**
* 接口定义
*  @WebService
*  用于定义webservice对外开放的接口
*/
@WebService
public interface WorkflowService {
	
	/**开启流程*/
	public String startWorkflow(String dtoJson);
	/**强制结束流程*/
	public String dropWorkflow(String dtoJson);
	/**获取用户角色的待办任务*/
	public String getRoleTasks(String dtoJson);
	/**认领任务*/
	public String claimTask(String dtoJson);
	/**获取用户的待办任务*/
	public String getUserTasks(String dtoJson);
	/**提交任务*/
	public String execTask(String dtoJson);
	/**查看已走过的任务节点*/
	public String getHistoryTasks(String dtoJson);
	/**跳转到某个任务节点*/
	public String jumpTask(String dtoJson);
	/**查询某人及角色身上的待办列表*/
	public String getUserRolesTasks(String dtoJson);
	/**查询某流程全部任务节点*/
	public String getProcessTasks(String dtoJson);
	/**驳回到上一步*/
	public String goBack(String dtoJson);
	
	/**查询最近一次起始到当前的历史节点*/
	public String getRecentHistoryTasks(String dtoJson);
	
	/**用户角色关系表 数据同步*/
	public String modWorkflowUser(String json);
	public String delWorkflowUser(String json);
	public String modWorkflowRole(String json);
	
}
