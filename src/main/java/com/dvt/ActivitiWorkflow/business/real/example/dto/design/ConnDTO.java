package com.dvt.ActivitiWorkflow.business.real.example.dto.design;

public class ConnDTO {
	private String connection_id;
	private String from_id;
	private String to_id;
	
	public ConnDTO(String connection_id, String from_id, String to_id) {
		super();
		this.connection_id = connection_id;
		this.from_id = from_id;
		this.to_id = to_id;
	}
	public ConnDTO() {
		super();
	}
	public String getConnection_id() {
		return connection_id;
	}
	public void setConnection_id(String connection_id) {
		this.connection_id = connection_id;
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
	
	
}
