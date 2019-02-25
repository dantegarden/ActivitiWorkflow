package com.dvt.ActivitiWorkflow.business.real.example.dto.design;

import java.util.List;

public class ProcessForm {
	private String task_id;
	private List<FormDTO> form;
	
	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public List<FormDTO> getForm() {
		return form;
	}

	public void setForm(List<FormDTO> form) {
		this.form = form;
	}

	public ProcessForm() {
		super();
	}

	public ProcessForm(String task_id, List<FormDTO> form) {
		super();
		this.task_id = task_id;
		this.form = form;
	}
	
	
}
