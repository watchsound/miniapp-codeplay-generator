package com.pi.code.tool.quiz;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.google.gson.Gson;
import com.pi.code.tool.codeplayer.DataProcessor; 

public class Quizbench extends JFrame{
 
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Quizbench bench = new Quizbench();
		bench.setVisible(true);
	}

	private JTextArea workspace;
	
	public Quizbench() {
		setLayout(new BorderLayout());
		
		 
		  workspace = new JTextArea(60,100); 
		workspace.setText("##title=title; \n ##desc=desc \n ##problem\n##image=xxx(optional) \n ##questionBody=THis is a question body\n##answer=A\noption1\noption2\noption3\noption4");
		JScrollPane phaseScrolPane = new JScrollPane(workspace);
		phaseScrolPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		phaseScrolPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		add(phaseScrolPane, BorderLayout.CENTER);
		
		JPanel bottomRow = new JPanel();
		bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS)); 
		 add(bottomRow, BorderLayout.SOUTH);
		 final JButton saveAsJson = new JButton("保存");
		 saveAsJson.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				 export();
			}});
		 final JButton importJson = new JButton("导入");
		 importJson.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					importFile();
				}});
		 bottomRow.add(importJson, Box.LEFT_ALIGNMENT);
		 bottomRow.add(Box.createHorizontalGlue());
		 bottomRow.add(saveAsJson, Box.RIGHT_ALIGNMENT);
			
		 
		 
	       setSize(1200, 800);
	       setVisible(true);
	}
	
	public static Quiz quizFromString(String content) {
		if( content.trim().length() == 0 )
			return null;
		System.out.println(content);
		Quiz quiz = new Quiz();
		Problem problem = null;
		String[] lines = content.trim().split("\n");
		for(int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if( line.length() == 0)
				continue;
			if( line.startsWith("##title=")) {
				quiz.setTitle(line.substring("##title=".length())); 
				continue;
			}
			if( line.startsWith("##desc=")) {
				quiz.setDesc(line.substring("##desc=".length())); 
				continue;
			}
			if( line.startsWith("##problem")) {
				 problem = new Problem();
				 quiz.getProblems().add(problem);
				 continue;
			}
			if( line.startsWith("##image=")) {
				 problem.setImage(line.substring("##image=".length()));
				 continue;
			}
			if( line.startsWith("##questionBody=")) {
				 problem.setQuestionBody(line.substring("##questionBody=".length()));
				 continue;
			}
			if( line.startsWith("##answer=")) {
				 problem.setAnswer(line.substring("##answer=".length()));
				 continue;
			}
			if( problem == null)
				continue; //!!!
		   if( problem.getOption1() == null) {
				problem.setOption1(line);
			}
			else if( problem.getOption2() == null) {
				problem.setOption2(line);
			}
			else if( problem.getOption3() == null) {
				problem.setOption3(line);
			}
			else if( problem.getOption4() == null) {
				problem.setOption4(line);
			} 
		}
		return quiz;
	}

	private void export() {
		String content = workspace.getText();
		Quiz quiz = quizFromString(content);
		if( quiz == null)
			return;
		if(  !isValid(quiz)) {
			JOptionPane.showMessageDialog(null, "Quiz is invalid!");
			return;
		}
		JFileChooser chooser = new JFileChooser( ); 
		int option = chooser.showSaveDialog(null);
		if(option==JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
			File file = chooser.getSelectedFile();
			if( file != null) {
				  Gson gson = new Gson();
				  String jsonStr = gson.toJson(quiz);
				  System.out.println(jsonStr);
					try {
						DataProcessor.stringToFile(jsonStr, file);
					} catch (IOException e1) {
						 e1.printStackTrace();
					}
			}
		} 
	}
	public static boolean isValid(Quiz quiz) {
		if( quiz.getTitle() == null || quiz.getTitle().length() ==0)
			return false;
		if( quiz.getProblems() == null || quiz.getProblems().isEmpty())
			return false;
		for(Problem p : quiz.getProblems()) {
			if( p.getAnswer() == null || p.getAnswer().length() != 1)
				return false;
			if( "ABCD".indexOf(p.getAnswer()) <0)
				return false;
			if( p.getOption1() == null || p.getOption1().length() == 0)
				return false;
			if( p.getOption2() == null || p.getOption2().length() == 0)
				return false; 
		}
		return true;
	}
	public static String objToString(Quiz quiz) {
		StringBuilder sb = new StringBuilder();
		sb.append("##title="+ quiz.getTitle()+ "\n");
		sb.append("##desc=" + quiz.getDesc()+ "\n");
		for(Problem p : quiz.getProblems()) {
			sb.append("##problem" + "\n");
			if(p.getImage() != null && p.getImage().length()>0) {
				sb.append("##image=" + p.getImage() + "\n");
			}
			sb.append("##answer=" +p.getAnswer() + "\n");
			sb.append("##questionBody=" +p.getQuestionBody() + "\n");
			sb.append(p.getOption1() + "\n");
			sb.append(p.getOption2() + "\n");
			if(p.getOption3() != null && p.getOption3().length()>0)
			   sb.append(p.getOption3() + "\n");
			if(p.getOption4() != null && p.getOption4().length()>0)
			   sb.append(p.getOption4() + "\n");
		}
		
		
		
		return sb.toString();
	}
	private void importFile() {
		JFileChooser jfc=new JFileChooser( );  
		 
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
        jfc.showDialog(null, "选择");  
        File file=jfc.getSelectedFile();  
        if(file.isDirectory()){  
           return;
        }else if(file.isFile()){  
        	try {
			   String content =	DataProcessor.fileToString( file);
			   Gson gson = new Gson();
			   Quiz quiz =  gson.fromJson(content, Quiz.class);
			   String s = objToString(quiz);
			   this.workspace.setText(s);
			} catch ( Exception e1) {
				 e1.printStackTrace();
			}
        	 
        }  
	}
}
