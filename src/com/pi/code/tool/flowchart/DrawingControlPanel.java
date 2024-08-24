package com.pi.code.tool.flowchart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pi.code.tool.codefillin.PropertyUIHelper;
import com.pi.code.tool.flowchart.DataProcessor.ToolType;
import com.pi.code.tool.flowchart.DrawingPanel.ChartComponent;
import com.pi.code.tool.flowchart.DrawingPanel.DrawingCallback;
import com.pi.code.tool.flowchart.UndoManager.UndoAction; 
  
public class DrawingControlPanel extends JPanel implements DrawingPanel.DrawingCallback{

	public static enum DRAW_STATUS{ circle, parall, diamond, rrect,  rect,  line, letter,   select  }
	
	
	private JToggleButton dcircleButton;
	private JToggleButton dparallButton;
	private JToggleButton ddiamandButton;
	private JToggleButton drrectButton;
	private JToggleButton dletterButton;
	private JToggleButton dlineButton; 
	private JToggleButton drectButton;
	private JToggleButton dselectButton;

	private DRAW_STATUS status  = DRAW_STATUS.line;
	
	DrawingPanel drawingPanel;  
	
	
	private JButton undoBtn;
	private JButton redoBtn;
	
	private ChartComponent curSelection; 
	static List<ChartComponent> copyboard;
	  
	public DrawingControlPanel(final DrawingPanel drawingPanel ){
		drawingPanel.setCallback(this);
		
		this.drawingPanel = drawingPanel;
		
		
		
		ImageIcon icon =  getToolIcon("d-circle.png",22);
		dcircleButton  = new JToggleButton(icon);
		dcircleButton.setPreferredSize(new Dimension(55,25));
		dcircleButton.setToolTipText("圆");
		dcircleButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( dcircleButton.isSelected() ){
					 status = DRAW_STATUS.circle;
					 drawingPanel.setStatus(status);
				 }
			}});
		
	    
		icon =  getToolIcon("d-letter.png",22);
		dletterButton  = new JToggleButton(icon);
		dletterButton.setPreferredSize(new Dimension(55,25));
		dletterButton.setToolTipText("文字");
		dletterButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( dletterButton.isSelected() ){
					 status = DRAW_STATUS.letter;
					 drawingPanel.setStatus(status);
				 }
			}});
		
		icon = getToolIcon("d-line.png",22);
		dlineButton  = new JToggleButton(icon);
		dlineButton.setPreferredSize(new Dimension(55,25));
		dlineButton.setToolTipText("工作流方向");
		dlineButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( dlineButton.isSelected() ){
					 status = DRAW_STATUS.line;
					 drawingPanel.setStatus(status);
				 }
			}});
		
		icon = getToolIcon("d-parall.png",22);
		dparallButton  = new JToggleButton(icon);
		dparallButton.setPreferredSize(new Dimension(55,25));
		dparallButton.setToolTipText("输入符号");
		dparallButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( dparallButton.isSelected() ){
					 status = DRAW_STATUS.parall;
					 drawingPanel.setStatus(status);
				 }
			}});
		
		icon = getToolIcon("d-diamond.png",22);
		ddiamandButton  = new JToggleButton(icon);
		ddiamandButton.setPreferredSize(new Dimension(55,25));
		ddiamandButton.setToolTipText("判断符号");
		ddiamandButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( ddiamandButton.isSelected() ){
					 status = DRAW_STATUS.diamond;
					 drawingPanel.setStatus(status);
				 }
			}});
		
		icon = getToolIcon("d-rect.png",22);
		drectButton  = new JToggleButton(icon);
		drectButton.setPreferredSize(new Dimension(55,25));
		drectButton.setToolTipText("处理符号");
		drectButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( drectButton.isSelected() ){
					 status = DRAW_STATUS.rect;
					 drawingPanel.setStatus(status);
				 }
			}});
		
		icon = getToolIcon("d-rrect.png",22);
		drrectButton  = new JToggleButton(icon);
		drrectButton.setPreferredSize(new Dimension(55,25));
		drrectButton.setToolTipText("开始结束");
		drrectButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( drrectButton.isSelected() ){
					 status = DRAW_STATUS.rrect;
					 drawingPanel.setStatus(status);
				 }
			}});
		
		icon = getToolIcon("d-select.png",22);
		dselectButton  = new JToggleButton(icon);
		dselectButton.setPreferredSize(new Dimension(55,25));
		dselectButton.setToolTipText("选择模式");
		dselectButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( dselectButton.isSelected() ){
					 status = DRAW_STATUS.select;
					 drawingPanel.setStatus(status);
					// aUndoManager.clear();
				 }
			}});
		
		ButtonGroup bg = new ButtonGroup();
		 
		bg.add( dlineButton);
		bg.add( ddiamandButton);
		bg.add(dcircleButton);
		bg.add( drectButton);
		bg.add( drrectButton);
		bg.add( dselectButton); 
		bg.add( dparallButton); 
		bg.add( dletterButton); 
		
	
		
		JPanel buttonPanel = this;
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		 
		buttonPanel.add( dlineButton);
		buttonPanel.add( ddiamandButton);
		buttonPanel.add(dcircleButton);
		buttonPanel.add( drectButton);
		buttonPanel.add( drrectButton);
		buttonPanel.add( dparallButton); 
		buttonPanel.add( dletterButton); 
		buttonPanel.add( dselectButton); 
	
		icon = getToolIcon("copy_dv.png",22);
		JButton copyBtn = new JButton(icon);
		copyBtn.setToolTipText("复制选择");
		copyBtn.setPreferredSize(new Dimension(55,25));
		copyBtn.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				copyboard = drawingPanel.copySelected();
			}}); 
		buttonPanel.add( copyBtn ); 
		
		icon = getToolIcon("_button_cancel.png",22);
		JButton clearAllBtn = new JButton(icon);
		clearAllBtn.setToolTipText("删除全部");
	 	clearAllBtn.setPreferredSize(new Dimension(55,25));
		clearAllBtn.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				int op = JOptionPane.showConfirmDialog(null, "删除全部内容？");
				if( op == JOptionPane.NO_OPTION)
					return;
				drawingPanel.reset(true);
			//	aUndoManager.clear();
			}}); 
		icon = getToolIcon("d-eraser.png",22);
		JButton deleteBtn = new JButton(icon);
		deleteBtn.setToolTipText("删除");
		 deleteBtn.setPreferredSize(new Dimension(55,25));
		deleteBtn.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.deleteSelected();
				//aUndoManager.clear();
			}});
		buttonPanel.add( deleteBtn); 
		buttonPanel.add( clearAllBtn); 
		icon = getToolIcon("arrow_right1.png",22);
		JButton outlineBtn = new JButton(icon);
		outlineBtn.setToolTipText("添加流程输出");
		outlineBtn.setPreferredSize(new Dimension(55,25));
		outlineBtn.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.addOutlineForSelected();
			}});
		buttonPanel.add( outlineBtn); 
		icon = getToolIcon("d-zoom-in.png",22);
		JButton zoominBtn = new JButton(icon);
		zoominBtn.setToolTipText("放大");
		zoominBtn.setPreferredSize(new Dimension(55,25));
		zoominBtn.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.zoomInSelected();
			}});
		icon = getToolIcon("d-zoom-out.png",22);
		JButton zoomoutBtn = new JButton(icon);
		zoomoutBtn.setToolTipText("缩小");
		zoomoutBtn.setPreferredSize(new Dimension(55,25));
		zoomoutBtn.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.zoomOutSelected();
			}});
		buttonPanel.add( zoominBtn); 
		buttonPanel.add( zoomoutBtn);
		
		
	//	buttonPanel.add(PropertyUIHelper.createLeftAlignment(deleteBtn, clearAllBtn, false, null));  
		
		
	   undoBtn = new JButton( getToolIcon("d-undo.png",22));
		undoBtn.addActionListener(new ActionListener(){ 
				@Override
				public void actionPerformed(ActionEvent e) {
					drawingPanel.undo();
				}});
		 
		 redoBtn = new JButton( getToolIcon("d-redo.png",22));
		redoBtn.addActionListener(new ActionListener(){ 
				@Override
				public void actionPerformed(ActionEvent e) {
					drawingPanel.redo();
				}});
		buttonPanel.add( undoBtn );  
		buttonPanel.add( redoBtn  ); 
		
		 
		 
	}
	 
     

	 
	
	public static ImageIcon getToolIcon(String iconName, int size){
		URL url = DrawingControlPanel.class.getResource( iconName);
		try {
			BufferedImage simage = ImageIO.read( url );
			if( size <=0 ) size = 16;
			//return new ImageIcon( resizeImage(simage, size, size)); 
			return new ImageIcon( simage  );
		} catch (Exception e) {
			 
		}
	  	return null;
	}
	
	public static BufferedImage resizeImage(Image image, int width, int height) {
		return resizeImage(image, width, height, false);
	}
	public static BufferedImage resizeImage(Image image, int width, int height, boolean keepRatio) {
		if( image == null ) return null;
        if ( keepRatio ){
			double thumbRatio = (double) width / (double) height;
			int imageWidth = image.getWidth(null);
			int imageHeight = image.getHeight(null);
			double imageRatio = (double) imageWidth / (double) imageHeight;
			if (thumbRatio < imageRatio * 0.75 ) {
				height = (int) (width / imageRatio);
			} else if ( imageRatio < thumbRatio * 0.75 ) {
				width = (int) (height * imageRatio);
			}
        }
		BufferedImage thumbImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		
		return thumbImage;
	}
	
	public class R9CompactButton extends JButton{
 
		private static final long serialVersionUID = 1L;

		public R9CompactButton(ImageIcon  icon){
			super(icon); 
	        setMargin(new Insets(1, 1, 1, 1));
			setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
			setMaximumSize(new Dimension(icon.getIconWidth(), icon.getIconHeight())); 
			 setIconTextGap(0); 
			 setBorderPainted(false);  
			 setBorder(null);   
		} 
	}



	@Override
	public void onSelection(ChartComponent comp) {
		curSelection = comp;
	 
		dselectButton.setSelected(true);
		 status = DRAW_STATUS.select;
		// drawingPanel.setStatus(status);
		// aUndoManager.clear();
		 
		 
		  
	}
}
