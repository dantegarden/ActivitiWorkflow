package com.dvt.ActivitiWorkflow.business.real.example.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dvt.ActivitiWorkflow.business.real.example.dto.design.BlockDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.design.ConnDTO;
import com.dvt.ActivitiWorkflow.business.real.example.dto.design.ProcessData;
import com.dvt.ActivitiWorkflow.business.real.example.dto.design.ProcessForm;
import com.dvt.ActivitiWorkflow.business.real.example.dto.design.ProcessJudge;
import com.dvt.ActivitiWorkflow.business.real.example.entity.ProcessDefination;
import com.dvt.ActivitiWorkflow.business.real.example.service.DesignService;
import com.dvt.ActivitiWorkflow.business.real.example.service.RoleService;
import com.dvt.ActivitiWorkflow.business.real.example.vo.ProcessDefinationVO;
import com.dvt.ActivitiWorkflow.commons.entity.Result;
import com.dvt.ActivitiWorkflow.commons.utils.ChineseCharToEn;
import com.dvt.ActivitiWorkflow.commons.utils.JsonUtils;
import com.dvt.ActivitiWorkflow.commons.utils.XmlUtils;
import com.google.common.collect.Lists;


@Controller
@RequestMapping("/designer")
public class DesignerController {
	private static final Logger logger = LoggerFactory.getLogger(DesignerController.class);
	private static final String LINE_SEP = System.getProperty("line.separator");
	private static final String PATH_SEP = File.separator;
	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private DesignService designService;
	
	@RequestMapping("/findProcessDefinationById")
	@ResponseBody
	public Result findProcessDefinationById(@RequestParam Integer id){
		try {
			ProcessDefination pd = designService.findProcessDefinationById(id);
			return new Result(Boolean.TRUE, pd);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Boolean.FALSE,"查找失败",null);
		}
	}
	
	@RequestMapping("/saveProcessDefinations")
	@ResponseBody
	public Result saveProcessDefinations(@ModelAttribute ProcessDefination pd, HttpServletRequest request){
		try {
			designService.saveProcessDefination(pd);
			return new Result(Boolean.TRUE,"保存流程定义成功",null);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Boolean.FALSE,"保存流程定义失败",null);
		}
	}
	
	@RequestMapping("/findGroups")
	@ResponseBody
	public Result findGroups(){
		try {
			List<Group> list = roleService.findGroups();
			return new Result(Boolean.TRUE, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Boolean.FALSE,"查找角色报错",null);
		}
	}
	
	@RequestMapping("/findProcessDefinations")
	@ResponseBody
	public Result findProcessDefinations(){
		try {
			List<ProcessDefination> list = designService.findProcessDefinations();
			return new Result(Boolean.TRUE, list);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Boolean.FALSE,"查找流程定义报错",null);
		}
	}
	
	@RequestMapping("/addDeployment")
	@ResponseBody
	public Result addDeployment(@RequestParam Integer id){
		try {
			ProcessDefination pdVO = designService.findProcessDefinationById(id);
			DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
			FileInputStream fileInputStream = new FileInputStream(pdVO.getFilePath());
			deploymentBuilder.addInputStream(pdVO.getProcessDefinationId()+".bpmn", fileInputStream).deploy();
			
			ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
		    long count = processDefinitionQuery.processDefinitionKey(pdVO.getProcessDefinationId()).count();
		    if(count==1){
		    	return new Result(Boolean.TRUE);
		    }
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(Boolean.FALSE,"部署失败");
	}
	
	@RequestMapping("/saveDesign")
	@ResponseBody
	public Result saveDesign(@RequestParam String processData,@RequestParam String processForm, @RequestParam String processJudge,HttpServletRequest request){
		try {
			ProcessData pd = JsonUtils.jsonToJavaBean(processData, ProcessData.class);
			List<ProcessForm> pfList = JsonUtils.jsonToList(processForm, ProcessForm.class);
			List<ProcessJudge> pjList = JsonUtils.jsonToList(processJudge, ProcessJudge.class);
			
			if(pd!=null){
				String process_defined_id = ChineseCharToEn.getAllFirstLetter(pd.getProcess_name());
				System.out.println(process_defined_id);
				
				String xmlText = fillBpmn(process_defined_id, pd, pfList, pjList);
				
				String timestamp = "." + new Date().getTime();
				String folderPath = request.getSession().getServletContext().getRealPath("")+"process"+ PATH_SEP + process_defined_id + timestamp;
				File templateFolder = new File(folderPath);
				String filePath = folderPath+PATH_SEP+process_defined_id+".bpmn";
				if(!templateFolder.exists()){
					templateFolder.mkdir();
					XmlUtils.toXMLFile(XmlUtils.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xmlText), filePath, null);
				}
				//保存流程定义
				designService.createProcessDefination(process_defined_id,pd.getProcess_name(), processData, processForm, processJudge, filePath);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Result(Boolean.FALSE,"保存失败",null);
	}
	
	private String fillBpmn(String processDefinedId,ProcessData pd,List<ProcessForm> pfList,List<ProcessJudge> pjList){
		String xmlText = "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:activiti=\"http://activiti.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://www.activiti.org/test\">";
		xmlText+= "<process id=\""+processDefinedId+"\" name=\""+pd.getProcess_name()+"\" isExecutable=\"true\">";
		//开始节点
		xmlText+= "<startEvent id=\"theStart\" name=\"开始流程\" activiti:initiator=\"userId\"></startEvent>";
		BlockDTO startpoint = this.findBlockByType(pd, "startpoint");
		xmlText+="<sequenceFlow id=\"start-flow\" sourceRef=\"theStart\" targetRef=\""+startpoint.getTask_id()+"\"></sequenceFlow>";
		for (BlockDTO task : this.findTasks(pd)) {
			xmlText += this.makeUserTaskXml(task, pd, pjList);
		}
		
		xmlText += "</process></definitions>";
		return xmlText;
	}
	
	
	private String makeUserTaskXml(BlockDTO block,ProcessData pd,List<ProcessJudge> pjList){
		String xmlText = "";
		if(block!=null&&block.getTask_id().startsWith("task")){//是任务节点
				if(block.getTask_type().equals("startpoint")){
					xmlText+="<userTask id=\""+block.getTask_id()+"\" name=\""+block.getTask_name()+"\" activiti:assignee=\"${userId}\">";
					xmlText+="<documentation>"+block.getTask_name()+"</documentation>";
					//从输出process_to获得下个节点
					
					if(StringUtils.isBlank(block.getProcess_to())){//结束节点
						xmlText+= "</userTask>";
						xmlText+="<sequenceFlow id=\"end-flow\" sourceRef=\""+block.getTask_id()+"\" targetRef=\"theEnd\"></sequenceFlow>";
						xmlText+="<endEvent id=\"theEnd\" name=\"结束流程\"></endEvent>";
					}else{
						BlockDTO nextBlock =  this.findBlockByProcessId(pd, Integer.parseInt(block.getProcess_to()));
						if(nextBlock.getTask_id().startsWith("task")){//下个节点是任务节点
							xmlText += "</userTask>";
							ConnDTO conn = findConnByFromAndTo(pd, block.getTask_id(), nextBlock.getTask_id());
							xmlText += "<sequenceFlow id=\""+conn.getConnection_id()+"\" sourceRef=\""+block.getTask_id()+"\" targetRef=\""+nextBlock.getTask_id()+"\"></sequenceFlow>";
						}else if(nextBlock.getTask_id().startsWith("gateway")){//下个节点是网关
							ConnDTO currentConn = findConnByFromAndTo(pd, block.getTask_id(), nextBlock.getTask_id());
							xmlText += "<extensionElements>";
							String[] gatewayTo = nextBlock.getProcess_to().split(",");
							xmlText += "<activiti:formProperty id=\"judge\" name=\"选路条件\" type=\"enum\" required=\"true\">";
							
							List<ProcessJudge> outputs = this.findProcessJudgesBySourceId(pjList, block.getTask_id());
							for (int j=0;j<outputs.size();j++) {
								xmlText += "<activiti:value id=\"sel"+j+"\" name=\""+outputs.get(j).getName()+"\"></activiti:value>";
							}

							xmlText += "</activiti:formProperty>";
							xmlText += "</extensionElements>";
							xmlText += "</userTask>";
							
							xmlText += "<sequenceFlow id=\""+currentConn.getConnection_id()+"\" sourceRef=\""+block.getTask_id()+"\" targetRef=\""+nextBlock.getTask_id()+"\"></sequenceFlow>";
							xmlText += "<exclusiveGateway id=\""+nextBlock.getTask_id()+"\"></exclusiveGateway>";
							
							for (int i=0;i<gatewayTo.length;i++) {
								BlockDTO gateway2Block =  this.findBlockByProcessId(pd, Integer.parseInt(gatewayTo[i]));
								ConnDTO conn = findConnByFromAndTo(pd, nextBlock.getTask_id(), gateway2Block.getTask_id());
								xmlText += "<sequenceFlow id=\""+conn.getConnection_id()+"\" sourceRef=\""+conn.getFrom_id()+"\" targetRef=\""+conn.getTo_id()+"\">";
								xmlText += "<conditionExpression xsi:type=\"tFormalExpression\">${judge=='sel"+i+"'}</conditionExpression>";
								xmlText += "</sequenceFlow>";
							}
						}
					}
					
				}else if(block.getTask_type().equals("single-role")){
					xmlText+="<userTask id=\""+block.getTask_id()+"\" name=\""+block.getTask_name()+"\" activiti:candidateGroups=\""+block.getProcess_role()+"\">";
					xmlText+="<documentation>"+block.getTask_name()+"</documentation>";
					
					//从输出process_to获得下个节点
					
					if(StringUtils.isBlank(block.getProcess_to())){//结束节点
						xmlText+= "</userTask>";
						xmlText+="<sequenceFlow id=\"end-flow\" sourceRef=\""+block.getTask_id()+"\" targetRef=\"theEnd\"></sequenceFlow>";
						xmlText+="<endEvent id=\"theEnd\" name=\"结束流程\"></endEvent>";
					}else{
						BlockDTO nextBlock =  this.findBlockByProcessId(pd, Integer.parseInt(block.getProcess_to()));
						if(nextBlock.getTask_id().startsWith("task")){//下个节点是任务节点
							xmlText += "</userTask>";
							ConnDTO conn = findConnByFromAndTo(pd, block.getTask_id(), nextBlock.getTask_id());
							xmlText += "<sequenceFlow id=\""+conn.getConnection_id()+"\" sourceRef=\""+block.getTask_id()+"\" targetRef=\""+nextBlock.getTask_id()+"\"></sequenceFlow>";
						}else if(nextBlock.getTask_id().startsWith("gateway")){//下个节点是网关
							ConnDTO currentConn = findConnByFromAndTo(pd, block.getTask_id(), nextBlock.getTask_id());
							xmlText += "<extensionElements>";
							String[] gatewayTo = nextBlock.getProcess_to().split(",");
							xmlText += "<activiti:formProperty id=\"judge\" name=\"选路条件\" type=\"enum\" required=\"true\">";
							
							List<ProcessJudge> outputs = this.findProcessJudgesBySourceId(pjList, block.getTask_id());
							for (int j=0;j<outputs.size();j++) {
								xmlText += "<activiti:value id=\"sel"+j+"\" name=\""+outputs.get(j).getName()+"\"></activiti:value>";
							}

							xmlText += "</activiti:formProperty>";
							xmlText += "</extensionElements>";
							xmlText += "</userTask>";
							
							xmlText += "<sequenceFlow id=\""+currentConn.getConnection_id()+"\" sourceRef=\""+block.getTask_id()+"\" targetRef=\""+nextBlock.getTask_id()+"\"></sequenceFlow>";
							xmlText += "<exclusiveGateway id=\""+nextBlock.getTask_id()+"\"></exclusiveGateway>";
							
							for (int i=0;i<gatewayTo.length;i++) {
								BlockDTO gateway2Block =  this.findBlockByProcessId(pd, Integer.parseInt(gatewayTo[i]));
								ConnDTO conn = findConnByFromAndTo(pd, nextBlock.getTask_id(), gateway2Block.getTask_id());
								xmlText += "<sequenceFlow id=\""+conn.getConnection_id()+"\" sourceRef=\""+conn.getFrom_id()+"\" targetRef=\""+conn.getTo_id()+"\">";
								xmlText += "<conditionExpression xsi:type=\"tFormalExpression\">${judge=='sel"+i+"'}</conditionExpression>";
								xmlText += "</sequenceFlow>";
							}
						}
					}
					
				}
			
			
		}
		return xmlText;
	}
	
	private BlockDTO findBlockById(ProcessData pd,String task_id){
		
		for (BlockDTO block : pd.getBlocks()) {
			if(block.getTask_id().equals("task_id")){
				return block;
			}
		}
		return null;
	}
	private BlockDTO findBlockByProcessId(ProcessData pd,Integer process_id){
		
		for (BlockDTO block : pd.getBlocks()) {
			if(block.getProcess_id()==process_id){
				return block;
			}
		}
		return null;
	}
	private BlockDTO findBlockByType(ProcessData pd,String task_type){
		for (BlockDTO block : pd.getBlocks()) {
			if(StringUtils.isNotBlank(block.getTask_type())&&block.getTask_type().equals(task_type)){
				return block;
			}
		}
		return null;
	}
	private List<BlockDTO> findTasks(ProcessData pd){
		List<BlockDTO> blocks = Lists.newArrayList();
		for (BlockDTO block : pd.getBlocks()) {
			if(block.getTask_id().startsWith("task")){
				blocks.add(block);
			}
		}
		return blocks;
	}
	private ConnDTO findConnByFromAndTo(ProcessData pd,String from_id,String to_id){
		for (ConnDTO conn : pd.getConnections()) {
			if(conn.getFrom_id().equals(from_id) && conn.getTo_id().equals(to_id)){
				return conn;
			}
		}
		return null;
	}
	
	private List<ProcessJudge> findProcessJudgesBySourceId(List<ProcessJudge> pjList,String sourceId){
		List<ProcessJudge> list  = Lists.newArrayList();
		for (ProcessJudge processJudge : pjList) {
			if(processJudge.getSource_task_id().equals(sourceId)){
				list.add(processJudge);
			}
		}
		return list;
	}
}
