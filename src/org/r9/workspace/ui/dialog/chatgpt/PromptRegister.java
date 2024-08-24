package org.r9.workspace.ui.dialog.chatgpt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
 
import com.google.gson.Gson;
import com.pi.code.tool.codefillin.DataProcessor;
import com.pi.code.tool.codeplayer.Workbench;

public class PromptRegister {

	public static PromptRegister instance = new PromptRegister();
	
	private PromptRegister() {}
	private PromptCollection data;
	
	public  synchronized PromptCollection getData() {
		if( data == null )
			data = loadPromptCollection();
		if( data == null || data.getPrompts().isEmpty()) {
			data = loadDataFromFile();
			this.save();
		}
		return data;
	}
	
	public synchronized void clear() {
		this.data = null;
	}
	
	public synchronized PromptCollection reload() {
		data =  loadPromptCollection();
		return data;
	}
	
	public void save() {
		 savePromptCollection(data);
	}
	
	public static void savePromptCollection(PromptCollection data) {
		Gson gson = new Gson();
		String content = gson.toJson(data);
	    File f = new File(Workbench.codePlayerHome, "_prompt_list.json");
		try {
			DataProcessor.stringToFile(content, f);
		} catch (IOException e) { 
			System.err.print(e);
		}
	} 
	public static PromptCollection loadPromptCollection() {
		  File f = new File(Workbench.codePlayerHome, "_prompt_list.json");
		  if(! f.exists() ) 
			return new PromptCollection(); 
		  String content = DataProcessor.fileToString(f);
		  Gson gson = new Gson();
  	  return gson.fromJson(content, PromptCollection.class); 
		
	}
	
	public PromptCollection loadDataFromFile() {
		final String filename = "_chatgpt_prompt_init_file.txt";
		
		PromptCollection data = new PromptCollection(); 
		File f = new File(Workbench.codePlayerHome, filename);
		if(!f.exists()) return data;
		BufferedReader reader = null; 
		
		try { 
			reader = new BufferedReader(new FileReader(f));
			String line = null;
            String role = "";
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if( line.length() == 0) continue;
			    if(line.startsWith("#")) {
			    	role = line.substring(1);
			    } else {
			    	Prompt p = new Prompt();
			    	p.setRole(role);
			    	p.setContent(line); 
			    	data.addPrompt(p);
			    } 
			} 
			reader.close(); 
		} catch (IOException e) { 
		} finally {
			if( reader != null)
				try {
					reader.close();
				} catch (IOException e) {  } 
		}
		return data;
		
	}
}
