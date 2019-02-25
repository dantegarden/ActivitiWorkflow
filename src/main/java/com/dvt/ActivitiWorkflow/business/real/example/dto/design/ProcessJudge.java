package com.dvt.ActivitiWorkflow.business.real.example.dto.design;

public class ProcessJudge {
	private String connection_id;
	private String source_task_id;
	private String from_id;
	private String to_id;
	private String name;
	private String formobj;
	private String logic;
	private String logic_value;
	public String getConnection_id() {
		return connection_id;
	}
	public void setConnection_id(String connection_id) {
		this.connection_id = connection_id;
	}
	public String getSource_task_id() {
		return source_task_id;
	}
	public void setSource_task_id(String source_task_id) {
		this.source_task_id = source_task_id;
	}
	public String getFrom_id() {
		return from_id;
	}
	public void setFrom_id(String from_id) {
		this.from_id = from_id;
	}
	public String getTo_id() {
		return to_id;
	}
	public void setTo_id(String to_id) {
		this.to_id = to_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormobj() {
		return formobj;
	}
	public void setFormobj(String formobj) {
		this.formobj = formobj;
	}
	public String getLogic() {
		return logic;
	}
	public void setLogic(String logic) {
		this.logic = logic;
	}
	public String getLogic_value() {
		return logic_value;
	}
	public void setLogic_value(String logic_value) {
		this.logic_value = logic_value;
	}
	public ProcessJudge(String connection_id, String source_task_id,
			String from_id, String to_id, String name, String formobj,
			String logic, String logic_value) {
		super();
		this.connection_id = connection_id;
		this.source_task_id = source_task_id;
		this.from_id = from_id;
		this.to_id = to_id;
		this.name = name;
		this.formobj = formobj;
		this.logic = logic;
		this.logic_value = logic_value;
	}
	public ProcessJudge() {
		super();
	}
	
	
}
