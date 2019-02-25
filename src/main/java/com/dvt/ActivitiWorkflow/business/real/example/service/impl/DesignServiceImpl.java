package com.dvt.ActivitiWorkflow.business.real.example.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dvt.ActivitiWorkflow.business.real.example.dao.ProcessDefinationDao;
import com.dvt.ActivitiWorkflow.business.real.example.entity.ProcessDefination;
import com.dvt.ActivitiWorkflow.business.real.example.service.DesignService;
import com.dvt.ActivitiWorkflow.business.real.example.vo.DeploymentVO;
import com.dvt.ActivitiWorkflow.business.real.example.vo.ProcessDefinationVO;
import com.dvt.ActivitiWorkflow.commons.query.DynamicQuery;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Transactional
@Service
public class DesignServiceImpl implements DesignService{
	
	@Autowired
	private DynamicQuery dynamicQuery;
	@Autowired
	private ProcessDefinationDao processDefinationDao;
	
	@Override
	public void createProcessDefination(String processDefinationId,String processDefinationName,
			String processData, String processForm, String processJudge,
			String filePath) {
		ProcessDefination pd = new ProcessDefination(null, processDefinationId, processDefinationName, filePath, processData, processForm, processJudge);
		processDefinationDao.save(pd);
	}
	
	@Override
	public void createProcessDefination(String deploymentId, String filePath, String md5) {
		ProcessDefination pd = new ProcessDefination(deploymentId, filePath, md5);
		processDefinationDao.save(pd);
	}
	
	@Override
	public List<ProcessDefination> findProcessDefinations() {
		String sql = "select * "
				   + "from t_process_defination ";
		return dynamicQuery.nativeQuery(ProcessDefination.class, sql, null);
	}

	@Override
	public ProcessDefination findProcessDefinationById(Integer id) {
		return processDefinationDao.findById(id);
	}

	@Override
	public void saveProcessDefination(ProcessDefination pd) {
		processDefinationDao.save(pd);
	}

	@Override
	public ProcessDefination findProcessDefinationByMd5(String md5) {
		return processDefinationDao.findByMd5(md5);
	}

	@Override
	public int deleteByDeploymentId(String deploymentId) {
		return processDefinationDao.deleteByDeploymentId(deploymentId);
	}
	
	
}
