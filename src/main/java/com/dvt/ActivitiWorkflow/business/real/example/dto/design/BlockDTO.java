package com.dvt.ActivitiWorkflow.business.real.example.dto.design;

public class BlockDTO {
	private String task_id;
	private String task_name;
	private String task_type;
	private Integer process_id;
	private String process_role;
	private String style;
	private String process_to;
	public BlockDTO(String task_id, String task_name, String task_type,
			Integer process_id, String process_role, String style,
			String process_to) {
		super();
		this.task_id = task_id;
		this.task_name = task_name;
		this.task_type = task_type;
		this.process_id = process_id;
		this.process_role = process_role;
		this.style = style;
		this.process_to = process_to;
	}
	public BlockDTO() {
		super();
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	public String getTask_type() {
		return task_type;
	}
	public void setTask_type(String task_type) {
		this.task_type = task_type;
	}
	public Integer getProcess_id() {
		return process_id;
	}
	public void setProcess_id(Integer process_id) {
		this.process_id = process_id;
	}
	public String getProcess_role() {
		return process_role;
	}
	public void setProcess_role(String process_role) {
		this.process_role = process_role;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getProcess_to() {
		return process_to;
	}
	public void setProcess_to(String process_to) {
		this.process_to = process_to;
	}
	
	
	
}
