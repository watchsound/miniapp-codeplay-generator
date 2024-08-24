package org.r9.workspace.ui.dialog.chatgpt;

import java.util.List;

import com.pi.code.tool.util.StringUtils;
 

public class Prompt {

	private String name;
	private String content; 
	private String role;
	
	private String preQuestion;
	private String preAnswer;
	
	private boolean pinned;
	
	
	
	public boolean isEmpty() {
		return StringUtils.isEmpty(content) && StringUtils.isEmpty(preAnswer) && StringUtils.isEmpty(preQuestion);
	}
	
	public String getName() {
		if( name != null && name.length() > 0) return name;
		return content == null ? "" : content.substring(0, Math.min(80, content.length()));
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	 
	
	
	public String getPreQuestion() {
		return preQuestion;
	}
	public void setPreQuestion(String preQestion) {
		this.preQuestion = preQestion;
	}
	public String getPreAnswer() {
		return preAnswer;
	}
	public void setPreAnswer(String preAnswer) {
		this.preAnswer = preAnswer;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String toString() {
		return getName();
	}

	public boolean isPinned() {
		return pinned;
	}

	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}

	 
	
}
