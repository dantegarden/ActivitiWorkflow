package com.dvt.ActivitiWorkflow.business.real.example.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.dvt.ActivitiWorkflow.business.real.example.entity.CounterSignRecord;


public interface CounterSignRecordDao extends PagingAndSortingRepository<CounterSignRecord, String>,
JpaSpecificationExecutor<CounterSignRecord>{
	@Query("select lasttaskid from CounterSignRecord c where c.pk.processdefinitionid = ?1 and c.pk.processinstanceid = ?2 and c.pk.taskid = ?3 and c.pk.taskdefinitionkey = ?4 ")
	String findLastTaskIdByPk(String processDefinitionId,String processInstanceId,String taskId,String taskDefinitionKey);
	@Query("select c from CounterSignRecord c where c.pk.processdefinitionid = ?1 and c.pk.processinstanceid = ?2 and c.pk.taskid = ?3 and c.pk.taskdefinitionkey = ?4 ")
	CounterSignRecord findByPk(String processDefinitionId,String processInstanceId,String taskId,String taskDefinitionKey);
	@Query("select value from CounterSignRecord c where c.pk.processdefinitionid = ?1 and c.pk.processinstanceid = ?2 and c.lasttaskid = ?3  ")
	List<String> findValueByPkAndLasttaskid(String processDefinitionId,String processInstanceId,String lastTaskId);
	@Query("select value from CounterSignRecord c where c.pk.processdefinitionid = ?1 and c.pk.processinstanceid = ?2 and c.lasttaskid = ?3  ")
	String findValueByPkAndTaskid(String processDefinitionId,String processInstanceId,String taskId);
	@Query("select c from CounterSignRecord c where c.pk.taskid = ?1 ")
	CounterSignRecord findByTaskid(String taskId);
	@Query("select c from CounterSignRecord c where c.lasttaskid = ?1 ")
	List<CounterSignRecord> findByLasttaskid(String lastTaskId);
}
