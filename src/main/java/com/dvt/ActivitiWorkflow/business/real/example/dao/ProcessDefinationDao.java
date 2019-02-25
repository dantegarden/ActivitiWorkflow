package com.dvt.ActivitiWorkflow.business.real.example.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dvt.ActivitiWorkflow.business.real.example.entity.ProcessDefination;

public interface ProcessDefinationDao extends PagingAndSortingRepository<ProcessDefination, Integer>,
JpaSpecificationExecutor<ProcessDefination>{
	
	ProcessDefination findById(Integer id);
	ProcessDefination findByMd5(String md5);
	
	@Modifying
	@Query("delete from ProcessDefination pd where pd.deploymentId = ?")
	int deleteByDeploymentId(String deploymentId);
}
