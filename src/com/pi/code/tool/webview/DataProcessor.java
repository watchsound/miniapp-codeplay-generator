package com.pi.code.tool.webview;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.gson.Gson;
import com.pi.code.tool.flowchart.DataProcessor.ToolType;
import com.pi.code.tool.util.PageColors; 
/*
 * 文件格式：
 
 */
public class DataProcessor {
	public static enum WebitemType {
		Title("tt", PageColors.red1), Code("cd",PageColors.green1), Console("cs", PageColors.purple1), List("lt", Color.pink), 
		Table("tb", PageColors.orange1), Paragraph("pg", PageColors.yellow1),  Image("im", PageColors.blue1);
		
		public final String type;
		public final Color color;
		private WebitemType(String type, Color color) {
			this.type = type;
			this.color = color;
		}
		 
		public static WebitemType fromType(String type) {
			for(WebitemType tt : WebitemType.values()) {
				if( tt.type.equals(type))
					return tt;
			}
			return Paragraph;
		}
	}
	public static class WebviewItem {
		public String t;
		public String c;
		public String getT() {
			return t;
		}
		public void setT(String t) {
			this.t = t;
		}
		public String getC() {
			return c;
		}
		public void setC(String c) {
			this.c = c;
		} 
	}
	public static class WebviewPage {
		public List<WebviewItem> items = new ArrayList<>();

		public List<WebviewItem> getItems() {
			return items;
		}

		public void setItems(List<WebviewItem> items) {
			this.items = items;
		}
		
		public boolean isEmpty() {
			return items.isEmpty();
		}
		
	}
	
	public static WebviewPage loadFile(File file){
		String content = fileToString(file);
		Gson gson = new Gson();
		WebviewPage lesson = gson.fromJson(content, WebviewPage.class);
		 
		return lesson;
	}
	
	public static void saveFile(WebviewPage data, File file) {
		 
		Gson  gson = new Gson();
		try {
			stringToFile(gson.toJson(data, WebviewPage.class), file);
		} catch (IOException e) {
			 e.printStackTrace();
		}
	}
	 
	 
	 public static String fileToString(File file)  {
	        try{
	         	 return fileToString( new FileInputStream(file) );
	        }catch(Exception ex){
	        	
	        } 
	        return "";
	    }
	    
	 
    public static String fileToString(InputStream in) throws IOException {
        Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        StringWriter writer = new StringWriter();
        char[] buf = new char[1024];
        while(true) {
            int n = reader.read(buf);
            if(n == -1) {
                break;
            }
            writer.write(buf,0,n);
        }
        return  writer.toString()  ;
    }
    
    
    public static void stringToFile(String text, File file) throws IOException {
    	
    	try (OutputStreamWriter writer =
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)){
    		 StringReader reader = new StringReader(text);
    	        char[] buf = new char[1000];
    	        while(true) {
    	            int n = reader.read(buf,0,1000);
    	            if(n == -1) {
    	                break;
    	            }
    	            writer.write(buf,0,n);
    	        }
    	        writer.flush();
        } 
    }
  
    
}
