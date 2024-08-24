package com.pi.code.tool.flowchart;


import java.awt.geom.Point2D;
 
 
public class GeomUtil {
	
	public static class AngleData{
		public double angle;
		public double angle1;
		public double angle2;
	}
    /**
     * calculate the point created by starting at the point point, then moving
     * dist along the line at the angle angle, in degrees.
     */
    public static Point2D calcPoint(Point2D point, double angle, double dist) {
        return new Point2D.Double(
                point.getX()+Math.sin(Math.toRadians(angle))*dist,
                point.getY()+Math.cos(Math.toRadians(angle))*dist
        );
    }
    
    public static Point2D calcPoint2(Point2D point, double angle, double dist) {
        return new Point2D.Double(
                point.getX()+Math.cos(Math.toRadians(angle))*dist,
                point.getY()+Math.sin(Math.toRadians(angle))*dist
        );
    }

    /**
     * snap angle to the nearest 45 degree axis
     * @param angle the angle in degrees
    */
    public static double snapTo45(double angle) {
        angle = (angle+360) % 360; //make positive
        long iangle = Math.round(angle / 45); //round to nearest octant
        return iangle * 45.0;
    }
    
    public static double distance(Point2D point1, Point2D point2){
    	return point1.distance(point2);
    }
 
    /**
     *  calculate the angle of the line formed by the two points, in degrees
     */
    public static double calcAngle(Point2D point1, Point2D point2) {
        return calcAngle(point1, point2, false);
    }
    
    public static double calcAngle(Point2D point1, Point2D point2, boolean considerFlip) {
    	if ( considerFlip ){
    	    int yHeight = (int) point1.getY();
            return Math.toDegrees(Math.atan2( yHeight -point2.getY(), point2.getX()-point1.getX() ) );
    	} else {
    		return Math.toDegrees(Math.atan2(point2.getY()-point1.getY(), point2.getX()-point1.getX()));
    	}
    	
//    	if ( considerFlip ){
//    	    int yHeight = Constants.CARD_HEIGHT;
//            return Math.toDegrees(Math.atan2(point2.getX()-point1.getX(), yHeight -point2.getY()- yHeight + point1.getY()));
//    	} else {
//    		return Math.toDegrees(Math.atan2(point2.getX()-point1.getX(),point2.getY()-point1.getY()));
//    	}
    }
    
    public static double calcAngle(double x1, double y1, double x2, double y2) {
        return Math.toDegrees(Math.atan2(x2-x1,y2-y1));
    }
    
    public static double calcAngle(Point2D point1, Point2D point2, Point2D base)   {
     
    		double x1 = point1.getX() - base.getX();
        	double y1 = point1.getY() - base.getY();
        	
        	double x2 = point2.getX() - base.getX();
        	double y2 = point2.getY() - base.getY();
            return Math.toDegrees(Math.atan2(x2-x1,y2-y1)); 
    }
    

    public static Point2D subtract(Point2D point1, Point2D point2) {
        return new Point2D.Double(point1.getX()-point2.getX(),point1.getY()-point2.getY());
    }
    
    public boolean isBetween(Point2D start, Point2D end, Point2D target){
    	return  (  ( start.getX() <= target.getX() && target.getX() <= end.getX()  ) ||
    			   ( start.getX() >= target.getX() && target.getX() >= end.getX()    ) ) && 
    			   (  ( start.getY() <= target.getY() && target.getY() <= end.getY()  ) ||
    	    			   ( start.getY() >= target.getY() && target.getY() >= end.getY()    )  );
    }
    
    
    public boolean isInline(Point2D start, Point2D end, Point2D target){
    	 double angle1 = calcAngle( start, target);
    	 double angle2 = calcAngle( target, end);
    	 return  NumberHelp.isSameValue(angle1, angle2);
    }
    
    public static AngleData calAngleFromLinePoints(Point2D point, Point2D prevPoint, Point2D prevprevPoint){
    	return calAngleFromLinePoints(point, prevPoint, prevprevPoint, false);
    }
    
    public static AngleData calAngleFromLinePoints(Point2D startPoint, Point2D middlePoint, Point2D endPoint, boolean considerFlip){
    	 
    	AngleData data = new AngleData();
         if( considerFlip ){
             double angle = GeomUtil.calcAngle(middlePoint,startPoint, considerFlip);
             System.out.println(" angle = " + angle  + " (" + middlePoint + " :" + startPoint);
//             if ( angle >=0 && angle < 90  ){ 
//            	 angle = 90 - angle ; 
//             } else if ( angle >= -180 && angle < -90){ 
//            	 angle = 90 - angle;
//             } else if ( angle <0 && angle > -90 ){ 
//            	 angle = 90 - angle;
//             } else { // angle >= 90
//            	 angle = 450 - angle; 
//             }
             if( angle < 0)
            	 angle = 360 + angle;
             data.angle1 = angle;
             System.out.println(" angle 1= " + angle );
             if ( endPoint == null ){
            	 data.angle = angle;
            	 return data;
              } else {
            	   double angle2 = GeomUtil.calcAngle( middlePoint, endPoint, considerFlip);
            	   System.out.println(" angle 2 = " + angle2  + " (" + middlePoint + " :" + endPoint);
//            	   if ( angle2 >=0 && angle2 < 90  ){ 
//            		   angle2 = 90 - angle2 ; 
//                   } else if ( angle2 >= -180 && angle2 < -90){ 
//                	   angle2 = 90 - angle;
//                   } else if ( angle2 <0 && angle2 > -90 ){ 
//                	   angle2 = 90 - angle2;
//                   } else { // angle >= 90
//                	   angle2 = 450 - angle2 ; 
//                   }
            	   if( angle2 < 0)
                  	 angle2 = 360 + angle2;
            	   data.angle2 = angle2;
            	   System.out.println(" angle 2= " + angle2 );
            	 double angle3 = Math.abs( angle2 - angle);
            	// angle3 = angle3 < 180 ? angle3 : 360 - angle3;
            	 System.out.println(" angle 3= " + angle3 );
            	 data.angle = angle3;
            	 return data;
              }
         } else {
             double angle = GeomUtil.calcAngle(middlePoint,startPoint, considerFlip);
             System.out.println(" angle = " + angle  + " (" + middlePoint + " :" + startPoint);
//             if ( angle >=0 && angle < 90  ){
//            	 angle = angle + 270;
//             } else if ( angle >= 90 && angle < 180){
//            	 angle = angle - 90;
//             } else if ( angle <0 && angle > -90 ){
//            	 angle = angle + 270;
//             } else { // angle <= -90 && angle > -180
//            	 angle = angle + 270; 
//             }
             if( angle < 0)
            	 angle = 360 + angle;
             data.angle1 = angle;
             System.out.println(" angle = " + angle );
             if ( endPoint == null ){
            	 data.angle = angle;
            	 return data;
              } else {
            	   double angle2 = GeomUtil.calcAngle(endPoint,middlePoint, considerFlip);
//                 if ( angle2 >=0 && angle2 < 90  ){
//                	 angle2 = angle2 + 270;
//                 } else if ( angle2 >= 90 && angle2 < 180){
//                	 angle2 = angle2 - 90;
//                 } else if ( angle <0 && angle > -90 ){
//                	 angle2 = angle2 + 270;
//                 } else { // angle <= -90 && angle > -180
//                	 angle2 = angle2 + 270; 
//                 }
            	   if( angle2 < 0)
                  	 angle2 = 360 + angle2;
                 data.angle2 = angle2;
            	 double angle3 = Math.abs( angle2 - angle);
            	// angle3 = angle3 < 180 ? angle3 : 360 - angle3;
            	 data.angle = angle3;
            	 return data;
              } 
         }
  
    }
}
