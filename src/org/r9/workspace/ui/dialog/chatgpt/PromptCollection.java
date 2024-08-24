package org.r9.workspace.ui.dialog.chatgpt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pi.code.tool.util.StringUtils;
 

public class PromptCollection {
     private List<Prompt>  prompts = new ArrayList<>();

	public List<Prompt> getPrompts(String role, boolean createNew) {
		if( StringUtils.isEmpty(role) )
			return prompts;
		List<Prompt> pts =  new ArrayList<>();
		for(Prompt  p : prompts) {
			if( p.getRole().equals(role))
				pts.add(p);
		}
		if( pts.isEmpty() && createNew ) {
			Prompt p = new Prompt();
			p.setRole(role);
			prompts.add(p);
			pts.add(p);
		}
		return pts;
	}
	
	public List<Prompt> pinned(){ 
		List<Prompt> pts =  new ArrayList<>();
		for(Prompt  p : prompts) {
			if( p.isPinned() )
				pts.add(p);
		} 
		return pts;
	}
	
	public List<Prompt> filter(String filter) {
		if( StringUtils.isEmpty(filter) )
			return prompts;
		List<Prompt> pts =  new ArrayList<>();
		for(Prompt  p : prompts) {
			if( p.getRole().indexOf(filter) >=0 || p.getContent().indexOf(filter)>=0)
				pts.add(p);
		} 
		return pts;
	}

	public void addPrompt(  Prompt prompt ) {
		this.prompts.add(prompt);
	}
	public void removePrompt(  Prompt prompt ) {
		this.prompts.remove(prompt);
	}
	public List<Prompt>  getPrompts(){
		return prompts;
	}
	  
	public Set<String> generateRoleList(){
		Set<String> roles = new HashSet<>();
 		for(Prompt p : prompts) {
			roles.add(p.getRole());
		} 
		return roles;
	}
	
	
	
}
