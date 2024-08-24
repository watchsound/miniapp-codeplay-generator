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
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import com.pi.code.tool.codefillin.CustomKeyboardEditPane;
import com.pi.code.tool.codefillin.PropertyUIHelper;
import com.pi.code.tool.codeplayer.Workbench;
import com.pi.code.tool.flowchart.DataProcessor.FlowChart;
import com.pi.code.tool.flowchart.DataProcessor.ToolType;
import com.pi.code.tool.flowchart.DrawingPanel.ChartComponent;
import com.pi.code.tool.flowchart.DrawingPanel.DrawingCallback;
import com.pi.code.tool.flowchart.DrawingPanel.LineChartComponent;
import com.pi.code.tool.flowchart.UndoManager.UndoAction; 
  
public class DrawingContentEditPanel extends JPanel implements DrawingPanel.DrawingCallback{
  
	private static final long serialVersionUID = 1L;

	DrawingPanel drawingPanel;  
	 
	private ChartComponent curSelection;
	private JTextArea contentField;
	private JButton saveButton;
	private JRadioButton lineButton;
	private JRadioButton leftarrowButton;
	private JRadioButton rightarrowButton;

	private JButton whileButton;

	private JButton forButton;

	private JButton dowhileButton;

	private CustomKeyboardEditPane fillinEditor;

	private JButton doubleForButton;
	  
	public DrawingContentEditPanel(final DrawingPanel drawingPanel ){
		drawingPanel.setCallback2(this); 
		this.drawingPanel = drawingPanel;
		   
		
		  lineButton = new JRadioButton("直线");
		  lineButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				if( curSelection != null &&  ToolType.isLine(  curSelection.data.t )) {
					curSelection.data.t = ToolType.Line.type;
				}
			}});
		  leftarrowButton = new JRadioButton("左、上箭头");
		  leftarrowButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					if( curSelection != null &&  ToolType.isLine(  curSelection.data.t )) {
						curSelection.data.t = ToolType.ArrowLeft.type;
					}
				}});
		  rightarrowButton = new JRadioButton("右，下箭头");
		  rightarrowButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					if( curSelection != null &&  ToolType.isLine(  curSelection.data.t )) {
						curSelection.data.t = ToolType.ArrowRight.type;
					}
				}});
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(lineButton);
		bg2.add(leftarrowButton);
		bg2.add(rightarrowButton);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		 add( PropertyUIHelper.createTitleRow("常见流程", true)); 
		   doubleForButton = new JButton("双重for 循环");
		   doubleForButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					FlowChart fc = DataProcessor. fromContent("{\"w\":394,\"h\":408,\"data\":[{\"t\":\"ar\",\"x\":174,\"y\":12,\"w\":5,\"h\":43,\"c\":\"\",\"op\":[]},{\"t\":\"di\",\"x\":64,\"y\":57,\"w\":221,\"h\":61,\"c\":\"外循环条件\",\"op\":[]},{\"t\":\"ar\",\"x\":174,\"y\":120,\"w\":5,\"h\":47,\"c\":\"\",\"op\":[]},{\"t\":\"di\",\"x\":74,\"y\":168,\"w\":201,\"h\":65,\"c\":\"内循环条件\",\"op\":[]},{\"t\":\"ar\",\"x\":174,\"y\":233,\"w\":5,\"h\":36,\"c\":\"\",\"op\":[]},{\"t\":\"ro\",\"x\":94,\"y\":268,\"w\":181,\"h\":55,\"c\":\"内循环逻辑\",\"op\":[]},{\"t\":\"ln\",\"x\":275,\"y\":293,\"w\":45,\"h\":7,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":324,\"y\":199,\"w\":5,\"h\":96,\"c\":\"\",\"op\":[]},{\"t\":\"al\",\"x\":275,\"y\":197,\"w\":50,\"h\":7,\"c\":\"True\",\"op\":[]},{\"t\":\"ln\",\"x\":285,\"y\":85,\"w\":56,\"h\":6,\"c\":\"False\",\"op\":[]},{\"t\":\"ln\",\"x\":344,\"y\":88,\"w\":5,\"h\":279,\"c\":\"\",\"op\":[]},{\"t\":\"ro\",\"x\":144,\"y\":357,\"w\":81,\"h\":31,\"c\":\"结束\",\"op\":[]},{\"t\":\"al\",\"x\":233,\"y\":368,\"w\":113,\"h\":5,\"c\":\"\",\"op\":[]},{\"t\":\"ar\",\"x\":20,\"y\":83,\"w\":44,\"h\":7,\"c\":\"\",\"op\":[]},{\"t\":\"ar\",\"x\":26,\"y\":200,\"w\":48,\"h\":5,\"c\":\"False\",\"op\":[]},{\"t\":\"ln\",\"x\":24,\"y\":86,\"w\":5,\"h\":118,\"c\":\"\",\"op\":[]},{\"t\":\"tt\",\"x\":314,\"y\":53,\"w\":21,\"h\":13,\"c\":\"\",\"op\":[]},{\"t\":\"tt\",\"x\":284,\"y\":42,\"w\":40,\"h\":19,\"c\":\"\",\"op\":[]},{\"t\":\"tt\",\"x\":294,\"y\":33,\"w\":61,\"h\":30,\"c\":\"\",\"op\":[]},{\"t\":\"tt\",\"x\":354,\"y\":37,\"w\":20,\"h\":12,\"c\":\"\",\"op\":[]}]}"  );
					drawingPanel.setData(fc);
				}});
		 
		 forButton = new JButton("for 循环");
		 forButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					FlowChart fc = DataProcessor. fromContent("{\"w\":396,\"h\":391,\"data\":[{\"t\":\"ar\",\"x\":130,\"y\":43,\"w\":5,\"h\":60,\"c\":\"\",\"op\":[]},{\"t\":\"di\",\"x\":82,\"y\":223,\"w\":101,\"h\":65,\"c\":\"判断条件\",\"op\":[]},{\"t\":\"re\",\"x\":73,\"y\":103,\"w\":118,\"h\":37,\"c\":\"初始化\",\"op\":[]},{\"t\":\"ar\",\"x\":130,\"y\":139,\"w\":5,\"h\":86,\"c\":\"\",\"op\":[]},{\"t\":\"ar\",\"x\":183,\"y\":252,\"w\":54,\"h\":7,\"c\":\"Yes\",\"op\":[]},{\"t\":\"al\",\"x\":290,\"y\":191,\"w\":25,\"h\":43,\"c\":\"\",\"op\":[]},{\"t\":\"al\",\"x\":136,\"y\":160,\"w\":99,\"h\":13,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":22,\"y\":253,\"w\":62,\"h\":5,\"c\":\"\",\"op\":[]},{\"t\":\"ar\",\"x\":20,\"y\":255,\"w\":5,\"h\":116,\"c\":\"No\",\"op\":[]},{\"t\":\"re\",\"x\":235,\"y\":144,\"w\":135,\"h\":45,\"c\":\"改变条件\",\"op\":[]},{\"t\":\"re\",\"x\":241,\"y\":234,\"w\":135,\"h\":43,\"c\":\"循环主体内容\",\"op\":[]}]}");
					drawingPanel.setData(fc);
				}});
		 whileButton = new JButton("while 循环");
		 whileButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					FlowChart fc = DataProcessor. fromContent("{\"w\":266,\"h\":250,\"data\":[{\"t\":\"ar\",\"x\":130,\"y\":21,\"w\":5,\"h\":32,\"c\":\"\",\"op\":[]},{\"t\":\"di\",\"x\":85,\"y\":57,\"w\":93,\"h\":51,\"c\":\"\",\"op\":[]},{\"t\":\"ar\",\"x\":130,\"y\":109,\"w\":5,\"h\":36,\"c\":\"Yes\",\"op\":[]},{\"t\":\"re\",\"x\":71,\"y\":146,\"w\":118,\"h\":45,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":130,\"y\":194,\"w\":5,\"h\":30,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":132,\"y\":222,\"w\":110,\"h\":8,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":239,\"y\":32,\"w\":7,\"h\":194,\"c\":\"\",\"op\":[]},{\"t\":\"al\",\"x\":133,\"y\":31,\"w\":108,\"h\":5,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":24,\"y\":81,\"w\":59,\"h\":5,\"c\":\"\",\"op\":[]},{\"t\":\"ar\",\"x\":20,\"y\":83,\"w\":5,\"h\":116,\"c\":\"No\",\"op\":[]}]}");
					drawingPanel.setData(fc);
				}});
		 dowhileButton = new JButton("do-while 循环");
		 dowhileButton.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					FlowChart fc = DataProcessor. fromContent("{\"w\":266,\"h\":391,\"data\":[{\"t\":\"ar\",\"x\":130,\"y\":11,\"w\":5,\"h\":71,\"c\":\"\",\"op\":[]},{\"t\":\"di\",\"x\":82,\"y\":223,\"w\":101,\"h\":65,\"c\":\"\",\"op\":[]},{\"t\":\"re\",\"x\":73,\"y\":82,\"w\":118,\"h\":57,\"c\":\"\",\"op\":[]},{\"t\":\"ar\",\"x\":130,\"y\":139,\"w\":5,\"h\":86,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":183,\"y\":252,\"w\":59,\"h\":6,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":239,\"y\":58,\"w\":7,\"h\":197,\"c\":\"Yes\",\"op\":[]},{\"t\":\"al\",\"x\":134,\"y\":51,\"w\":108,\"h\":14,\"c\":\"\",\"op\":[]},{\"t\":\"ln\",\"x\":22,\"y\":253,\"w\":62,\"h\":5,\"c\":\"\",\"op\":[]},{\"t\":\"ar\",\"x\":20,\"y\":255,\"w\":5,\"h\":116,\"c\":\"No\",\"op\":[]}]}");
					drawingPanel.setData(fc);
				}});
		 add(PropertyUIHelper.createRow("", doubleForButton, forButton, whileButton, dowhileButton, false, null));  
		 
		 add( PropertyUIHelper.createTitleRow("箭头类型", true)); 
		 add(PropertyUIHelper.createRow(lineButton, leftarrowButton, rightarrowButton, false, null));  
		
		
		 add( PropertyUIHelper.createTitleRow("显示标注的文字内容", true)); 
		contentField = new JTextArea();
		 add( contentField); 
		saveButton = new JButton("保存");
		saveButton.addActionListener(new ActionListener(){ 
			@Override
			public void actionPerformed(ActionEvent e) {
				 if( curSelection != null)
					 curSelection.data.c = contentField.getText().trim();
			}});
		 add( PropertyUIHelper.createRow(saveButton, false)); 
		 
		 add( PropertyUIHelper.createTitleRow("作为选择填空试题选项", true)); 
		 fillinEditor = new CustomKeyboardEditPane(false, false);
		 add(fillinEditor);
		 
		 add( PropertyUIHelper.createTitleRow("使用图片背景", true)); 
		 
		 final JLabel imageLabel = new JLabel();
		 final JButton  importButton = new JButton("导入图片文件");
		 importButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				String lastopened = DataProcessor.fileToString( new File("last-opened-file")); 
				JFileChooser jfc=new JFileChooser(lastopened);  
				 
		        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY );
		        jfc.setFileFilter(new FileFilter() { 
					public boolean accept(File f) { 
						return f.getName().endsWith(".png") || f.getName().endsWith(".jpg");
					} 
					public String getDescription() { 
						return "";
					}});
		        jfc.showDialog(null, "选择");  
		        File file=jfc.getSelectedFile();  
		        if( file == null  ){  
		           return;
		        }else if(file.isFile()){  
		        	try {
						DataProcessor.stringToFile(file.getAbsolutePath(), new File("last-opened-file"));
					} catch (IOException e1) {
						 e1.printStackTrace();
					}
		        	try {
		        		if( Workbench.codePlayerHome != null )
						   DataProcessor.copy(file, new File(Workbench.codePlayerHome, file.getName()));
					} catch (IOException e1) { 
					}
		         	drawingPanel.setImage(file.getName());
		         	imageLabel.setText(file.getName());
		        }  
			}});
		 final JButton  deleteImageButton = new JButton("删除图片");
		 deleteImageButton.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingPanel.setImage(null);
				imageLabel.setText("");
			}
		 });
		 add( PropertyUIHelper.createRow(imageLabel, importButton, deleteImageButton)); 
		 
	}
	 
     
	 
	
	public static ImageIcon getToolIcon(String iconName, int size){
		URL url = DrawingContentEditPanel.class.getResource( iconName);
		try {
			BufferedImage simage = ImageIO.read( url );
			if( size <=0 ) size = 16;
			return new ImageIcon( resizeImage(simage, size, size)); 
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

		public R9CompactButton(String text){
			super(text);
			FontMetrics fm =  getFontMetrics( getFont() );
	        int length = fm.stringWidth(text);
	        setMargin(new Insets(1, 2, 1, 2));
			setPreferredSize(new Dimension(length+8, 25));
			setMaximumSize(new Dimension(length+8, 25));
			 setIconTextGap(0);//将标签中显示的文本和图标之间的间隔量设置为0  
			 setBorderPainted(false);//不打印边框  
			 setBorder(null);//除去边框  
			 
		} 
	}



	@Override
	public void onSelection(ChartComponent comp) {
		curSelection = comp;
		if( comp != null ) {
			 contentField.setText( curSelection.data.c ) ; 
			 if(!(comp instanceof LineChartComponent)) {
			    this.fillinEditor.setData(comp.data.op, "");
			 }
		}
		 if( comp == null )
			 return;
		 
		if( comp.data.t.equals(ToolType.Line.type)) {
			lineButton.setSelected(true);
			leftarrowButton.setSelected(false);
			rightarrowButton.setSelected(false);
		}
		else if( comp.data.t.equals(ToolType.ArrowLeft.type)) {
			lineButton.setSelected(false);
			leftarrowButton.setSelected(true);
			rightarrowButton.setSelected(false);
		}  
		else if( comp.data.t.equals(ToolType.ArrowRight.type)) {
			lineButton.setSelected(false);
			leftarrowButton.setSelected(false);
			rightarrowButton.setSelected(true);
		}  
		  
	}
}
