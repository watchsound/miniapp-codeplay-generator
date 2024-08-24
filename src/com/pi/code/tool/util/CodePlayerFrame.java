package com.pi.code.tool.util;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
 

public class CodePlayerFrame extends JFrame{
 
	private static final long serialVersionUID = 1L;

	public CodePlayerFrame() {
		init();
	}
	public CodePlayerFrame(String title) {
		super(title);
		init();
	}
	private void init() {
		ImageIcon mIcon = new ImageIcon(CodePlayerFrame.class.getResource("video.png"));
		List<Image> images = new ArrayList<Image>();
		images.add(mIcon.getImage());
		setIconImages(images);
		
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) { 
			}

			@Override
			public void windowClosing(WindowEvent e) { 
			}

			@Override
			public void windowClosed(WindowEvent e) {
				 System.exit(0);
			}

			@Override
			public void windowIconified(WindowEvent e) { 
			}

			@Override
			public void windowDeiconified(WindowEvent e) { 
			}

			@Override
			public void windowActivated(WindowEvent e) { 
			}

			@Override
			public void windowDeactivated(WindowEvent e) { 
				
			}});
	}
}
