package omniDesk.net.rdp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Message;

public class WrappedImage {
    ColorModel256 cm = null;
    Message msg;
    public static android.graphics.Bitmap bi=null ; 
    public static final int TYPE_BYTE  = 0;
    public static final int TYPE_USHORT = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT   = 3;
    public static final int TYPE_FLOAT  = 4;
    public static final int TYPE_DOUBLE  = 5;
    public static final int TYPE_UNDEFINED = 32;
    protected int transferType;
    boolean supportsAlpha = true;
    public WrappedImage(int width, int height) {
    	Common.bitmapHeight=bi.getHeight();
    	Common.bitmapWidth=bi.getWidth();	
    }
    
    public WrappedImage(int arg0, int arg1, int arg2, ColorModel256 cm) {
        bi = Bitmap.createBitmap(arg0,arg1,Bitmap.Config.RGB_565);     
        this.cm = cm;
    } 
    public int getWidth(){ return bi.getWidth(); }
    public int getHeight(){ return bi.getHeight(); }
    public android.graphics.Bitmap getBufferedImage(){ return bi; }
    public Bitmap getSubimage(int x,int y, int width, int height){
        return Bitmap.createScaledBitmap(bi,width,height,false);
    }
 
    public void setIndexColorModel(ColorModel256 cm){
        this.cm = cm;
    }
    
    public void setRGB(int x, int y, int color){     
    	color = ColorModel256.getColors()[color];
        bi.setPixel(x,y,color);
    }
    /**
     * Apply a given array of colour values to an area of pixels in the image, do not convert for colour model
     * @param x x-coordinate for left of area to set
     * @param y y-coordinate for top of area to set
     * @param cx width of area to set
     * @param cy height of area to set
     * @param data array of pixel colour values to apply to area
     * @param offset offset to pixel data in data
     * @param w width of a line in data (measured in pixels)
     */
    public void setRGBNoConversion(int x, int y, int cx, int cy, int[] data, int offset,int w){
    	bi.setPixels(data, offset, w, x, y, w, cy);
    }
    public void setRGB(int x, int y, int cx, int cy, int[] data, int offset,int width){
        bi.setPixels(data, offset,  width, x, y,cx,cy);
        Common.BITMAP_READY_TO_RENDER=true;
    
    } 
   public int[] getRGB(int x,
            int y,
            int cx,
            int cy,
            int[] data,
            int offset, 
            int width){
       		int [] retData = null;
	   bi.getPixels(retData, offset, width, x, y, width,cy); 
    	return null; 
    }
      
    public int getRGB(int x, int y){
        if(x >= this.getWidth() || x < 0 || y >= this.getHeight() || y < 0) return 0;
        if(cm == null) return bi.getPixel(x, y);
        int pix = bi.getPixel(x, y) & 0xFFFFFF;
        int out=Color.rgb(((pix >> 16) & 0xFF),((pix >> 8) & 0xFF),((pix) & 0xFF));
        return out;
    }
}
