package com.dvt.ActivitiWorkflow.business.real.example.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionAttribute;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvt.ActivitiWorkflow.business.real.example.dao.TaskDefinationDao;
import com.dvt.ActivitiWorkflow.business.real.example.entity.ProcessDefination;
import com.dvt.ActivitiWorkflow.business.real.example.entity.TaskDefination;
import com.dvt.ActivitiWorkflow.business.real.example.service.DesignService;
import com.dvt.ActivitiWorkflow.business.real.example.service.InitService;
import com.dvt.ActivitiWorkflow.business.real.example.service.ProcessCoreService;
import com.dvt.ActivitiWorkflow.commons.utils.CommonHelper;
import com.dvt.ActivitiWorkflow.commons.utils.MD5Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
@Transactional
@Service
public class InitServiceImpl implements InitService{
	private static final Logger logger = LoggerFactory.getLogger(InitServiceImpl.class);
	private static final String LINE_SEP = System.getProperty("line.separator");
	private static final String PATH_SEP = File.separator;
	
	@Autowired
	private ProcessCoreService processCoreService;
	@Autowired
	private DesignService designService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private IdentityService identityService;
	@Autowired
	private TaskDefinationDao taskDefinationDao;
	
	@Override
	public void init() {
		try {
			File classes = new File(InitServiceImpl.class.getResource("/").toURI());
			String classesPath = classes.getAbsolutePath();
			String rootPath = classesPath + PATH_SEP + "deployments";
			File deployments = new File(rootPath);
			if(deployments.isDirectory()){
				int count = 0;//需要部署的流程文件个数
				DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
				deploymentBuilder.name(CommonHelper.getNowStr("yyyy年MM月dd日HH点mm分") + "自动化部署");
				List<List<String>> effectFiles = Lists.newArrayList();
				for (File deployFile : deployments.listFiles()) {
					if(deployFile.isFile() && deployFile.getName().endsWith(".bpmn")) {
						String md5 = MD5Utils.getFileMD5(deployFile);
						ProcessDefination pd = designService.findProcessDefinationByMd5(md5);
						if(pd==null){
							FileInputStream fileInputStream = new FileInputStream(deployFile);
							deploymentBuilder.addInputStream(deployFile.getName(), fileInputStream);
							System.out.println(deployFile.getName() + "加入部署计划");
							
							effectFiles.add(ImmutableList.of(deployFile.getAbsolutePath(), md5));
							count++;
						}
						
					}
				}
				if(effectFiles.size() > 0){
					Deployment deployment = deploymentBuilder.deploy();
					for (List<String> effectFile : effectFiles) {
						designService.createProcessDefination(deployment.getId(), effectFile.get(0), effectFile.get(1));
					}
					System.out.println("自定义流程部署成功！");
					this.maintainRoles(deployment);
					//TODO
					
				}else{
					System.out.println("没有需要部署的自定义流程！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**自动维护角色**/
	public void maintainRoles(Deployment deployment){
		List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
		if(CollectionUtils.isNotEmpty(processDefinitionList)){
			for (ProcessDefinition pd : processDefinitionList) {
				BpmnModel model = repositoryService.getBpmnModel(pd.getId());
				String processKey = pd.getId().split(":")[0];
				String processName = pd.getName();
				if(model != null) {  
					Collection<FlowElement> flowElements = model.getMainProcess().getFlowElements();
					for(FlowElement e : flowElements) {
						if (e instanceof UserTask){
							UserTask fe = ((UserTask) e);
							String groupName = fe.getName();
							System.out.println(groupName);
							List<String> candidateGroups = fe.getCandidateGroups();//指定审批角色
							String assigneer = fe.getAssignee();//指定审批人
							MultiInstanceLoopCharacteristics mic = fe.getLoopCharacteristics();
							if(CollectionUtils.isNotEmpty(candidateGroups)){
								for (String groupId : candidateGroups) {
									Group group = processCoreService.searchGroup(groupId);
									if(group==null){
										group = identityService.newGroup(groupId);
										group.setName(groupName);
										group.setType("assignment");
										identityService.saveGroup(group);
										System.out.println("创建自定义固定角色：" + groupName + " " + groupId);
										
										TaskDefination td = new TaskDefination(groupName, 
												processName, fe.getId(), groupId, deployment.getId());
										taskDefinationDao.save(td);
									}
								}
							}else if(mic!=null){
								List<ExtensionAttribute> l = fe.getAttributes().get("candidategroup");
								if(CollectionUtils.isNotEmpty(l) && l.size()==1 && String.valueOf(l.get(0)).contains("activiti:candidategroup")){
									//传角色的多实例节点
									System.out.println("需要配置角色的多实例节点：" + groupName);
									//TODO 和学院做笛卡尔积
									String groupId_perfix = processKey.toUpperCase() + "_" + fe.getId().toUpperCase() + "_";
									TaskDefination td = new TaskDefination(groupName, 
											processName, fe.getId(), groupId_perfix, deployment.getId());
									td.setIsHq(1);
									taskDefinationDao.save(td);
								}
							}
						}
					} 
				}
			}
		}
		
	}
}
