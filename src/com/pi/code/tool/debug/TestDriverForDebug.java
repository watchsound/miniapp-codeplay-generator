package com.pi.code.tool.debug;
 
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.optionpane.WebOptionPaneUI;
import com.pi.code.tool.codeplayer.OSUtil;
 

public class TestDriverForDebug {

	public static void main(String[] vargv){
  
		try {
			SwingUtilities.invokeAndWait( new Runnable ()
			{
			    public void run ()    
			    {
			        // Install WebLaF as application L&F 
			    	int fontSize = OSUtil.isMac() ? 14 : 12; 
			    	WebLookAndFeel.globalControlFont  = new FontUIResource("宋体",0, fontSize); //            
			    	WebLookAndFeel.globalTooltipFont  = new FontUIResource("宋体",0, fontSize);//             
			    	//WebLookAndFeel.globalAlertFont= new FontUIResource("瀹嬩綋",0, fontSize); //       
			    	
			    	WebLookAndFeel.globalMenuFont = new FontUIResource("宋体",0, fontSize);//             
			    //	WebLookAndFeel.globalAcceleratorFont = new FontUIResource("瀹嬩綋",0, fontSize);//        
			    //	WebLookAndFeel.globalTitleFont = new FontUIResource("瀹嬩綋",0, fontSize); //             
			    	WebLookAndFeel.globalTextFont = new FontUIResource("宋体",0, fontSize);            
			    	           
			    	 try {
			    		  WebLookAndFeel.install ();
			    	 }catch(Exception ex) {
			    		// u.p(ex.getMessage(), u.LEVEL.DEBUG);
			    	 } 
			        UIManager.put("OptionPaneUI",  WebOptionPaneUI.class.getCanonicalName() );
			       
			    }
			} );
		} catch ( Exception e) { 
			e.printStackTrace();
		}  
			
		WorkbenchForDebug dialog = new WorkbenchForDebug( null ); 
		dialog.setVisible(true);
	}
}
