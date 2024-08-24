package com.pi.code.tool.webview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser; 
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;

import com.pi.code.tool.codefillin.PropertyUIHelper;
import com.pi.code.tool.util.CodePlayerFrame;
import com.pi.code.tool.webview.DataProcessor.WebitemType;
import com.pi.code.tool.webview.DataProcessor.WebviewItem;
import com.pi.code.tool.webview.DataProcessor.WebviewPage;  

public class WorkbenchForWebview extends CodePlayerFrame{
 
	private static final long serialVersionUID = 1L;

	public static interface DataProvider{
		 
		WebviewPage getData();
		void setResult(WebviewPage data);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		WorkbenchForWebview workbench = new WorkbenchForWebview(null);
		workbench.setVisible(true);
	}
 
	private JTextArea workspace;
	 
	  
	private MyHighlightPainter curFillin;
	 

	private JButton newRecordButton;  
 

 



	private JButton backToCodePlayerButton;

 
	private DataProvider provider;



	private JRadioButton titleRadButton;



	private JRadioButton codeRadButton;



	private JRadioButton consoleRadButton;



	private JRadioButton listRadButton;



	private JRadioButton tableRadButton;



	private JRadioButton paragraphRadButton;


	private JButton addFillinButton;


	private JButton clearHighlightersButton;


	private JButton deleteFillinButton;


	private JRadioButton imageRadButton;


	private JRadioButton resetButton;
	public WorkbenchForWebview(final DataProvider provider) {
		this.provider = provider;
		setLayout(new BorderLayout());
		
		workspace = new JTextArea(80,500);
		
		workspace.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK),
						BorderFactory.createEmptyBorder(4, 4, 4, 4) )));
		
		JScrollPane wspane = new JScrollPane(workspace);
		
	//	Title("tt", Color.red), Code("cd",PageColors.green1), Console("cs", PageColors.blue1), List("lt", Color.pink), 
	//	Table("tb", PageColors.orange1), Paragraph("pg", PageColors.yellow1)  ;
		
		titleRadButton = new JRadioButton("标题");
		titleRadButton.setBackground( WebitemType.Title.color);
		titleRadButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				 handleTypeSelection(  titleRadButton) ;
			}});
		codeRadButton = new JRadioButton("代码");
		codeRadButton.setBackground( WebitemType.Code.color);
		codeRadButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				 handleTypeSelection(  codeRadButton) ; 	 	 
			}});
		consoleRadButton = new JRadioButton("输出");
		consoleRadButton.setBackground( WebitemType.Console.color);
		consoleRadButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				handleTypeSelection(  consoleRadButton) ; 		 
			}});
		listRadButton = new JRadioButton("列表") ;
		listRadButton.setBackground( WebitemType.List.color);
		listRadButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				handleTypeSelection(  listRadButton) ; 		 	 	 
			}});
		tableRadButton = new JRadioButton("表格");
		tableRadButton.setBackground( WebitemType.Table.color);
		tableRadButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				handleTypeSelection(  tableRadButton) ;  	 
			}});
		paragraphRadButton = new JRadioButton("内容"); 
		paragraphRadButton.setBackground( WebitemType.Paragraph.color);
		paragraphRadButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				handleTypeSelection(  paragraphRadButton) ;  	 
			}});
		imageRadButton = new JRadioButton("图片(图片名，一页最多支持2个图片)"); 
		imageRadButton.setBackground( WebitemType.Image.color);
		imageRadButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				handleTypeSelection(  imageRadButton) ; 	 
			}});
		resetButton = new JRadioButton("Reset");  
		resetButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				//handleTypeSelection(  resetButton) ; 	 
				 if( curFillin != null ) {
					 Highlighter hilite = workspace.getHighlighter();
					 Highlighter.Highlight[] hilites = hilite.getHighlights();
					    for (int i=0; i<hilites.length; i++)
					    {
					        if (hilites[i].getPainter()  == curFillin)
					        {
					            hilite.removeHighlight(hilites[i]);
					            curFillin = null;
					            break;
					        }
					    } 
					 
				 }
			}});
		ButtonGroup bg = new ButtonGroup();
		bg.add(  titleRadButton);
		bg.add( codeRadButton );
		bg.add( consoleRadButton );
		bg.add( listRadButton );
		bg.add( tableRadButton );
		bg.add( paragraphRadButton );
		bg.add( imageRadButton );
		bg.add( resetButton );
       workspace.addCaretListener(new CaretListener() {

		@Override
		public void caretUpdate(CaretEvent e) {
			 
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
				        	select(   curFillin.type  );
							 break;
				        }
				    }
				    
			}
			
			    
			System.out.println("---------CaretEvent "  + e.getMark() + ":" + 
			           e.getDot()   +":" );
			System.out.println("---------calculated selection: " + workspace.getSelectedText());
			 
		}});
		        
 
	  add(wspane, BorderLayout.CENTER);
	 
	   
				 
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
	        	WebviewPage data =  DataProcessor.loadFile(file);  
	        	highlightFromData(data);
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
						WebviewPage data = fromUI();
						DataProcessor.saveFile(data, file);
					}
				} 
			}});
				 
			 
			 newRecordButton = new JButton("清空文件");
			 newRecordButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					int op = JOptionPane.showConfirmDialog(null, "清空所有内容，从头开始？");
					if( op == JOptionPane.NO_OPTION)
						return; 
					 workspace.setText(""); 
					 workspace.getHighlighter().removeAllHighlights();
					 curFillin = null;  
				}});
			 clearHighlightersButton = new JButton("清空所有标记");
			 clearHighlightersButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					int op = JOptionPane.showConfirmDialog(null, "清空所有标记，从头开始？");
					if( op == JOptionPane.NO_OPTION)
						return; 
				 	 workspace.getHighlighter().removeAllHighlights();
					 curFillin = null;  
				}});
			 backToCodePlayerButton = new JButton("返回CodePlayer");
			 backToCodePlayerButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					 if( provider == null )
						 return;
					 WebviewPage data = fromUI();
					 provider.setResult(data);
					 setVisible(false);
				}});
			 
			 addFillinButton =  new JButton("添加 Fill-in");
			 addFillinButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						 int[] poss = getAdjustedSelectionRange();
						 curFillin = createNewRange(poss);
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
			bottomRow.add(PropertyUIHelper.createTitleRow("标记类型", true));
			bottomRow.add(PropertyUIHelper.createRow("", 
					titleRadButton, codeRadButton, consoleRadButton, listRadButton, tableRadButton, paragraphRadButton,imageRadButton ,resetButton ));
 
			 
			
			bottomRow.add(PropertyUIHelper.createTitleRow("修改", true));
			bottomRow.add(PropertyUIHelper.createRow("",  addFillinButton, deleteFillinButton, clearHighlightersButton ));
			
			bottomRow.add(PropertyUIHelper.createLine());
			 
			bottomRow.add(PropertyUIHelper.createRow("保存到CodePlayer:  " ,newRecordButton, importButton,savetButton, backToCodePlayerButton));  
			
			
			add(bottomRow, BorderLayout.SOUTH);
			
			if( provider != null ) {
			 	highlightFromData(provider.getData());
			}	 
		   setSize(900, 600);
		   setVisible(true);
	}
	private void handleTypeSelection(JRadioButton button) {
		 if(! button.isSelected()   ) return;
		 int[] poss = canCreateNewRange();
		 if(poss != null) {
			 curFillin = createNewRange(poss);
		 } 
		 if( curFillin == null ) 
			 return;
		 curFillin.type = getSelected( );
		repaint();	 
	}
	public MyHighlightPainter createNewRange(int[] poss) {
		if( poss == null) return null;
		 Highlighter hilite = workspace.getHighlighter(); 
		 try {
			    MyHighlightPainter curFillin = new MyHighlightPainter( getSelected( ));
			    hilite.addHighlight(poss[0],poss[1],curFillin );
			    return curFillin;
		 
		 } catch (BadLocationException e1) {
		 	e1.printStackTrace();
		 	return null;
		 }  
	}
	public int[] canCreateNewRange() {
		int[] poss = getAdjustedSelectionRange() ;
		if(poss == null)
			return null;
		if( this.curFillin == null)
			return poss;
		Highlight hl = getHighlight( this.curFillin );
		if( hl == null)
			return poss;
		if( hl.getEndOffset() <= poss[0] || hl.getStartOffset() >= poss[1])
			return poss;
		return null;
	}
	public Highlight getHighlight(MyHighlightPainter painter) {
		 Highlighter hilite = workspace.getHighlighter();
		 Highlighter.Highlight[] hilites = hilite.getHighlights();
	    for (int i=0; i<hilites.length; i++)
	    {
	        if (hilites[i].getPainter()  == painter)
	        {
	            return hilites[i];
	        }
	    } 
	    return null;
	}
	
	public int[] getAdjustedSelectionRange() {
		 if(workspace.getSelectedText() == null  )
			 return null;
	 	 int startpos = workspace.getSelectionStart();
		 int endpos = workspace.getSelectionEnd();
		 return addjustHighlighter(startpos, endpos) ;  
	}
	 
	public void select(WebitemType type) {
		switch(type) {
		case Title : titleRadButton.setSelected(true );break;
		case Code : codeRadButton.setSelected(true);break;
		case Console : consoleRadButton.setSelected(true);break;
		case List : listRadButton.setSelected(true);break;
		case Table : tableRadButton.setSelected(true);break;
		case Paragraph : paragraphRadButton.setSelected(true);break;
		case Image : imageRadButton.setSelected(true);break;
		} 
	}
	public WebitemType getSelected( ) {
		if( titleRadButton.isSelected(  )) return WebitemType.Title;
		if( codeRadButton.isSelected(  )) return WebitemType.Code;
		if( consoleRadButton.isSelected(  )) return WebitemType.Console;
		if( listRadButton.isSelected(  )) return WebitemType.List;
		if( tableRadButton.isSelected(  )) return WebitemType.Table;
		if( paragraphRadButton.isSelected(  )) return WebitemType.Paragraph; 
		if( imageRadButton.isSelected(  )) return WebitemType.Image; 
		return WebitemType.Paragraph; 
	} 
	   
	protected void highlightFromData(WebviewPage fillin) {
		if( fillin == null ) return;
		int startPos = 0;
		for(WebviewItem fin :  fillin.items) {
			workspace.insert(fin.c, startPos);
			startPos += fin.c.length();
		}
		 Highlighter hilite = workspace.getHighlighter();
		 startPos = 0;
		for(WebviewItem fin :  fillin.items) {
			try {
				MyHighlightPainter h = new MyHighlightPainter(WebitemType.fromType(fin.t));
				h.message = fin.c; 
				hilite.addHighlight(startPos, startPos+ h.message.length(), h );
				startPos = startPos+ h.message.length();
			} catch (BadLocationException e) {
				 	e.printStackTrace();
			}
		}
	}
	
	public WebviewPage fromUI() {
		WebviewPage model = new WebviewPage();  
		 Highlighter hilite = workspace.getHighlighter();
		    Highlighter.Highlight[] hilites = hilite.getHighlights();
		    List<Highlighter.Highlight> hs = Arrays.asList(hilites);
		    Collections.sort(hs, new Comparator<Highlighter.Highlight>() {

				@Override
				public int compare(Highlighter.Highlight o1, Highlighter.Highlight o2) { 
					if( o1.getStartOffset() == o2.getStartOffset() ) return 0;
					return o1.getStartOffset() < o2.getStartOffset() ? -1 : 1;
				}});
		    
		    int prevEnd = 0;
		    for (int i=0; i<hs.size(); i++)
		    {
		    	Highlight myh = hs.get(i);
		        if (myh.getPainter() instanceof MyHighlightPainter)
		        {
		        	if( prevEnd +1 <  myh.getStartOffset()) {
		        		WebviewItem fi = new WebviewItem();
			        	fi.c =  workspace.getText().substring( prevEnd   , myh.getStartOffset()-1);
			        	fi.t = WebitemType.Paragraph.type;
			            model.items.add(fi); 
		        	}
		        	MyHighlightPainter p = (MyHighlightPainter)myh.getPainter();
		        	WebviewItem fi = new WebviewItem();
		        	fi.c =  workspace.getText().substring(  myh.getStartOffset() , myh.getEndOffset());
		        	fi.t = p.type.type;
		            model.items.add(fi); 
		            prevEnd = myh.getEndOffset();
		        }
		    }
		
		return model;
	}


	protected int[] addjustHighlighter(int startpos, int endpos) {
		String text = workspace.getText();
		if( text.length() == 0) return null;
		int[] pos = new int[2];
		if( startpos == 0 || text.charAt(startpos-1) == '\n') {
			pos[0] = startpos;
		} else {
			char c = ' ';
			do {
				startpos --;
				c = text.charAt(startpos); 
			}while( c != '\n' && startpos > 0);
			pos[0] = startpos;
		}
		if( endpos >= text.length() -1 || text.charAt(endpos) == '\n') {
			pos[1] = endpos;
		} else {
			char c = ' ';
			do {
				  endpos ++;
				  c = text.charAt(endpos); 
			}while( c != '\n' && endpos <  text.length() -1 );
			pos[1] = endpos;
		}
		return pos;
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
		 
		public String message;
		public WebitemType type ;
	    public MyHighlightPainter(WebitemType atype)
	    {
	        super(atype.color);
	        this.type = atype;
	        this.message = "";
	    }
	    public void setType(WebitemType atype) {
	    	this.type = atype; 
	    }
	    public Color getColor() {
            return type.color;
        }
	}
}
