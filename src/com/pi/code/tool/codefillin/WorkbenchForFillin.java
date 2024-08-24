package com.pi.code.tool.codefillin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.apache.commons.lang3.StringUtils;
import org.r9.workspace.ui.dialog.chatgpt.SuggestionHelper;
import org.r9.workspace.ui.dialog.chatgpt.SuggestionHelper.FetchSuggestion;

import com.pi.code.tool.codefillin.DataProcessor.CodePlayerFillin;
import com.pi.code.tool.codefillin.DataProcessor.Fillin;
import com.pi.code.tool.codeplayer.Workbench;
import com.pi.code.tool.util.CodePlayerFrame;
import com.pi.code.tool.util.PageColors;  

public class WorkbenchForFillin extends CodePlayerFrame{
 
	private static final long serialVersionUID = 1L;

	public static interface DataProvider{
		String getFullCode();
		List<Fillin> getFillin();
		void setResult(String content, List<Fillin> fillin);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorkbenchForFillin workbench = new WorkbenchForFillin(null);
		workbench.setVisible(true);
	}
 
	private JTextArea workspace;
	 
	 
 
	private JLabel cursorPosLabel;  
	private MyHighlightPainter curFillin;
	//private CodePlayerFillin data;
	//private List<String> textLines = new ArrayList<>(); 
	
	private JTextArea titleField;
	private JTextArea descField;


	private JButton newRecordButton; 
	CustomKeyboardEditPane fillinEditor;


	private JTextField fillinFeedbackField;


	private JButton addFillinButton;


	private JButton deleteFillinButton;


	private JButton saveFillinButton;



	private JButton backToCodePlayerButton;

 
	private DataProvider provider;



	private JButton addFillinGPTButton;



	private JLabel addFillinLabel;



	 

	private JButton selectCodeAreaButton;



	private String curCodeArea;



	private int curCodeStart;



	private int curCodeEnd;
	public WorkbenchForFillin(final DataProvider provider) {
		this.provider = provider;
		setLayout(new BorderLayout());
		workspace = new JTextArea(200,80);
		 JScrollPane scrollPane = new JScrollPane(workspace);
	       scrollPane .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	       scrollPane .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);//.VERTICAL_SCROLLBAR_AS_NEEDED);

	       //scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	       scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
							BorderFactory.createEmptyBorder(4, 4, 4, 4) )));
		   
		    
       workspace.addCaretListener(new CaretListener() {

		@Override
		public void caretUpdate(CaretEvent e) {
			cursorPosLabel.setText(e.getMark() +"");
			if( e.getMark() == e.getDot()) {
				    Highlighter hilite = workspace.getHighlighter();
				    Highlighter.Highlight[] hilites = hilite.getHighlights();
				    for (int i=0; i<hilites.length; i++)
				    {
				        if (hilites[i].getPainter() instanceof MyHighlightPainter
				        		&& hilites[i].getStartOffset() <= e.getMark() &&
				        		hilites[i].getEndOffset() >= e.getMark())
				        {
				        	curFillin = (MyHighlightPainter)hilites[i].getPainter();
				        	  fillinFeedbackField.setText(  curFillin.message );
				        	  String correct = curFillin.correct;
							  fillinEditor.setData( curFillin.options, correct );
							 break;
				        }
				    }
				    
			}
			
			    
			System.out.println("---------CaretEvent "  + e.getMark() + ":" + 
			           e.getDot()   +":" );
			System.out.println("---------calculated selection: " + workspace.getSelectedText());
			 
		}});
		        
 
	  add(scrollPane, BorderLayout.CENTER);
 
	   
				 
	 final JButton  importButton = new JButton("打开文件");
	 importButton.addActionListener(new ActionListener() { 
		@Override
		public void actionPerformed(ActionEvent e) {
			 
			JFileChooser jfc=new JFileChooser();  
	        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
	        jfc.showDialog(null, "选择");  
	        File file=jfc.getSelectedFile();  
	        if(file.isDirectory()){  
	           return;
	        }else if(file.isFile()){  
	        	CodePlayerFillin data =  DataProcessor.loadFile(file); 
	        	titleField.setText(data.title);
	        	descField.setText(data.desc);  
	        	workspace.setText(data.code);
	        	highlightFromData(data.fillin);
	        	 curFillin = null;  
	        }  
		}});
				 
		 final JButton savetButton = new JButton("保存到文件");
		 savetButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser(); 
				int option = chooser.showSaveDialog(null);
				if(option==JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
					File file = chooser.getSelectedFile();
					if( file != null) {
						CodePlayerFillin data = fromUI();
						DataProcessor.saveFile(data, file);
					}
				} 
			}});
				 
			 
			 newRecordButton = new JButton("新文件");
			 newRecordButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					int op = JOptionPane.showConfirmDialog(null, "清空所有记录，从头开始？");
					if( op == JOptionPane.NO_OPTION)
						return; 
					 workspace.getHighlighter().removeAllHighlights();
					 workspace.setText(""); 
					 curFillin = null;  
				}});
			 
			 backToCodePlayerButton = new JButton("返回CodePlayer");
			 backToCodePlayerButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					 if( provider == null )
						 return;
					 CodePlayerFillin data = fromUI();
					 provider.setResult(workspace.getText(), data.fillin);
					 setVisible(false);
				}});
			 	    
				 
		    cursorPosLabel = new JLabel();
				 
		 
			
			titleField = new JTextArea();
			descField = new JTextArea();
		 
			
			 fillinEditor = new CustomKeyboardEditPane(false, false);
			 fillinFeedbackField = new JTextField();
			 fillinFeedbackField.setPreferredSize(new Dimension(300,38));
			 fillinFeedbackField.setMaximumSize(new Dimension(300,38));
				
			 saveFillinButton =  new JButton("保存修改");
			 saveFillinButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						 if( curFillin == null )
							 return;
						 curFillin.message = fillinFeedbackField.getText();
						 curFillin.options = fillinEditor.getData();
					}});
			 
			 addFillinLabel = new JLabel("添加 Fill-in");
			
			 
			 curCodeArea = "";
			 curCodeStart = -1;
			 curCodeEnd = -1;
			 selectCodeAreaButton  = new JButton("选择代码区");
			 selectCodeAreaButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						String code = workspace.getSelectedText() ;
						
						if( code == null)
							 return;
						 Highlighter hilite = workspace.getHighlighter();
						 int startpos = workspace.getSelectionStart();
						 int endpos = workspace.getSelectionEnd();
						 try {
							if( canAddHighlighter(startpos, endpos)) {
							   MyHighlightPainter	curFillin = new MyHighlightPainter(PageColors.green1, true);
							   hilite.addHighlight(startpos,endpos,curFillin );
							   curCodeArea = code;
							   curCodeStart = startpos;
							   curCodeEnd = endpos; 
							}
						 } catch (BadLocationException e1) {
						 	e1.printStackTrace();
						 } 
					}
			 });
			 
			 addFillinButton =  new JButton("添加 Fill-in");
			 addFillinButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						 if(workspace.getSelectedText() == null  )
							 return;
						 Highlighter hilite = workspace.getHighlighter();
						 int startpos = workspace.getSelectionStart();
						 int endpos = workspace.getSelectionEnd();
						 try {
							if( canAddHighlighter(startpos, endpos)) {
								curFillin = new MyHighlightPainter(PageColors.red1);
							   hilite.addHighlight(startpos,endpos,curFillin );
							}
						 } catch (BadLocationException e1) {
						 	e1.printStackTrace();
						 } 
						 Random r = new Random();
						 String correct =  workspace.getSelectedText();
						 List<String> ops = DataProcessor.getCandidates(StringUtils.isEmpty(curCodeArea) ? workspace.getText()  : curCodeArea, correct);
						 if( ops.size() > 0 && ops.indexOf(correct) < 0)
						     ops.add(r.nextInt(ops.size()), correct);
						 curFillin.options = ops;
						 curFillin.correct = correct;
						 fillinEditor.setData(ops, correct);
					}});
			 
			 addFillinGPTButton =  new JButton("添加 Fill-in (chatGPT)"); 
			 addFillinGPTButton.setEnabled( !StringUtils.isEmpty(Workbench.CHATGPT_KEY) );
			 
			 addFillinGPTButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						 if(workspace.getSelectedText() == null  )
							 return;
						 Highlighter hilite = workspace.getHighlighter();
						 int startpos = workspace.getSelectionStart();
						 int endpos = workspace.getSelectionEnd();
						 try {
							if( canAddHighlighter(startpos, endpos)) {
								curFillin = new MyHighlightPainter(PageColors.red1);
							   hilite.addHighlight(startpos,endpos,curFillin );
							}
						 } catch (BadLocationException e1) {
						 	e1.printStackTrace();
						 } 
						 String correct = workspace.getSelectedText(); 
						 FetchSuggestion callback = new FetchSuggestion() { 
							public void onResult(List<String> data) { 
								 System.out.println( data );
								 Random r = new Random(); 
								 data.add(r.nextInt(data.size()), correct);
								 curFillin.options = data;
								 curFillin.correct = correct;
								 fillinEditor.setData(data, correct);  
							} 
						  };
						  String code = ""; 
						  if( StringUtils.isEmpty(curCodeArea) ) {
							  code = workspace.getText().substring(0, startpos) + "XXXXX" + workspace.getText().substring(endpos);
						  } else {
							  code = curCodeArea.substring(0, startpos-curCodeStart) + "XXXXX" + curCodeArea.substring(endpos-curCodeStart);
						  }
						  
						  SuggestionHelper.getSuggestions(code, correct, callback);
						  
					}});
			 
			 
			 deleteFillinButton =  new JButton("删除 Fill-in");
			 deleteFillinButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) { 
						 Highlighter hilite = workspace.getHighlighter();
						 Highlighter.Highlight[] hilites = hilite.getHighlights();
						    for (int i=0; i<hilites.length; i++)
						    {
						        if (hilites[i].getPainter()  == curFillin)
						        {
						            hilite.removeHighlight(hilites[i]);
						            break;
						        }
						    } 
					}});
			JPanel bottomRow = new JPanel();
			bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.Y_AXIS));
			bottomRow.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			bottomRow.add(PropertyUIHelper.createTitleRow("标题", true));
			bottomRow.add(titleField);
			bottomRow.add(PropertyUIHelper.createTitleRow("说明", true));
			bottomRow.add(descField); 
			bottomRow.add(PropertyUIHelper.createTitleRow("Fill-in", true));
			bottomRow.add(PropertyUIHelper.createRow("", selectCodeAreaButton, new JLabel("|"),
					addFillinButton,addFillinGPTButton, new JLabel("|"), deleteFillinButton ));
			
			bottomRow.add(PropertyUIHelper.createTitleRow("Fill-in 细节"));
			bottomRow.add(fillinEditor);
			bottomRow.add(PropertyUIHelper.createTitleRow("Fill-in 反馈"));
			bottomRow.add(PropertyUIHelper.createRow( fillinFeedbackField)); 
			bottomRow.add(PropertyUIHelper.createRow(saveFillinButton, false));
			
			bottomRow.add(Box.createVerticalGlue( )); 
			bottomRow.add(PropertyUIHelper.createRow("Cursor位置:  ", cursorPosLabel));  
			bottomRow.add(PropertyUIHelper.createLine());
			bottomRow.add(PropertyUIHelper.createRow(newRecordButton, importButton, savetButton, false, null));
			 
			bottomRow.add(PropertyUIHelper.createRow("保存到CodePlayer:  ", backToCodePlayerButton));  
			
			
			add(bottomRow, BorderLayout.EAST);
			
			if( provider != null ) {
				workspace.setText(provider.getFullCode());
				highlightFromData(provider.getFillin());
			}	 
		   setSize(900, 600);
		   setVisible(true);
	}
	 
	   
	protected void highlightFromData(List<Fillin> fillin) {
		 Highlighter hilite = workspace.getHighlighter();
		for(Fillin fin :  fillin) {
			try {
				MyHighlightPainter h = new MyHighlightPainter(PageColors.red1);
				h.message = fin.tip;
				h.correct = fin.op.get(fin.as);
				h.options = fin.op;
				hilite.addHighlight(fin.sp, fin.ep, h );
			} catch (BadLocationException e) {
				 	e.printStackTrace();
			}
		}
	}
	
	public CodePlayerFillin fromUI() {
		CodePlayerFillin model = new CodePlayerFillin();
		model.title = titleField.getText();
		model.desc = descField.getText();
		model.code = workspace.getText();
		 Highlighter hilite = workspace.getHighlighter();
		    Highlighter.Highlight[] hilites = hilite.getHighlights();
		    for (int i=0; i<hilites.length; i++)
		    {
		        if (hilites[i].getPainter() instanceof MyHighlightPainter)
		        {
		        	MyHighlightPainter p = (MyHighlightPainter)hilites[i].getPainter();
		        	if( p.codeArea )
		        		continue;
		        	Fillin fi = new Fillin();
		        	fi.sp = hilites[i].getStartOffset();
		        	fi.ep = hilites[i].getEndOffset();
		        	fi.tip = p.message;
		        	fi.op = p.options;
		        	fi.as = p.options.indexOf(p.correct);
		            model.fillin.add(fi); 
		        }
		    }
		Collections.sort(model.fillin, new Comparator<Fillin>() {

			@Override
			public int compare(Fillin o1, Fillin o2) { 
				if( o1.sp == o2.sp ) return 0;
				return o1.sp < o2.sp ? -1 : 1;
			}});
		return model;
	}


	protected boolean canAddHighlighter(int startpos, int endpos) {
		return workspace.getText().substring(startpos, endpos).indexOf("\n") < 0; 
	}


	// Removes only our private highlights
	public void removeHighlights( )
	{
	    Highlighter hilite = workspace.getHighlighter();
	    Highlighter.Highlight[] hilites = hilite.getHighlights();
	    for (int i=0; i<hilites.length; i++)
	    {
	        if (hilites[i].getPainter() instanceof MyHighlightPainter)
	        {
	            hilite.removeHighlight(hilites[i]);
	        }
	    }
	}
	// An instance of the private subclass of the default highlight painter
	//Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.red);

	// A private subclass of the default highlight painter
	class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter
	{
		public List<String> options;
		public String message;
		public String correct ;
		public boolean codeArea;
		public MyHighlightPainter(Color color ) {
			this(color, false);
		}
	    public MyHighlightPainter(Color color, boolean codeArea )
	    {
	        super(color);
	        this.codeArea = codeArea;
	        this.options = new ArrayList<>();
	        this.message = "";
	    }
	}
}
