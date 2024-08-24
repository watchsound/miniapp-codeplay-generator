package com.pi.code.tool.codefillin;

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
 *   title：
 *   desc:
 *   code: 
 *   fillin:[
 *     {
 *        sp: start position
 *        ep: end position
 *        op: [v1, v2...] 
 *        tip: message 
 *     }
 *   ]
 * } 
 */
public class DataProcessor {
 
	public static class Fillin {
		public int sp;
		public int ep;
		public int as;
		public List<String> op  = new ArrayList<>(); 
		public String tip; 
		 
		public int getSp() {
			return sp;
		}
		public void setSp(int sp) {
			this.sp = sp;
		}
		public int getEp() {
			return ep;
		}
		public void setEp(int ep) {
			this.ep = ep;
		}
		public List<String> getOp() {
			return op;
		}
		public void setOp(List<String> op) {
			this.op = op;
		}
		public String getTip() {
			return tip;
		}
		public void setTip(String tip) {
			this.tip = tip;
		}
		public int getAs() {
			return as;
		}
		public void setAs(int as) {
			this.as = as;
		}
		
		 
	}
	public static class CodePlayerFillin{
		public String title = "";
		public String desc ="";
		public List<Fillin> fillin = new ArrayList<>();
		public String code = "";
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
		public List<Fillin> getFillin() {
			return fillin;
		}
		public void setFillin(List<Fillin> fillin) {
			this.fillin = fillin;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
	 
		
		
	}
	
	public static CodePlayerFillin loadFile(File file){
		String content = fileToString(file);
		Gson gson = new Gson();
		CodePlayerFillin lesson = gson.fromJson(content, CodePlayerFillin.class);
		 
		return lesson;
	}
	
	public static void saveFile(CodePlayerFillin data, File file) {
		 
		Gson  gson = new Gson();
		try {
			stringToFile(gson.toJson(data, CodePlayerFillin.class), file);
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
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }
    public static boolean canbeVariable(String name) {
    	if(name == null || name.length() == 0 || name.length() > 10)
    		return false;
    	char f = name.charAt(0);
    	if( f != '_' && !Character.isAlphabetic(f) && f != '$') {
    		return false;
    	}
    	for(int i = 1; i < name.length(); i++) {
    		char c = name.charAt(i);
    		if( f != '_' &&  !Character.isLetterOrDigit(c))
    			return false;
    	}
    	return true;
    }
    
    public static List<String> getVariables(String input, String correct){
    	List<String> results = new ArrayList<>();
    	
    	String c1 = correct.replaceAll("d+", "0");
    	if( !correct.equals(c1))
    		results.add(c1);
    	c1 = correct.replaceAll("d+", "1");
    	if( !correct.equals(c1))
    		results.add(c1);
    	c1 = correct.replaceAll("d+", new Random().nextInt(100) + "");
    	if( !correct.equals(c1))
    		results.add(c1);
    	
    	String[] tokens = input.split("\\s|=|\\^|\\[|\\]|\\{|\\}|\\(|\\)|;|,|\\/|\\^|\\!|\\*|\\/|\\+|\\-");
    	//String[] tokens = input.split("\\s|=|\\[|\\]|\\{|\\}|\\(|\\)|;|,|\\+|\\-|\\*|\\/||\\!|\\^");
    	for(String token : tokens) {
    		if( canbeVariable(token) && results.indexOf(token) < 0 && !isChinese(token.charAt(0)) )
    			results.add(token);
    		if( results.size() > 5)
    			break;
    	}
    	return results;
    }
    public static List<String> getCandidates(String fullcode, String correct){
    	try {
    		Integer.parseInt(correct);
    		return getRandomNumbers(correct.length(), 0, 5);
    	}catch(Exception e) { 
    	}
    	try {
    		Float.parseFloat(correct);
    		int pos = correct.indexOf(".");
    		return getRandomNumbers(pos, correct.length() - pos-1,  5);
    	}catch(Exception e) { 
    	}
    	return getVariables(fullcode, correct);
    }
    
    public static List<String> getRandomNumbers(int digits1, int digits2, int count){
    	List<String> results = new ArrayList<>();
    	Random r = new Random();
        for(int i=0; i < count; i++) {
        	String v = "";
        	for(int j = 0; j <digits1; j++) {
        		v = v + r.nextInt(10) +"";
        	}
        	if( digits2 > 0) {
        		v += ".";
            	for(int j = 0; j <digits2; j++) {
            		v = v + r.nextInt(10) +"";
            	}
        	}
        	
        	
        	results.add(v);
        }
    	return results;
    }
}
