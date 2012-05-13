/* Subversion properties, do not modify!
 * 
 * Sound Channel Process Functions - javax.sound-driver
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

import java.util.GregorianCalendar;

/*import javax.sound.sampled.AudioFormat;
 import javax.sound.sampled.AudioSystem;
 import javax.sound.sampled.DataLine;
 import javax.sound.sampled.FloatControl;
 import javax.sound.sampled.Line;
 import javax.sound.sampled.LineUnavailableException;
 import javax.sound.sampled.Mixer;
 import javax.sound.sampled.Port;
 import javax.sound.sampled.SourceDataLine;
 */

import android.media.*;
import android.util.Log;
import omniDesk.net.rdp.Input;
import omniDesk.net.rdp.OmniDeskException;
import omniDesk.net.rdp.RdpPacket;
import omniDesk.net.rdp.rdp5.VChannels;

public class SoundDriver {

	private class AudioPacket {
		protected RdpPacket s;

		protected int tick;

		protected int index;
	}

	private SoundChannel soundChannel;

	private static final int MAX_QUEUE = 20;

	private static final int BUFFER_SIZE = 65536;

	private AudioPacket[] packetQueue;

	private int queueHi, queueLo;

	// private SourceDataLine oDevice;
	private AudioTrack audioTrack;

	// private FloatControl volumeControl;

	private int volume;

	private WaveFormatEx format;

	private boolean reopened;

	private boolean dspBusy;

	private byte[] buffer, outBuffer;

	private GregorianCalendar prevTime;

	public SoundDriver(SoundChannel sndChannel) {
		soundChannel = sndChannel;
		packetQueue = new AudioPacket[MAX_QUEUE];
		for (int i = 0; i < MAX_QUEUE; i++)
			packetQueue[i] = new AudioPacket();
		queueHi = 0;
		queueLo = 0;
		reopened = true;
		dspBusy = false;
		buffer = new byte[BUFFER_SIZE];
		outBuffer = new byte[BUFFER_SIZE];
		audioTrack = null;
		format = null;
		volume = 65535;
		// volumeControl = getVolumeControl();
	}

	public boolean waveOutOpen() {
		return true;
	}

	public void waveOutClose() throws OmniDeskException {
		while (queueLo != queueHi) {
			soundChannel.sendCompletion(packetQueue[queueLo].tick,
					packetQueue[queueLo].index);
			queueLo = (queueLo + 1) % MAX_QUEUE;
		}
		if (audioTrack != null) {
			audioTrack.stop();
			audioTrack.flush();
			audioTrack.release();
			audioTrack = null;
		}
	}

	public boolean waveOutSetFormat(WaveFormatEx fmt) {

		Log.d("sound driver.java","inside wave out set format");
		format = fmt;

		WaveFormatEx trFormat = SoundDecoder.translateFormatForDevice(fmt);
		/*
		 * AudioFormat audioFormat = new AudioFormat(trFormat.nSamplesPerSec,
		 * trFormat.wBitsPerSample, trFormat.nChannels, true, false);
		 */
		int bufferSize = AudioTrack.getMinBufferSize(trFormat.nSamplesPerSec,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		
		Log.d("sounddriver.java","buffer size= " + bufferSize);

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				trFormat.nSamplesPerSec,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize,
				AudioTrack.MODE_STREAM);

		audioTrack.play();
		try {
			/*
			 * if (audioTrack != null) { audioTrack.flush();
			 * audioTrack.release(); }
			 */
			/*
			 * DataLine.Info dataLineInfo = new DataLine.Info(
			 * SourceDataLine.class, audioFormat); oDevice = (SourceDataLine)
			 * AudioSystem.getLine(dataLineInfo);
			 * 
			 * oDevice.open(audioFormat); oDevice.start();
			 */
			

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		reopened = true;

		return true;
	}

	public void waveOutWrite(RdpPacket s, int tick, int packetIndex)
			throws OmniDeskException {
		AudioPacket packet = packetQueue[queueHi];
		int nextHi = (queueHi + 1) % MAX_QUEUE;
		Log.e("sounddriver.java","value of queuelo= " + queueLo + "  nextHi=" + nextHi);

		if (nextHi == queueLo) {
			
			Log.e("sounddriver.java", "No space to queue audio packet");
			Log.e("sounddriver.java","value of queuelo= " + queueLo + "  nextHi=" + nextHi);
			return;
		}

		queueHi = nextHi;

		packet.s = s;
		packet.tick = tick;
		packet.index = packetIndex;

		packet.s.incrementPosition(4);

		if (!dspBusy)
		{
			Log.e("sound driver.java","value of dspbusy" + dspBusy);
			waveOutPlay();
		}
	}

	public void waveOutVolume(int left, int right) {
		/*
		 * volume = left < right ? right : left; if (volumeControl != null)
		 * volumeControl.setValue(volume * volumeControl.getPrecision() +
		 * volumeControl.getMinimum());
		 */}

	public void waveOutPlay() throws OmniDeskException {
		
		Log.e("sounddriver.java","wave out play");

		if (reopened) {
			reopened = false;
			prevTime = new GregorianCalendar();
		}

		if (queueLo == queueHi) {
			Log.d("sound driver.java","low = high");
			dspBusy = false;
			return;
		}
		Log.e("sounddriver.java","queuelo=" + queueLo + "  queuehi=" + queueHi);

		AudioPacket packet = packetQueue[queueLo];
		RdpPacket out = packet.s;

		int nextTick;
		if (((queueLo + 1) % MAX_QUEUE) != queueHi)
			nextTick = packetQueue[(queueLo + 1) % MAX_QUEUE].tick;
		else
			nextTick = (packet.tick + 65535) % 65536;

		int len = (BUFFER_SIZE > out.size() - out.getPosition()) ? (out.size() - out
				.getPosition())
				: BUFFER_SIZE;
		out.copyToByteArray(buffer, 0, out.getPosition(), len);
		out.incrementPosition(len);

		int outLen = SoundDecoder.getBufferSize(len, format);
		if (outLen > outBuffer.length)
			outBuffer = new byte[outLen];
		outBuffer = SoundDecoder.decode(buffer, outBuffer, len, format);

		Log.e("sound driver.java","audioTrack write");
		if(audioTrack.write(outBuffer, 0, outLen)== AudioTrack.ERROR_INVALID_OPERATION)
		{
			Log.e("sound driver.java","wave out play audio track write FAILED");
		}

		GregorianCalendar tv = new GregorianCalendar();

		long duration = tv.getTimeInMillis() - prevTime.getTimeInMillis();

		if (packet.tick > nextTick)
			nextTick += 65536;

		if ((out.getPosition() == out.size())
				|| (duration > nextTick - packet.tick + 500)) {
			prevTime = tv;
			soundChannel.sendCompletion(
					((packet.tick + (int) duration) % 65536), packet.index);
			queueLo = (queueLo + 1) % MAX_QUEUE;
		}
		dspBusy = false;//neer true;
		return;
	}

	public boolean isDspBusy() {
		return dspBusy;
	}

	public boolean waveOutFormatSupported(WaveFormatEx fmt) {
		switch (fmt.wFormatTag) {
		case VChannels.WAVE_FORMAT_ALAW:
			return ((fmt.nChannels == 1) || (fmt.nChannels == 2))
					&& (fmt.wBitsPerSample == 8);
		case VChannels.WAVE_FORMAT_PCM:
			return ((fmt.nChannels == 1) || (fmt.nChannels == 2))
					&& ((fmt.wBitsPerSample == 8) || (fmt.wBitsPerSample == 16));
			// ADPCM crashes the "RDP Clip monitor" on the server
			// case VChannels.WAVE_FORMAT_ADPCM:
			// logger.info( "ADPCM" );
			// return ( ( fmt.nChannels == 1 ) || ( fmt.nChannels == 2 ) ) && (
			// fmt.wBitsPerSample == 4 );
		default:
			return false;
		}
	}

	/*
	 * private FloatControl getVolumeControl() { Mixer.Info[] mixerInfo =
	 * AudioSystem.getMixerInfo(); Line.Info portInfo = new
	 * Line.Info(Port.class);
	 * 
	 * for (int i = 0; i < mixerInfo.length; i++) { Mixer mixer =
	 * AudioSystem.getMixer(mixerInfo[i]);
	 * 
	 * if (mixer.isLineSupported(portInfo)) { Line.Info[] lineInfo =
	 * mixer.getTargetLineInfo(); for (int j = 0; j < lineInfo.length; j++) {
	 * try { Line line = mixer.getLine(lineInfo[j]); line.open(); if
	 * (line.isControlSupported(FloatControl.Type.VOLUME)) return (FloatControl)
	 * line .getControl(FloatControl.Type.VOLUME); line.close(); } catch
	 * (LineUnavailableException e) { // do nothing } } } } return null; }
	 */
}
