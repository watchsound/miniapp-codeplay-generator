package org.r9.workspace.ui.dialog.chatgpt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pi.code.tool.util.CodePlayerFrame;
import com.pi.code.tool.util.LabelButton;
import com.pi.code.tool.util.PropertyUIHelper;
 

public   class PromptLookupPanel extends JPanel{ 
	private static final long serialVersionUID = 1L;
	
	public static interface Callback {
		void onSelection(Prompt p);
	}
	 

	private JComboBox<String> categoryComboBox;

	  

	private PromptListModel promptListModel;

	private JTextField promptNameField;

	private JTextPane promptBody;

	private LabelButton createNewButton;

	private LabelButton saveButton;

	protected Prompt curPrompt;

	private LabelButton deleteButton;

	private LabelButton selectButton;

	private JTextArea contextQuestionField;

	private JTextPane contextAnswerField;

	private JPanel pinnedPane ;

	private LabelButton pinButton;
	/**
	 * 
	 * @param context
	 * @param rb2
	 * @param callback if callback is not null, we are in pick up model, 
	 *                 editing button will not showup
	 */
	public PromptLookupPanel( 
			final Callback callback) { 
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createCompoundBorder(
				    BorderFactory.createLineBorder(Color.GRAY),
				    BorderFactory.createEmptyBorder(4,4,4,4)
				)
				));
	    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    promptListModel = new PromptListModel();
	    
	    final boolean lookupOnly = callback != null;
	    
	    if( lookupOnly ) {
	    	JTextField filterField = new JTextField(60);
	    	 LabelButton filter = new LabelButton( "filter" );
	    	 filter.setPreferredSize(new Dimension(45,35));
	    	 filter.setActionListener(new ActionListener() { 
		 			public void actionPerformed(ActionEvent e) {
		 				   String searchText = filterField.getText();
		                   promptListModel.filter(searchText);
		 			}}); 
		 	    add(PropertyUIHelper.createRow( "", filterField, filter)); 
	    }
	    else {
	    	 Set<String> saveCats = PromptRegister.instance.getData().generateRoleList();
	 	    Vector<String> saveCats2 = new Vector<>(saveCats);
	 	    final DefaultComboBoxModel<String> categories = new DefaultComboBoxModel<>(saveCats2);
	 	    categoryComboBox = new JComboBox<String>(categories);
	 	    categoryComboBox.setEditable(true);
	 	    LabelButton addCat = new LabelButton( "add" );
	 	    addCat.setPreferredSize(new Dimension(45,35));
	 	    addCat.setActionListener(new ActionListener() { 
	 			public void actionPerformed(ActionEvent e) {
	 				  String searchText = (String) categoryComboBox.getSelectedItem();
	 				  if( searchText == null || searchText.length() ==0 )
	 					 searchText = (String) categoryComboBox.getEditor().getItem();
	 				  if( searchText == null )
	 					  searchText = "";
	 	              searchText = searchText.trim().toLowerCase(); 
	                    DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) categoryComboBox.getModel();
	                    if (!searchText.isEmpty() && model.getIndexOf(searchText) == -1) {
	                       model.addElement(searchText);
	                   } 
	                    promptListModel.rebuildList(searchText, true);
	 			}}); 
	 	    add(PropertyUIHelper.createRow( "category" , categoryComboBox, addCat)); 
	    } 
	    
		final JList<Prompt> promptListView = new JList<>(promptListModel);
		
		JScrollPane promptListScrolPane;
		promptListScrolPane = new JScrollPane(promptListView);
		promptListScrolPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		promptListScrolPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		promptListScrolPane.setPreferredSize(new Dimension(600,250));
		add( promptListScrolPane );
		

		promptListView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						ListSelectionModel lsm = (ListSelectionModel) e.getSource();
						if (!lsm.isSelectionEmpty()) { 
							int row = lsm.getLeadSelectionIndex();
							if( row >= 0){
								curPrompt = promptListModel.get(row); 
								 promptNameField.setText(curPrompt.getName());
								 promptBody.setText(curPrompt.getContent()); 
									contextQuestionField.setText(curPrompt.getPreQuestion());
									contextAnswerField.setText(curPrompt.getPreAnswer()); 
							}
						}
					}
				}
			});
	     
		pinnedPane  = new JPanel();
		pinnedPane.setLayout(new BoxLayout(pinnedPane, BoxLayout.Y_AXIS));
		add(pinnedPane);
		populatePinnedPane(true);
		 add(PropertyUIHelper.createLine(1)); 
	     
	    
		 add(PropertyUIHelper.createTitleRow( "name" , false)); 
		 promptNameField = new JTextField(60);
		 add(promptNameField);
		 promptNameField.setEditable(!lookupOnly);
		 
		 
		 JPanel contextPane = new JPanel();
		 contextPane.setLayout(new BoxLayout(contextPane, BoxLayout.Y_AXIS));
		 contextPane.setBorder(BorderFactory.createTitledBorder( "context" ));
		 add(contextPane);
		 
		 contextPane. add(PropertyUIHelper.createTitleRow( "question" , false));
		  contextQuestionField = new JTextArea(4, 60);
		  contextQuestionField.setLineWrap(true);
		  contextPane.add(contextQuestionField);
		  contextQuestionField.setEditable(!lookupOnly);
		  ImageIcon mIcon = new ImageIcon(PromptLookupPanel.class.getResource("chatgpt.png"));
		  contextPane.add(PropertyUIHelper.createRow( "answer" , new JLabel( mIcon)  )); 
		  
		  
		  contextAnswerField = new JTextPane( );
		    contextAnswerField.setPreferredSize(new Dimension(400,100));
		     JScrollPane canswerScroll = new JScrollPane(contextAnswerField);
		     contextPane.   add(canswerScroll);
		     contextAnswerField.setEditable(!lookupOnly);
		  
		  
		 add(PropertyUIHelper.createTitleRow( "content" , false));   
		 
		 
	     promptBody = new JTextPane( );
	     promptBody.setEditable(!lookupOnly); 
	     promptBody.setPreferredSize(new Dimension(600,200));
	     JScrollPane answerScroll = new JScrollPane(promptBody);
	     add( answerScroll );
	     add(PropertyUIHelper.createLine());
	     createNewButton = new LabelButton( "new" );
	     createNewButton.setActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				contextQuestionField.setText("");
				contextAnswerField.setText(""); 
				 promptNameField.setText("");
				 promptBody.setText("");
				 curPrompt = new Prompt();
				 promptListModel.add(curPrompt);
			}});
	     saveButton = new LabelButton( "save" );
	     saveButton.setActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					if( curPrompt == null ) return;
					String name = promptNameField.getText();
					String body = promptBody.getText();
					if( name.length() == 0 || body.length() == 0 )
						return;
					curPrompt.setName(name);
					curPrompt.setContent(body);
					curPrompt.setPreAnswer(contextAnswerField.getText());
					curPrompt.setPreQuestion(contextQuestionField.getText());
					 
					PromptRegister.instance.save(); 
				}});
	     deleteButton = new LabelButton( "delete" );
	     deleteButton.setActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					if( curPrompt == null ) return;
					PromptRegister.instance.getData().removePrompt(curPrompt);
					PromptRegister.instance.save(); 
					curPrompt = null;
					promptListModel.rebuildList();
				}});
	     selectButton = new LabelButton(  "select" );
	     selectButton.setActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					 if( callback != null )
						 callback.onSelection(curPrompt);
				}});
	       mIcon = new ImageIcon(PromptLookupPanel.class.getResource("indicate2.png"));
	     pinButton = new LabelButton("", mIcon);
	     pinButton.setActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					 if( curPrompt != null && !curPrompt.isPinned()) {
						 curPrompt.setPinned(true);
						 populatePinnedPane(false);
					 }
						 
				}});
	     if( callback == null) {
	    //	 List<JComponent> left = new ArrayList<>();
	    //	 left.add(pinButton);
	    	 List<JComponent> right = new ArrayList<>();
	    	 
	    	 right.add( pinButton );
	    	 right.add( createNewButton );
	    	 right.add( saveButton );
	    	 right.add( deleteButton );
		    	 
	        add(PropertyUIHelper.createRow("", right ));
	     }  else {
	        add(PropertyUIHelper.createRow("", selectButton));
	     }
	     
	     promptListModel.rebuildList("", false);
	 
	}  
	
	
	
	
	private void populatePinnedPane(boolean first) {
		 this.pinnedPane.removeAll();
		 this.pinnedPane.setLayout(new BoxLayout(pinnedPane, BoxLayout.Y_AXIS));
		 ImageIcon mIcon = new ImageIcon(PromptLookupPanel.class.getResource("indicate2.png"));
		 for(final Prompt p : PromptRegister.instance.getData().pinned()) {
			 final LabelButton b = new LabelButton("", mIcon);
			 b.setActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					 p.setPinned(false);
					 populatePinnedPane(false);
				}});
			 final LabelButton b2 = new LabelButton( p.getName() );
			 b2.setActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					 curPrompt = p; 
					 promptNameField.setText(curPrompt.getName());
					 promptBody.setText(curPrompt.getContent()); 
						contextQuestionField.setText(curPrompt.getPreQuestion());
						contextAnswerField.setText(curPrompt.getPreAnswer()); 
				}});
			 this.pinnedPane.add(PropertyUIHelper.createRow(b,  b2, true, null) );
		 }
		 if(!first) {
			 this.invalidate();
			 this.revalidate();
		 }
	}




	public class PromptListModel extends AbstractListModel<Prompt> {
		private static final long serialVersionUID = 1L;

		 
		String category;
		List<Prompt> data = new ArrayList<>();
		public PromptListModel() {
			 
		}

		public void rebuildList() {
			this.fireContentsChanged(this, 0, getSize());
		}
		
		public void rebuildList(String category, boolean createNew) {
			this.category = category;
			this.data = PromptRegister.instance.getData().getPrompts(category, createNew);
			if( this.data == null )
				this.data = new ArrayList<>();
			rebuildList();
		}
		
		public void filter(String filter ) { 
			this.data = PromptRegister.instance.getData().filter(filter);
			if( this.data == null )
				this.data = new ArrayList<>();
			rebuildList();
		}

		public void delete(int index) {
			if( index >= data.size() ) return;
			data.remove(index);
			rebuildList();
		}
		public void delete(Prompt p) {
			int index = data.indexOf(p);
			if( index < 0 ) return;
			delete(index);
		}
		public void add(Prompt p) {
			this.data.add(p);
			rebuildList();
		}

		public Prompt get(int index) {
			return this.data.get(index);
		}

		public void setValueAt(int index, Prompt p) {
		    this.data.set(index, p);
			rebuildList();
		}

		public int getSize() {
			return this.data.size();
		}

		public Prompt getElementAt(int index) {
			return this.get(index);
		}
	}

}