package com.pi.code.tool.codeplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JTextArea; 

import com.pi.code.tool.codeplayer.DataProcessor.Record;

public class CodePlayer {
	public static int SPEED = 100; 
	public static interface TextWorkspace {
		void clear();
		void insert( char c, int pos);
		void insert(  String content, int pos);
		void delete(int pos);
		void delete(int start, int end);
		void select(int start, int end);
		void setCursorPos(int pos);
	}
	public static interface PlayCallback{
		void playRecordAtRow(int row);
		void playDone();
	}
	
	List<Record> records = new ArrayList<Record>();
	
	boolean isPlaying;
	int curPos;
	boolean repeat;
	TextWorkspace  workspace;
	PlayCallback playCallback;
	Timer timer = new Timer();  
	JFrame parent;
	public CodePlayer(JFrame parent, final JTextArea textarea) {    
		this.parent = parent;
		this.workspace = new TextWorkspace() { 
			@Override
			public void clear() {
				textarea.setText("");
			}

			@Override
			public void insert(  char c, int pos) {
				 textarea.insert(c+"", pos);
			}

			@Override
			public void insert(  String content, int pos) {
				 textarea.insert(content , pos);
			}

			@Override
			public void delete(int pos) {
				textarea. replaceRange("", pos, pos+1);
			}

			@Override
			public void delete(int start, int end) {
				textarea. replaceRange("", start, end+1);
			}
			@Override
			public void select(int start, int end) {
				textarea. select( start, end);
			}
			public 	void setCursorPos(int pos) {
				textarea.setCaretPosition(pos);
			}
		};
	}
	public void skipToEnd(List<Record> data) {
		if( this.timer != null )
			timer.cancel();
		records = data; 
		workspace.clear();
		curPos = -1;
		skipToEnd( ) ;
	}
	public void play(List<Record> data, boolean repeat, PlayCallback playCallback) {
		if( this.timer != null )
			timer.cancel();
		timer = new Timer();
		records = data;
		this.repeat = repeat;
		workspace.clear();
		curPos = -1;
		this.playCallback = playCallback;
		play();
	}
	public void play( ) {
		if( records.isEmpty()) {
			this.playCallback.playDone();
			return;
		}
		
		isPlaying = true; 
		if( curPos == records.size() -1) {
			curPos = -1;
			if(!repeat) {
				this.playCallback.playDone();
			   return;
			}
			workspace.clear();
		}
		curPos ++;
		while ( curPos < records.size()) {
			Record r = records.get(curPos);
			if( playCallback != null) {
				playCallback.playRecordAtRow(curPos);
			}
			if( r.prefix.equals("m")) {
			    final Toast toast = new Toast(parent, r.content,r.keepTime * 1000, Toast.error);  
			    toast.start();
			    if( r.stopTime > 0 ) {
			    	resumePlay( r.stopTime * 1000);
			        return;
			    }
			} else if( r.prefix.equals("i")) {
				if( r.bulk ) {
					workspace.insert(r.content , r.startPos );
					resumePlay( SPEED * 4 );
			        return; 
				}
				int delayTime = r.content.length() * SPEED;
				for(int i = 0; i < r.content.length(); i++) {
					final int pos =i;
					  timer.schedule(new TimerTask() {  
				            @Override  
				            public void run() {  
				            	workspace.insert(r.content.charAt(pos)+"", r.startPos + pos);
				            }  
				        }, SPEED * i);  
				}
				
				resumePlay( delayTime );
		        return; 
			}  else if( r.prefix.equals("d")) {
				if( r.bulk ) {
					workspace.  delete(  r.startPos, r.endPos);
					resumePlay( SPEED * 4 );
			        return; 
				}
				int len = r.endPos - r.startPos +1;
				int delayTime =  len * SPEED;
				for(int i = 0; i <len; i++) {
					final int pos = r.endPos - i;
					  timer.schedule(new TimerTask() {  
				            @Override  
				            public void run() {  
				            	workspace.  delete(   pos, pos);
				            }  
				        }, SPEED * i);  
				}
				
				resumePlay( delayTime );
		        return; 
			}
			else if( r.prefix.equals("s")) {
				workspace.select(r.startPos, r.endPos);  
				resumePlay( SPEED * 4 );
		        return; 
			}
			else if( r.prefix.equals("r")) {
				workspace.setCursorPos(r.startPos);  
				resumePlay( SPEED * 3 );
		        return; 
			}
			curPos ++;
		}
	}
	public void skipToEnd( ) {
		if( records.isEmpty()) return;  
		if( curPos == records.size() -1) {
			 return;
		}
		curPos ++;
		while ( curPos < records.size()) {
			Record r = records.get(curPos);
			if( r.prefix.equals("m")) {
			     skipToEnd();
			     return;
			} else if( r.prefix.equals("i")) {
				 
					workspace.insert(r.content , r.startPos );
					  skipToEnd();
					  return;
			    
			}  else if( r.prefix.equals("d")) {
 
					workspace.  delete(  r.startPos, r.endPos);
					 skipToEnd();
			        return; 
				  
			}
			else if( r.prefix.equals("s")) {
				 skipToEnd();
			        return; 
			}
			else if( r.prefix.equals("r")) {
				workspace.setCursorPos(r.startPos);  
				 skipToEnd();
			        return; 
			}
			curPos ++;
		}
	}
	
	private void resumePlay(int delay) { 
        timer.schedule(new TimerTask() {  
            @Override  
            public void run() {  
                 play();  
            }  
        }, delay );  
	}
 
	public void pause() {
		isPlaying = false;
	}
}
