/* Subversion properties, do not modify!
 * 
 * $Date$
 * $Revision$
 * $Author$
 * 
 * Author: Miha Vitorovic
 * 
 * Based on: (rdpsnd_libao.c)
 *  rdesktop: A Remote Desktop Protocol client.
 *  Sound Channel Process Functions
 *  Copyright (C) Matthew Chapman 2003
 *  Copyright (C) GuoJunBo guojunbo@ict.ac.cn 2003
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package omniDesk.net.rdp.rdp5.rdpsnd;

public class WaveFormatEx {
	public static final int MAX_CBSIZE = 256;

	public int wFormatTag; // uint16

	public int nChannels; // uint16

	public int nSamplesPerSec; // uint32

	public int nAvgBytesPerSec; // uint32

	public int nBlockAlign; // uint16

	public int wBitsPerSample; // uint16

	public int cbSize; // uint16

	public byte cb[] = new byte[MAX_CBSIZE]; // uint8

	public String toString() {
		StringBuffer out = new StringBuffer(256);
		out.append("[wFormatTag: ").append(wFormatTag).append(", nChannels: ")
				.append(nChannels).append(", nSamplesPerSec: ");
		out.append(nSamplesPerSec).append(", nAvgBytesPerSec: ").append(
				nAvgBytesPerSec).append(", nBlockAlign: ");
		out.append(nBlockAlign).append(", wBitsPerSample: ").append(
				wBitsPerSample).append(", cbSize: ").append(cbSize);
		out.append("]");
		return out.toString();
	}

}
