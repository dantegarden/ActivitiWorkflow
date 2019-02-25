package com.dvt.ActivitiWorkflow.business.real.example.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dvt.ActivitiWorkflow.commons.entity.BaseEntity;

@Entity
@Table(name = "T_COLLEGE")
public class College  extends BaseEntity {
	private Integer id;
	private String code;
	private String name;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false, precision = 12, scale = 0)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Column(name = "CODE", length = 500)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@Column(name = "NAME", length = 500)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public College(Integer id, String code, String name) {
		super();
		this.id = id;
		this.code = code;
		this.name = name;
	}
	public College() {
		super();
	}
	
	
}
