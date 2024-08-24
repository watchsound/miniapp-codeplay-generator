package com.pi.code.tool.codeplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser; 
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.pi.code.tool.codefillin.CustomKeyboardEditPane;
import com.pi.code.tool.codefillin.DataProcessor.Fillin;
import com.pi.code.tool.codefillin.PropertyUIHelper;
import com.pi.code.tool.codefillin.WorkbenchForFillin;
import com.pi.code.tool.codeplayer.DataProcessor.Lesson2;
import com.pi.code.tool.codeplayer.DataProcessor.Record;
import com.pi.code.tool.debug.DataProcessor.Debug;
import com.pi.code.tool.debug.WorkbenchForDebug;
import com.pi.code.tool.flowchart.DataProcessor.FlowChart;
import com.pi.code.tool.flowchart.WorkbenchForFlowChart;
import com.pi.code.tool.util.CodePlayerFrame;
import com.pi.code.tool.webview.DataProcessor.WebviewPage;
import com.pi.code.tool.webview.WorkbenchForWebview; 

public class Workbench extends CodePlayerFrame{
 
	private static final long serialVersionUID = 1L;
	public  static final String CHATGPT_KEY = "";
	
	private List<Record> records = new ArrayList<Record>();
	private List<Fillin> fillins = new ArrayList<Fillin>();
	private String quizContent;
	private Debug debug = new Debug();
	private  WebviewPage wpage = new  WebviewPage();
	private  WebviewPage tippage = new  WebviewPage();
	private FlowChart flowChart1 = new FlowChart();
	private FlowChart flowChart2 = new FlowChart();
	private FlowChart flowChart3 = new FlowChart();
	private PhaseListModel phaseListModel;
	private JList phaseListView;
	
	private JTextArea workspace;
	private JScrollPane phaseScrolPane;
	private JButton insertMessageButton;
	private JButton newRecordButton;
	//private boolean startRecord;
	//private boolean startInsertMessage;
	private JTextField startPosField;
	private JTextField endPosField;
	private JTextField stopTimeField;
	private JTextField keepTimeField;
	private JTextField messageField;
	private JButton saveMessageButton; 
	private JLabel cursorPosLabel;
	
	 
	private Record curQuizRecord;
	private Record curMessageRecord;
	private Record curRecord;
	private JRadioButton recordStateRadioButton;
	private JRadioButton editStateRadioButton;
	private JButton deleteMessageButton;
	private JTextField titleField;
	private JTextField descField;
	private JButton fillinEditButton;
	private CustomKeyboardEditPane popupquizEditor;
	private JTextField popupquizbodyField;
	private JTextField popupquizFeedbackField;
	private JButton popupquizNewButton;
	private JButton popupquizSaveButton;
	private JButton popupquizDeleteButton;
	private JButton insertOutputButton;
	private JTextField ouputstopTimeField; 
	private JTextField outputmessageField;
	private JButton saveOutputButton;
	private JButton deleteOutputButton;
	private JButton debugEditButton; 
	
	CodePlayer player;
	private JButton flowChartButton;
	private JButton wpageEditButton;
	protected File curOpendFile;
	private JButton tipsEditButton;
	public static File codePlayerHome;
	public Workbench() {
		setLayout(new BorderLayout());
		 
		workspace = new JTextArea(80,500);
		workspace.setEditable(false);
	//	workspace.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		 workspace.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void insertUpdate(DocumentEvent e) {
					if( editStateRadioButton.isSelected()) return;
					 Document doc = workspace.getDocument();
			            String txt = "";
						try {
							txt = doc.getText(e.getOffset(), e.getLength());
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					if( curRecord == null || !curRecord.prefix.equals("i")) {
						curRecord = new Record("i");
						curRecord.startPos = e.getOffset();
						curRecord.content = txt;
						 curRecord.bulk = txt.length() >1;
						records.add(curRecord);
						
					}
					else {
						if( e.getOffset()  == curRecord.startPos + curRecord.content.length() 
						    && txt.length() == 1 && !txt.equals("\n")) {
							curRecord.content += txt;
						}
						else {
							curRecord = new Record("i");
							curRecord.startPos = e.getOffset();
							curRecord.content = txt;
							curRecord.bulk = txt.length() >1;
							records.add(curRecord);
						}
					}
					phaseListModel.rebuildList();
					//for debug purpose.
					
					System.out.println("insertUpdate  "   + e.getType() + ":" + 
				           e.getOffset() +":" + e.getLength() +":" + txt  + ":");
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					if( editStateRadioButton.isSelected()) return;
					if( curRecord == null || !curRecord.prefix.equals("d")) {
						curRecord = new Record("d");
						curRecord.startPos = e.getOffset();
						curRecord.endPos = e.getOffset() + e.getLength() -1;
						curRecord.bulk = e.getLength() > 1;
						records.add(curRecord); 
					}
					else {
						if( e.getOffset() + e.getLength() -1 == curRecord.startPos-1  && e.getLength() == 1 ) {
							//continues delete... merge into one.
							curRecord.startPos = e.getOffset();
						}
						else {
							curRecord = new Record("d");
							curRecord.startPos = e.getOffset();
							curRecord.endPos = e.getOffset() + e.getLength() -1;
							curRecord.bulk = e.getLength() > 1;
							records.add(curRecord);
						}
					}
					phaseListModel.rebuildList();
					
					
					//for debug
					System.out.println("removeUpdate  "  + e.getType() + ":" + 
					           e.getOffset() +":" + e.getLength() +":" );
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					 
					System.out.println("changedUpdate  "  + e.getType() + ":" + 
					           e.getOffset() +":" + e.getLength() +":" );
				}});
		    
		    
		       workspace.addCaretListener(new CaretListener() {

				@Override
				public void caretUpdate(CaretEvent e) {
					cursorPosLabel.setText(e.getMark() +"");
					System.out.println("---------CaretEvent "  + e.getMark() + ":" + 
					           e.getDot()   +":" );
					System.out.println("---------calculated selection: " + workspace.getSelectedText());
					
					
					if( editStateRadioButton.isSelected()) return;
					   
					if( e.getMark() != e.getDot() ) {
						curRecord = new Record("s", e.getMark(), e.getDot(), 0,0);
						records.add(curRecord);
						phaseListModel.rebuildList();
					}
					
					
				}});
		        
//		       drawingPanel = new DrawingPanel();
//				
//				final JPanel centerPane = new JPanel();
//				final CardLayout card = new CardLayout() ;
//				 centerPane.setLayout(card) ;
//				 centerPane.add(workspace, "workspace");
//				 centerPane.add(drawingPanel, "drawingpane");
				 
				 
		       JScrollPane scrollPane = new JScrollPane(workspace);
		       scrollPane .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		       scrollPane .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);//.VERTICAL_SCROLLBAR_AS_NEEDED);

		       scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		       add(scrollPane, BorderLayout.CENTER);

		       phaseListModel = new PhaseListModel();
				phaseListView = new JList(phaseListModel);
				phaseListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			 	phaseListView.getSelectionModel().addListSelectionListener(
						new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								if( !workspace.isEditable( ))
									return;
								if (!e.getValueIsAdjusting()) {
									ListSelectionModel lsm = (ListSelectionModel) e
											.getSource();
									if (!lsm.isSelectionEmpty()) { 
										final int row = lsm.getLeadSelectionIndex(); 
										if( row >=0 ) {
											final Record arecord = phaseListModel.getElementAt(row);
											if( arecord.prefix.equals("m")) {
												curMessageRecord = arecord;
												setUIFromMessageRecord();
												curQuizRecord = null;
												setUIFromQuizRecord();
											} else if( arecord.prefix.equals("q")) {
												curMessageRecord = null;
												setUIFromMessageRecord();
													curQuizRecord = arecord;
													setUIFromQuizRecord();
											} else if ( arecord.prefix.equals("i") ) {
												JPopupMenu menu = new JPopupMenu();
												JMenuItem option1 = new JMenuItem("在前面插入提示");
												option1.addActionListener(new ActionListener() { 
													public void actionPerformed(ActionEvent e) {
														editStateRadioButton.setSelected(true);
														curMessageRecord = new Record("m");
														records.add(row, curMessageRecord);
														setUIFromMessageRecord();
														phaseListModel.rebuildList();
													}});
												JMenuItem option10 = new JMenuItem("在前面插入输出");
												option10.addActionListener(new ActionListener() { 
													public void actionPerformed(ActionEvent e) {
														editStateRadioButton.setSelected(true);
														curMessageRecord = new Record("m");
														curMessageRecord.isoutput = true;
														records.add(row, curMessageRecord);
														setUIFromMessageRecord();
														phaseListModel.rebuildList();
													}});
												JMenuItem option11 = new JMenuItem("在前面插入选择题");
												option11.addActionListener(new ActionListener() { 
													public void actionPerformed(ActionEvent e) {
														editStateRadioButton.setSelected(true);
														curQuizRecord = new Record("q");
														records.add(row, curQuizRecord);
														setUIFromQuizRecord();
														phaseListModel.rebuildList();
													}});
												JMenuItem option2 = new JMenuItem("在后面插入提示");
												option2.addActionListener(new ActionListener() { 
													public void actionPerformed(ActionEvent e) {
														editStateRadioButton.setSelected(true);
														curMessageRecord = new Record("m");
														records.add(row+1, curMessageRecord);
														setUIFromMessageRecord();
														phaseListModel.rebuildList();
													}});
												JMenuItem option20 = new JMenuItem("在后面插入输出");
												option20.addActionListener(new ActionListener() { 
													public void actionPerformed(ActionEvent e) {
														editStateRadioButton.setSelected(true);
														curMessageRecord = new Record("m");
														curMessageRecord.isoutput = true;
														records.add(row+1, curMessageRecord);
														setUIFromMessageRecord();
														phaseListModel.rebuildList();
													}});
												JMenuItem option22 = new JMenuItem("在后面插入选择题");
												option22.addActionListener(new ActionListener() { 
													public void actionPerformed(ActionEvent e) {
														editStateRadioButton.setSelected(true);
														curQuizRecord = new Record("q");
														records.add(row+1, curQuizRecord);
														setUIFromQuizRecord();
														phaseListModel.rebuildList();
													}});
												menu.add(option1);
												menu.add(option10);
												menu.add(option11);
												menu.add(option2);
												menu.add(option20);
												menu.add(option22);
												if( !arecord.bulk && arecord.content.length() > 1) {
													JMenuItem option3 = new JMenuItem("将插入操作记录分解为2部分");
													option3.addActionListener(new ActionListener() { 
														public void actionPerformed(ActionEvent e) {
															SplitDialog adialog = new SplitDialog(arecord);
															adialog.setSize(300, 300);
															adialog.setVisible(true);
														}});
												    menu.add(option3);
												    JMenuItem option4 = new JMenuItem("将插入改为批块模式");
												    option4.addActionListener(new ActionListener() { 
														public void actionPerformed(ActionEvent e) {
															arecord.bulk = true;
															phaseListModel.rebuildList();
														}});
													menu.add(option4);
												}
												else if (arecord.bulk) {
													 JMenuItem option4 = new JMenuItem("将插入改为单个模式");
													 option4.addActionListener(new ActionListener() { 
															public void actionPerformed(ActionEvent e) {
																arecord.bulk = false;
																phaseListModel.rebuildList();
															}});
														menu.add(option4);
												}
												 JMenuItem optionx = new JMenuItem("删除所有之后的记录");
												 optionx.addActionListener(new ActionListener() { 
														public void actionPerformed(ActionEvent e) {
														    for(int i = records.size()-1; i > row; i--)
														        records.remove(i);
															phaseListModel.rebuildList();
															skipToEnd();
															
														}});
													menu.add(optionx);
												
												menu.show(phaseListView, 50, 300);
												
											}
										}
									}
								}
							}
						});
				  
			 	
				phaseScrolPane = new JScrollPane(phaseListView);
				phaseScrolPane
						.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				phaseScrolPane
						.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
				phaseScrolPane.setMinimumSize(new Dimension(200,600));
				phaseScrolPane.setPreferredSize (new Dimension(200,600));
				phaseScrolPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
				 
				 add(phaseScrolPane, BorderLayout.EAST);
		       
				 
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
								 return;
							} 
				        	curOpendFile = file;
				        	codePlayerHome = file.getParentFile();
				        	Lesson2 l2 =  DataProcessor.loadFile(file);
				        	records = l2.records;
				        	fillins = l2.fillins;
				        	quizContent = l2.quizContent;
				        	wpage = l2.wpage;
				        	debug = l2.debug;
				        	flowChart1 = l2.fc1;
				        	flowChart2 = l2.fc2;
				        	flowChart3 = l2.fc3;
				        	titleField.setText(l2.title);
				        	descField.setText(l2.desc);
				        	phaseListModel.rebuildList();
				        }  
					}});
				 
				 final JButton savetButton = new JButton("保存到文件");
				 savetButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						if( curOpendFile != null && curOpendFile.exists() ) {
							DataProcessor.saveFile(records, wpage, tippage, fillins, quizContent, debug, flowChart1, flowChart2, flowChart3, titleField.getText(), descField.getText(), curOpendFile);
							return;
						}
						String lastopened = DataProcessor.fileToString( new File("last-opened-file"));
						JFileChooser chooser = new JFileChooser(lastopened); 
						int option = chooser.showSaveDialog(null);
						if(option==JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
							File file = chooser.getSelectedFile();
							if( file != null) {
								if( file.exists() ) {
									int op = JOptionPane.showConfirmDialog(Workbench.this, "同名文件存在，需要覆盖？");
									if( op == JOptionPane.NO_OPTION ||  op == JOptionPane.CANCEL_OPTION )
										return;
								} 
								DataProcessor.saveFile(records, wpage, tippage, fillins, quizContent, debug, flowChart1, flowChart2, flowChart3, titleField.getText(), descField.getText(), file);
								curOpendFile = file;
								codePlayerHome = file.getParentFile();
							}
						} 
					}});
				 
				 final JButton saveAsButton = new JButton("另存为");
				 saveAsButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						String lastopened = DataProcessor.fileToString( new File("last-opened-file"));
						JFileChooser chooser = new JFileChooser(lastopened); 
						int option = chooser.showSaveDialog(null);
						if(option==JFileChooser.APPROVE_OPTION){	//假如用户选择了保存
							File file = chooser.getSelectedFile();
							if( file != null) { 
								DataProcessor.saveFile(records, wpage, tippage, fillins, quizContent, debug, flowChart1, flowChart2, flowChart3, titleField.getText(), descField.getText(), file);
								curOpendFile = file;
								codePlayerHome = file.getParentFile();
							}
						} 
					}});
				 
				 
				 popupquizbodyField = new JTextField();
				 popupquizFeedbackField = new JTextField();
				 popupquizEditor = new CustomKeyboardEditPane(true, true);
				 popupquizNewButton = new JButton("插入新选择题");
				 popupquizNewButton.addActionListener(new ActionListener() { 
						@Override
						public void actionPerformed(ActionEvent e) { 
							editStateRadioButton.setSelected(true);
							curQuizRecord = new Record("q");
							int pos = phaseListView.getSelectedIndex();
							if( pos < 0 )
								pos = records.size();
							records.add(pos,curQuizRecord);  
							phaseListModel.rebuildList();
						}});
				 popupquizSaveButton = new JButton("保存修改");
				 popupquizSaveButton.addActionListener(new ActionListener() { 
						@Override
						public void actionPerformed(ActionEvent e) { 
							if( curQuizRecord == null )
								return;
							 setQuizRecordFromUI(); 
							phaseListModel.rebuildList();
						}});
				 popupquizDeleteButton = new JButton("删除选择题");
				 popupquizDeleteButton.addActionListener(new ActionListener() { 
						@Override
						public void actionPerformed(ActionEvent e) { 
							if( curQuizRecord == null )
								return;
							records.remove(curQuizRecord); 
							phaseListModel.rebuildList();
						}});
				 
				 JPanel quizPanel = new JPanel();
				// quizPanel.setBorder(BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder("选择题"), BorderFactory.createLineBorder(Color.GRAY)));
				 quizPanel.setLayout(new BoxLayout(quizPanel, BoxLayout.Y_AXIS));	
				 JPanel quizBodyRow = new JPanel(); 
				 quizBodyRow.setLayout(new BoxLayout(quizBodyRow, BoxLayout.X_AXIS));
				  quizBodyRow.add(new JLabel("题目"));
				  quizBodyRow.add(popupquizbodyField);
				  quizBodyRow.add(new JLabel("反馈"));
				  quizBodyRow.add(popupquizFeedbackField);
				  quizPanel.add(PropertyUIHelper.createRow(popupquizNewButton, popupquizSaveButton, popupquizDeleteButton, false, null));
				   quizPanel.add(quizBodyRow);
				  quizPanel.add(popupquizEditor);
				  
			 
				 newRecordButton = new JButton("录制新文件");
				 newRecordButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						int op = JOptionPane.showConfirmDialog(null, "清空所有记录，从头开始？");
						if( op == JOptionPane.NO_OPTION || op == JOptionPane.CANCEL_OPTION)
							return;
					    recordStateRadioButton.setSelected(true); 
						workspace.setEditable(true);
						//card.show(centerPane, "workspace");
						workspace.setText("");
						records.clear();
						fillins.clear();
						quizContent = "";
						wpage.items.clear();
						tippage.items.clear();
						debug = new Debug();
						 curMessageRecord = null;
						 curRecord = null;
						 flowChart1 = null;
						 flowChart2 = null;
						 flowChart3 = null;
						phaseListModel.rebuildList(); 
							 
					
					}});
				 recordStateRadioButton = new JRadioButton("录制");
				 editStateRadioButton = new JRadioButton("编辑");
				 recordStateRadioButton.addItemListener(new ItemListener() { 
					@Override
					public void itemStateChanged(ItemEvent e) {
						workspace.setEditable(true);
					//	card.show(centerPane, "workspace");
					}});
				 editStateRadioButton.addItemListener(new ItemListener() { 
						@Override
						public void itemStateChanged(ItemEvent e) {
							workspace.setEditable(false);
						//	card.show(centerPane, "drawingpane");
						}});
				 ButtonGroup bg = new ButtonGroup();
				 bg.add(recordStateRadioButton);
				 bg.add(editStateRadioButton);
				 
				 final JButton previewButton = new JButton("预览");
				 previewButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						editStateRadioButton.setSelected(true);
						workspace.setEditable(false);
					//	optimizeCode2();
						
						if( player == null)
						    player = new CodePlayer(Workbench.this, workspace);
						player.play(records, false,new CodePlayer.PlayCallback() { 
							@Override
							public void playRecordAtRow(int row) {
								phaseListView.setSelectedIndex(row);
								phaseListView.scrollRectToVisible(
										new Rectangle(phaseListView.getCellBounds(row, row)));
							}
							public void playDone() {
								workspace.setEditable(true);
							}
						});
					}});
				 
				 final JButton toEndButton = new JButton("到最后");
				 toEndButton.addActionListener(new ActionListener() { 
					@Override
					public void actionPerformed(ActionEvent e) {
						skipToEnd();
					}});
				 
				 
		    cursorPosLabel = new JLabel();
		    
		    wpageEditButton = new JButton("编辑文字页面部分");
		    wpageEditButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					skipToEnd();
					WorkbenchForWebview.DataProvider provider = new WorkbenchForWebview.DataProvider() { 
						@Override
						public void setResult(WebviewPage afillin) {
							 wpage = afillin;
						}
						 
						@Override
						public WebviewPage getData() { 
							return wpage;
						}
					};
					WorkbenchForWebview dialog = new WorkbenchForWebview(provider);
					dialog.setVisible(true);
					 
				}});
		    tipsEditButton = new JButton("编辑小贴士部分");
		    tipsEditButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					skipToEnd();
					WorkbenchForWebview.DataProvider provider = new WorkbenchForWebview.DataProvider() { 
						@Override
						public void setResult(WebviewPage afillin) {
							 tippage = afillin;
						}
						 
						@Override
						public WebviewPage getData() { 
							return tippage;
						}
					};
					WorkbenchForWebview dialog = new WorkbenchForWebview(provider);
					dialog.setVisible(true);
					 
				}});
		    
		    
		    fillinEditButton = new JButton("编辑测试题部分");
		    fillinEditButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					skipToEnd();
					WorkbenchForFillin.DataProvider provider = new WorkbenchForFillin.DataProvider() { 
						@Override
						public void setResult(String content, List<Fillin> afillin) {
							 fillins = afillin;
							 quizContent = content;
						}
						
						@Override
						public String getFullCode() { 
							if( quizContent != null && quizContent.length()>0)
								return quizContent;
							return workspace.getText();
						}
						
						@Override
						public List<Fillin> getFillin() { 
							return fillins;
						}
					};
					WorkbenchForFillin dialog = new WorkbenchForFillin(provider);
					dialog.setVisible(true);
					 
				}});
		    debugEditButton = new JButton("编辑模拟调试部分");
		    debugEditButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					skipToEnd();
					WorkbenchForDebug.DataProvider provider = new WorkbenchForDebug.DataProvider() {

						@Override
						public String getCode() {
						 	return workspace.getText();
						}

						@Override
						public Debug getDebug() {
						    return debug;
						}

						@Override
						public void setResult(Debug adebug) {
							 debug = adebug;
						} 
						 
					};
					WorkbenchForDebug dialog = new WorkbenchForDebug(provider);
					dialog.setVisible(true);
					 
				}});
		    
		    flowChartButton = new JButton("编辑流程图部分");
		    flowChartButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					skipToEnd();
					WorkbenchForFlowChart.DataProvider provider = new WorkbenchForFlowChart.DataProvider() {
  

						@Override
						public FlowChart getFlowChart(int num) {
							if( num == 0) return flowChart1;
						    return num == 2 ? flowChart2 : flowChart3;
						}

						@Override
						public void setResult(FlowChart aflowChart, int num) {
							if( num == 0)   flowChart1 = aflowChart;
							if( num == 1)   flowChart2 = aflowChart;
							if( num == 2)   flowChart3 = aflowChart;
						} 
						 
					};
					WorkbenchForFlowChart dialog = new WorkbenchForFlowChart(provider);
					dialog.setVisible(true);
					 
				}});
		    
			JPanel buttonRow = new JPanel(); 
			buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
		 	buttonRow.setBorder(  BorderFactory.createEmptyBorder(2, 4, 4, 2)); 
			buttonRow.add(newRecordButton, Box.LEFT_ALIGNMENT); 
			buttonRow.add(recordStateRadioButton, Box.LEFT_ALIGNMENT); 
			buttonRow.add(editStateRadioButton, Box.LEFT_ALIGNMENT);  
			 
			buttonRow.add(Box.createHorizontalStrut(100));
			buttonRow.add(previewButton, Box.RIGHT_ALIGNMENT);
			buttonRow.add(toEndButton, Box.RIGHT_ALIGNMENT); 
			buttonRow.add(Box.createHorizontalStrut(100));
			buttonRow.add(new JLabel("Cursor位置:  "));
			buttonRow.add(cursorPosLabel);
			buttonRow.add(Box.createHorizontalStrut(100));
			buttonRow.add(Box.createHorizontalGlue());
			buttonRow.add(wpageEditButton, Box.RIGHT_ALIGNMENT);
			buttonRow.add(fillinEditButton, Box.RIGHT_ALIGNMENT);
			buttonRow.add(debugEditButton, Box.RIGHT_ALIGNMENT);
			buttonRow.add(flowChartButton, Box.RIGHT_ALIGNMENT);
			buttonRow.add(tipsEditButton, Box.RIGHT_ALIGNMENT);
			
			 insertMessageButton = new JButton("插入新提示");
			 insertMessageButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					editStateRadioButton.setSelected(true);
					curMessageRecord = new Record("m");
					records.add(curMessageRecord);
					startPosField.setText( (parseInt(cursorPosLabel.getText())-1)+"");
					phaseListModel.rebuildList();
				}});
			startPosField = new JTextField(4);
			endPosField = new JTextField(4);
			stopTimeField = new JTextField(4);
			keepTimeField = new JTextField(4);
			messageField = new JTextField(50);
			saveMessageButton = new JButton("保存");
			saveMessageButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					if( curMessageRecord == null )
						return;
					 setMessageRecordFromUI(); 
					phaseListModel.rebuildList();
				}});
			deleteMessageButton = new JButton("删除");
			deleteMessageButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					if( curMessageRecord == null )
						return;
					records.remove(curMessageRecord); 
					phaseListModel.rebuildList();
				}});
			 
			JPanel editRow = new JPanel(); 
			editRow.setLayout(new BoxLayout(editRow, BoxLayout.Y_AXIS));
	 
			startPosField.setPreferredSize(new Dimension(120, 32));
			startPosField.setMaximumSize(new Dimension(120, 32));
			endPosField.setPreferredSize(new Dimension(120, 32));
			endPosField.setMaximumSize(new Dimension(120, 32));
			stopTimeField.setPreferredSize(new Dimension(120, 32));
			stopTimeField.setMaximumSize(new Dimension(120, 32));
			keepTimeField.setPreferredSize(new Dimension(120, 32));
			keepTimeField.setMaximumSize(new Dimension(120, 32));
		
			editRow.add(PropertyUIHelper.createRow("",
					new JLabel("开始光标"), startPosField,
					new JLabel("结束光标"), endPosField,
					new JLabel("播放停留时间"), stopTimeField,
					new JLabel("文字保留时间"),keepTimeField,
					new JLabel("文字"), messageField
					));
			
			editRow.add(PropertyUIHelper.createRow("",  insertMessageButton, saveMessageButton, deleteMessageButton));	
			 
			
			//
			 insertOutputButton = new JButton("插入新输出");
			 insertOutputButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					editStateRadioButton.setSelected(true);
					curMessageRecord = new Record("m");
					curMessageRecord.isoutput = true;
					records.add(curMessageRecord);
					phaseListModel.rebuildList();
				}});
		 
			ouputstopTimeField = new JTextField(4);
			ouputstopTimeField.setPreferredSize(new Dimension(160,32));
			ouputstopTimeField.setMaximumSize(new Dimension(160,32));
			outputmessageField = new JTextField(50);
			saveOutputButton = new JButton("保存");
			saveOutputButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					if( curMessageRecord == null )
						return;
					 setMessageRecordFromUI(); 
					phaseListModel.rebuildList();
				}});
			deleteOutputButton = new JButton("删除");
			deleteOutputButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) { 
					if( curMessageRecord == null )
						return;
					records.remove(curMessageRecord); 
					phaseListModel.rebuildList();
				}});
			 
			JPanel outputeditRow = new JPanel(); 
			outputeditRow.setLayout(new BoxLayout(outputeditRow, BoxLayout.Y_AXIS));  
			outputeditRow.add(PropertyUIHelper.createRow("", new JLabel("播放停留时间"), ouputstopTimeField, new JLabel("输出"), outputmessageField)); 
 
			outputeditRow.add(Box.createVerticalGlue() );
			outputeditRow.add(PropertyUIHelper.createRow(insertOutputButton, saveOutputButton, deleteOutputButton, false, null));
 
			
			titleField = new JTextField();
			descField = new JTextField();
			JPanel titleRow = new JPanel(); 
			titleRow.setLayout(new BoxLayout(titleRow, BoxLayout.X_AXIS));
			titleRow.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4),
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
							BorderFactory.createEmptyBorder(4, 4, 4, 4) )));
			
			titleRow.add(new JLabel("标题"));
			titleRow.add(titleField);
			titleRow.add(new JLabel("描述"));
			titleRow.add(descField);
			
			
			JTabbedPane  tabpane = new JTabbedPane(); 
			tabpane.add("选择题", quizPanel);
			tabpane.add("输出显示", outputeditRow);
			tabpane.add("备注/提示", editRow);
			 
			
			 add(titleRow, BorderLayout.NORTH); 
			
			JPanel bottomRow = new JPanel();
			add(bottomRow, BorderLayout.SOUTH);
			bottomRow.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			
			bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.Y_AXIS));
			bottomRow.add(PropertyUIHelper.createLine(2)); 
			
			bottomRow.add(tabpane); 
			
			bottomRow.add(PropertyUIHelper.createTitleRow("控制", true)); 
			bottomRow.add(buttonRow);  
			bottomRow.add(PropertyUIHelper.createLine(2));
			
			bottomRow.add(PropertyUIHelper.createRow(importButton, saveAsButton, savetButton, false, null));
	         setSize(1200, 800);
		     setVisible(true);
	}
	public void updateMsgPosition(int pos, int length, boolean add) {
		for(int i = 0;  i < records.size(); i++) {
		   Record r = records.get(i);
		   if( r.startPos > pos)
			   break;
		   if( r.prefix.equals("m") && !r.isoutput && r.startPos > 0 && r.startPos >= 0 ) {
			   if( add)
			      r.startPos2 += length;
			   else {
				  if( r.startPos2 >= pos && r.startPos2 <= pos + length) {
					  r.startPos2 = -1;
				  } else {
					  r.startPos2 -= length;  
				  } 
			   }
		   }
		}
	}
	private void skipToEnd() {
		editStateRadioButton.setSelected(true);
		workspace.setEditable(false);
	//	optimizeCode2();
		if( player == null)
		   player = new CodePlayer(Workbench.this, workspace);
		player.skipToEnd(records);
	}
	protected void optimizeCode2() {
		List<Record> temp = new ArrayList<>();
		 for(int i = 0;  i < records.size(); i++) {
			 Record r = records.get(i);
			 temp.add(r);
			 int indexInTemp = temp.size()-1;
			 if( r.prefix .equals("i") && !r.bulk ) {
				 int pos = -1;
				 int splitstart = -1;
				 for(int j = 0 ; j < r.content.length(); j++) {
					 if( r.content.charAt(j) == ' ' || r.content.charAt(j) == '\n' ) {
						 if( pos == -1) {
							 pos = j;
						 }
						 else if ( j == r.content.length()-1 && j - pos > 2) {
							 
							 Record bk = new Record("i");
							 bk.bulk = true;
							 bk.content = r.content.substring(pos );
							 
							 r.content = r.content.substring(0,pos);
							 bk.startPos = r.startPos + pos;
							 
							 temp.add(  bk);
						 }
					 } else {
						 if( pos != -1 && j - pos > 2) {
							 Record bk = new Record("i");
							 bk.bulk = true;
							 bk.content = r.content.substring(pos, j-1);
							 Record third = new Record("i");
							 third.content = r.content.substring(j);
							 
							 r.content = r.content.substring(0,pos);
							 bk.startPos = r.startPos + pos;
							 third.startPos = r.startPos + j;
							 
							 temp.add(  bk);
							 temp.add(  third);
							 
						 }
						 pos = -1; 
					 }
					
				 }
			 }  
		 }
		 this.records = temp;
	}
	public void setUIFromMessageRecord() { 
			startPosField.setText( ""); 
			endPosField.setText( ""); 
			stopTimeField.setText( ""); 
			keepTimeField .setText( ""); 
			messageField .setText( ""); 
			ouputstopTimeField .setText( ""); 
			outputmessageField .setText( ""); 
		 
			if( curMessageRecord == null )
				return;
			if( curMessageRecord.isoutput) {
				ouputstopTimeField .setText(curMessageRecord.stopTime +""); 
				outputmessageField .setText( curMessageRecord.content); 
			} else {
				startPosField.setText(curMessageRecord.startPos +""); 
				endPosField.setText(curMessageRecord.endPos +""); 
				stopTimeField.setText(curMessageRecord.stopTime +""); 
				keepTimeField .setText(curMessageRecord.keepTime +""); 
				messageField .setText(curMessageRecord.content +""); 	
			}  
	}
	public void setMessageRecordFromUI() {
		if( curMessageRecord == null)
			return;
		if( curMessageRecord.isoutput) {
			curMessageRecord.keepTime = parseInt(ouputstopTimeField.getText().trim());
			curMessageRecord.content = outputmessageField.getText().trim();
		} else {
			curMessageRecord.startPos = parseInt(startPosField.getText().trim());
			if( curMessageRecord.startPos2 == 0 ) {
				curMessageRecord.startPos2 = curMessageRecord.startPos ;
			}
			curMessageRecord.endPos = parseInt(endPosField.getText().trim());
			curMessageRecord.stopTime = parseInt(stopTimeField.getText().trim());
			curMessageRecord.keepTime = parseInt(keepTimeField.getText().trim());
			curMessageRecord.content = messageField.getText().trim();
		} 
	}
	public void setUIFromQuizRecord() { 
		if( curQuizRecord == null ) {
			popupquizEditor.setData( new ArrayList<String>(), "");
			popupquizbodyField.setText(""); 
			popupquizFeedbackField.setText("");  
		} else {
			popupquizEditor.setData( curQuizRecord.op, curQuizRecord.op.isEmpty()?"":curQuizRecord.op.get(curQuizRecord.as));
			popupquizbodyField.setText(curQuizRecord.content); 
			popupquizFeedbackField.setText(curQuizRecord.feedback);  	
		}

	}
	public void setQuizRecordFromUI() { 
		if( curQuizRecord == null ) {
			return;
		}
		curQuizRecord.op =popupquizEditor.getData();
		String c = popupquizEditor.getCorrected();
		if( curQuizRecord.op.isEmpty() || c == null || c.length() ==0)
		   curQuizRecord.as = -1;
		else
			curQuizRecord.as = curQuizRecord.op.indexOf(c);
		curQuizRecord.content = popupquizbodyField.getText().trim(); 
		curQuizRecord.feedback = popupquizFeedbackField.getText().trim();  
	}
	
	public int parseInt(String text) {
		try {
			return Integer.parseInt(text);
		}catch(Exception ex) {
			return 0;
		}
	}
	
	class SplitDialog extends JDialog  implements    PropertyChangeListener{
		private JOptionPane optionPane;
		Object[] options = {"保存", "取消"};
		int splitpoint = -1;
		Record target;
		SplitDialog(Record target){
		    this.target = target;
			JPanel cpanel = new JPanel();
			cpanel.setLayout(new BorderLayout());
			cpanel.add(new JLabel("选择分裂点"), BorderLayout.NORTH);
			JTextArea area = new JTextArea();
			area.setText(target.content);
			area.addCaretListener(new CaretListener() { 
				public void caretUpdate(CaretEvent e) {
					splitpoint = e.getMark();
				}});
			cpanel.add(area, BorderLayout.CENTER);
			 optionPane = new JOptionPane(cpanel,
		                                    JOptionPane.PLAIN_MESSAGE,
		                                    JOptionPane.YES_NO_OPTION,
		                                    null,
		                                    options,
		                                    options[0]);
		    setContentPane(optionPane);
		    optionPane.addPropertyChangeListener(this);
		}
		
		 public void propertyChange(PropertyChangeEvent e) {
		        String prop = e.getPropertyName();
		 
		        if (isVisible()
		         && (e.getSource() == optionPane)
		         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
		             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
		            Object value = optionPane.getValue(); 
		            if (value == JOptionPane.UNINITIALIZED_VALUE) { 
		                return;
		            } 
		            optionPane.setValue(
		                    JOptionPane.UNINITIALIZED_VALUE);
		 
		            if (options[0].equals(value)) {
		            	if( splitpoint > 0 ) {
		            		Record newone = new Record("i");
		            		newone.startPos = target.startPos + splitpoint;
		            		newone.content = target.content.substring(splitpoint);
		            		target.content = target.content.substring(0, splitpoint);
		            		int  index = records.indexOf(target);
		            		records.add(index+1, newone);
		            		phaseListModel.rebuildList();
		            	}
		            	setVisible(false);
		            } else {   
		            	   setVisible(false);
		            }
		        }
		    } 
	}
	
	 
	
	class PhaseListModel extends AbstractListModel  {
		private static final long serialVersionUID = 1L; 
		public PhaseListModel() {
			 
		} 
		private void rebuildList() {  
			this.fireContentsChanged(this, 0, getSize());
		}

		public int getSize() {
			 return records.size();
		}

		public Record getElementAt(int index) {
			 return records.get(index);
		}
      
	}
	
	

}
