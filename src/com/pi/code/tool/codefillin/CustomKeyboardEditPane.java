package com.pi.code.tool.codefillin;
  

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pi.code.tool.util.PageColors; 

public class CustomKeyboardEditPane extends JPanel{
 
	private CustomKeyboardValueModel keyListModel;
	private JList keyListView;
	private JScrollPane phaseScrolPane;
	private JButton addNewButton;
	private JButton deleteSelectedButton;
	//private JPopupMenu popupMenu;
	private ImageIcon correctIcon;
	private JTextField inputField;
	private JButton deleteAllButton;

	private List<String> customKeySet;
	private String corrected;
	private JButton toggleCorrectdButton;
	public CustomKeyboardEditPane(final boolean horizontal, final boolean canEditCorrectOne){
		URL url = CustomKeyboardEditPane.class.getResource( "_button_ok.png" );
		try {
			BufferedImage simage = ImageIO.read( url ); 
			correctIcon = new ImageIcon(ImageUtil.resizeImage(simage, 16, 16)); 
		} catch (Exception e) {
			 
		}
		keyListModel = new CustomKeyboardValueModel();
		keyListView = new JList(keyListModel);
		keyListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	 	keyListView.setCellRenderer(new CustomKeyCellRenderer());
	 	keyListView.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							ListSelectionModel lsm = (ListSelectionModel) e
									.getSource();
							if (!lsm.isSelectionEmpty()) {
							     String s = (String)keyListView.getSelectedValue();
							     inputField.setText(s);
							}
						}
					}
				});
		 
	 	keyListView.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	 	keyListView.setVisibleRowCount(-1);
	  	phaseScrolPane = new JScrollPane(keyListView);
		phaseScrolPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		phaseScrolPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		inputField = new JTextField(30);
		
		addNewButton = new JButton("添加/修改");
		addNewButton.addActionListener(new ActionListener(){ 
			public void actionPerformed(ActionEvent e) {
				int selected = keyListView.getSelectedIndex();
				if( selected >= 0) {
					if( inputField.getText().length() > 0)
						keyListModel.setValueAt(selected, inputField.getText());
					else
					    keyListModel.delete(selected);
				} else {
					if( inputField.getText().length() > 0)
					    keyListModel.add(inputField.getText());
				}
				inputField.setText("");
				keyListView.clearSelection();
			}});
		if( canEditCorrectOne ) {
			toggleCorrectdButton = new JButton("正确");
			toggleCorrectdButton.addActionListener(new ActionListener(){ 
				public void actionPerformed(ActionEvent e) {
					int selected = keyListView.getSelectedIndex();
					if( selected >= 0) {
						corrected = keyListModel.getElementAt(selected);
						keyListModel.rebuildList();
					}
				}});
		}

		deleteSelectedButton = new JButton("删除");
		deleteSelectedButton.addActionListener(new ActionListener(){ 
			public void actionPerformed(ActionEvent e) {
				int selected = keyListView.getSelectedIndex();
				if( selected >= 0) {
					keyListModel.delete(selected);
					inputField.setText("");
					keyListView.clearSelection();
				}
			}});
		deleteAllButton = new JButton("删除全部");
		deleteAllButton.addActionListener(new ActionListener(){ 
			public void actionPerformed(ActionEvent e) {
				keyListModel.clear();
				inputField.setText("");
				keyListView.clearSelection();
				//keyListModel.add(corrected); 
			}});

		JPanel buttonRow = new JPanel(); 
	    buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
		buttonRow.add(inputField, Box.LEFT_ALIGNMENT);
		buttonRow.add(Box.createHorizontalGlue());
		buttonRow.add(addNewButton, Box.RIGHT_ALIGNMENT);
		if( canEditCorrectOne ) {
			buttonRow.add(toggleCorrectdButton, Box.RIGHT_ALIGNMENT);
		}
		buttonRow.add(deleteSelectedButton, Box.RIGHT_ALIGNMENT);
		buttonRow.add(deleteAllButton, Box.RIGHT_ALIGNMENT);
		
		setLayout(new BorderLayout());
		add(phaseScrolPane, BorderLayout.CENTER);  
		if( horizontal )
		    add(buttonRow, BorderLayout.EAST);
		else
			 add(buttonRow, BorderLayout.SOUTH);
	}
	
	public void setData( List<String> customKeySet, String corrected){ 
		this.corrected = corrected;
		this.customKeySet = customKeySet;
		if( this.customKeySet == null)
			this.customKeySet = new ArrayList<>();
		keyListModel.rebuildList();
	
	}
	 
	public List<String> getData(){ 
		return  customKeySet;
	}
	public String getCorrected() {
		return this.corrected;
	}
	 
	  
	class CustomKeyCellRenderer implements ListCellRenderer {

			public Component getListCellRendererComponent(final JList list,
					final Object value, final int index, final boolean isSelected,
					final boolean cellHasFocus) {
				return new CellPreviewPanel(list, value, index, isSelected,
						cellHasFocus);
			}

			private class CellPreviewPanel extends JLabel {
				private static final long serialVersionUID = -4624762713662343786L;
				private JList list;
				private int index;
				private boolean isSelected;
				private boolean cellHasFocus;
				private String value; 
			 
				public CellPreviewPanel(final JList list, final Object value,
						final int index, final boolean isSelected,
						final boolean cellHasFocus) {
					setLayout(null);
					this.list = list;
					this.value = (String) value;
					this.index = index;
					this.isSelected = isSelected;
					this.cellHasFocus = cellHasFocus;
					  
					this.setText(this.value);
					Color defaultColor = Color.darkGray;
					int defaultLineWidth = 1;
					 
					if( value.equals(corrected)) {
						this.setIcon(correctIcon);
					}
					
					if (!this.isSelected)
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createEmptyBorder(2, 2, 2, 2),
								BorderFactory.createLineBorder(defaultColor, defaultLineWidth)));
					else
						setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createLineBorder(PageColors.red1, 1),
								BorderFactory.createLineBorder(defaultColor, defaultLineWidth)));

	 			} 
			}
		}
	
	 public   class CustomKeyboardValueModel extends AbstractListModel {
	 		private static final long serialVersionUID = 1L;
	 		
	 		
	 		
	 		public CustomKeyboardValueModel() {
	 			customKeySet = new ArrayList<String>();
	 		}
	 		 
	 		public  List<String>  getData(){
	 			if( customKeySet == null)
	 				customKeySet = new ArrayList<String>();
	 			return customKeySet;
	 		}
	 		
	 	 
	 	 
	 		private void rebuildList() {
	 			this.fireContentsChanged(this, 0, getSize());
	 		}

	 		public void setValueAt(int index, String value){
	 			if ( customKeySet == null)
	 				return;
	 			customKeySet.set(index, value);
	 			rebuildList();
	 		} 
	 		public void add(String key) {
	 			customKeySet.add(key);
	 			rebuildList();
	 		}
	 		public void delete(String key ){
	 			if ( customKeySet == null)
	 				return;
	 			customKeySet.remove(key);
	 			rebuildList();
	 		} 
	 		public void delete(int index ){
	 			if ( customKeySet == null)
	 				return;
	 			customKeySet.remove(index);
	 			rebuildList();
	 		} 
	 		public void clear(  ){
	 			if ( customKeySet == null)
	 				return;
	 			customKeySet.clear();
	 			rebuildList();
	 		} 
	 		
	 		public int getSize() {
	 			return customKeySet == null ? 0 : customKeySet.size();
	 		}
	 		 

	 		public String getElementAt(int index) {
	 			return customKeySet == null ? "": 
	 				customKeySet.get(index) ;//+ " (" + DifficultLevel.getName(curAspectValues.getValueAt(index)) + ")";
	 		}
	 	}
}
