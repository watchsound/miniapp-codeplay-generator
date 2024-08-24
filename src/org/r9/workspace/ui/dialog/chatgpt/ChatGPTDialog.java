package org.r9.workspace.ui.dialog.chatgpt;
 

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener; //property change stuff 
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
 
 
public class ChatGPTDialog extends  JDialog
                   implements    PropertyChangeListener {
 
      
	private static final long serialVersionUID = 1L;

	private JOptionPane optionPane;
 
    private String btnString1  ;//"保存"; 
 
    
	  
    /** Creates the reusable dialog. */
    public ChatGPTDialog(Frame aFrame  ) {
        super(aFrame, true);
        
        
        JPanel cpanel = new ChatGTPPanel( );
        cpanel.setLayout(new BoxLayout(cpanel, BoxLayout.Y_AXIS));
          
       
        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1 };
 
        //Create the JOptionPane.
        optionPane = new JOptionPane(cpanel,
                                    JOptionPane.PLAIN_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);
 
        //Make this dialog display it.
        setContentPane(optionPane);
  
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) { 
                    optionPane.setValue( JOptionPane.CLOSED_OPTION );
            }
        });
 
        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) { 
            }
        });
 
      
        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }
 
    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
 
        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();
 
            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            } 
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);
 
            if (btnString1.equals(value) ) {
            	   setVisible(false);
            } 
        }
    } 
    
 
}