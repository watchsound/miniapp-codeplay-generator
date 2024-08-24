package com.pi.code.tool.debug;
 

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
/*
 * 文件格式：
 * { 
 *   vars: {  variblename1 : shortname1, variablename2. shortname2},：
 *   data:[
 *     {
 *        row: rownumber,
 *        shortname1: value,
 *        shortname2: value     [if value is unchanged compared to previous one, it can be omitted
 *     }
 *   ]
 * } 
 */
public class DataProcessor {
 
	public static class DebugLine {
		public int r;
		//variable:value: variable:value.......
		//two special one:
		//msg:value          show message value in popup
		//out：value          show message value in output console
		public List<String> vs = new ArrayList<>();
		public int getRow() {
			return r;
		}
		public void setRow(int row) {
			this.r = row;
		}
		public List<String> getVs() {
			return vs;
		}
		public void setVs(List<String> vs) {
			this.vs = vs;
		}
		
		public void cleanup() {
			String v = getValue("msg");
			if( v.length() == 0 )
				deleteVariable("msg");
		}
		 
		public String getMsgTagUsed() {
			for(int i = 0; i < vs.size(); i+=2) {
				if( vs.get(i).equals("msg"))
					return "msg";
				if( vs.get(i).equals("out"))
					return "out";
			}
			return "";
		}
		public String getValue(String variable) {
			boolean ismsgtag = this.isOutmsgTag(variable);
			if( ismsgtag ) {
				 String v = getValue2("msg");
				 if( v.length() == 0)
					 v = getValue2("out");
				 return v;
			} else {
				return getValue2(variable);
			}
		}
		public String getValue2(String variable) {
			for(int i = 0; i < vs.size(); i+=2) {
				if( vs.get(i).equals(variable))
					return vs.get(i+1);
			}
			return "";
		}
		public boolean isOutmsgTag(String variable) {
			return "out".equals(variable) || "msg".equals(variable);
		}
		public void setValue(String variable,String value) {
			boolean ismsgtag = this.isOutmsgTag(variable);
			if( ismsgtag ) {
				deleteVariable("out"); 
				vs.add(variable);
				vs.add(value);
			} else {
				for(int i = 0; i < vs.size(); i+=2) {
					if( vs.get(i).equals(variable) ) {
						vs.set(i+1, value);
						return;
					}
				}
				vs.add(variable);
				vs.add(value);
			} 
		}
		public void deleteVariable(String variable) {
			boolean ismsgtag = this.isOutmsgTag(variable);
			for(int i = 0; i < vs.size(); i+=2) {
				if( ismsgtag ) {
					if( vs.get(i).equals("out") || vs.get(i).equals("msg")) {
						vs.remove(i);
						vs.remove(i);
						return;
					}
				} else {
					if( vs.get(i).equals(variable)) {
						vs.remove(i);
						vs.remove(i);
						return;
					}
				} 
			}
		}
		
	}
	public static class Debug {
		public Map<String, String> vars = new HashMap<>(); 
		public List<DebugLine> lines  = new ArrayList<>();
		public Map<String, String> getVars() {
			return vars;
		}
		public void setVars(Map<String, String> vars) {
			this.vars = vars;
		}
		public List<DebugLine> getLines() {
			return lines;
		}
		public void setLines(List<DebugLine> lines) {
			this.lines = lines;
		}  
		public void deleteVariable(String variable) {
			vars.remove(variable);
			for(int i = 0; i < lines.size(); i+=2) {
				 lines.get(i).deleteVariable(vars.get(variable));
			}
		}
		public void cleanup() {
			for(int i = 0; i < lines.size(); i+=2) {
				 lines.get(i).cleanup();
			}
		}
	} 
	 
 
}
