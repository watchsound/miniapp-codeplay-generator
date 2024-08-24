package com.pi.code.tool.codeplayer;



import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException; 
 
public class OSUtil {
    private OSUtil() {
    }

    public static boolean isMac() { 
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            return true;
        }
        return false;
    }

    public static boolean isJava6() {
        return (System.getProperty("java.version").startsWith("1.6"));
    }
 
    public static void setStringToClipboard(String selectedText) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection content = new StringSelection(selectedText);
        clipboard.setContents(content, new ClipboardOwner(){ 
            public void lostOwnership(Clipboard clipboard, Transferable transferable) {
               System.err.println("clipboard lost ownership");
            }
        });
    }

    public static File getJavaWSExecutable() {
        if(isMac()) {
            File javaws6 = new File("/System/Library/Frameworks/JavaVM.framework/Versions/1.6/Home/bin/javaws");
            if(javaws6.exists()) {
                return javaws6;
            }
            return new File("/System/Library/Frameworks/JavaVM.framework/Versions/1.5/Home/bin/javaws");
        }
        return new File(System.getProperty("java.home"),"bin/javaws");
    }

    public static String getFFMPEG(){
    	if( isMac() ){
    		return "tools/ffmpeg/ffmpeg"; 
    	}
 		if ( is64bitOS() ){
    		return "tools/ffmpeg/win64/ffmpeg.exe";
    	} else {
    		return "tools/ffmpeg/win32/ffmpeg.exe";
    	} 
    }
    public static boolean is64bitOS() {
        String field = System.getProperty("os.arch");
        if (field != null && field.indexOf("64") >=0 ) {
            return true;
        }
        field = System.getProperty("os.version");
        if (field != null && field.indexOf("_64") >=0 ) {
            return true;
        }
        return false;
    }
    public static boolean is64bitJava() {
        String field = System.getProperty("sun.arch.data.model");
        if (field != null && field.indexOf("64") >=0 ) {
            return true;
        } 
        return false;
    }
   
    
    public static String getBaseStorageDir(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        StringBuffer filepath = new StringBuffer(System.getProperty("user.home"));
        if(os.indexOf("windows xp") != -1) {
            filepath.append(File.separator);
            filepath.append("Local Settings");
            filepath.append(File.separator);
            filepath.append("Application Data");
            filepath.append(File.separator);
            filepath.append(appName);
            filepath.append(File.separator);
        } else if (os.indexOf("vista") != -1) {
            filepath.append(File.separator);
            filepath.append("appdata");
            filepath.append(File.separator);
            filepath.append("locallow");
            filepath.append(File.separator);
            filepath.append(appName);
            filepath.append(File.separator);
        } else if (os.startsWith("mac")) {
            filepath.append(File.separator);
            filepath.append("Library");
            filepath.append(File.separator);
            filepath.append("Preferences");
            filepath.append(File.separator);
            filepath.append(appName);
            filepath.append(File.separator);
        } else {
            //if we don't know what OS it is then just use user.home followed by a .
            filepath.append(File.separator);
            filepath.append(".");
            filepath.append(appName);
            filepath.append(File.separator);
        }
        System.out.println("final base storage dir = " + filepath.toString());
        return filepath.toString();
    }
}