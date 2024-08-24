package com.pi.code.tool.flowchart; 

import java.text.NumberFormat;
 

public class NumberHelp {
	public static final float epsilon = 0.00000001f;
	static NumberFormat format = NumberFormat.getInstance();
	
	public static String trimFloat(double value){
	    format.setMaximumFractionDigits(2);
	    return format.format(value);
	}
	public static double[] copy(double[] values){
		double[] copyed = new double[values.length];
		for(int i = 0; i < values.length; i++)
			copyed[i] = values[i];
		return copyed;
	}
	 
	public static final boolean isSameValue(float v1, float v2){
		return Math.abs(v1 - v2) < epsilon;
	}
	public static final boolean isSameValue(double v1, double v2){
		return Math.abs(v1 - v2) < epsilon;
	}
	public static final boolean isZero(double v){
		return Math.abs(v) < epsilon;
	}
	//if both v1 and v3 are null,  treat them as same
	public static final boolean isSameValue(String v1, String v2){
		 if ( v1 == null && v2 == null )
			 return true;
		 if ( v1 != null )
			 return v1.equals(v2);
		 return v2.equals(v1);
	}
	
	public static int getEstimatedDurationForText(String content){
		if ( content == null || content.length() < 2 )
			return 0;
		return content.length() / 12 + 1;
	}
}
