package com.dvt.ActivitiWorkflow.business.real.example.dto.design;

import java.util.List;

public class ProcessData {
	private Integer total;
	private String process_name;
	private List<BlockDTO> blocks;
	private List<ConnDTO> connections;
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public String getProcess_name() {
		return process_name;
	}
	public void setProcess_name(String process_name) {
		this.process_name = process_name;
	}
	public List<BlockDTO> getBlocks() {
		return blocks;
	}
	public void setBlocks(List<BlockDTO> blocks) {
		this.blocks = blocks;
	}
	public List<ConnDTO> getConnections() {
		return connections;
	}
	public void setConnections(List<ConnDTO> connections) {
		this.connections = connections;
	}
	public ProcessData(Integer total, String process_name,
			List<BlockDTO> blocks, List<ConnDTO> connections) {
		super();
		this.total = total;
		this.process_name = process_name;
		this.blocks = blocks;
		this.connections = connections;
	}
	public ProcessData() {
		super();
	}
	
	
}
