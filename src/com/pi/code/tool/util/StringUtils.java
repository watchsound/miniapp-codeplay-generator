package com.pi.code.tool.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

public class StringUtils {

	public static final boolean isEmpty(String content) {
		return content == null || content.trim().length() == 0;
	}
	
	

	/**
	 * //check if a line has number start;
	 * @param content
	 * @return
	 */
	public static int checkNumberRow(String content) {
	    Pattern p = Pattern.compile("^([0-9A-Za-z]{1,2})\\.([\\s]*).+");
		Matcher m = p.matcher(content);
    	if( m.matches() ) {
    		String n = m.group(1);
    		try {
    			return Integer.parseInt(n);
    		}catch(Exception e) {
    			if( n.length() == 1 ) {
    				char nc = n.charAt(0);
    				if( Character.isAlphabetic(nc) && Character.isUpperCase(nc) )
    					return (nc -'A');
    				if( Character.isAlphabetic(nc) && Character.isLowerCase(nc))
    					return (nc - 'a');
    			}
    		} 
    	}
    	return -1;
	}
	
	public static String stripLeadingNumberOrder(String content) {
	    Pattern p = Pattern.compile("^([0-9A-Za-z]{1,2})\\.([\\s]*)(.+)");
		Matcher m = p.matcher(content);
    	if( m.matches() ) {
    		return m.group(3); 
    	}
    	return content;
	}
	
	
	public static boolean hasNumberOrder(String[] lines, int minCount) {
		int preNum = -1;
		for(String line: lines) {
			int n = checkNumberRow(line);
			if(n > preNum) {
				minCount --;
				preNum = n;
			}
		}
		return minCount <= 0;
	}
	/**
	 * example:
	 * this is a header
	 * 1. xxxxx
	 * 2. xxxxx
	 * xxxxx
	 * 3. xxxxx
	 * 
	 * @param lines
	 * @param numCount  minimum paragraphs.
	 * @return  if not , return null
	 */
	public static Pair<String,List<String>> parseAsNumberedParagrah(String[] lines, int numCount) {
		if(!hasNumberOrder(lines, numCount)) return null;
		String title = "";
		List<String> body = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		boolean inHeader = true;
		for(String line: lines) {
			if( line.trim().length() == 0 ) continue; 
			int n = checkNumberRow(line);
			if(n >= 0 ) {
				 if( inHeader ) {
					 title = sb.toString();
				 } else {
					 body.add(sb.toString());
				 }
				 sb = new StringBuilder();
				 sb.append(line);
				 inHeader = false;
			} else {
				if( sb.length()>0) sb.append("\n");
				sb.append(line);
			}
		}
		return Pair.of(title, body);
	}
	
	
}
