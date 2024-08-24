package com.pi.code.tool.flowchart;
 

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import com.pi.code.tool.codefillin.DataProcessor.Fillin;
import com.pi.code.tool.codeplayer.DataProcessor.Lesson;
import com.pi.code.tool.codeplayer.DataProcessor.Lesson2;
import com.pi.code.tool.codeplayer.DataProcessor.Record;
import com.pi.code.tool.debug.DataProcessor.Debug;
import com.pi.code.tool.flowchart.DataProcessor.FlowChart; 
/*
 * type of ui components :  
 * round-rect   (ro)
 * rect         (re)
 * diamond       (di)
 * parallelogram   (pa)
 * arrow  left, right, up, down   (al, ar, au, ad)
 * line:  vertical horizontal   (lv, lh)
 * 
 * all ui component can contain text.  text can be multiple lines?
 * 
 * 
 * 文件格式：
 * { 
 *   width: 
 *   height:
 *   
 *   data:[
 *     {
 *        t (type),
 *        s (size) x:y:w:h
 *        c (content) text  
 *     }
 *   ]
 * } 
 */
public class DataProcessor {
 
	public static enum ToolType {
		Circle("cc"), RoundRect("ro"), Rect("re"), Diamond("di"), Parallelogram("pa"), Line("ln"), ArrowLeft("al"),
		ArrowRight("ar"), Text("tt") ;
		
		public final String type;
		private ToolType(String type) {
			this.type = type;
		}
		public static boolean isLine(String type) {
			return "ln".equals(type) || "al".equals(type) || "ar".equals(type);
		}
		public static ToolType fromType(String type) {
			for(ToolType tt : ToolType.values()) {
				if( tt.type.equals(type))
					return tt;
			}
			return Rect;
		}
	}
 
	public static class UIComponent {
		public String t="";
		public int x ;
		public int y ;
		public int w ;
		public int h;
	//	public String s = "";
		public String c="";
		public List<String> op = new ArrayList<>() ;
		public UIComponent() {}
		public UIComponent(String t) {
			this.t = t;
		}
		public String getT() {
			return t;
		}
		public void setT(String t) {
			this.t = t;
		}
	
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		public int getW() {
			return w;
		}
		public void setW(int w) {
			this.w = w;
		}
		public int getH() {
			return h;
		}
		public void setH(int h) {
			this.h = h;
		}
		public String getC() {
			return c;
		}
		public void setC(String c) {
			this.c = c;
		}
		
//		public String getS() {
//			return s;
//		}
//		public void setS(String s) {
//			this.s = s;
//		}
		public List<String> getOp() {
			return op;
		}
		public void setOp(List<String> op) {
			this.op = op;
		}
		public UIComponent copy() {
			UIComponent copy = new UIComponent();
			copy.t = t;
			copy. x = x;
			copy. y = y;
			copy. w = w;
			copy. h = h;
			copy.c = c;
//			copy.s = s;
			if(op != null && op.size() >0)
				copy.op = new ArrayList<String>(op);
			return copy;
		}
	}
	public static class FlowChart {
		 public int w;
		 public int h;
		 public List<UIComponent> data = new ArrayList<>();
		 
		 public String image;
		 
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
		public int getW() {
			return w;
		}
		public void setW(int w) {
			this.w = w;
		}
		public int getH() {
			return h;
		}
		public void setH(int h) {
			this.h = h;
		}
		public List<UIComponent> getData() {
			return data;
		}
		public void setData(List<UIComponent> data) {
			this.data = data;
		}
		 
		 
	} 
	
	public static FlowChart fromContent( String content ){
	 	Gson gson = new Gson();
		FlowChart lesson = gson.fromJson(content, FlowChart.class); 
		return lesson;
	}
	
	
	public static FlowChart loadFile(File file){
		String content = fileToString(file);
		return fromContent(content);
	}
	
	public static void saveFile(FlowChart records , File file) { 
		Gson  gson = new Gson();
		try {
			stringToFile(gson.toJson(records, FlowChart.class), file);
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
   public static void copy(File filein, File fileout) throws IOException {
	   	if( filein.getAbsolutePath().equals(fileout.getAbsolutePath()))
	   		return;
			InputStream in = new FileInputStream(filein);
			if (! fileout.exists() )
				fileout.createNewFile();
			FileOutputStream out = new FileOutputStream(fileout);
			try {
				copy(in, out);
			} finally {
				in.close();
				out.close();
			}
		}
   public static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		while (true) {
			int readCount = in.read(buffer);
			if (readCount < 0) {
				break;
			}
			out.write(buffer, 0, readCount);
		}
	}
}
