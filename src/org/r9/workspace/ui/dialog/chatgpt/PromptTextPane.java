package org.r9.workspace.ui.dialog.chatgpt;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.rtf.RTFEditorKit;
 

public class PromptTextPane extends JTextPane {

	private static final long serialVersionUID = 1L;

	public boolean f_skipUpdate;
	public int f_xStart = -1;
	public int f_xFinish = -1;

	protected boolean forceFocus;

	FocusListener flst = new FocusListener() {
		/**
		 * Cursor FocusListener
		 */
		@Override
		public void focusGained(FocusEvent e) {
			if (f_xStart >= 0 && f_xFinish >= 0)
				if (getCaretPosition() == f_xStart) {
					setCaretPosition(f_xFinish);
					moveCaretPosition(f_xStart);
				} else
					select(f_xStart, f_xFinish);
			// forceFocus = true;
		}

		@Override
		public void focusLost(FocusEvent e) {
			f_xStart = getSelectionStart();
			f_xFinish = getSelectionEnd();
			if (forceFocus)
				requestFocus();
		}
	};

	private RTFEditorKit w_kit;
	 
 

	public PromptTextPane( ) { 
		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

		  Font boldFont = new Font(this.getFont().getFontName(),
	    this.getFont().getStyle(), 22);
		 
		this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		w_kit = new RTFEditorKit(); // new HTMLEditorKit();//

		//setEditorKit(w_kit);
 
		addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) { 
				if (e.getMark() == e.getDot()) {
					Highlighter hilite = getHighlighter();
					Highlighter.Highlight[] hilites = hilite.getHighlights();
					for (int i = 0; i < hilites.length; i++) {
						if (hilites[i].getPainter() instanceof MyHighlightPainter
								&& hilites[i].getStartOffset() <= e.getMark()
								&& hilites[i].getEndOffset() >= e.getMark()) {
							final Highlighter.Highlight highlight = hilites[i];
							MyHighlightPainter curFillin = (MyHighlightPainter) highlight.getPainter();
							if (curFillin.options.length > 0) {
                                 JPopupMenu popup = new JPopupMenu();
                                 for(final String opt : curFillin.options) {
                                	 JMenuItem item = new JMenuItem(opt);
                            		 item.addActionListener(new ActionListener() { 
                						public void actionPerformed(ActionEvent e) { 
                							 PromptTextPane.this.select(highlight.getStartOffset(), highlight.getEndOffset());
                						     PromptTextPane.this.replaceSelection(opt);	 
                						     hilite.removeHighlight(highlight); 
                						}
                            		 });
                            		 popup.add(item);
                                 }
                                 PromptTextPane p = PromptTextPane.this;
                                 try {
									Rectangle2D rectangle = p.modelToView( p.getCaretPosition() );
								    popup.show(PromptTextPane.this, (int) rectangle.getX(), 
								    		(int)(rectangle.getY() + rectangle.getHeight()));
								} catch (BadLocationException e1) {
									popup.show(PromptTextPane.this, 100,20);
								}
                               
							}
							break;
						}
					}

				} 
			}
		});

	}
 

	protected void highlightFromData() {
		Highlighter hilite = getHighlighter();
		String text = this.getText();
		int pos0 = 0;
		int pos1 = 0;
		while ((pos1 = text.indexOf("[", pos0)) != -1) {
			pos0 = text.indexOf("]", pos1);
			if (pos0 == -1)
				break;

			String sub = text.substring(pos1 + 1, pos0);
			if (sub.indexOf("\n") < 0) { // we dont allow multiple lines in pattern
				String[] fs = sub.split("\\|");
				MyHighlightPainter h = new MyHighlightPainter(fs.length == 1 ? Color.blue : Color.green);
				if (fs.length > 1)
					h.options = fs;
				try {
					hilite.addHighlight(pos1, pos0+1, h);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			pos0 = pos1 + 1;
		}
	}
	
	public void setText(String text) {
		super.setText(text);
		highlightFromData();
	}

	protected boolean canAddHighlighter(int startpos, int endpos) {
		return getText().substring(startpos, endpos).indexOf("\n") < 0;
	}

	// Removes only our private highlights
	public void removeHighlights() {
		Highlighter hilite = getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (int i = 0; i < hilites.length; i++) {
			if (hilites[i].getPainter() instanceof MyHighlightPainter) {
				hilite.removeHighlight(hilites[i]);
			}
		}
	}

	class MyHighlightPainter extends javax.swing.text.DefaultHighlighter.DefaultHighlightPainter {
		public String[] options;

		public MyHighlightPainter(Color color) {
			super(color);
			this.options = new String[0];
		}
	}

}
