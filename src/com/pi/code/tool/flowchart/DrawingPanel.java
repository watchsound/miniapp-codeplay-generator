package com.pi.code.tool.flowchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.pi.code.tool.codefillin.ImageUtil;
import com.pi.code.tool.codeplayer.Workbench;
import com.pi.code.tool.flowchart.DataProcessor.FlowChart;
import com.pi.code.tool.flowchart.DataProcessor.ToolType;
import com.pi.code.tool.flowchart.DataProcessor.UIComponent;
import com.pi.code.tool.flowchart.DrawingControlPanel.DRAW_STATUS;
import com.pi.code.tool.flowchart.DrawingPanel.ChartComponent;
import com.pi.code.tool.flowchart.UndoManager.UndoAction;
import com.pi.code.tool.util.PageColors; 

public class DrawingPanel extends JPanel {
	
	public static interface DrawingCallback {
		void onSelection(ChartComponent comp);
		
	}
	enum Direction {
		left, right, top, bottom, inside, outside
	}

	public static int GAP = 10;

	// private int width;
	// private int height;

	private DRAW_STATUS status = DRAW_STATUS.select;

	private Point lastPoint;

	UndoManager undoManager;

	private int strokeWidth = 1;
	private Color strokeColor = Color.BLACK;

	private Point startPoint;
	private Point endPoint;

	private boolean duringDragging;

	private boolean useRegion() {
		return status == DRAW_STATUS.select || status == DRAW_STATUS.rrect || status == DRAW_STATUS.parall
				|| status == DRAW_STATUS.diamond || status == DRAW_STATUS.rect || status == DRAW_STATUS.circle;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	private ChartComponent[] curSelected; 

	private ChartComponent newlyCreated;
	private List<ChartComponent> uicomponents = new ArrayList<>();

	private int[]  rowLines;
	private int[]  colLines;
	DrawingCallback callback;
	DrawingCallback callback2;
	
	SelectRegion selectionRegion;

	private String image;
	private BufferedImage bimage;
	public DrawingPanel( ) {
        this.setBorder(BorderFactory.createLineBorder(PageColors.red1));
		this.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
				if( DrawingControlPanel.copyboard != null && DrawingControlPanel.copyboard.size() > 0) {
					 Point topleft =  getTopLeftPoint( DrawingControlPanel.copyboard );
					 int diffX = e.getX() - topleft.x;
					 int diffY = e.getY() - topleft.y;
					 
					 for(ChartComponent comp :  DrawingControlPanel.copyboard) {
						 comp.x += diffX;
						 comp.y += diffY;
						 addComp(comp);
					 }
					 repaint();
					 DrawingControlPanel.copyboard = null;
					 return;
				}
				clearAndSelectByPos(e.getPoint());
				selectionRegion = null;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				lastPoint = new Point(e.getPoint()); 
				if( DRAW_STATUS.select == status) { 
					 ChartComponent comp =  getAlreadySelected(e.getPoint());
					 if( comp == null) {
						 clearSelection();
						 selectionRegion = new SelectRegion(lastPoint.x, lastPoint.y, 1,1);
					 } else if ( comp.hitType != Direction.inside ) {
						 clearSelection();
						 selectionRegion = null;
						 curSelected = new ChartComponent[1];
						 curSelected[0] = comp;
						 comp.selected = true;
					 } else {
						 selectionRegion = null;
					 } 
				}
				else {
					newlyCreated = createNewComponent( status, lastPoint.x, lastPoint.y, false);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if( newlyCreated != null) {
					if( newlyCreated.isnull()) {
						newlyCreated = null;
					}
					else {
						newlyCreated.adjust();
						newlyCreated.adjustToHookPosition();
						addComp(newlyCreated); 
						curSelected = new ChartComponent[1];
						curSelected[0] = newlyCreated;
						newlyCreated = null;
						 status = DRAW_STATUS.select;
						if( callback != null )
							callback.onSelection(curSelected[0]);
						if( callback2 != null )
							callback2.onSelection(curSelected[0]);
					}
					lastPoint = null;
					duringDragging = false;
					repaint();
					//repaint();
					return;
				}
				if( selectionRegion != null) {
					List<ChartComponent> ss = new ArrayList<>();
					for(ChartComponent cc : uicomponents) {
						if( selectionRegion.intersects(cc))
							ss.add(cc);
					}
					curSelected = new ChartComponent[ss.size()];
					for(int i = 0; i < ss.size(); i++) {
						 curSelected[i] = ss.get(i);
						 curSelected[i].selected = true;
					}
					selectionRegion = null;
					repaint();
				} else {
					if( duringDragging && curSelected != null) {
						for(ChartComponent c : curSelected)
						    c.adjustToHookPosition();
					 	repaint();
					}
				}
				
				lastPoint = null;
				duringDragging = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				duringDragging = true;
				if( newlyCreated != null) {
					Point curPoint = new Point(e.getPoint());
					newlyCreated.x = Math.min(curPoint.x, lastPoint.x);
					newlyCreated.y = Math.min(curPoint.y, lastPoint.y);
					newlyCreated.width = Math.abs(curPoint.x - lastPoint.x);
					newlyCreated.height = Math.abs(curPoint.y - lastPoint.y);
					repaint();
				} else {
					 if( lastPoint != null && curSelected != null){
						   Point newpoint = new Point(e.getPoint());
						   for(ChartComponent c : curSelected) {
							   c.adjustPos( newpoint.x - lastPoint.x, newpoint.y - lastPoint.y); 
							   
							   if(uicomponents.size() > 1 ) {
								   rowLines = new int[2];
								   colLines = new int[2];
								   rowLines[0] = c.y + c.height/2;
								   colLines[0] = c.x + c.width/2;
								   
								   int minRowDif = Integer.MAX_VALUE;
								   int minColDif = Integer.MAX_VALUE;
								   ChartComponent rowV =null;
								   ChartComponent colV=null;
								   for(ChartComponent cc : uicomponents) {
									   if( cc == c) continue;
									   int rd =  Math.abs( cc.y +cc.height/2 - rowLines[0]  );
									   if( rd < minRowDif) {
										   minRowDif = rd;
										   rowV = cc;
									   }
									   
									   int cd =  Math.abs( cc.x +cc.width/2 - colLines[0]  );
									   if( cd < minColDif) {
										   minColDif = cd; 
										   colV = cc;
									   }
								   }
								   rowLines[1] = minRowDif < 10 ? ( rowV.y + rowV.height/2) : -1;
								   colLines[1] = minColDif < 10 ? ( colV.x + colV.width/2) : -1; 
							   } 
						   }
						   lastPoint = new Point(e.getPoint());
					    }
					 else if( selectionRegion != null) {
						 Point curPoint = new Point(e.getPoint());
						 selectionRegion.x = Math.min(curPoint.x, lastPoint.x);
						 selectionRegion.y = Math.min(curPoint.y, lastPoint.y);
						 selectionRegion.width = Math.abs(curPoint.x - lastPoint.x);
						 selectionRegion.height = Math.abs(curPoint.y - lastPoint.y);
					 }
					 else {
						 lastPoint = new Point(e.getPoint());
					 }
					  
					   
					  repaint();
				    }

				}
				
				
			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});
	}
	
	public void setCallback(DrawingCallback callback) {
		this.callback = callback;
	}
	public void setCallback2(DrawingCallback callback) {
		this.callback2 = callback;
	}
	public void setData( FlowChart debug ) {
		if( debug == null) return;
		this.reset(false);
		this.status =  DRAW_STATUS.select;
		setBImage(debug.image);
		
		int width = this.getWidth();
		int height = this.getHeight();
		int cw = debug.w;
		int ch = debug.h;
		int xoffset =  (width - cw) /2;
		xoffset = (xoffset / 10 ) * 10;
		if(xoffset < 0 ) xoffset = 0;
		
		for(UIComponent comp : debug.data) {
			uicomponents.add(createNewComponent(  comp, xoffset,0, true)  );
		}
		repaint();
	}
	public void setImage(String image) {
		setBImage(image);
		repaint();
	}
	private void setBImage(String image) {
		this.bimage = null;
		this.image = image;
		if( this.image != null && this.image.length()>0 && Workbench.codePlayerHome != null) {
			File ifile = new File(Workbench.codePlayerHome, this.image);
			this.bimage = ImageUtil. getImageFromFile(ifile, this);
		}
		
	}
	
	public Point getTopLeftPoint(List<ChartComponent> comps) {
		int minX = Integer.MAX_VALUE; 
		int minY = Integer.MAX_VALUE;
		for(ChartComponent b : comps) {
			if( b.x < minX) minX = b.x;
			if( b.y < minY) minY = b.y;
		}
		return new Point(minX, minY);
	}
	public FlowChart getData() { 
		if( this.bimage != null ) { 
			FlowChart fc = new FlowChart();
			fc.image = this.image;
			fc.w = this.bimage.getWidth();
			fc.h = this.bimage.getHeight();
			for(ChartComponent b : uicomponents) {
				fc.data.add(b.getUpdateData(0, 0));
			}
			return fc;
		}
		
		if( this.uicomponents.isEmpty())
			return null;
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for(ChartComponent b : uicomponents) {
			if( b.x < minX) minX = b.x;
			if( b.x + b.width > maxX) maxX = b.x + b.width;
			if( b.y + b.height > maxY ) maxY = b.y + b.height;
		}
		final int margin = 20;
		 
		FlowChart fc = new FlowChart();
		fc.w = (maxX - minX ) + margin +margin;
		fc.h = maxY + margin;
		for(ChartComponent b : uicomponents) {
			fc.data.add(b.getUpdateData(minX - margin, 0));
		}
		return fc;
	}
	public void undo() {
		if( this.undoManager != null)
			this.undoManager.undo( );
	}
	public void redo() {
		if( this.undoManager != null)
			this.undoManager.redo( );
	}
	public void addComp(ChartComponent comp) {
		this.uicomponents.add(comp);
		if( this.undoManager != null)
			this.undoManager.pushAdd(comp);
	}
	public void removeComp(ChartComponent comp) {
		this.uicomponents.remove(comp);
		if( this.undoManager != null)
			this.undoManager.pushDelete(comp);
	}
	public void addOutlineForSelected() {
		if( curSelected != null && curSelected.length == 1) {
			 ChartComponent s = curSelected[0]; 
			 if( s instanceof TextChartComponent) return;
			 if( s instanceof LineChartComponent) return;
			 	
			 //left 
			 ChartComponent  comp =  createNewComponent(new UIComponent(ToolType.ArrowLeft.type), 0,0, false);
			 comp.x = s.x -100;
			 comp.y = s.y + s.height/2 - 30;
			 comp.width = 100;
			 comp.height =60;
			 this.addComp(comp);
			 
			 //right
			 comp =  createNewComponent(new UIComponent(ToolType.ArrowRight.type), 0,0, false);
			 comp.x = s.x + s.width;
			 comp.y = s.y + s.height/2 - 30;
			 comp.width = 100;
			 comp.height =60;
			 this.addComp(comp);
			
			 //bottom
			 comp =  createNewComponent(new UIComponent(ToolType.ArrowRight.type), 0,0, false);
			 comp.x = s.x + s.width/2 -30;
			 comp.y = s.y + s.height;
			 comp.width = 60;
			 comp.height =100;
			 this.addComp(comp);
			 
			repaint();
		}
	}
	
	public List<ChartComponent>  copySelected(){
		List<ChartComponent> copy = new ArrayList<>();
		if( curSelected == null )
			return copy;
		for(ChartComponent s : curSelected) {
			copy.add(s.copy());
		}
		return copy;
	}
	
	public void zoomInSelected() {
		if( curSelected != null) {
			for(ChartComponent s : curSelected)
				s.zoomIn();
			repaint();
		}
		
	}
	public void zoomOutSelected() {
		if( curSelected != null) {
			for(ChartComponent s : curSelected)
				s.zoomOut();
			repaint();
		}
	}
	public void reset(boolean needUndo) { 
		startPoint = null;
		endPoint = null;
		lastPoint = null;
		duringDragging = false; 
		
		  curSelected = null; 
        newlyCreated = null;
        
        if( needUndo) {
        	  for(int i = uicomponents.size()-1; i >=0 ; i--) {
              	ChartComponent s  = uicomponents.get(i);
              	this.removeComp(s);
              }
        } else {
        	uicomponents.clear();
        }
      
        	
		
		repaint();
	}
	public void deleteSelected() {
		startPoint = null;
		endPoint = null;
		lastPoint = null;
		duringDragging = false; 
		
		if( curSelected != null) {
			for(ChartComponent s : curSelected)
				this.removeComp(s);
		}
			
		  curSelected = null; 
	        newlyCreated = null;
	        repaint();
	}
 
	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public Color getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	protected ChartComponent getAlreadySelected(Point point) {
		 for(ChartComponent comp : uicomponents) {
			  Direction dir = comp.hitTest(point) ;
			 if( dir != Direction.outside ) {
				 return comp;
			 }
		  }
		 return null;
	}
	
	protected void clearSelection() {
		  this.curSelected = null;
		  for(ChartComponent comp : uicomponents) {
			  comp.selected = false;
		  }
	}
	
	protected void clearAndSelectByPos(Point point) {
		if( DRAW_STATUS.select == status) {
			  clearSelection() ;
			  for(ChartComponent comp : uicomponents) {
				  Direction dir = comp.hitTest(point) ;
				 if( dir != Direction.outside ) {
					 this.curSelected = new ChartComponent[1];
					 this.curSelected[0]= comp; 
					 comp.selected = true;
					 break;
				 }
			  }
			  if( callback != null)
				  callback.onSelection(this.curSelected == null ? null : this.curSelected[0]);
			  if( callback2 != null)
				  callback2.onSelection(this.curSelected== null ? null : this.curSelected[0]);
			  repaint();
		} 
		else {
			if( this.curSelected != null) {
				  this.curSelected[0].hitTest(point);
			}
		}
    }

	protected void undoredo(UndoAction action, boolean undo) {
		try {
			 if( undo ) {
				 if( action.addNew )
					 this.uicomponents.remove(action.comp);
				 else
					 this.uicomponents.add(action.comp); 
			 } else {
				 if( action.addNew )
					 this.uicomponents.add(action.comp);
				 else
					 this.uicomponents.remove(action.comp); 
			 }
				repaint();
			 
		} catch (Exception ex) {
		}
	}

	 

	public void clear(Rectangle r) {

	}

	public Dimension getPreferedSize() {
		return new Dimension(650, 450);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = getWidth();
		int h = getHeight();
		g.setColor(Color.white);
		g.fillRect(0, 0, w, h);
		if( this.bimage != null ) {
			g.drawImage(this.bimage, 0,0, this);
		}
		// draw snap lines
		g.setColor(new Color(	245,245,220));
		for (int i = GAP; i < w; i += GAP) {
			g.drawLine(i, 0, i, h);
		}
		for (int i = GAP; i < h; i += GAP) {
			g.drawLine(0, i, w, i);
		}
		g.setColor(Color.PINK);
		for (int i = 100; i < w; i += 100) {
			g.drawLine(i, 0, i, h);
		}
		
		g.setColor(Color.black);
		// draw figs
		Graphics2D g2d = (Graphics2D) g;
		for (ChartComponent comp : uicomponents) {
			comp.paintComponent(g2d);
		}

		if( newlyCreated != null)
			newlyCreated.paintComponent(g2d);
		
	
		if( rowLines != null && rowLines.length > 0) {
			for(int i = 0; i < rowLines.length; i++) {
				if( rowLines[i] >=0 ) {
					g.setColor( i ==0 ? PageColors.blue1 : PageColors.red1);
					g.drawLine(0, rowLines[i], w, rowLines[i]);
				}
			}
			rowLines = null;
		}
		if(colLines != null && colLines.length > 0) {
			for(int i = 0; i < colLines.length; i++) {
				if( colLines[i] >=0 ) {
					g.setColor( i ==0 ? PageColors.blue1 : PageColors.red1);
					g.drawLine(colLines[i],0, colLines[i], h);
				}
			}
			colLines = null;
		}
		g.setColor(PageColors.red1);
		if( selectionRegion != null)
			selectionRegion.paintComponent(g2d);
		
		 
		g.setColor(Color.black);
	}

	public DRAW_STATUS getStatus() {
		return status;
	}

	public void setStatus(DRAW_STATUS status) {
		this.status = status; 
        this.curSelected = null;
        repaint();
	}

	public static class SelectRegion extends Rectangle {

		Rectangle top;
		Rectangle left;
		Rectangle right;
		Rectangle bottom;

		Direction hitType = Direction.outside;
		public boolean selected;
		public SelectRegion(int x, int y, int width, int height) {
			super(x, y, width, height);
			adjust();
		}

		public void paintComponent(Graphics g) {
           Color c = g.getColor();
			g.setColor(PageColors.red1);
			g.drawRect(x, y, width, height);
			g.drawRect(top.x, top.y, top.width, top.height);
			g.drawRect(left.x, left.y, left.width, left.height);
			g.drawRect(right.x, right.y, right.width, right.height);
			g.drawRect(bottom.x, bottom.y, bottom.width, bottom.height);
			g.setColor(c);
		}

		public void adjustPos(int xdiff, int ydiff) {
			if (hitType == Direction.inside) {
				this.x += xdiff;
				this.y += ydiff;
			} else if (hitType == Direction.left) {
				this.x += xdiff;
				this.width -= xdiff;
			} else if (hitType == Direction.right) {
				this.width += xdiff;
			} else if (hitType == Direction.top) {
				this.y += ydiff;
				this.height -= ydiff;
			} else if (hitType == Direction.bottom) {
				this.height += ydiff;
			} else if( selected ) {
				this.x += xdiff;
				this.y += ydiff;
			}
			this.adjust();
		}

		public Direction hitTest(Point p) {
			if (hitLeft(p)) {
				hitType = Direction.left;
				return hitType;
			}
			if (hitRight(p)) {
				hitType = Direction.right;
				return hitType;
			}
			if (hitTop(p)) {
				hitType = Direction.top;
				return hitType;
			}
			if (hitBottom(p)) {
				hitType = Direction.bottom;
				return hitType;
			}
			if (contains(p)) {
				hitType = Direction.inside;
				return hitType;
			}
			hitType = Direction.outside;
			return hitType;
		}

		public boolean hitLeft(Point p) {
			return left.contains(p);
		}

		public boolean hitRight(Point p) {
			return right.contains(p);
		}

		public boolean hitTop(Point p) {
			return top.contains(p);
		}

		public boolean hitBottom(Point p) {
			return bottom.contains(p);
		}

		public void setX(int x) {
			this.x = x;
			adjust();
		}

		public void setY(int y) {
			this.y = y;
			adjust();
		}

		public void setWidth(int w) {
			this.width = w;
			adjust();
		}

		public void setHeight(int h) {
			this.height = h;
			adjust();
		}

		public void adjust() {
			top = new Rectangle(x + width / 2 - 3, y - 3, 6, 6);
			left = new Rectangle(x - 3, y + height / 2 - 3, 6, 6);
			bottom = new Rectangle(x + width / 2 - 3, y + height - 3, 6, 6);
			right = new Rectangle(x + width - 3, y + height / 2 - 3, 6, 6);
		}

	}
	
	public List<Point> getCloseLine(ChartComponent exclude, Point p, boolean horizontal){
		List<Point> points = new ArrayList<>();
		for(ChartComponent comp : this.uicomponents) {
			if( comp == exclude ) continue;
			if(!( comp instanceof LineChartComponent)) continue;
			boolean isHorizontal = comp.width > comp.height;
			if( isHorizontal != horizontal ) continue;
			Point[] ends = comp.getEnds(); 
		    points.add(  ends[0]); 
		}
		return points;
	}

	public List<Point> getEndPositions(ChartComponent exclude){
		List<Point> points = new ArrayList<>();
		for(ChartComponent comp : this.uicomponents) {
			if( comp == exclude ) continue;
			if( comp instanceof TextChartComponent) continue;
			
			Point[] ends = comp.getEnds(); 
			if( comp instanceof LineChartComponent) {
				points.add(  ends[0]);
				points.add( ends[1]);
			} else {
				points.add(  ends[0]);
				points.add( ends[1]);
				points.add(  ends[2]);
				points.add( ends[3]);
			}
		}
		return points;
	}
	
	public static int parseInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	public  ChartComponent createNewComponent(DRAW_STATUS status) {
		//Circle("cc"), RoundRect("ro"), Rect("re"), Diamond("di"), Parallelogram("pa"), Line("ln"), ArrowLeft("al"),
		//ArrowRight("ar"), Text("tt");
		//public static enum DRAW_STATUS{ circle, parall, diamond, rrect,  rect,  line, letter,   select  }
		switch(status) {
		case circle : return new CircleChartComponent();
		case parall : return new ParallChartComponent();
		case diamond : return new DiamondChartComponent();
		case rrect : return new RoundRectChartComponent();
		case rect : return new  RectChartComponent();
		case letter : return new TextChartComponent();
		case line : return new LineChartComponent();
		}
		return null;
	}
	
	public  ChartComponent createNewComponent(UIComponent comp, int x, int y, boolean changedValue) {
		ToolType status =  ToolType.fromType(comp.t);
		ChartComponent compx = null;
		switch(status) {
		case Circle : compx = new CircleChartComponent(comp);break;
		case Parallelogram : compx = new ParallChartComponent(comp);break;
		case Diamond : compx = new DiamondChartComponent(comp);break;
		case RoundRect : compx = new RoundRectChartComponent(comp);break;
		case Rect : compx = new  RectChartComponent(comp);break;
		case Text : compx = new TextChartComponent(comp);break;
		case Line : compx = new LineChartComponent(comp);break;
		case ArrowLeft : compx = new LineChartComponent(comp);break;
		case ArrowRight : compx = new LineChartComponent(comp);break;
		}
		if( changedValue ) {
			if( compx != null ) {
				compx.x += x;
				compx.y += y;
			}
		} else {
			if( compx != null && x >=0 && y >=0) {
				compx.x = x;
				compx.y = y;
			}
		}
	
		return compx;
	}
	
	public  ChartComponent createNewComponent(DRAW_STATUS status, int x, int y, boolean changedValue) {
		//Circle("cc"), RoundRect("ro"), Rect("re"), Diamond("di"), Parallelogram("pa"), Line("ln"), ArrowLeft("al"),
		//ArrowRight("ar"), Text("tt");
		//public static enum DRAW_STATUS{ circle, parall, diamond, rrect,  rect,  line, letter,   select  }
		ChartComponent comp = null;
		switch(status) {
		case circle : comp = new CircleChartComponent(); break;
		case parall : comp = new ParallChartComponent();break;
		case diamond : comp = new DiamondChartComponent();break;
		case rrect : comp = new RoundRectChartComponent();break;
		case rect : comp = new  RectChartComponent();break;
		case letter : comp = new TextChartComponent();break;
		case line : comp = new LineChartComponent();break;
		}
		if( changedValue ) {
			if( comp != null ) {
				comp.x += x;
				comp.y += y;
			}
		} else {
			if( comp != null && x >=0 && y >=0) {
				comp.x = x;
				comp.y = y;
			}
		}
		return comp;
	}
	 

	public abstract class ChartComponent extends SelectRegion {
		UIComponent data;
		
		public ChartComponent(String type) {
			super(0, 0, 2, 2);
			this.data = new UIComponent(type); 
		}
		public ChartComponent(UIComponent data) {
			super(0, 0, 100, 100);
			this.data = data;
			if( data.w >  0 && data.h > 0) {
				this.x = data.x;
				this.y = data.y;
				this.width = data.w;
				this.height = data.h;
			}
//			if (data.s != null && data.s.length() > 0) {
//				String[] fs = data.s.split(":");
//				this.x = parseInt(fs[0], 0);
//				this.y = parseInt(fs[1], 0);
//				this.width = parseInt(fs[2], 100);
//				this.height = parseInt(fs[3], 100);
//
//			}
		}
		
		public void copyPropertiesTo(ChartComponent comp) {
			comp.data = data.copy();
			comp.x = x;
			comp.y = y;
			comp.width = width;
			comp.height = height;
		}
		public abstract ChartComponent copy();
	 
		
		public boolean isnull() {
			return width < 10 && height < 10;
		}
		public void zoomIn() {
			this.x = (int) ( this.x * 1.1);
			this.y = (int) ( this.y * 1.1);
			this.width = (int) ( this.width * 1.1);
			this.height = (int)(this.height *1.1);
		}
		public void zoomOut() {
			this.x = (int) ( this.x * 0.9);
			this.y = (int) ( this.y * 0.9);
			this.width = (int) ( this.width * 0.9);
			this.height = (int)(this.height *0.9);
		}
		public void adjustToHookPosition() {
			
			int cx = x + width/2; 
			int nx = cx % 10;
			if( nx == 0) return;
			if( nx < 5 ) {
				x -= nx;
			}
			else {
				x +=  10 - nx;
			}
			nx = x % 10;
			if( nx == 0) return;
			if( nx < 5 ) {
				x -= nx;
				width += nx + nx;
			} else {
				x += 10 - nx;
				width -= 10 - nx + 10 - nx;
			}
		}
		public Point[] getEnds() {
			Point[] ps = new Point[4]; 
				ps[0] = new Point(x + width/2, y );
				ps[1] = new Point(x+width, y + height/2);
				ps[2] = new Point(x + width/2, y  + height);
				ps[3] = new Point(x  , y+ height/2 );
		 	return ps;
		}
		

		public void paintComponent(Graphics2D g) {
			if (selected) {
				super.paintComponent(g);
			}
			if( width < 5) width = 5;
			if( height < 5) height = 5;
			
			if( data.c.length() == 0)
				return;
			FontMetrics metrics = g.getFontMetrics(g.getFont()); 
	        int lineH = (int)( metrics.getHeight());
		//	g.drawRect(x, y, width, height);
			String[] fs = data.c.split("\n");
			
			int yoffset = metrics.getAscent() + ( height - fs.length * metrics.getHeight())/2;
			for(String f : fs) {
				int www = metrics.stringWidth(f);
				int xoffset = (width - www)/2;
				g.drawString(f, x+ xoffset, y + yoffset);
				yoffset += lineH;
			} 
		}

		public UIComponent getUpdateData(int leftoffset, int topoffset) {
			UIComponent ndata = new UIComponent();
			ndata.t = data.t;
			ndata.op = data.op;
			int x2 = x - leftoffset;
			int y2 = y - topoffset;
			ndata.x = x2;
			ndata.y = y2;
			ndata.w  = width;
			ndata.h = height;
		//	ndata.s = x2 + ":" + y2 + ":" + width + ":" + height;
			ndata.c = data.c;
			return ndata;
		}
	}

	public class TextChartComponent extends ChartComponent {

		public TextChartComponent(UIComponent data) {
			super(data);
		}
		public TextChartComponent( ) {
			super(ToolType.Text.type);
		}

		public void paintComponent(Graphics2D g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			
		}
		public   TextChartComponent copy() {
			TextChartComponent copy = new TextChartComponent();
			super.copyPropertiesTo(copy);
			return copy;
		}
	}
	
	public class CircleChartComponent extends ChartComponent {

		public CircleChartComponent(UIComponent data) {
			super(data);
		}
		public CircleChartComponent( ) {
			super(ToolType.Circle.type);
		}
		public void paintComponent(Graphics2D g) {
			super.paintComponent(g);
			g.drawArc(x, y, width, height, 0, 360);
		}
		
		public   CircleChartComponent copy() {
			CircleChartComponent copy = new CircleChartComponent();
			super.copyPropertiesTo(copy);
			return copy;
		}
	}


	public class RectChartComponent extends ChartComponent {

		public RectChartComponent(UIComponent data) {
			super(data);
		}
		public RectChartComponent( ) {
			super(ToolType.Rect.type);
		}
		public void paintComponent(Graphics2D g) {
			super.paintComponent(g);
			g.drawRect(x, y, width, height);
		}
		
		public   RectChartComponent copy() {
			RectChartComponent copy = new RectChartComponent();
			super.copyPropertiesTo(copy);
			return copy;
		}
	}

	public class RoundRectChartComponent extends ChartComponent {

		public RoundRectChartComponent(UIComponent data) {
			super(data);
		}
		public RoundRectChartComponent( ) {
			super(ToolType.RoundRect.type);
		}
		public void paintComponent(Graphics2D g) {
			super.paintComponent(g);
			g.drawRoundRect(x, y, width, height, 15, 15);
		}
		
		public   RoundRectChartComponent copy() {
			RoundRectChartComponent copy = new RoundRectChartComponent();
			super.copyPropertiesTo(copy);
			return copy;
		}
	}

	public class DiamondChartComponent extends ChartComponent {

		public DiamondChartComponent(UIComponent data) {
			super(data);
		}
		public DiamondChartComponent( ) {
			super(ToolType.Diamond.type);
		}
		public void paintComponent(Graphics2D g) {
			super.paintComponent(g);
			g.drawLine(x + width / 2, y, x, y + height / 2);
			g.drawLine(x, y + height / 2, x + width / 2, y + height);
			g.drawLine(x + width / 2, y + height, x + width, y + height / 2);
			g.drawLine(x + width / 2, y, x + width, y + height / 2);
		}
		
		public   DiamondChartComponent copy() {
			DiamondChartComponent copy = new DiamondChartComponent();
			super.copyPropertiesTo(copy);
			return copy;
		}
	}

	public class ParallChartComponent extends ChartComponent {

		public ParallChartComponent(UIComponent data) {
			super(data);
		}
		public ParallChartComponent( ) {
			super(ToolType.Parallelogram.type);
		}
		public void paintComponent(Graphics2D g) {
			super.paintComponent(g);
			int xoffset = width / 9;
			g.drawLine(x + xoffset, y, x + width, y);
			g.drawLine(x + width, y, x + width - xoffset, y + height);
			g.drawLine(x + width - xoffset, y + height, x, y + height);
			g.drawLine(x, y + height, x + xoffset, y);
		}
		
		public   ParallChartComponent copy() {
			ParallChartComponent copy = new ParallChartComponent();
			super.copyPropertiesTo(copy);
			return copy;
		}
	}

	public class LineChartComponent extends ChartComponent {

		public LineChartComponent(UIComponent data) {
			super(data);
		}
		public LineChartComponent( ) {
			super(ToolType.Line.type);
		}
		public void paintComponent(Graphics2D g) {
			super.paintComponent(g);
			boolean horizontal = width > height;
			if (horizontal) {
				g.drawLine(x, y + height / 2, x + width, y + height / 2);
				if (data.t.equals("al")) {
					int xx = x;
					int yy = y +height/2;
					g.fillPolygon(new int[]{xx,xx+10, xx+10},  new int[] {yy, yy-5, yy+5}, 3); 
				} else if (data.t.equals("ar")) {
					int xx = x + width;
					int yy = y +height/2;
					g.fillPolygon(new int[]{xx,xx-10, xx-10},  new int[] {yy, yy-5, yy+5}, 3); 
				}
			} else {
				g.drawLine(x + width / 2, y, x + width / 2, y + height);
				if (data.t.equals("al")) {
					int xx = x + width/2;
					int yy = y ;
					g.fillPolygon(new int[]{xx,xx-5, xx+5},  new int[] {yy, yy+10, yy+10}, 3); 
				} else if (data.t.equals("ar")) {
					int xx = x + width/2;
					int yy = y + height ;
					g.fillPolygon(new int[]{xx,xx-5, xx+5},  new int[] {yy, yy-10, yy-10}, 3); 
				}
			} 
		}
		public Point[] getEnds() {
			Point[] ps = new Point[2];
			boolean horizontal = width > height;
			if (horizontal) {
				ps[0] = new Point(x, y + height/2);
				ps[1] = new Point(x+width, y + height/2);
				  
			} else {
				ps[0] = new Point(x + width / 2, y  );
				ps[1] = new Point(x + width / 2, y + height );
				 
			} 
			return ps;
		}
		
		public void adjustToHookPosition() {
			boolean horizontal = width > height;
			if( !horizontal) {
				super.adjustToHookPosition();
			}
			
			List<Point> ends = getEndPositions(this);
			int minD = Integer.MAX_VALUE;
			Point found= null;
			 Point[] selfends = getEnds();
			int x1 =  selfends[0].x;
			int y1 =  selfends[0].y;
			for(Point p : ends) {
				int d = (int)Math.sqrt((x1 - p.x)*(x1 - p.x) + (y1 -p.y)*(y1 -p.y));
				if( d < minD) {
					minD = d;
					found = p;
				}
			}
			if( minD < 10) {
				if( horizontal ) {
					x += found.x -x1;
					width -= found.x -x1;
				} 
				y += found.y - y1;
				height -= found.y - y1;
				
				return;
			}
			 minD = Integer.MAX_VALUE;
		    found= null;
		    x1 =  selfends[1].x;
		    y1 =  selfends[1].y;
			for(Point p : ends) {
				int d = (int)Math.sqrt((x1 - p.x)*(x1 - p.x) + (y1 -p.y)*(y1 -p.y));
				if( d < minD) {
					minD = d;
					found = p;
				}
			}
			if( minD < 10) {
				if( horizontal ) {
				    width += found.x -x1;
				}
				height += found.y - y1;
				return;
			}
			
			//let's adjust to line body?
			List<Point> ends2 = getCloseLine(this, selfends[0],   !horizontal);
			if( horizontal ) {
				found = null;
				 minD = Integer.MAX_VALUE;
				x1 = selfends[0].x;
				for(Point p : ends2) {
					int d =  Math.abs(p.x - x1);
					if( d < minD) {
						minD = d;
						found = p;
					}
				}
				if(minD < 10 ) {
					this.x += minD;
					this.width -= minD;
				}
				
				found = null;
				 minD = Integer.MAX_VALUE;
				x1 = selfends[1].x;
				for(Point p : ends2) {
					int d =  Math.abs(p.x - x1);
					if( d < minD) {
						minD = d;
						found = p;
					}
				}
				if(minD < 10 ) { 
					this.width += minD;
				}
			} else { //vertical
				found = null;
				 minD = Integer.MAX_VALUE;
				y1 = selfends[0].y;
				for(Point p : ends2) {
					int d =  Math.abs(p.y - y1);
					if( d < minD) {
						minD = d;
						found = p;
					}
				}
				if(minD < 10 ) {
					this.y += minD;
					this.height -= minD;
				}
				
				found = null;
				 minD = Integer.MAX_VALUE;
				y1 = selfends[1].y;
				for(Point p : ends2) {
					int d =  Math.abs(p.y - y1);
					if( d < minD) {
						minD = d;
						found = p;
					}
				}
				if(minD < 10 ) { 
					this.height += minD;
				}
			}
			
			
		} 
		public   LineChartComponent copy() {
			LineChartComponent copy = new LineChartComponent();
			super.copyPropertiesTo(copy);
			return copy;
		}
	}


}
