/* Subversion properties, do not modify!
 * 
 * $Date$
 * $Revision$
 * $Author$
 * 
 * Author: Miha Vitorovic
 * 
 * Based on: (rdpsnd.c)
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

import java.io.IOException;

import android.util.Log;

import omniDesk.net.rdp.OmniDeskException;
import omniDesk.net.rdp.RdesktopException;
import omniDesk.net.rdp.RdpPacket;
import omniDesk.net.rdp.RdpPacket_Localised;
import omniDesk.net.rdp.crypto.CryptoException;
import omniDesk.net.rdp.rdp5.VChannel;
import omniDesk.net.rdp.rdp5.VChannels;

public class SoundChannel extends VChannel {

	public static final int RDPSND_CLOSE = 1;

	public static final int RDPSND_WRITE = 2;

	public static final int RDPSND_SET_VOLUME = 3;

	public static final int RDPSND_UNKNOWN4 = 4;

	public static final int RDPSND_COMPLETION = 5;

	public static final int RDPSND_SERVERTICK = 6;

	public static final int RDPSND_NEGOTIATE = 7;

	public static final int MAX_FORMATS = 10;

	private boolean awaitingDataPacket;

	private boolean deviceOpen;

	private int format;

	private int currentFormat;

	private int tick;

	private int packetIndex;

	private int formatCount;

	private SoundDriver soundDriver;

	private WaveFormatEx[] formats;

	public SoundChannel() {
		super();
		awaitingDataPacket = false;
		deviceOpen = false;
		format = 0;
		currentFormat = 0;
		tick = 0;
		packetIndex = 0;
		formatCount = 0;
		formats = new WaveFormatEx[MAX_FORMATS];
		for (int i = 0; i < MAX_FORMATS; i++)
			formats[i] = new WaveFormatEx();
		soundDriver = new SoundDriver(this);
	}

	public int flags() {
		return VChannels.CHANNEL_OPTION_INITIALIZED
				| VChannels.CHANNEL_OPTION_ENCRYPT_RDP;
	}

	public String name() {
		return "rdpsnd";
	}

	public void process(RdpPacket data) throws IOException,
			CryptoException, OmniDeskException {
		
		Log.v("soundchannel.java","inside PROCESS packet");
		int type, length;

		if (awaitingDataPacket) {
			if (format >= MAX_FORMATS) {
				//logger.error("RDPSND: Invalid format index\n");
				return;
			}

			Log.e("soundchannel.java","value of deviceOPen = " + deviceOpen);
			if (!deviceOpen || (format != currentFormat)) {
				Log.d("soundchannel.java","PROCESS/ inside IF");
				if (!deviceOpen && !soundDriver.waveOutOpen()) {
					sendCompletion(tick, packetIndex);
					return;
				}
				if (!soundDriver.waveOutSetFormat(formats[format])) {
					sendCompletion(tick, packetIndex);
					soundDriver.waveOutClose();
					deviceOpen = false;
					return;
				}
				deviceOpen = true;
				currentFormat = format;
			}
			soundDriver.waveOutWrite(data, tick, packetIndex);
			awaitingDataPacket = false;
			return;
		}

		type = data.get8();
		data.get8(); // ? unknown ?
		length = data.getLittleEndian16();

		switch (type) {
		case RDPSND_WRITE:
			Log.d("soundchannel.java/process","case RDPSND_WRITE");
			tick = data.getLittleEndian16() & 0xFFFF;
			format = data.getLittleEndian16() & 0xFFFF;
			packetIndex = data.getLittleEndian16() & 0xFFFF;
			awaitingDataPacket = true;
			break;
		case RDPSND_CLOSE:
			Log.d("soundchannel.java/process","case RDPSND_CLOSE");
			soundDriver.waveOutClose();
			deviceOpen = false;
			break;
		case RDPSND_NEGOTIATE:
			Log.d("soundchannel.java/process","case RDPSND_NEGOTIATE");			
			negotiate(data);
			break;
		case RDPSND_SERVERTICK:
			Log.d("soundchannel.java/process","case RDPSND_SERVERTICK");			
			processServerTick(data);
			break;
		case RDPSND_SET_VOLUME:
			Log.d("soundchannel.java/process","case RDPSND_SET_VOLUME");			
			int volume = data.getLittleEndian32();
			if (deviceOpen) {
				soundDriver.waveOutVolume((volume & 0xffff),
						(volume >> 16) & 0xffff);
			}
			break;
		default:
		//	logger.error("RDPSND packet type " + type);
			break;
		}
	}

	public void waveOutPlay() throws OmniDeskException {
		if (soundDriver.isDspBusy())
		{
			Log.v("soundchannel.java","through sound channel dsp busy!!!!!!");
			soundDriver.waveOutPlay();
			
		}
	}

	public void sendCompletion(int tick, int packetIndex) throws OmniDeskException {
		RdpPacket_Localised out = initPacket(RDPSND_COMPLETION, 4);
		out.setLittleEndian16(tick);
		out.set8(packetIndex);
		out.set8(0);
		out.markEnd();
		try {
			send_packet(out);
		} catch (RdesktopException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (CryptoException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void negotiate(RdpPacket data) throws OmniDeskException {
		boolean deviceAvailable = false;

		data.incrementPosition(14); // advance 14 bytes - flags, volume, pitch, UDP port

		int inFormatCount = data.getLittleEndian16();

		data.incrementPosition(4); // pad, status, pad

		// test the device
		//if( LibAO.waveOutOpen() ) {
		//	LibAO.waveOutClose();
		deviceAvailable = true;
		//}

		formatCount = 0;

		if (checkRemaining(data, 18 * inFormatCount)) {
			for (int i = 0; i < inFormatCount; i++) {
				WaveFormatEx format = formats[formatCount];
				format.wFormatTag = data.getLittleEndian16();
				format.nChannels = data.getLittleEndian16();
				format.nSamplesPerSec = data.getLittleEndian32();
				format.nAvgBytesPerSec = data.getLittleEndian32();
				format.nBlockAlign = data.getLittleEndian16();
				format.wBitsPerSample = data.getLittleEndian16();
				format.cbSize = data.getLittleEndian16();

				int readCnt = format.cbSize;
				int discardCnt = 0;
				if (format.cbSize > WaveFormatEx.MAX_CBSIZE) {
			//		logger.error("cbSize too large for buffer: "
			//				+ format.cbSize);
					readCnt = WaveFormatEx.MAX_CBSIZE;
					discardCnt = format.cbSize - WaveFormatEx.MAX_CBSIZE;
				}
				data.copyToByteArray(format.cb, 0, data.getPosition(), readCnt);
				// advance packet position
				data.incrementPosition(readCnt + discardCnt);

				if (deviceAvailable
						&& soundDriver.waveOutFormatSupported(format)) {
					formatCount++;
					if (formatCount == MAX_FORMATS)
						break;
				}
			}
		}

		RdpPacket_Localised out = initPacket(RDPSND_NEGOTIATE | 0x200,
				20 + 18 * formatCount);
		out.setLittleEndian32(3); // flags
		out.setLittleEndian32(0xffffffff); // volume
		out.setLittleEndian32(0); // pitch
		out.setLittleEndian16(0); // UDP port

		out.setLittleEndian16(formatCount);
		out.set8(0x95); // pad ?
		out.setLittleEndian16(2); // status
		out.set8(0x77); // pad ?

		for (int i = 0; i < formatCount; i++) {
			WaveFormatEx format = formats[i];
			out.setLittleEndian16(format.wFormatTag);
			out.setLittleEndian16(format.nChannels);
			out.setLittleEndian32(format.nSamplesPerSec);
			out.setLittleEndian32(format.nAvgBytesPerSec);
			out.setLittleEndian16(format.nBlockAlign);
			out.setLittleEndian16(format.wBitsPerSample);
			out.setLittleEndian16(0); // cbSize
		}

		out.markEnd();
		try {
			send_packet(out);
		} catch (RdesktopException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (CryptoException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean checkRemaining(RdpPacket p, int required) {
		return p.getPosition() + required <= p.size();
	}

	private void processServerTick(RdpPacket data) throws OmniDeskException {
		int tick1, tick2;

		tick1 = data.getLittleEndian16();
		tick2 = data.getLittleEndian16();

		RdpPacket_Localised out = initPacket(RDPSND_SERVERTICK | 0x2300, 4);
		out.setLittleEndian16(tick1);
		out.setLittleEndian16(tick2);
		out.markEnd();

		try {
			send_packet(out);
		} catch (RdesktopException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (CryptoException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private RdpPacket_Localised initPacket(int type, int size) {
		RdpPacket_Localised s = new RdpPacket_Localised(size + 4);
		s.setLittleEndian16(type);
		s.setLittleEndian16(size);
		return s;
	}
}
