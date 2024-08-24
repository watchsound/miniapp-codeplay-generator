package com.pi.code.tool.flowchart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.pi.code.tool.codefillin.PropertyUIHelper;
import com.pi.code.tool.flowchart.DataProcessor.FlowChart;
import com.pi.code.tool.flowchart.UndoManager.UndoAction;
import com.pi.code.tool.util.CodePlayerFrame;

public class WorkbenchForFlowChart extends CodePlayerFrame {
 
	private static final long serialVersionUID = 1L;


	public static interface DataProvider { 
		FlowChart getFlowChart(int num); 
		void setResult(FlowChart debug, int num);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorkbenchForFlowChart workbench = new WorkbenchForFlowChart(null);
		workbench.setVisible(true);
	}

	private DataProvider provider; 
	private DrawingPanel drawingPanel;
	private DrawingControlPanel controlPanel;
	private JButton backToCodePlayerButton;
	private DrawingContentEditPanel editPanel;
	private JButton chart1Button;
	
	FlowChart fc1;
	FlowChart fc2;
	FlowChart fc3;
	FlowChart curFc;
	private JButton chart2Button;
	private JButton chart3Button;
	UndoManager aUndoManager1;
	UndoManager aUndoManager2;
	UndoManager aUndoManager3;
	
	public WorkbenchForFlowChart(final DataProvider provider) {  
		this.provider = provider;
		if( provider != null) {
			fc1 = provider.getFlowChart(0);
			fc2 = provider.getFlowChart(1);
			fc3 = provider.getFlowChart(2);
		}
		if(fc1 == null)
			fc1 = new FlowChart();
		if(fc2 == null)
			fc2 = new FlowChart();
		if(fc3 == null)
			fc3 = new FlowChart();
		
		setLayout(new BorderLayout());

		aUndoManager1 = new UndoManager();
		aUndoManager2 = new UndoManager();
		aUndoManager3 = new UndoManager();
		UndoManager.UndoCallback undocallback = new UndoManager.UndoCallback() { 
			@Override
			public void undo(UndoAction action) {
				drawingPanel.undoredo(action, true);
			} 
			@Override
			public void redo(UndoAction action) {
				drawingPanel.undoredo(action, false);
			} 
			@Override
			public void setEnabled(boolean enableUndo, boolean enableRedo) {
				//.setEnabled(enableUndo);
				//redoBtn.setEnabled(enableRedo);
			}
		};
		aUndoManager1.setUndoCallback(undocallback);
		aUndoManager2.setUndoCallback(undocallback);
		aUndoManager3.setUndoCallback(undocallback);
		drawingPanel = new DrawingPanel();
		drawingPanel.setUndoManager(aUndoManager1);
		
	    controlPanel = new DrawingControlPanel(drawingPanel);
	    controlPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,0));
    	editPanel = new DrawingContentEditPanel(drawingPanel);
    	editPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
    	
    	JScrollPane scrollPane = new JScrollPane(drawingPanel);
	       scrollPane .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	       scrollPane .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);//.VERTICAL_SCROLLBAR_AS_NEEDED);

	       //scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	       scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
							BorderFactory.createEmptyBorder(4, 4, 4, 4) )));
    	
    	
	    add(controlPanel, BorderLayout.WEST);
	    add(scrollPane, BorderLayout.CENTER);
	    add(editPanel, BorderLayout.EAST);
	    
	    chart1Button =   new JButton("流程图-1");
	    chart1Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (provider == null)
					return;
				saveCurDataFromChartPane();
			    curFc = fc1;	
			    drawingPanel.setUndoManager(aUndoManager1);
			    drawingPanel.setData(curFc);
			}
		});
	    chart2Button =   new JButton("流程图-2");
	    chart2Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (provider == null)
					return;
				saveCurDataFromChartPane();
			    curFc = fc2;	
			    drawingPanel.setUndoManager(aUndoManager2);
			    drawingPanel.setData(curFc);
			}
		});
	    chart3Button =   new JButton("流程图-3");
	    chart3Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (provider == null)
					return;
				saveCurDataFromChartPane();
			    curFc = fc3;	
			    drawingPanel.setUndoManager(aUndoManager3);
			    drawingPanel.setData(curFc);
			}
		});
	    backToCodePlayerButton = new JButton("返回CodePlayer");
		backToCodePlayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (provider == null)
					return; 
				saveCurDataFromChartPane();
				
				provider.setResult( fc1, 0);
				provider.setResult( fc2, 1);
				provider.setResult( fc3, 2);
				setVisible(false);
			}
		});

		
		 
		 final JButton  importButton = new JButton("打开文件");
		 importButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				String lastopened = DataProcessor.fileToString( new File("last-opened-file")); 
				JFileChooser jfc=new JFileChooser(lastopened);  
				 
		        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
		        jfc.showDialog(null, "选择");  
		        File file=jfc.getSelectedFile();  
		        if(file == null || file.isDirectory()){  
		           return;
		        }else if(file.isFile()){  
		        	try {
						DataProcessor.stringToFile(file.getAbsolutePath(), new File("last-opened-file"));
					} catch (IOException e1) {
						 e1.printStackTrace();
					}
		        	FlowChart l2 =  DataProcessor.loadFile(file);
		        	drawingPanel.setData(l2);
		        }  
			}});
		 
		 final JButton savetButton = new JButton("保存到文件");
		 savetButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				String lastopened = DataProcessor.fileToString( new File("last-opened-file"));
				JFileChooser chooser = new JFileChooser(lastopened); 
				int option = chooser.showSaveDialog(null);
				if(option==JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
					File file = chooser.getSelectedFile();
					if( file != null) {
						DataProcessor.saveFile(drawingPanel.getData(), file);
					}
				} 
			}});
		 
		JPanel bottomRow = new JPanel();
		bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.Y_AXIS)); 
		bottomRow.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		
		bottomRow.add(PropertyUIHelper.createLine());
		bottomRow.add(PropertyUIHelper.createRow(  chart1Button,  chart2Button, chart3Button, true, null));

		bottomRow.add(PropertyUIHelper.createRow(" ", importButton, savetButton, backToCodePlayerButton));

		add(bottomRow, BorderLayout.SOUTH);
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				 if( provider != null) {
					    curFc = fc1;
				    	drawingPanel.setData(curFc);
				 }
			}});
		
		
		setSize(900, 600); 
		setVisible(true);
	}

	  
	private void saveCurDataFromChartPane() {
		if(curFc != null) {
			if( curFc == fc1)
				fc1 = drawingPanel.getData();
			if( curFc == fc2)
				fc2 = drawingPanel.getData();
			if( curFc == fc3)
				fc3 = drawingPanel.getData();
		}
	}
 
 
}
