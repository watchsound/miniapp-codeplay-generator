package com.pi.code.tool.codefillin;
 

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;
 
 

public class ImageUtil {
	 public static Image makeColorTransparent
	    (Image im, final Color color) {
	    ImageFilter filter = new RGBImageFilter() {
	      // the color we are looking for... Alpha bits are set to opaque
	      public int markerRGB = color.getRGB() | 0xFF000000;

	      public final int filterRGB(int x, int y, int rgb) {
	        if ( ( rgb | 0xFF000000 ) == markerRGB ) {
	          // Mark the alpha bits as zero - transparent
	          return 0x00FFFFFF & rgb;
	          }
	        else {
	          // nothing to do
	          return rgb;
	          }
	        }
	      }; 

	    ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	    return Toolkit.getDefaultToolkit().createImage(ip);
	    }
	 
	 public static BufferedImage toBufferedImage(Image img)
	 {
	     if (img instanceof BufferedImage)
	     {
	         return (BufferedImage) img;
	     }

	     // Create a buffered image with transparency
	     BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	     // Draw the image on to the buffered image
	     Graphics2D bGr = bimage.createGraphics();
	     bGr.drawImage(img, 0, 0, null);
	     bGr.dispose();

	     // Return the buffered image
	     return bimage;
	 }
	 
	public static BufferedImage resizeImage(Image image, int width, int height) {
		return resizeImage(image, width, height, false, false);
	}
	public static BufferedImage resizeImage(Image image, int width, int height, boolean keepRatio, boolean rotateIfNeeded) {
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
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		
		return rotateIfNeeded ?   rotate90ToLeftIfNeeded( thumbImage ) : thumbImage;
	}
	
	public static BufferedImage copyImage(BufferedImage image ) {
         
			int imageWidth = image.getWidth( );
			int imageHeight = image.getHeight( ); 
	 
		BufferedImage thumbImage = new BufferedImage(imageWidth, imageHeight, 
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, imageWidth, imageHeight, null);
		
		return  thumbImage;
	}
	
	public static BufferedImage resizeImage(Image image, double scale, boolean rotateIfNeeded) {
       
			int width = (int)(image.getWidth(null) * scale);
			int height = (int)( image.getHeight(null) * scale);
	 
		BufferedImage thumbImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		
		return rotateIfNeeded ? rotate90ToLeftIfNeeded ( thumbImage ) : thumbImage;
	}
	public static BufferedImage resizeImage2(Image image, double scale, boolean rotateIfNeeded) {
	       
		int width = (int)(image.getWidth(null) * scale);
		int height = (int)( image.getHeight(null) * scale);
 
	BufferedImage thumbImage = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);
	Graphics2D graphics2D = thumbImage.createGraphics();
	graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	graphics2D.drawImage(image, 0, 0, width, height, null);
	
	return rotateIfNeeded ? rotate90ToLeftIfNeeded ( thumbImage ) : thumbImage;
}
	
	public static BufferedImage  cropImage(Image image,  Rectangle rect){ 
		CropImageFilter   cropFilter = new CropImageFilter(rect.x, rect.y, rect.width, rect.height);  
		Image  img = Toolkit.getDefaultToolkit().createImage(
				new FilteredImageSource(image.getSource(), cropFilter));  
        BufferedImage tag = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_ARGB);  
          Graphics g = tag.getGraphics();  
          g.drawImage(img, 0, 0, null); // ����Сͼ  
          g.dispose();  
          return tag; 
	}
 
 
	public static BufferedImage getImageFromFile(File file, Component comp){
		Image image = Toolkit.getDefaultToolkit().getImage(file.getAbsolutePath());
		  MediaTracker tracker = new MediaTracker(comp);  
	        tracker.addImage(image,1);   
	          //wait for images to load  
	        try{  
	            tracker.waitForID(1);  
	        }catch(InterruptedException e){  
	        	 
	        }  
	        
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage thumbImage = new BufferedImage( width, 
                                                      height,
                                                      BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, width, height, null);
		
		return thumbImage;
	}
	
	public static BufferedImage createBlackImage(int width, int height){
		 
		BufferedImage thumbImage = new BufferedImage( width, 
                                                      height,
				                                      BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	//	graphics2D.setColor(Color.white);
	//	graphics2D.fillRect(0, 0, width, height);
	 
		return thumbImage;
	}
	
	 
	
	
	public static void createThumbnail(String filename, int thumbWidth,
			int thumbHeight, int quality, String outFilename, boolean forceFit)
			throws InterruptedException, FileNotFoundException, IOException {
		// load image from filename
		Image image = Toolkit.getDefaultToolkit().getImage(filename);
		 
		BufferedImage thumbImage =   (BufferedImage) resizeImage(image, thumbWidth, thumbHeight, forceFit, true);
		 
		// save thumbnail image to outFilename
		saveImageToFile(thumbImage, outFilename, quality);
	}
	
	
	public static void createThumbnail(String filename, int thumbWidth,
			int thumbHeight, int quality, String outFilename)
			throws InterruptedException, FileNotFoundException, IOException {
		// load image from filename
		Image image = Toolkit.getDefaultToolkit().getImage(filename);
		 
		BufferedImage thumbImage =   (BufferedImage) resizeImage(image, thumbWidth, thumbHeight);
		 
		// save thumbnail image to outFilename
		saveImageToFile(thumbImage, outFilename, quality);
	}
	
	public static String saveImageToFile(BufferedImage image, String outFilename,  int quality) throws IOException
	{
		File f =new File(  outFilename);
	    if(!f.exists()){
		  f.createNewFile();
	    }
	    String type = outFilename.substring(outFilename.lastIndexOf(".") + 1);
	    Set format = getFormats();
	 //   if ( !format.contains(type) )
	    	type = "png";
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(  outFilename));
		ImageIO.write(image, type, out);
		out.close();
		return f.getAbsolutePath();
	}
	
	public static String saveImageToFile(Image image, String outFilename,  int quality, boolean rotateIfNeeded) throws IOException
	{
		BufferedImage bimage = resizeImage(image, 1.0, rotateIfNeeded);
		return saveImageToFile(bimage, outFilename, quality);
	}
	
	 public static Set getFormats() {
	        String[] formats = ImageIO.getWriterFormatNames();
	        Set<String> formatSet = new TreeSet<String>();
	        for (String s : formats) {
	            formatSet.add(s.toLowerCase());
	        }
	        return formatSet;
	    }
	
	public static boolean deleteImageFile(String fileName){
		  boolean success = Boolean.FALSE;  
	        File f = new File(fileName);  
	        if (f.exists()) {  
	           f.delete();  
	           success = Boolean.TRUE;  
	        }   
	        return success;  
	}
	public static String createUID(){
		return createUID(40);
	}
	public static int createUniqueIntID(){
		return createUID(10).hashCode();
	}
	public static String createCompactUID(){
		return createUID(10);
	}
	
	public static String createUID(int length)
	{
		String uuid = UUID.randomUUID().toString();
		//create a random string with 40 characters.
		char[]  uid = new char[length];
		Random random = new Random();
		 uid[0] = 'a';
		for (int i =1; i < length; i++ )
		{
			uid[i] = uuid.charAt(random.nextInt(uuid.length()));
			if ( uid[i] == '-' )
				uid[i] = '_';
		}
		return new String(uid);
	}
	
	public static BufferedImage  rotate90ToLeftIfNeeded( BufferedImage inputImage ){
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		if ( width - 50 > height   ){
			return rotate90ToLeft( inputImage );
		} else {
			return inputImage;
		}
	}
	
	public static BufferedImage rotate90ToLeft( BufferedImage inputImage ){
		return rotate(inputImage, 90);
//		//The most of code is same as before
//			int width = inputImage.getWidth();
//			int height = inputImage.getHeight();
//			BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );
//		//We have to change the width and height because when you rotate the image by 90 degree, the
//		//width is height and height is width <img src='http://forum.codecall.net/public/style_emoticons/<#EMO_DIR#>/smile.png' class='bbc_emoticon' alt=':)' />
//
//			for( int x = 0; x < width; x++ ) {
//				for( int y = 0; y < height; y++ ) {
//					returnImage.setRGB(y, width - x - 1, inputImage.getRGB( x, y  )  );
//		//Again check the Picture for better understanding
//				}
//				}
//			return returnImage;

		}
	
	public static BufferedImage rotate(BufferedImage img, double angle)
	{
	    double sin = Math.abs(Math.sin(Math.toRadians(angle))),
	           cos = Math.abs(Math.cos(Math.toRadians(angle)));

	    int w = img.getWidth(null), h = img.getHeight(null);

	    int neww = (int) Math.floor(w*cos + h*sin),
	        newh = (int) Math.floor(h*cos + w*sin);

	    BufferedImage bimg = new  BufferedImage( neww, newh, img.getType());
	    Graphics2D g = bimg.createGraphics();

	    g.translate((neww-w)/2, (newh-h)/2);
	    g.rotate(Math.toRadians(angle), w/2, h/2);
	    g.drawRenderedImage( img , null);
	    g.dispose();

	    return  bimg ;
	}
	
	public static Dimension getMergedSize(List<BufferedImage> imageList){
		int width = 0;
		int height = Integer.MIN_VALUE;
		for( BufferedImage image : imageList){
			width += image.getWidth();
			height =  Math.max(height, image.getHeight());
		}
		return new Dimension(width, height);
	}
	
	 
	
	public static class ImageWrapper {
		public String uuid;
		public String name;
		public Image image;
	}
	  
	public static boolean isTextFile(File file){
		 String name = file.getName();
    	 if( name.endsWith(".txt") )
    		 return true;
    	 String mimetype= new MimetypesFileTypeMap().getContentType(file);
    	 if( mimetype == null){
    		 return false;
    	 }
    	 return mimetype.equals("text/plain");
	}
	public static boolean isSVGFile(File file){
		 String name = file.getName();
   	   if( name.endsWith(".svg") )
   		   return true;
   	   return false;
	}
	
    public static boolean isImageFile(File file){
    	if (file.isDirectory())
    		return false;
    	if ( file.isHidden())
    		return false;
    	if( !file.canRead())
    		return false;
    	 String name = file.getName();
    	 if( name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif") )
    		 return true;
    	 String mimetype= new MimetypesFileTypeMap().getContentType(file);
    	 String type = "";
    	 if( mimetype == null){
    			try {
					mimetype = Files.probeContentType(file.toPath());
					if( mimetype == null ){
						return false;
					}
					//if("application/octet-stream".equals(mimetype))
					//	return true;
					 type = mimetype.split("/")[0];
					 return type.equals("image");
				} catch (Exception e) {
					 
				} 
    	 }
    	// if("application/octet-stream".equals(mimetype))
		//		return true;
         type = mimetype.split("/")[0];
         if(type.equals("image"))
             return true;
         else {
        	 try {
				mimetype = Files.probeContentType(file.toPath());
				if( mimetype == null ){
					return false;
				}
				//if("application/octet-stream".equals(mimetype))
				//	return true;
				 type = mimetype.split("/")[0];
				 return type.equals("image");
			} catch (Exception e) {
			    return false;
			}
         }
            
    }
    
    public static Dimension getScreenSize( ){
        return Toolkit.getDefaultToolkit().getScreenSize(); 
    } 
    
    public static Dimension getScreenSize(Window window) {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screensize.width;
        int h = screensize.height;
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
          window.getGraphicsConfiguration());
        w = w - (screenInsets.left + screenInsets.right);
        h = h - (screenInsets.top + screenInsets.bottom); 
        return new Dimension(w, h);
    } 
}
