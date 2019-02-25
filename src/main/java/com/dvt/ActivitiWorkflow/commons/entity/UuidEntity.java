package com.dvt.ActivitiWorkflow.commons.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

/**
 * Uuid实体,供使用uuid作为主键的实体继承使用
 * 
 * @author wh
 *
 */
@MappedSuperclass
public abstract class UuidEntity extends BaseEntity {
	
	@Id
	@GeneratedValue(generator="uuidGenerator")
	@GenericGenerator(name="uuidGenerator", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(length = 36)
	
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
