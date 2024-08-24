package com.pi.code.tool.quiz;

import java.util.ArrayList;
import java.util.List;

public class Quiz {

	private String id;
	private String type;
	private String title;
	private String desc;
	private String content;
	private List<Problem> problems = new ArrayList<Problem>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<Problem> getProblems() {
		return problems;
	}
	public void setProblems(List<Problem> problems) {
		this.problems = problems;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}
