package omniDesk.net.rdp;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;


public class OmniDeskCanvas extends Canvas{
    
	public static final int ROP2_COPY = 0xc;

    private static final int ROP2_XOR = 0x6;

    private static final int ROP2_AND = 0x8;

    private static final int ROP2_NXOR = 0x9;

    private static final int ROP2_OR = 0xe;

    private static final int MIX_TRANSPARENT = 0;

    private static final int MIX_OPAQUE = 1;

    private static final int TEXT2_VERTICAL = 0x04;

    private static final int TEXT2_IMPLICIT_X = 0x20;

//    public KeyCode keys = null;

//    public KeyCode_FileBased fbKeys = null;

    public String sKeys = null;

    public int width = 0;

    public int height = 0;

    // private int[] colors = null; // needed for integer backstore

    protected ColorModel256 colormap = null;
    WrappedImage backstore;

    //neer private Cache cache = null;

    public Rdp rdp = null;

    // protected int[] backstore_int = null;
    // Clip region
    private int top = 0;

    private int left = 0;

    private int right = 0;

    private int bottom = 0;

    public Rect Screen = null;
    /**
     * Initialise this canvas to specified width and height, also initialise
     * backstore
     * 
     * @param width
     *            Desired width of canvas
     * @param height
     *            Desired height of canvas
     */
    public OmniDeskCanvas(int width, int height) {
        super();
       //rop = new RasterOp();  amd
        this.width = width;
        this.height = height;
        this.right = width - 1; // changed
        this.bottom = height - 1; // changed
        Screen = new Rect();
        Screen.set(0, 0,width, height);

        backstore = new WrappedImage(width, height);//andrBufferedImage.TYPE_INT_RGB

        // now do input listeners in registerCommLayer() / registerKeyboard()
    }
    
    /**
     * Draw an image (from an integer array of colour data) to the backstore,
     * does not call repaint. Image is drawn to canvas on next update.
     * 
     * @param data
     *            Integer array of pixel colour information
     * @param w
     *            Width of image
     * @param h
     *            Height of image
     * @param x
     *            x coordinate for drawing location
     * @param y
     *            y coordinate for drawing location
     * @param cx
     *            Width of drawn image (clips, does not scale)
     * @param cy
     *            Height of drawn image (clips, does not scale)
     * @throws RdesktopException
     */
    public void displayImage(int[] data, int w, int h, int x, int y, int cx,
            int cy) throws RdesktopException {
    	Log.d("omnideskcanvas.java"," displayImage");
        backstore.setRGB(x, y, cx, cy, data, 0, w);

       /*  ********* Useful test for identifying image boundaries ************ 
        // Graphics g = backstore.getGraphics();
        // g.drawImage(data,x,y,null);
        // g.setColor(Color.RED);
        // g.drawRect(x,y,cx,cy);
        // g.dispose();
*/    
    }
    
    
    /**
     * Display a compressed bitmap direct to the backstore NOTE: Currently not
     * functioning correctly, see Bitmap.decompressImgDirect Does not call
     * repaint. Image is drawn to canvas on next update
     * 
     * @param x
     *            x coordinate within backstore for drawing of bitmap
     * @param y
     *            y coordinate within backstore for drawing of bitmap
     * @param width
     *            Width of bitmap
     * @param height
     *            Height of bitmap
     * @param size
     *            Size (bytes) of compressed bitmap data
     * @param data
     *            Packet containing compressed bitmap data at current read
     *            position
     * @param Bpp
     *            Bytes-per-pixel for bitmap
     * @param cm
     *            Colour model currently in use, if any
     * @throws RdesktopException
     * @throws OmniDeskException 
     */
    public void displayCompressed(int x, int y, int width, int height,
            int size, RdpPacket_Localised data, int Bpp)
            throws RdesktopException, OmniDeskException {
    	
    	Log.d("omnideskcanvas.java","displayCompress");
        backstore = Bitmap.decompressImgDirect(width, height, size, data, Bpp,
                x, y, backstore);
    }
    public void registerPalette(ColorModel256 cm) {
        this.colormap = cm;
        backstore.setIndexColorModel(cm);
    }
    
}
