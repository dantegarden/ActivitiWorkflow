package com.dvt.ActivitiWorkflow.business.real.example.dto.design;

public class FormDTO {
	
	private String id;
	private String name;
	private String type;
	private Boolean isRequire;
	
	public FormDTO(String id, String name, String type, Boolean isRequire) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.isRequire = isRequire;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getIsRequire() {
		return isRequire;
	}

	public void setIsRequire(Boolean isRequire) {
		this.isRequire = isRequire;
	}

	public FormDTO() {
		super();
	}
	
	
}
