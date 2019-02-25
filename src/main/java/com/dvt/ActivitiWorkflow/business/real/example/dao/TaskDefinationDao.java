package com.dvt.ActivitiWorkflow.business.real.example.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dvt.ActivitiWorkflow.business.real.example.entity.TaskDefination;

public interface TaskDefinationDao extends PagingAndSortingRepository<TaskDefination, Integer>,
JpaSpecificationExecutor<TaskDefination>{
	
	@Query(value = "delete from TaskDefination t where deploymentId=?1 ")
	@Modifying
	public void deleteByDeploymentId(String id);
}
