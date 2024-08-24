package com.pi.code.tool.codeplayer;

import java.awt.Color;  
import java.awt.Composite;  
import java.awt.Dimension;  
import java.awt.Font;  
import java.awt.FontMetrics;  
import java.awt.Graphics;  
import java.awt.Graphics2D;  
import java.awt.Insets;  
import java.awt.RenderingHints;  
import java.awt.Window;  
import java.awt.font.FontRenderContext;  
import java.awt.geom.Rectangle2D;  
import java.util.Timer;  
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JWindow;  
  
/** 
 * ��˾��ʾ����� 
 *  
 *  
 * @author ccw 
 * @since:2014-2-28 
 */  
public class Toast extends JFrame {  
  
    private static final long serialVersionUID = 1L;  
    private String message = "";  
    private final Insets insets = new Insets(12, 24, 12, 24);  
    private int period = 1500;  
    private Font font;  
    public static final int msg = 0;// ��ʾ ��ɫ����ɫ  
    public static final int success = 1;// �ɹ���ʾ ǳ��ɫ����ɫ  
    public static final int error = 2;// ������ʾ �ۺ�ɫ����ɫ  
    private Color background;  
    private Color foreground;  
  
    /** 
     *  
     * @param parent 
     *            ������ (Frame Dialog Window) 
     * @param message 
     *            ��Ϣ 
     * @param period 
     *            ��ʾʱ�� 
     */  
    public Toast(JFrame parent, String message, int period) {  
        this(parent, message, period, 0);  
  
    }  
  
    /** 
     *  
     * @param parent 
     * @param message 
     * @param period 
     * @param type 
     *            ��ʾ���� msg:��ɫ����ɫ success :ǳ��ɫ����ɫ  error: �ۺ�ɫ����ɫ 
     */  
    public Toast(JFrame parent, String message, int period, int type) {  
    //    super(parent);  
    	
        this.message = message;  
        this.period = period;  
        font = new Font("����", Font.PLAIN, 14);  
        setSize(getStringSize(font, true, message));  
        // ���JFrame��λ��  
        setLocationRelativeTo(parent);  
        installTheme(type);  
  
    }  
  
    @Override  
    public void paint(Graphics g) {  
        Graphics2D g2 = (Graphics2D) g;  
        // old  
        Composite oldComposite = g2.getComposite();  
  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  
                RenderingHints.VALUE_ANTIALIAS_ON);  
        g2.setFont(font);  
        FontMetrics fm = g2.getFontMetrics();  
        g2.setColor(background);  
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);  
        g2.setColor(foreground);  
        g2.drawString(message, insets.left, fm.getAscent() + insets.top);  
        // restore  
        g2.setComposite(oldComposite);  
    }  
  
    /** 
     * ������ʾ 
     */  
  
    public void start() {  
        this.setVisible(true);  
        Timer timer = new Timer();  
        timer.schedule(new TimerTask() {  
            @Override  
            public void run() {  
                setVisible(false);  
            }  
        }, period);  
    }  
  
    /** 
     * �޸���Ϣ 
     * @param message 
     */  
    public void setMessage(String message) {  
        this.message = message;  
        Dimension size = getStringSize(font, true, message);  
        setSize(size);  
        revalidate();  
        repaint(0, 0, size.width, size.height);  
        if (!isVisible()) {  
            start();  
        }  
    }  
  
    /* 
     * ������ʽ 
     */  
    private void installTheme(int type) {  
        switch (type) {  
        case msg:  
            background = new Color(0x515151);  
            foreground = Color.WHITE;  
            break;  
        case success:  
            background = new Color(223, 240, 216);  
            foreground = new Color(49, 112, 143);  
            break;  
        case error:  
            background = new Color(242, 222, 222);  
            foreground = new Color(221, 17, 68);  
            break;  
  
        default:  
            background = new Color(0x515151);  
            foreground = Color.WHITE;  
            break;  
        }  
    }  
  
    /** 
     * �õ��ַ����Ŀ�-�� 
     *  
     * @param font 
     *            ���� 
     * @param isAntiAliased 
     *            ����� 
     * @param text 
     *            �ı� 
     * @return 
     */  
    private Dimension getStringSize(Font font, boolean isAntiAliased,  
            String text) {  
        FontRenderContext renderContext = new FontRenderContext(null,  
                isAntiAliased, false);  
        Rectangle2D bounds = font.getStringBounds(text, renderContext);  
        int width = (int) bounds.getWidth() + 2 * insets.left;  
        int height = (int) bounds.getHeight() + insets.top * 2;  
        return new Dimension(width, height +100);  
    }  
  
}  