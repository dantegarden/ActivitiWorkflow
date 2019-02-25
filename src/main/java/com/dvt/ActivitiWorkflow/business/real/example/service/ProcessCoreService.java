package com.dvt.ActivitiWorkflow.business.real.example.service;

import java.util.List;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.data.domain.Page;

import com.dvt.ActivitiWorkflow.business.real.example.dto.RoleDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.UserDTO;
import com.dvt.ActivitiWorkflow.business.real.example.vo.DeploymentVO;


public interface ProcessCoreService {
	
	/** 
	 * 部署流程定义 类路径从classpath
	 * @param bpmns 装着bpmn文件路径的list 
	 * @param name 流程定义名
	 * @see
	 * act_re_deployment存放流程定义的显示名和部署时间，每部署一次增加一条记录；
     * act_re_procdef（存放流程定义的属性信息，部署每个新的流程定义都会在这张表中增加一条记录，需要注意一下的当流程定义的key相同的情况下，使用的是版本升级；
     * act_ge_bytearray存储流程定义相关的部署信息。即流程定义文档的存放地。每部署一次就会增加两条记录，一条是关于bpmn规则文件的，一条是图片的（如果部署时只指定了bpmn一个文件，activiti会在部署时解析bpmn文件内容自动生成流程图）。两个文件不是很大，都是以二进制形式存储在数据库中。
	 */  
	public void deoploymentProcessDefinitionByClasspath(List<String> bpmns, String name);
	/** 
	 * 部署流程定义 从zip
	 * @param bpmnsZip xxx.bpmn与xxx.png压缩成zip文件的路径
	 * @param name 流程定义名
	 */ 
	public void deploymentProcessDefinitionByZip(String bpmnsZip, String name);
	/**
	 * 查询所有的流程定义 
	 * @param processDefinitionKey  流程定义id
	 * @see
	 * key属性被用来区别不同的流程定义
	 * 带有特定key的流程定义第一次部署时，version为1。之后每次部署都会在当前最高版本号上加1
	 * Id的值的生成规则为:{processDefinitionKey}:{processDefinitionVersion}:{generated-id},这里的generated-id是一个自动生成的唯一的数字
	 * 重复部署一次，deploymentId的值以一定的形式变化规则act_ge_property表生成
	 * */
	public List<ProcessDefinition> findProcessDefinition(String processDefinitionId);
	/**
	 * 查询最新版本的流程定义
	 * @param processDefinitionKey  流程定义id 
	 * */
	public ProcessDefinition findLastVersionProcessDefinition(String processDefinitionId); 
	/**
	 * 删除流程定义
	 * @param processDefinitionKey  流程定义id
	 * @see
	 * 根据流程定义的key先查询出key值相同的所有版本的流程定义，然后获取每个流程定义的部署对象id
	 * 利用部署对象id，进行级联删除
	 * */
	public void deleteProcessDefinitionByKey(String processDefinitionKey);
	
	/**
	 * 启动流程
	 * @param processDefinitionKey 流程定义的key
	 * @param userId 流程启动者id
	 * @see
	 * 1)在数据库的act_ru_execution正在执行的执行对象表中插入一条记录
       2)在数据库的act_hi_procinst程实例的历史表中插入一条记录
       3)在数据库的act_hi_actinst活动节点的历史表中插入一条记录
       4)我们图中节点都是任务节点，所以同时也会在act_ru_task流程实例的历史表添加一条记录
       5)在数据库的act_hi_taskinst任务历史表中也插入一条记录。
	 * */
	public ProcessInstance startWorkflow(String processDefinitionKey, String userId);
	/**
	 * 查询流程实例
	 * @param processInstanceId 流程实例id
	 * */
	public ProcessInstance findProcessInstance(String processInstanceId);
	/**
	 * 查询历史流程实例
	 * @param processInstanceId 流程实例id
	 * */
	public HistoricProcessInstance findHistoryProcessInstance(String processInstanceId);
	
	/**
	 * 获取用户个人任务列表
	 * @param userid 用户id
	 * */
	public List<Task> findTasksByUser(String userid);
	/**
	 * 获取用户个人的某个流程下的任务列表
	 * @param userid 用户id
	 * @param processid 流程 id
	 * @see
	 * 任务ID、名称、办理人、创建时间可以从act_ru_task表中查到
	 * 任务ID在数据库表act_ru_task中对应“ID_”列
	 * */
	public List<Task> findTasksByUserAndProcess(String userid,String processId);
	/**
	 * 认领任务
	 * @param taskId 任务id
	 * @param userId 用户id
	 * @return boolean 认领成功或失败
	 * */
	public boolean assigneeTask(String taskId, String userId);
	/**
	 * 完成任务 （单向）
	 * @param taskId 任务id
	 * @see
	 * 对于执行完的任务，activiti将从act_ru_task表中删除该任务，下一个任务会被插入进来。
	 * */
	public void completeTask(String taskId);
	/**
	 * 完成任务（标准多极）
	 * @param taskId
	 * @see
	 * 
	 * */
	public void multipolarCompleteTask(String taskId, String polarValue);
	/**
	 * 查询某流程的任务
	 * @param processInstanceId 流程实例id
	 * */
	public List<Task> findTasksByProcessInstanceId(String processInstanceId);
	/**
	 * 查询某流程的下一个任务
	 * @param processInstanceId 流程实例id
	 * */
	public Task findNextTaskByProcessInstanceId(String processInstanceId);
	/**
	 * 查询某流程的下一个任务（会签）
	 * @param processInstanceId 流程实例id
	 * */
	public List<Task> findNextTasksByProcessInstanceId(String processInstanceId);
	/**
	 * 查询某流程的历史任务
	 * @param processInstanceId 流程实例id
	 * */
	public List<HistoricTaskInstance> findHistoryTasksByProcessInstanceId(String processInstanceId);
	/**
	 * 查询用户在某流程中的历史任务
	 * @param userid 用户id
	 * @param processInstanceId 流程实例id
	 * */
	public List<HistoricTaskInstance> findActiveHistoryTasksByUserIdandProcessInstanceId(String userid, String processInstanceId);
	/**
	 * 查询某流程的历史任务 过滤无效会签节点
	 * @param processInstanceId 流程实例id
	 * */
	public List<HistoricTaskInstance> findActiveHistoryTasksByProcessInstanceId(String processInstanceId);
	/**
	 * 查询某流程的历史任务 过滤无效会签节点
	 * @param processInstanceId 流程实例id
	 * */
	public List<HistoricTaskInstance> findActiveHistoryTasksByUserId(String userId);
	/**
	 * 查询用户在某流程中的历史任务 过滤无效会签节点
	 * @param userid 用户id
	 * @param processInstanceId 流程实例id
	 * */
	public List<HistoricTaskInstance> findHistoryTasksByUserIdandProcessInstanceId(String userid, String processInstanceId);
	/**
	 * 查询流程是否结束（判断流程正在执行，还是结束）
	 * @param processInstanceId 流程实例id
	 * */
	public boolean isProcessEnd(String processInstanceId);
	/**
	 * 获取某用户组的代认领任务
	 * @param group 用户组id
	 * @param processId 流程实例id
	 * */
	public List<Task> findTasksByUserGroup(String group);
	/**
	 * 获取某流程的某用户组的代认领任务
	 * @param group 用户组id
	 * @param processId 流程实例id
	 * */
	public List<Task> findTasksByUserGroupAndProcessId(String group, String processId);
	/**
	 * 删除流程实例
	 * @param processId 流程实例id
	 * */
	public void deleteProcessInstance(String processId);
	/**
	 * 删除任务定义
	 * @param deploymentId 部署id
	 * */
	public void deleteTaskDefination(String deploymentId);
	/**
	 * 获取当前流程定义模型的所有任务节点
	 * @param procInstId 流程实例id
	 * */
	public List<ActivityImpl> getActivitiesByProcessId(String procInstId);
	/**
	 * 根据实例编号查找下一个任务节点
	 * @param proInstId 流程实例id
	 * */
	public ActivityImpl nextTaskDefinition(String procInstId,String judge);
	/**
     * 下一个任务节点
     * @param activityImpl
     * @param activityId
     * @param elString
     * @return
     */
	public ActivityImpl nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString);
	/**
	 * 驳回到某个节点
	 * @param proInstId 流程实例id
	 * @param destTaskKey 目标任务key
	 * **/
	public void rejectTask(String proInstId, String destTaskKey);
	/**
	 * 设置参与会签的人员列表
	 * @param participantUsers 人员列表
	 * **/
	public void setCounterSignParticipants(String proInstId, String participantUsers);
	/**
	 * 绑定待办任务到角色
	 * @param processInstanceId 流程实例id
	 * @param participantRoles 角色列表
	 * */
	public void bundleTask2Role(String processInstanceId, String participantRoles);
	
	/**
	 * 记录会签的并行任务 到 数据库
	 * **/
	public void insertCounterSignRecord(String taskId, String lastTaskId,
			String taskDefinitionKey, String processDefinitionId,
			String processInstanceId);
	
	/**
	 * 是否是并签节点
	 * @param processDefinitionId
	 * @param taskKey
	 * **/
	public boolean hasMultiInstanceCharacteristics(String processDefinitionId,String taskKey);
	/**
	 * 更新会签的并行任务的状态
	 * @param signResult 
	 * @return 0全票通过 1一票否决 2未完成 3其他
	 * */
	public int updateCounterSignRecord(String id, String processDefinitionId,
			String processInstanceId, String taskDefinitionKey, String assigneer, String signResult);
	/**
	 * 更新会签的并行任务的状态 一票否决
	 * @param signResult 
	 * @return 0全票通过 1一票否决 2未完成 3其他 4一票否决制
	 * */
	public int updateCounterSignRecordOneDeny(String id, String processDefinitionId,
			String processInstanceId, String taskDefinitionKey, String assigneer, String signResult);
	/**
	 * 通过用户id找他的角色
	 * @param roleid
	 * */
	public List<String> findRolesByUserId(String userId);
	/**
	 * 通过任务id获取任务定义
	 * @param taskId
	 * */
	public ActivityImpl findTaskActivity(String taskId);
	/**
	 * 通过任务key获取任务定义
	 * @param processDefinitionId
	 * @param taskKey
	 * */
	public ActivityImpl findTaskActivity(String processDefinitionId,String taskKey);
	/**
	 * 查询上一步任务和下步任务是否是同一批会签
	 * @param proInsId 流程实例id
	 * @param judge 选值 没有传null
	 * */
	public boolean IsPerviousCounterSign(String proInsId,String judge);
	/**
	 * 查询上次会签做出驳回的参与者
	 * @param processInstanceId 流程实例id
	 * */
	public String findPerviousDenyParticipants(String processInstanceId);
	/**
	 * 查询本次会签的决策状态
	 * @param 会签中的任务id
	 * */
	public List<String> findCounterSignStatus(String taskId);
	/**
	 * 查询本次会签否决的人
	 * @param 会签中的任务id
	 * */
	public List<String> findCounterSignDenyUsers(String taskId);
	/**
	 * 查询本次会签中某用户的审批状态
	 * @param 会签中的任务id
	 * @param 用户id
	 * */
	public String findCounterSignStatusByUser(String taskId,String userId);
	/**
	 * 更改整次会签的记录状态
	 * @param taskId 其中一个会签任务
	 * @status 改成什么，枚举
	 * */
	public void updateCounterSignTaskStatus(String taskId,String status);
	/**
	 * 更改整次会签的记录状态
	 * @param taskId 其中一个会签任务
	 * @status 改成什么，枚举
	 * */
	public void updateTaskStatus(String taskId,String judge,String status);
	/**
	 * 该节点是否是单出节点
	 * @param taskId 
	 * */
	public boolean isSingleOutTask(String taskId);
	/**
	 * 添加修改用户
	 * */
	public void modWorkflowUser(List<UserDTO> users);
	/**
	 * 删除用户
	 * */
	public void delWorkflowUser(List<UserDTO> users);
	/**
	 * 添加修改角色
	 * */
	public void modWorkflowRole(List<RoleDTO> roles);
	/**
	 * 绑定待办任务到用户 单实例
	 * @param processInstanceId 流程实例id
	 * @param participantUsers 用户列表
	 * */
	public void setSignParticipants(String proInstId, String participantUsers);
	/**
	 * 绑定待办任务到角色 单实例
	 * @param processInstanceId 流程实例id
	 * @param participantRoles 角色列表
	 * */
	public void setSignParticipantsRoles(String proInstId, String participantRoles);
	/**
	 * 获得task的委派角色
	 * @param String taskId
	 * @return groupId逗号分隔的字符串
	 * */
	public String getCandidateGroupFromTask(String taskId);
	/**
	 * 获得historytask的委派角色
	 * @param String taskId
	 * @return groupId逗号分隔的字符串
	 * */
	public String getCandidateGroupFromHistoryTask(String taskId);
	
	//WEB-UI的相关方法
	
	/**
	 * 获取当前在部署的包
	 * */
//	public Page findDeployments(Integer pageNumber, Integer pageSize,
//			List<Object> params);
	public List findDeployments(Integer firstNumber, Integer pageSize,
			List<Object> params);
	public long countDeployments();
	/**
	 * 查看某部署包有多少活跃流程实例
	 * @param deploymentId
	 * */
	public Long countActiveProcessInstanceByDeploymentId(String deploymentId);
	public List<ProcessInstance> findActiveProcessInstanceByDeploymentId(String deploymentId);
	/**卸载部署包*/
	public void dropDeployment(String deploymentId);
	/**获得部署包详情*/
	public Page findDeployDetail(Integer pageNumber, Integer pageSize, List<Object> params);
	/**直接查用户id的pojo*/
	public User searchUser(String userId);
	/**直接查角色id的pojo*/
	public Group searchGroup(String groupId);
	/**创建角色**/
	public void saveGroup(Group aig);
	
}
