package org.r9.workspace.ui.dialog.chatgpt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import com.pi.code.tool.codeplayer.Workbench;
import com.pi.code.tool.util.LabelButton;
import com.pi.code.tool.util.PropertyUIHelper;
import com.pi.code.tool.util.StringUtils;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;

public   class ChatGTPPanel extends JPanel{ 
		private static final long serialVersionUID = 1L;
 	
	 
		private JComboBox<String> roleComboBox;

		private JPanel contextPane;

		private LabelButton showhideContextButton;

		private boolean showContext;

		private JTextArea contextQuestionField;

		private JTextPane contextAnswerField;

		private PromptTextPane questionField;

		private JTextPane answerField;

		private LabelButton savePromptButton;

		private LabelButton askGPTButton;

		private LabelButton copyButton;

		private LabelButton asContextButton;
		 
		public ChatGTPPanel( ) { 
			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(4, 4, 4, 4),
					BorderFactory.createCompoundBorder( 
					    BorderFactory.createLineBorder(Color.GRAY),
					    BorderFactory.createEmptyBorder(4,4,4,4)
					)
					));
		    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		    
		    
		    Set<String> savedRoles = PromptRegister.instance.getData().generateRoleList();
		    Vector<String> savedRoles2 = new Vector<>(savedRoles);
		    final DefaultComboBoxModel<String> roles = new DefaultComboBoxModel<>(savedRoles2);
		    roleComboBox = new JComboBox<String>(roles);
		    roleComboBox.setPreferredSize(new Dimension(250,26));
		    roleComboBox.setEditable(true);
		    LabelButton loadSaved = new LabelButton( "loadSaved");
		    loadSaved.setPreferredSize(new Dimension(45,35));
		    loadSaved.setActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) { 
					PromptLookupPanel.Callback callback = new PromptLookupPanel.Callback() { 
						public void onSelection(Prompt p) {
							 roles.addElement(p.getRole());
							 roleComboBox.setSelectedItem(p.getRole());
							 if( p.getPreAnswer() != null && p.getPreQuestion() != null ) {
								 showContext = true;
								 contextPane.setVisible(showContext);
								 contextQuestionField.setText(p.getPreQuestion());
								 contextAnswerField.setText(p.getPreAnswer()); 
							 } else {
								 showContext = false;
								 contextPane.setVisible(showContext);
							 }
							 questionField.setText(p.getContent());
							 answerField.setText("");
						}
					};
					PromptLookupPanel panel = new PromptLookupPanel( callback);
					JPopupMenu popup = new JPopupMenu();
					popup.add(panel);
					popup.show(loadSaved, 0, 0);
				}});
		    
		    this.add(PropertyUIHelper.createRow( "role", roleComboBox  , loadSaved));
		    showhideContextButton = new LabelButton( "context" );
		    showhideContextButton.setFont( this.getFont().deriveFont(16));
		    showhideContextButton.setPreferredSize(new Dimension(60,30)); 
		    this.add(PropertyUIHelper.createTitleRow("", false, null, showhideContextButton));
		    showContext = false;
		    showhideContextButton.setActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					 showContext = !showContext;
					 contextPane.setVisible(showContext);
				}});
		    contextPane = new JPanel();
		    contextPane.setLayout(new BoxLayout(contextPane, BoxLayout.Y_AXIS));
		    this.add(contextPane);
		    contextPane.setVisible(showContext);
		    contextPane.add(PropertyUIHelper.createTitleRow( "question" , false));
		    contextQuestionField = new JTextArea(4, 50);
		    contextPane.add(contextQuestionField);
		    contextQuestionField.setLineWrap(true);
		    contextPane.add(PropertyUIHelper.createTitleRow( "answer")  ); 
		    
		    contextAnswerField = new JTextPane( );
		    contextAnswerField.setPreferredSize(new Dimension(400,100));
		     JScrollPane canswerScroll = new JScrollPane(contextAnswerField);
		     contextPane.add(canswerScroll);
		    
		    
		    
		    add(PropertyUIHelper.createLine(1));
		     add(PropertyUIHelper.createTitleRow( "question" , false));
		     questionField = new PromptTextPane(   );
		     add(questionField);
		     add(PropertyUIHelper.createTitleRow( "answer") );
		     answerField = new JTextPane( );
		     answerField.setPreferredSize(new Dimension(400,200));
		     JScrollPane answerScroll = new JScrollPane(answerField);
		     add(answerScroll);
		    
		     savePromptButton = new LabelButton( "saveAsPrompt" );
		     savePromptButton.setActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					  String searchText = (String) roleComboBox.getSelectedItem();
					  if( searchText == null || searchText.length() ==0 )
	 					 searchText = (String) roleComboBox.getEditor().getItem();
	 				  if( searchText == null )
	 					  searchText = "";
		 				  
		              searchText = searchText.trim().toLowerCase(); 
	                   DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) roleComboBox.getModel();
	                   if (!searchText.isEmpty() && model.getIndexOf(searchText) == -1) {
	                      model.addElement(searchText);
	                  } 
	                  String name = JOptionPane.showInputDialog( "givepromptname" );
	                  if( name == null || name.length() == 0)
	                	  name = searchText;
	                  Prompt p = new Prompt();
	                  p.setRole( searchText);
	                  p.setName(name);
	                  if( showContext ) {
	                	  p.setPreQuestion(contextQuestionField.getText());
	                	  p.setPreAnswer(contextAnswerField.getText());
	                  }
	                  p.setContent(questionField.getText());
	                  
	                  PromptRegister.instance.getData().addPrompt(  p);
	                  PromptRegister.instance.save();
				}});
		     
		     askGPTButton = new LabelButton( "askGPT" );
		     askGPTButton.setActionListener(new ActionListener() { 
			    public void actionPerformed(ActionEvent e) {
					askGPT();		  
				}});
		     copyButton = new LabelButton( "copy" );
		     copyButton.setActionListener(new ActionListener() { 
			    public void actionPerformed(ActionEvent e) {
			    	String content = answerField.getSelectedText(); 
			    	if(  StringUtils.isEmpty(content)) 
			    		content = answerField.getText();
			    	if(  StringUtils.isEmpty(content)) 
			    		return; 
				//	R9Clipboard.sharedInstance.setCodeStyle(content, 20);
					StringSelection stringSelection = new StringSelection(content);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(stringSelection, null);
				}});
		      
		     asContextButton = new LabelButton( "asContext"  );
		     asContextButton.setActionListener(new ActionListener() { 
			    public void actionPerformed(ActionEvent e) {
			    	String content = answerField.getText();
			    	String question = questionField.getText();
			    	 contextQuestionField.setText(question);
					 contextAnswerField.setText(content); 
					 answerField.setText("");
					 questionField.setText(""); 
				}});
		     
		     
		     add(PropertyUIHelper.createLine(1));
		     List<JComponent> left = new ArrayList<>();
	    	 left.add(savePromptButton);
	    	 left.add(copyButton);
	    	 left.add(asContextButton);
	    	 left.add( askGPTButton ); 
	    	// List<JComponent> right = new ArrayList<>();
	    //	 right.add( askGPTButton ); 
		    	  
		     add(PropertyUIHelper.createRow("", left ));
		}  
		
		public void askGPT() {
			String key = Workbench.CHATGPT_KEY;
			if( key == null || key.length() == 0 ) return;
			
			 OpenAiClient openAiClient = OpenAiClient.builder()
		                .apiKey(Arrays.asList(  key  )) 
		                .build();
		                //聊天模型：gpt-3.5
			 
		     Message message1 = Message.builder().role(Message.Role.SYSTEM).content( (String)roleComboBox.getSelectedItem()).build();
		     String preQuestion = contextQuestionField.getText();
		     String preAnswer = contextAnswerField.getText();
		     if( showContext && preQuestion.length()>0 && preAnswer.length() >0 ) {
		    	 Message message2 = Message.builder().role(Message.Role.USER).content( preQuestion ).build();
		    	 Message message3 = Message.builder().role(Message.Role.ASSISTANT).content( preAnswer ).build();
		    	 Message message4 = Message.builder().role(Message.Role.USER).content( questionField.getText() ).build();
		    	 ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message1,message2, message3, message4 )).build();
			        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
			        chatCompletionResponse.getChoices().forEach(e -> {  
			        	parseGPTResult(e.getMessage());
			        });
             } else {
            	    Message message4 = Message.builder().role(Message.Role.USER).content( questionField.getText() ).build();
		    	    ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message1,  message4 )).build();
			        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
			        chatCompletionResponse.getChoices().forEach(e -> {
			        	 parseGPTResult(e.getMessage());
			        });
             } 
		}
		
		private void parseGPTResult(Message m) {
			answerField.setText(m.getContent());
		}
 }