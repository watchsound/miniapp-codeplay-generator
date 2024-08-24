package com.pi.code.tool.codeplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

import com.google.gson.Gson;
import com.pi.code.tool.codefillin.DataProcessor.Fillin;
import com.pi.code.tool.debug.DataProcessor.Debug;
import com.pi.code.tool.flowchart.DataProcessor.FlowChart;
import com.pi.code.tool.webview.DataProcessor.WebviewPage;

/**
 * file format:
 * 
 * #{} indicate following operation.
 * 
 * 
 * #{i:c-pos}   insert at cursor-position: c-pos
 *      #{i:24}Affsdfaasf asdf
 * #{s:c-pos:c:pos} select from c-pos to c-pos
 *      #{s:24:48} 
 * #{d:c-pos:c:pos} delect from c-pos to c-pos
 * #{r:c-pos}  change cursor pos to c-pos
 *    #{r:24}
 * #{m:c-pos:c-pos:stoptime:keeptime:[m|o]:c-pos}  show message around c-pos : c-pos highlight area, animation stop for stoptime seconds, message keep for keeptime seconds. 
 *              [m|o] m : message, o: show in output console,  c-pos: pos at final text.
 * #{q:as:op1:op2:op3...}quiz-body#feedback
 *
 */
public class DataProcessor {
	public static String[] asciiChartSet_c2en =  new String[] {
		//    " ", "&nbsp;",
		//    "!", "&excl;",
	          "\"", "&quot;",
	          "\n", "&nl;",
		//    "#", "&num;",
		//    "$", "&dollar;",
		//    "%", "&percnt;",
		//    "&", "&amp;",
		    "'", "&apos;",
		//    "(", "&lpar;",
		//    ")", "&rpar;",
		//    "*", "&ast;",
		//    "+", "&plus;",
		//    ",", "&comma;",
		//    "-", "&hyphen;",
		//    ".", "&period;",
		    "/", "&sol;",
		//    ",", "&colon;",
		//    ";", "&semi;",
		    "<", "&lt;",
		//    "=", "&equals;",
		    ">", "&gt;",
		//    "?", "&quest;",
		//    "@", "&commat;",
		    "\\[", "&lsqb;",
		    "\\\\", "&bsol;",
		    "]", "&rsqb;",
		//    "^", "&circ;",
		//    "_", "&lowbar;",
		//    "`", "&grave;",
		    "\\{", "&lcub;",
 		//    "|", "&verbar;",
 		    "}", "&rcub;",
 		 //   "~", "&tilde;" 
	};
	public static String[] c2en_asciiChartSet =  new String[] {
			"&quot;",  "\"", 
			 "&apos;", "'",
			 "&sol;",  "/",
			 "&lt;", "<",
			 "&gt;", ">", 
			 "&lsqb;",  "[", 
			 "&bsol;", "\\\\", 
			 "&rsqb;",  "]", 
			 "&lcub;", "{", 
			 "&rcub;", "}" ,
			 "&nl;", "\n",
		};
	
	
	public static Map<String, String> getAscii2entity(){
		Map<String,String> map = new HashMap<>();
		for(int i = 0; i < asciiChartSet_c2en.length; i+=2 ) {
			map.put(asciiChartSet_c2en[i], asciiChartSet_c2en[i+1]);
		}
		return map;
	}
	public static Map<String, String> getEntity2Ascii(){
		Map<String,String> map = new HashMap<>();
		for(int i = 0; i < c2en_asciiChartSet.length; i+=2 ) {
			map.put(c2en_asciiChartSet[i], c2en_asciiChartSet[i+1]);
		}
		return map;
	}
	public static String normalize_ascii2entity(String input) {
		for(Entry<String, String> b : getAscii2entity().entrySet() ) {
			input = input.replaceAll(b.getKey(), b.getValue());
		}
		return input;
	}
	public static String normalize_entity2ascii(String input) {
		for(Entry<String, String> b : getEntity2Ascii().entrySet() ) {
			try {
				input = input.replaceAll(b.getKey(), b.getValue());
			}catch(Exception e) {
				System.err.println(input +" =:= " + b.getKey() +" =:= " +  b.getValue());
				e.printStackTrace();
				
			}
			
		}
		return input;
	}
	public static class Lesson2{
		public String title;
		public String desc;
		public List<Record> records;
		public List<Fillin> fillins;
		public String quizContent;
		public Debug debug;
		public FlowChart fc1;
		public FlowChart fc2;
		public FlowChart fc3;
		public WebviewPage wpage;
		public WebviewPage tippage;
	}
	public static class Lesson {
		public String title;
		public String desc;
		public String content;
		public String quizContent;
		public List<Fillin> fillins;
		public Debug debug;
		public FlowChart fc1;
		public FlowChart fc2;
		public FlowChart fc3;
		public WebviewPage wpage;
		public WebviewPage tippage;
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
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public List<Fillin> getFillins() {
			return fillins;
		}
		public void setFillins(List<Fillin> fillins) {
			this.fillins = fillins;
		}
		
		
		public String getQuizContent() {
			return quizContent;
		}
		public void setQuizContent(String quizContent) {
			this.quizContent = quizContent;
		}
		public Debug getDebug() {
			return debug;
		}
		public void setDebug(Debug debug) {
			this.debug = debug;
		}
		public FlowChart getFc1() {
			return fc1;
		}
		public void setFc1(FlowChart fc1) {
			this.fc1 = fc1;
		}
		public FlowChart getFc2() {
			return fc2;
		}
		public void setFc2(FlowChart fc2) {
			this.fc2 = fc2;
		}
		public FlowChart getFc3() {
			return fc3;
		}
		public void setFc3(FlowChart fc3) {
			this.fc3 = fc3;
		}
		public WebviewPage getWpage() {
			return wpage;
		}
		public void setWpage(WebviewPage wpage) {
			this.wpage = wpage;
		}
		public WebviewPage getTippage() {
			return tippage;
		}
		public void setTippage(WebviewPage tippage) {
			this.tippage = tippage;
		}
		 
		
	}

	public static class Record {
		public String prefix;
		public int startPos;
		public int endPos; 
		public int stopTime;
		public int keepTime; 
		public boolean bulk;
	 
		public String content;
		public boolean isoutput;
		
		public int startPos2;
		
		public List<String> op; 
		public int as;
		public String feedback;
		public Record(String prefix) {
			this.prefix = prefix;
			this.content = "";
			if( prefix == "m") {
				stopTime = 3;
				keepTime = 3;
			}
			if( prefix == "q" ) {
				op =  new ArrayList<String>();
				feedback = "";
			}
		}
		public Record(String prefix, int startPos, int endPos,  int stoptime, int keeptime ) {
			this.prefix = prefix; this.startPos = startPos; this.endPos = endPos; 
			this.stopTime = stoptime; this.keepTime = keeptime; 
			this.content = "";
			if( prefix == "q" ) {
				op =  new ArrayList<String>();
				feedback = "";
			}
		}
		 
		public String toString() { 
			if( prefix.equals("i")   )
				return "#{" + prefix + ":" + startPos + (bulk? ":b" : "")+ "}" + this.content;
			if(  prefix.equals("r") )
				return "#{" + prefix + ":" + startPos + "}" + this.content;
			if(   prefix.equals("d"))
				return "#{" + prefix + ":" + startPos + ":" + endPos  + (bulk? ":b" : "") +  "}";
			if( prefix.equals("s")  )
				return "#{" + prefix + ":" + startPos + ":" + endPos + "}";
			if( prefix.equals("m")  )
			    return "#{" + prefix +":" + startPos +":"  + endPos +":"  + stopTime + ":" + keepTime  + (isoutput? ":o" : ":m")   + ":" + startPos2  +
			    		"}" + this.content;
			if( prefix.equals("q")  ) {
				StringBuilder sb = new StringBuilder();
				sb.append("#{q:" + as  );
				for(int i = 0; i < op.size(); i++)
					sb.append(":" + op.get(i));
				sb.append("}" + content );
				if( feedback != null &&feedback.length() > 0)
					sb.append("#" + feedback);
				return sb.toString();
			}
			return "";
		}
	}
	public static Record parseRecordHead(String record) {
		Record r = null;
		char type = record.charAt(0);
		String[] fs = record.split(":");
		int spos = 0; int epos = 0; int stime = 0; int ktime = 0; 
		switch( type ) {
	 	case 'i':   spos = Integer.parseInt(fs[1]);  r = new Record(type +"", spos,0, 0,0 ); if(fs.length == 3) r.bulk=true;      break;
		case 's':   spos = Integer.parseInt(fs[1]); epos = Integer.parseInt(fs[2]); 
	               	r = new Record(type +"", spos,epos,  0,0 ); break;
		case 'd':   spos = Integer.parseInt(fs[1]); epos = Integer.parseInt(fs[2]); 
		            r = new Record(type +"", spos,epos, 0,0 ); if(fs.length == 4) r.bulk=true; break;
		case 'r':   spos = Integer.parseInt(fs[1]);  
		            r = new Record(type +"", spos,0, 0,0 ); break;
		case 'm':   spos = Integer.parseInt(fs[1]); epos = Integer.parseInt(fs[2]);
		            stime = Integer.parseInt(fs[3]); ktime = Integer.parseInt(fs[4]); 
		            r = new Record(type +"", spos,epos, stime, ktime ); 
		            if( fs.length >= 6 && fs[5].equals("o")) r.isoutput=true; 
		            if( fs.length >= 7  ) r.startPos2=Integer.parseInt(fs[6]);break; 
		case 'q':   r = new Record("q");
		            r.as = Integer.parseInt(fs[1]);
		            for(int i =2; i < fs.length; i++)
		            	r.op.add(fs[i]); 
		}
		return r;
	}
	
	public static Lesson2 loadFile(File file){
		String content = fileToString(file);
		Gson gson = new Gson();
		Lesson lesson = gson.fromJson(content, Lesson.class);
		Lesson2 l2 = new Lesson2();
		l2.title = lesson.title;
		l2.desc = lesson.desc;
		l2.fillins = lesson.fillins;
		l2.quizContent = lesson.quizContent;
		l2.debug = lesson.debug;
		l2.wpage = lesson.wpage;
		l2.tippage = lesson.tippage;
		l2.fc1  = lesson.fc1;
		l2.fc2  = lesson.fc2;
		l2.fc3  = lesson.fc3;
		if( l2.fillins == null) l2.fillins = new ArrayList<>();
		content = normalize_entity2ascii( lesson.content );
		l2.records = parseStringToRecords(content);
		return l2;
	}
	
	public static void saveFile(List<Record> records, WebviewPage wpage, WebviewPage tippage, List<Fillin> fillins, String quizContent, Debug debug, 
			FlowChart fc1, FlowChart fc2, FlowChart fc3, String title, String desc, File file) {
		String content = toString(records);
		content =  normalize_ascii2entity(  content );
		Lesson lesson = new Lesson();
		lesson.title = title;
		lesson.desc = desc; 
		lesson.content = content;
		lesson.quizContent = quizContent;
		lesson.fillins = fillins;
		lesson.wpage = wpage;
		lesson.tippage = tippage;
		lesson.debug = debug;
		lesson.fc1 = fc1;
		lesson.fc2 = fc2;
		lesson.fc3 = fc3;
		Gson  gson = new Gson();
		try {
			stringToFile(gson.toJson(lesson, Lesson.class), file);
		} catch (IOException e) {
			 e.printStackTrace();
		}
	}
	
	
	
	public static String toString(List<Record> records) {
		StringBuilder sb = new StringBuilder();
		for(Record r : records) {
			sb.append(r.toString());
		} 
		return sb.toString();
	}
	public static List<Record> parseStringToRecords(String input) {
		List<Record> records = new ArrayList<Record>();
		if( input == null || input.length() == 0)
			return records;
		int pos0 = 0;
		int startPos =0;
		while( startPos >= 0 ) {
			pos0 = input.indexOf("}", startPos);
			if( pos0 < 0 ) return records;
			
			String arecord = input.substring(startPos+2, pos0 );
			Record record = parseRecordHead(arecord);
			records.add(record);
			startPos = input.indexOf("#{", pos0 );
			if( startPos >= 0) {
			    record.content = input.substring(pos0+1,startPos);
			}
			else if ( input.length() >= pos0+1){
				record.content = input.substring(pos0+1);
			}
			if( record.prefix.equals("q") && record.content.indexOf("#")>=0) {
				int pos = record.content.indexOf("#");
				record.feedback = record.content.substring(pos +1);
				record.content = record.content.substring(0,pos);
			}
			pos0 = startPos;
		}
		
		return records;
	}
	
	public static Record[] splitInsertionRecord(Record record, int spitCursorPos) {
		if( record.prefix != "i"  || record.bulk) {
			Record[] records = new Record[1];
			records[0] = record;
			return records;
		} 
		Record[] records = new Record[2];
		records[0] = new Record(record.prefix  , record.startPos,0,0,0 );
		records[0].content = record.content.substring(0, spitCursorPos - record.startPos);
		records[1] = new Record(record.prefix  , spitCursorPos,0,0,0 );
		records[1].content = record.content.substring(  spitCursorPos - record.startPos);
		
		return records;
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
    
//        FileWriter writer = new FileWriter(file);
//        StringReader reader = new StringReader(text);
//        char[] buf = new char[1000];
//        while(true) {
//            int n = reader.read(buf,0,1000);
//            if(n == -1) {
//                break;
//            }
//            writer.write(buf,0,n);
//        }
//        writer.flush();
//        writer.close();
    }
}
