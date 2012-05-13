/* RasterOp.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 1.7 $
 * Author: $Author: telliott $
 * Date: $Date: 2005/09/27 14:15:39 $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Set of operations used in displaying raster graphics
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * (See gpl.txt for details of the GNU General Public License.)
 * 
 */
// Created on 01-Jul-2003

package omniDesk.net.rdp;

import android.graphics.Color;
import android.util.Log;
import android.widget.AnalogClock;


//import java.awt.image.BufferedImage;

//import org.apache.log4j.Logger;

public class RasterOp {
	//static Logger logger = Logger.getLogger(RdesktopCanvas.class);

	private void ropInvert(android.graphics.Bitmap biDst, int[] dest, int width, int x, int y, int cx, int cy,
			int Bpp) {
		
		int mask = Options.bpp_mask;
		int pdest = (y * width + x);
		for (int i = 0; i < cy; i++) {
			for (int j = 0; j < cx; j++) {
                if(biDst != null){
                    int c = biDst.getPixel(x+j,y+i);
                    biDst.setPixel(x+j,y+i,~c & mask);
                }else dest[pdest] = (~dest[pdest]) & mask;
				pdest++;
			}
			pdest += (width - cx);
		}
	}

	private void ropClear(android.graphics.Bitmap biDst, int width, int x, int y, int cx, int cy,
			int Bpp) {

	    for(int i = x; i < x+cx; i++){
	            for(int j = y; j < y+cy; j++)
	                biDst.setPixel(i,j,0);
	    }
	}

	private void ropSet(android.graphics.Bitmap biDst, int width, int x, int y, int cx, int cy,
			int Bpp) {
        
        int mask = Options.bpp_mask;
        
            for(int i = x; i < x+cx; i++){
                for(int j = y; j < y+cy; j++)
                    biDst.setPixel(i,j,mask);
            }

    }

	private void ropCopy(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx, int cy,
			int[] src, int srcwidth, int srcx, int srcy, int Bpp) {

		if (src == null) { // special case - copy to self
            int[] imgSec = null;
            //biDst.getGraphics().copyArea(srcx,srcy,cx,cy,x-srcx,y-srcy);
		} else {
			biDst.setPixels(src,0,cx,x,y,cx,cy);
		}
	}

    /**
     * Perform an operation on a rectangular area of a android.graphics.Bitmap, using an integer array of colour values as
     * source if necessary
     * @param opcode Code defining operation to perform
     * @param biDst Destination image for operation
     * @param dstwidth Width of destination image
     * @param x X-offset of destination area within destination image
     * @param y Y-offset of destination area within destination image
     * @param cx Width of destination area
     * @param cy Height of destination area
     * @param src Source data, represented as an array of integer pixel values
     * @param srcwidth Width of source data
     * @param srcx X-offset of source area within source data
     * @param srcy Y-offset of source area within source data
     */
	public void do_array(int opcode, android.graphics.Bitmap biDst, int dstwidth, int x, int y,
			int cx, int cy, int[] src, int srcwidth, int srcx, int srcy) {
		int Bpp = Options.Bpp;
        //int[] dst = null;
		 System.out.println("do_array: opcode = 0x" + Integer.toHexString(opcode) );
		
		switch (opcode) {
		case 0x0:
			ropClear(biDst, dstwidth, x, y, cx, cy, Bpp);
			break;
		case 0x1:
			ropNor(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0x2:
			ropAndInverted(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx,
					srcy, Bpp);
			break;
		case 0x3: // CopyInverted
			ropInvert(biDst, src, srcwidth, srcx, srcy, cx, cy, Bpp);
			ropCopy(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0x4: // AndReverse
			ropInvert(biDst, null, dstwidth, x, y, cx, cy, Bpp);
			ropAnd(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0x5:
			ropInvert(biDst, null, dstwidth, x, y, cx, cy, Bpp);
			break;
		case 0x6:
			ropXor(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0x7:
			ropNand(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0x8:
			ropAnd(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0x9:
			ropEquiv(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy,
					Bpp);
			break;
		case 0xa: // Noop
			break;
		case 0xb:
			ropOrInverted(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx,
					srcy, Bpp);
			break;
		case 0xc:
			ropCopy(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0xd: // OrReverse
			ropInvert(biDst, null, dstwidth, x, y, cx, cy, Bpp);
			ropOr(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0xe:
			ropOr(biDst, dstwidth, x, y, cx, cy, src, srcwidth, srcx, srcy, Bpp);
			break;
		case 0xf:
			ropSet(biDst, dstwidth, x, y, cx, cy, Bpp);
			break;
		default:
			//logger.warn("do_array unsupported opcode: " + opcode);
		// rop_array(opcode,dst,dstwidth,x,y,cx,cy,src,srcwidth,srcx,srcy);
		}
	}
    
    /**
     * Perform an operation on a single pixel in a android.graphics.Bitmap
     * @param opcode Opcode defining operation to perform
     * @param dst Image on which to perform the operation
     * @param x X-coordinate of pixel to modify
     * @param y Y-coordinate of pixel to modify
     * @param color Colour to use in operation (unused for some operations)
     */
    public void do_pixel(int opcode, android.graphics.Bitmap dst, int x, int y, int color) {
        int mask = Options.bpp_mask;
        
        if(dst == null) return;
        
        int c = dst.getPixel(x,y);
        
        switch (opcode) {
        case 0x0: dst.setPixel(x,y,0); break;
        case 0x1: dst.setPixel(x,y,(~(c | color)) & mask); break;
        case 0x2: dst.setPixel(x,y, c & ((~color) & mask)); break;
        case 0x3: dst.setPixel(x,y,(~color) & mask); break;
        case 0x4: dst.setPixel(x,y,(~c & color) * mask); break;
        case 0x5: dst.setPixel(x,y,(~c) & mask);  break;
        case 0x6: dst.setPixel(x,y, c ^ ((color) & mask)); break;
        case 0x7: dst.setPixel(x,y,(~c & color) & mask); break;
        case 0x8: dst.setPixel(x,y, c & ( color & mask )); break;
        case 0x9: dst.setPixel(x,y,c ^ ( ~color & mask) ); break;
        case 0xa: /* Noop */ break;
        case 0xb: dst.setPixel(x,y,c | ( ~color & mask )); break;
        case 0xc: dst.setPixel(x,y,color); break;
        case 0xd: dst.setPixel(x,y,(~c | color) & mask); break;
        case 0xe: dst.setPixel(x,y, c | ( color & mask)); break;
        case 0xf: dst.setPixel(x,y,mask); break;
        default:
            //logger.warn("do_byte unsupported opcode: " + opcode);
        }
    }

	private void ropNor(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx, int cy,
			int[] src, int srcwidth, int srcx, int srcy, int Bpp) {
		// opcode 0x1
		int mask = Options.bpp_mask;
		int psrc = (srcy * srcwidth + srcx);

        for (int row = 0; row < cy; row++) {
			for (int col = 0; col < cx; col++) {
                biDst.setPixel(x+cx,y+cy,(~(biDst.getPixel(x+cx,y+cy) | src[psrc])) & mask);               
			}
			psrc += (srcwidth - cx);
		}
	}

	private void ropAndInverted(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx,
			int cy, int[] src, int srcwidth, int srcx, int srcy, int Bpp) {
		// opcode 0x2
		int mask = Options.bpp_mask;
		int psrc = (srcy * srcwidth + srcx);
		for (int row = 0; row < cy; row++) {
			for (int col = 0; col < cx; col++) {
                int c = biDst.getPixel(x+cx,y+cy);
                biDst.setPixel(x+cx,y+cy,c & ((~src[psrc]) & mask));              
				psrc++;
			}
			psrc += (srcwidth - cx);
		}
	}

	private void ropXor(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx, int cy,
			int[] src, int srcwidth, int srcx, int srcy, int Bpp) {
		// opcode 0x6
		int mask = 0xFFFFFF;//Options.bpp_mask;
		int psrc = (srcy * srcwidth + srcx);
		for (int row = 0; row < cy; row++) {
			for (int col = 0; col < cx; col++) {
                int c = biDst.getPixel(x+col,y+row);
                System.out.println("color before XOR:" +  Integer.toHexString(c));
                System.out.println("New color:" + Integer.toHexString(src[psrc] & mask));
                biDst.setPixel(x+col,y+row, c ^ ((src[psrc]) & mask));
                c = biDst.getPixel(x+col,y+row);
                System.out.println("color after XOR:" +  Integer.toHexString(c));
				psrc++;
			}
			psrc += (srcwidth - cx);
		}
	}

	private void ropNand(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx, int cy,
			int[] src, int srcwidth, int srcx, int srcy, int Bpp) {
		// opcode 0x7
		int mask = Options.bpp_mask;
		int psrc = (srcy * srcwidth + srcx);
		for (int row = 0; row < cy; row++) {
			for (int col = 0; col < cx; col++) {
			    int c = biDst.getPixel(x+col,y+row);
                biDst.setPixel(x+col,y+row, (~(c & src[psrc])) & mask);
				psrc++;
			}
			psrc += (srcwidth - cx);
		}
	}

	private void ropAnd(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx, int cy,
			int[] src, int srcwidth, int srcx, int srcy, int Bpp) {
		// opcode 0x8
		int mask = Options.bpp_mask;
		int psrc = (srcy * srcwidth + srcx);
		for (int row = 0; row < cy; row++) {
			for (int col = 0; col < cx; col++) {
                int c = biDst.getPixel(x+col,y+row);
                biDst.setPixel(x+col,y+row, c & ((src[psrc]) & mask));
				psrc++;
			}
			psrc += (srcwidth - cx);
		}
	}

	private void ropEquiv(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx,
			int cy, int[] src, int srcwidth, int srcx, int srcy, int Bpp) {
		// opcode 0x9
		int mask = Options.bpp_mask;
		int psrc = (srcy * srcwidth + srcx);
		for (int row = 0; row < cy; row++) {
			for (int col = 0; col < cx; col++) {
                int c = biDst.getPixel(x+col,y+row);
                biDst.setPixel(x+col,y+row, c ^ ((~src[psrc]) & mask));
                psrc++;
			}
			psrc += (srcwidth - cx);
		}
	}

	private void ropOrInverted(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx,
			int cy, int[] src, int srcwidth, int srcx, int srcy, int Bpp) {
		// opcode 0xb
		int mask = Options.bpp_mask;
		int psrc = (srcy * srcwidth + srcx);
		for (int row = 0; row < cy; row++) {
			for (int col = 0; col < cx; col++) {
                int c = biDst.getPixel(x+col,y+row);
                biDst.setPixel(x+col,y+row, c | ((~src[psrc]) & mask));
                psrc++;
			}
			psrc += (srcwidth - cx);
		}
	}

	private void ropOr(android.graphics.Bitmap biDst, int dstwidth, int x, int y, int cx, int cy,
			int[] src, int srcwidth, int srcx, int srcy, int Bpp) {
		// opcode 0xe
		int mask = Options.bpp_mask;
		int psrc = (srcy * srcwidth + srcx);
		for (int row = 0; row < cy; row++) {
			for (int col = 0; col < cx; col++) {
                int c = biDst.getPixel(x+col,y+row);
                biDst.setPixel(x+col,y+row, c | (src[psrc] & mask));
        		psrc++;
			}
			psrc += (srcwidth - cx);
		}
	}
}
