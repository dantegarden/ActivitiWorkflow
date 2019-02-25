package com.dvt.ActivitiWorkflow.commons.entity;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;


public class DataTablesPage {
	private Integer sEcho;
	private Long iTotalRecords;
	private Long iTotalDisplayRecords;
	private List<?> aaData = Collections.EMPTY_LIST;
	
	public DataTablesPage() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public DataTablesPage(Integer sEcho, Page page) {
		this.sEcho = sEcho;
		this.iTotalRecords = page.getTotalElements();
		this.iTotalDisplayRecords = page.getTotalElements();
		this.aaData = page.getContent();
	}
	
	
	public DataTablesPage(Integer sEcho, List queryList,Long totalRecord) {
		this.sEcho = sEcho;
		this.iTotalRecords = totalRecord;
		this.iTotalDisplayRecords =totalRecord;
		this.aaData = queryList;
	}
	
	public DataTablesPage(Integer sEcho, List<Object[]> queryList,Long totalRecord,String xx) {
		this.sEcho = sEcho;
		this.iTotalRecords = totalRecord;
		this.iTotalDisplayRecords =totalRecord;
		this.aaData = queryList;
	}
	
	
	
	public DataTablesPage(Integer sEcho, List queryList,
			Long totalRecord,Integer i) {
		this.sEcho = sEcho;
		this.iTotalRecords = totalRecord;
		this.iTotalDisplayRecords =totalRecord;
		this.aaData = queryList;
	}

	public Integer getsEcho() {
		return sEcho;
	}

	public void setsEcho(Integer sEcho) {
		this.sEcho = sEcho;
	}

	public Long getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(Long iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public Long getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(Long iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public List<?> getAaData() {
		return aaData;
	}

	public void setAaData(List<?> aaData) {
		this.aaData = aaData;
	}
	
}
