package omniDesk.net.rdp;

import omniDesk.rdp.orders.BoundsOrder;
import omniDesk.rdp.orders.Brush;
import omniDesk.rdp.orders.LineOrder;
import omniDesk.rdp.orders.PatBltOrder;
import omniDesk.rdp.orders.RectangleOrder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class ImageViewer extends SurfaceView {
	RasterOp rop = null;
	Paint paint;
	Rect src;
	Rect dst;
	int top = 0, left = 0, right, bottom;
	int[] rectangle;

	private static final int MIX_TRANSPARENT = 0;

	private static final int MIX_OPAQUE = 1;

	public ImageViewer(Context context,AttributeSet attributeset) {

		super(context,attributeset);
		setWillNotDraw(false);
		paint = new Paint();
		src = new Rect();
		dst = new Rect();
		right = Options.width - 1;
		bottom = Options.height - 1;
		rectangle = new int[Options.height * Options.width];
		rop = new RasterOp();

		paint.setStyle(Paint.Style.FILL);

	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d("ImageDraw.java", "drawImage Called");
		Common.canvasWidth = canvas.getWidth();
		Common.canvasHeight = canvas.getHeight();

		int width,height;
		if(Common.Fullscreen)
		{
			width=Common.bitmapWidth;
			height=Common.bitmapHeight;
			Common.x=Common.y=0;
		}
		else
		{
			width=Common.canvasWidth;
			height=Common.canvasHeight;
			
		}
		
		src.set((int) Common.x, (int) Common.y,
				(int) (Common.x + width),
				(int) (Common.y + height));
		dst.set(0, 0, Common.canvasWidth, Common.canvasHeight);

		Log.d("ImageDraw.java", "x=" + Common.x + " y=" + Common.y);
	
		try {
			canvas.drawBitmap(WrappedImage.bi, src, dst, paint);
		} catch (Exception e) {
			System.out.println("Expected: " + e);
		}
		

	}

	public void setClip(BoundsOrder bounds) {
		Log.d("imageviewer", "set clip");
		top = bounds.getTop();
		left = bounds.getLeft();
		right = bounds.getRight();
		bottom = bounds.getBottom();

	}

	public void drawRectangleOrder(RectangleOrder rect) {

		int x = rect.getX();
		int y = rect.getY();
		int cx = rect.getCX();
		int cy = rect.getCY();
		int color = rect.getColor();

		Log.d("imageviewer.java", "x : " + x + " y : " + y + "cx : " + cx
				+ "cy : " + cy + "  color : " + Integer.toHexString(color));

		if (x > this.right || y > this.bottom)
			return; // off screen

		int Bpp = Options.Bpp;

		// convert to 24-bit colour
		color = Bitmap.convertTo24(color);

		// correction for 24-bit colour
		if (Bpp == 3)
			color = ((color & 0xFF) << 16) | (color & 0xFF00)
					| ((color & 0xFF0000) >> 16);

		/*
		 * // Perform standard clipping checks, x-axis int clipright = x + cx -
		 * 1; if (clipright > this.right) clipright = this.right; if (x <
		 * this.left) x = this.left; cx = clipright - x + 1;
		 * 
		 * // Perform standard clipping checks, y-axis int clipbottom = y + cy -
		 * 1; if (clipbottom > this.bottom) clipbottom = this.bottom; if (y <
		 * this.top) y = this.top; cy = clipbottom - y + 1;
		 */

		Log.d("imageviewer.java", "x : " + x + " y : " + y + "cx : " + cx
				+ "cy : " + cy + "  color : " + Integer.toHexString(color));
		// construct rectangle as integer array, filled with color

		try {
			
			int length = cx * cy;
			for (int i = 0; i < length; i++)
				rectangle[i] = color;

			WrappedImage.bi.setPixels(rectangle, 0, cx, x, y, cx, cy);
			
			Common.currentImageViewer.postInvalidate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}

	}

	public void drawPatBltOrder(PatBltOrder patblt) {

		System.out.println("Inside DRAW PAT BLT order");
		Brush brush = patblt.getBrush();
		int x = patblt.getX();
		int y = patblt.getY();

		if (x > this.right || y > this.bottom)
			return; // off screen

		int cx = patblt.getCX();
		int cy = patblt.getCY();
		int fgcolor = patblt.getForegroundColor();
		int bgcolor = patblt.getBackgroundColor();
		int opcode = patblt.getOpcode();

		// convert to 24-bit colour
		fgcolor = Bitmap.convertTo24(fgcolor);
		bgcolor = Bitmap.convertTo24(bgcolor);

		/*
		 * // Perform standard clipping checks, x-axis int clipright = x + cx -
		 * 1; if (clipright > this.right) clipright = this.right; if (x <
		 * this.left) x = this.left; cx = clipright - x + 1;
		 * 
		 * // Perform standard clipping checks, y-axis int clipbottom = y + cy -
		 * 1; if (clipbottom > this.bottom) clipbottom = this.bottom; if (y <
		 * this.top) y = this.top; cy = clipbottom - y + 1;
		 */
		int i;
		int[] src = null;
		switch (brush.getStyle()) {
		case 0: // solid
			// make efficient version of rop later with int fgcolor and boolean
			// usearray set to false for single colour
			System.out.println("cx : " + cx + "cy  : " + cy);
			src = new int[cx * cy];

			for (i = 0; i < src.length; i++)
				src[i] = fgcolor;
			System.out.println("color :" + fgcolor);
			rop.do_array(opcode, WrappedImage.bi, Options.width, x, y, cx, cy,
					src, cx, 0, 0);
			// this.repaint(x, y, cx, cy);
			Common.currentImageViewer.postInvalidate();
			break;

		case 2: // hatch
			System.out.println("hatch");
			break;

		case 3: // pattern
			int brushx = brush.getXOrigin();
			int brushy = brush.getYOrigin();
			byte[] pattern = brush.getPattern();
			byte[] ipattern = pattern;

			/*
			 * // not sure if this inversion is needed byte[] ipattern = new
			 * byte[8]; for(i=0;i<ipattern.length;i++) {
			 * ipattern[ipattern.length-1-i] = pattern[i]; }
			 */

			src = new int[cx * cy];
			int psrc = 0;
			for (i = 0; i < cy; i++) {
				for (int j = 0; j < cx; j++) {
					if ((ipattern[(i + brushy) % 8] & (0x01 << ((j + brushx) % 8))) == 0)
						src[psrc] = fgcolor;
					else
						src[psrc] = bgcolor;
					psrc++;
				}
			}
			rop.do_array(opcode, WrappedImage.bi, Options.width, x, y, cx, cy,
					src, cx, 0, 0);
			// this.repaint(x, y, cx, cy);
			break;
		default:
			System.out.println("Unsupported brush style " + brush.getStyle());
		}
	}

	public void drawLineOrder(LineOrder line) {

		int x1 = line.getStartX();
		int y1 = line.getStartY();
		int x2 = line.getEndX();
		int y2 = line.getEndY();

		int fgcolor = line.getPen().getColor();

		int opcode = line.getOpcode() - 1;
		drawLine(x1, y1, x2, y2, fgcolor, opcode);

	}

	/**
	 * Draw a line to the screen
	 * 
	 * @param x1
	 *            x coordinate of start point of line
	 * @param y1
	 *            y coordinate of start point of line
	 * @param x2
	 *            x coordinate of end point of line
	 * @param y2
	 *            y coordinate of end point of line
	 * @param color
	 *            colour of line
	 * @param opcode
	 *            Operation code defining operation to perform on pixels within
	 *            the line
	 */
	public void drawLine(int x1, int y1, int x2, int y2, int color, int opcode) {
		// convert to 24-bit colour
		color = Bitmap.convertTo24(color);

		if (x1 == x2 || y1 == y2) {
			drawLineVerticalHorizontal(x1, y1, x2, y2, color, opcode);
			return;
		}

		int deltax = Math.abs(x2 - x1); // The difference between the x's
		int deltay = Math.abs(y2 - y1); // The difference between the y's
		int x = x1; // Start x off at the first pixel
		int y = y1; // Start y off at the first pixel
		int xinc1, xinc2, yinc1, yinc2;
		int num, den, numadd, numpixels;

		if (x2 >= x1) { // The x-values are increasing
			xinc1 = 1;
			xinc2 = 1;
		} else { // The x-values are decreasing
			xinc1 = -1;
			xinc2 = -1;
		}

		if (y2 >= y1) { // The y-values are increasing
			yinc1 = 1;
			yinc2 = 1;
		} else { // The y-values are decreasing
			yinc1 = -1;
			yinc2 = -1;
		}

		if (deltax >= deltay) { // There is at least one x-value for every
			// y-value
			xinc1 = 0; // Don't change the x when numerator >= denominator
			yinc2 = 0; // Don't change the y for every iteration
			den = deltax;
			num = deltax / 2;
			numadd = deltay;
			numpixels = deltax; // There are more x-values than y-values
		} else { // There is at least one y-value for every x-value
			xinc2 = 0; // Don't change the x for every iteration
			yinc1 = 0; // Don't change the y when numerator >= denominator
			den = deltay;
			num = deltay / 2;
			numadd = deltax;
			numpixels = deltay; // There are more y-values than x-values
		}

		for (int curpixel = 0; curpixel <= numpixels; curpixel++) {
			setPixel(opcode, x, y, color); // Draw the current pixel
			num += numadd; // Increase the numerator by the top of the fraction
			if (num >= den) { // Check if numerator >= denominator
				num -= den; // Calculate the new numerator value
				x += xinc1; // Change the x as appropriate
				y += yinc1; // Change the y as appropriate
			}
			x += xinc2; // Change the x as appropriate
			y += yinc2; // Change the y as appropriate
		}

		int x_min = x1 < x2 ? x1 : x2;
		int x_max = x1 > x2 ? x1 : x2;
		int y_min = y1 < y2 ? y1 : y2;
		int y_max = y1 > y2 ? y1 : y2;

		// this.repaint(x_min, y_min, x_max - x_min + 1, y_max - y_min + 1);
		Common.currentImageViewer.postInvalidate();
	}

	/**
	 * Helper function for drawLine, draws a horizontal or vertical line using a
	 * much faster method than used for diagonal lines
	 * 
	 * @param x1
	 *            x coordinate of start point of line
	 * @param y1
	 *            y coordinate of start point of line
	 * @param x2
	 *            x coordinate of end point of line
	 * @param y2
	 *            y coordinate of end point of line
	 * @param color
	 *            colour of line
	 * @param opcode
	 *            Operation code defining operation to perform on pixels within
	 *            the line
	 */
	public void drawLineVerticalHorizontal(int x1, int y1, int x2, int y2,
			int color, int opcode) {
		int pbackstore;
		int i;
		// only vertical or horizontal lines
		if (y1 == y2) { // HORIZONTAL
			if (y1 >= this.top && y1 <= this.bottom) { // visible
				if (x2 > x1) { // x inc, y1=y2
					if (x1 < this.left)
						x1 = this.left;
					if (x2 > this.right)
						x2 = this.right;
					pbackstore = y1 * Options.width + x1;
					for (i = 0; i < x2 - x1; i++) {
						rop
								.do_pixel(opcode, WrappedImage.bi, x1 + i, y1,
										color);
						pbackstore++;
					}
					// repaint(x1, y1, x2 - x1 + 1, 1);
				} else { // x dec, y1=y2
					if (x2 < this.left)
						x2 = this.left;
					if (x1 > this.right)
						x1 = this.right;
					pbackstore = y1 * Options.width + x1;
					for (i = 0; i < x1 - x2; i++) {
						rop
								.do_pixel(opcode, WrappedImage.bi, x2 + i, y1,
										color);
						pbackstore--;
					}
					// repaint(x2, y1, x1 - x2 + 1, 1);
					Common.currentImageViewer.postInvalidate();
				}
			}
		} else { // x1==x2 VERTICAL
			if (x1 >= this.left && x1 <= this.right) { // visible
				if (y2 > y1) { // x1=x2, y inc
					if (y1 < this.top)
						y1 = this.top;
					if (y2 > this.bottom)
						y2 = this.bottom;
					pbackstore = y1 * Options.width + x1;
					for (i = 0; i < y2 - y1; i++) {
						rop
								.do_pixel(opcode, WrappedImage.bi, x1, y1 + i,
										color);
						pbackstore += Options.width;
					}
					// repaint(x1, y1, 1, y2 - y1 + 1);
					Common.currentImageViewer.postInvalidate();
				} else { // x1=x2, y dec
					if (y2 < this.top)
						y2 = this.top;
					if (y1 > this.bottom)
						y1 = this.bottom;
					pbackstore = y1 * Options.width + x1;
					for (i = 0; i < y1 - y2; i++) {
						rop
								.do_pixel(opcode, WrappedImage.bi, x1, y2 + i,
										color);
						pbackstore -= Options.width;
					}
					// repaint(x1, y2, 1, y1 - y2 + 1);
					Common.currentImageViewer.postInvalidate();
				}
			}
		}
		// if(logger.isInfoEnabled()) logger.info("line
		// \t(\t"+x1+",\t"+y1+"),(\t"+x2+",\t"+y2+")");
	}

	/**
	 * Perform an operation on a pixel in the backstore
	 * 
	 * @param opcode
	 *            ID of operation to perform
	 * @param x
	 *            x coordinate of pixel
	 * @param y
	 *            y coordinate of pixel
	 * @param color
	 *            Colour value to be used in operation
	 */
	public void setPixel(int opcode, int x, int y, int color) {
		int Bpp = Options.Bpp;

		// correction for 24-bit colour
		if (Bpp == 3)
			color = ((color & 0xFF) << 16) | (color & 0xFF00)
					| ((color & 0xFF0000) >> 16);

		if ((x < this.left) || (x > this.right) || (y < this.top)
				|| (y > this.bottom)) { // Clip
		} else {
			rop.do_pixel(opcode, WrappedImage.bi, x, y, color);
		}
	}

	/**
	 * Draw a filled rectangle to the screen
	 * 
	 * @param x
	 *            x coordinate (left) of rectangle
	 * @param y
	 *            y coordinate (top) of rectangle
	 * @param cx
	 *            Width of rectangle
	 * @param cy
	 *            Height of rectangle
	 * @param color
	 *            Colour of rectangle
	 */
	public void fillRectangle(int x, int y, int cx, int cy, int color) {
		// clip here instead
		if (x > this.right || y > this.bottom)
			return; // off screen

		int Bpp = Options.Bpp;

		// convert to 24-bit colour
		color = Bitmap.convertTo24(color);

		// correction for 24-bit colour
		if (Bpp == 3)
			color = ((color & 0xFF) << 16) | (color & 0xFF00)
					| ((color & 0xFF0000) >> 16);

		// Perform standard clipping checks, x-axis
		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			x = this.left;
		cx = clipright - x + 1;

		// Perform standard clipping checks, y-axis
		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			y = this.top;
		cy = clipbottom - y + 1;

		// construct rectangle as integer array, filled with color
		int[] rect = new int[cx * cy];
		for (int i = 0; i < rect.length; i++)
			rect[i] = color;
		// draw rectangle to backstore

		WrappedImage.bi.setPixels(rect, 0, cx, x, y, cx, cy);
		Common.currentImageViewer.postInvalidate();

	}

	/**
	 * Draw a single glyph to the screen
	 * 
	 * @param mixmode
	 *            0 for transparent background, specified colour for background
	 *            otherwide
	 * @param x
	 *            x coordinate on screen at which to draw glyph
	 * @param y
	 *            y coordinate on screen at which to draw glyph
	 * @param cx
	 *            Width of clipping area for glyph
	 * @param cy
	 *            Height of clipping area for glyph
	 * @param data
	 *            Set of values defining glyph's pattern
	 * @param bgcolor
	 *            Background colour for glyph pattern
	 * @param fgcolor
	 *            Foreground colour for glyph pattern
	 */
	public void drawGlyph(int mixmode, int x, int y, int cx, int cy,
			byte[] data, int bgcolor, int fgcolor) {

		int pdata = 0;
		int index = 0x80;

		int bytes_per_row = (cx - 1) / 8 + 1;
		int newx, newy, newcx, newcy;

		int Bpp = Options.Bpp;

		// convert to 24-bit colour
		fgcolor = Bitmap.convertTo24(fgcolor);
		bgcolor = Bitmap.convertTo24(bgcolor);

		// correction for 24-bit colour
		if (Bpp == 3) {
			fgcolor = ((fgcolor & 0xFF) << 16) | (fgcolor & 0xFF00)
					| ((fgcolor & 0xFF0000) >> 16);
			bgcolor = ((bgcolor & 0xFF) << 16) | (bgcolor & 0xFF00)
					| ((bgcolor & 0xFF0000) >> 16);
		}

		// clip here instead

		if (x > this.right || y > this.bottom)
			return; // off screen

		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			newx = this.left;
		else
			newx = x;
		newcx = clipright - x + 1; // not clipright - newx - 1

		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			newy = this.top;
		else
			newy = y;

		newcy = clipbottom - newy + 1;

		int pbackstore = (newy * Options.width) + x;
		pdata = bytes_per_row * (newy - y); // offset y, but not x

		if (mixmode == MIX_TRANSPARENT) { // FillStippled
			for (int i = 0; i < newcy; i++) {
				for (int j = 0; j < newcx; j++) {
					if (index == 0) { // next row
						pdata++;
						index = 0x80;
					}

					if ((data[pdata] & index) != 0) {
						if ((x + j >= newx) && (newx + j > 0) && (newy + i > 0))
							// since haven't offset x
							WrappedImage.bi.setPixel(newx + j, newy + i, fgcolor);
					}
					index >>= 1;
				}
				pdata++;
				index = 0x80;
				pbackstore += Options.width;
				if (pdata == data.length) {
					pdata = 0;
				}
			}
		} else { // FillOpaqueStippled
			for (int i = 0; i < newcy; i++) {
				for (int j = 0; j < newcx; j++) {
					if (index == 0) { // next row
						pdata++;
						index = 0x80;
					}

					if (x + j >= newx) {
						if ((x + j > 0) && (y + i > 0)) {
							if ((data[pdata] & index) != 0)
								WrappedImage.bi.setPixel(x + j, y + i, fgcolor);
							else
								WrappedImage.bi.setPixel(x + j, y + i, bgcolor);
						}
					}
					index >>= 1;
				}
				pdata++;
				index = 0x80;
				pbackstore += Options.width;
				if (pdata == data.length) {
					pdata = 0;
				}
			}
		}

		// if(logger.isInfoEnabled()) logger.info("glyph
		// \t(\t"+x+",\t"+y+"),(\t"+(x+cx-1)+",\t"+(y+cy-1)+")");
		//this.repaint(newx, newy, newcx, newcy);
		Common.currentImageViewer.postInvalidate();
	}
}// end class
