/**
 * 
 */
package omniDesk.net.rdp.rdp5;

import java.io.IOException;
import java.lang.reflect.Array;

import omniDesk.net.rdp.crypto.CryptoException;


import omniDesk.net.rdp.*;

/*import omniDesk.net.rdp.MCS;
import omniDesk.net.rdp.OmniDeskException;
import omniDesk.net.rdp.Options;
import omniDesk.net.rdp.RdpPacket_Localised;
import omniDesk.net.rdp.crypto.CryptoException;
*/
/**
 * @author akshay
 *
 */
public class VChannels {
	// protected static Logger logger = Logger.getLogger(Input.class);

	    /* Sound format constants */
	    public static final int WAVE_FORMAT_PCM = 1;

	    public static final int WAVE_FORMAT_ADPCM = 2;

	    public static final int WAVE_FORMAT_ALAW = 6;

	    public static final int WAVE_FORMAT_MULAW = 7;

	    /* Virtual channel options */
	    public static final int CHANNEL_OPTION_INITIALIZED = 0x80000000;

	    public static final int CHANNEL_OPTION_ENCRYPT_RDP = 0x40000000;

	    public static final int CHANNEL_OPTION_COMPRESS_RDP = 0x00800000;

	    public static final int CHANNEL_OPTION_SHOW_PROTOCOL = 0x00200000;

	    /* NT status codes for RDPDR */
	    public static final int STATUS_SUCCESS = 0x00000000;

	    public static final int STATUS_INVALID_PARAMETER = 0xc000000d;

	    public static final int STATUS_INVALID_DEVICE_REQUEST = 0xc0000010;

	    public static final int STATUS_ACCESS_DENIED = 0xc0000022;

	    public static final int MAX_CHANNELS = 4;

	    public static final int CHANNEL_CHUNK_LENGTH = 1600;

	    public static final int CHANNEL_FLAG_FIRST = 0x01;

	    public static final int CHANNEL_FLAG_LAST = 0x02;

	    public static final int CHANNEL_FLAG_SHOW_PROTOCOL = 0x10;

	    private VChannel channels[];

	    private int num_channels;//Should not be initialised...Neer

	    public int num_channels() {
	        return num_channels;
	    }

	    private byte[] fragment_buffer = null;

	    /**
	     * Obtain the MCS ID for a specific numbered channel
	     * 
	     * @param c
	     *            Channel number for which to obtain MCS ID
	     * @return MCS ID associated with the supplied channel number
	     */
	    public int mcs_id(int c) {
	        return MCS.MCS_GLOBAL_CHANNEL + 1 + c;
	    }

	    /**
	     * Initialise the maximum number of Virtual Channels
	     */
	    public VChannels() {
	        channels = new VChannel[MAX_CHANNELS];
	    }

	    /**
	     * Retrieve the VChannel object for the numbered channel
	     * 
	     * @param c
	     *            Channel number
	     * @return The requested Virtual Channel
	     */
	    public VChannel channel(int c) {
	        if (c < num_channels)
	            return channels[c];
	        else
	            return null;
	    }

	    /**
	     * Retrieve the VChannel object for the specified MCS channel ID
	     * 
	     * @param channelno
	     *            MCS ID for the required channel
	     * @return Virtual Channel associated with the supplied MCS ID
	     */
	    public VChannel find_channel_by_channelno(int channelno) {
	        if (channelno > MCS.MCS_GLOBAL_CHANNEL + num_channels) {
	           // logger.warn("Channel " + channelno
	                //    + " not defined. Highest channel defined is "
	                 //   + MCS.MCS_GLOBAL_CHANNEL + num_channels);
	            return null;
	        } else
	            return channels[channelno - MCS.MCS_GLOBAL_CHANNEL - 1];
	    }

	    /**
	     * Remove all registered virtual channels
	     */
	    public void clear() {
	        channels = new VChannel[MAX_CHANNELS];
	        num_channels = 0;
	    }

	    /**
	     * Register a new virtual channel
	     * @param v Virtual channel to be registered
	     * @return True if successful
	     * @throws RdesktopException
	     */
	    public boolean register(VChannel v) throws OmniDeskException {
	        if (!Options.use_rdp5) {
	            return false;
	        }

	        if (num_channels >= MAX_CHANNELS)
	            throw new OmniDeskException(
	                    "Channel table full. Could not register channel.");

	        channels[num_channels] = v;
	        v.set_mcs_id(MCS.MCS_GLOBAL_CHANNEL + 1 + num_channels);
	        num_channels++;

	        return true;
	    }

	    /**
	     * Process a packet sent on a numbered channel
	     * @param data Packet sent to channel
	     * @param mcsChannel Number specified for channel
	     * @throws OmniDeskException
	     * @throws IOException
	     * @throws CryptoException
	     */
	    public void channel_process(RdpPacket_Localised data, int mcsChannel)
	            throws OmniDeskException, IOException, CryptoException {

	        @SuppressWarnings("unused")
			int length, flags,thislength = 0;
	        VChannel channel = null;

	        int i;

	        for (i = 0; i < num_channels; i++) {
	            if (mcs_id(i) == mcsChannel) {
	                channel = channels[i];
	                break;
	            }
	        }

	        if (i >= num_channels)
	            return;

	        length = data.getLittleEndian32();
	        flags = data.getLittleEndian32();

	        if (((flags & CHANNEL_FLAG_FIRST) != 0)
	                && ((flags & CHANNEL_FLAG_LAST) != 0)) {
	            // single fragment - pass straight up
	            channel.process(data);
	        } else {
	            // append to the defragmentation buffer
	            byte[] content = new byte[data.getEnd() - data.getPosition()];
	            data
	                    .copyToByteArray(content, 0, data.getPosition(),
	                            content.length);
	            fragment_buffer = append(fragment_buffer, content);

	            if ((flags & CHANNEL_FLAG_LAST) != 0) {
	                RdpPacket_Localised fullpacket = new RdpPacket_Localised(
	                        fragment_buffer.length);
	                fullpacket.copyFromByteArray(fragment_buffer, 0, 0,
	                        fragment_buffer.length);
	                // process the entire reconstructed packet
	                channel.process(fullpacket);
	                fragment_buffer = null;
	            }

	        }
	    }

	    /**
	     * Increase the size of an array
	     * @param a Array to expand
	     * @param amount Number of elements to add to the array
	     * @return Expanded array
	     */
	    static Object arrayExpand(Object a, int amount) {
	        Class<? extends Object> cl = a.getClass();
	        if (!cl.isArray())
	            return null;
	        int length = Array.getLength(a);
	        int newLength = length + amount; // 50% more

	        Class<?> componentType = a.getClass().getComponentType();
	        Object newArray = Array.newInstance(componentType, newLength);
	        System.arraycopy(a, 0, newArray, 0, length);
	        return newArray;
	    }

	    /**
	     * Concatenate two byte arrays
	     * @param target Contains initial bytes in output
	     * @param source Appended to target array
	     * @return Concatenation of arrays, target+source
	     */
	    static byte[] append(byte[] target, byte[] source) {
	        if (target == null || target.length <= 0)
	            return source;
	        else if (source == null || source.length <= 0)
	            return target;
	        else {
	            byte[] out = (byte[]) arrayExpand(target, source.length);
	            System.arraycopy(source, 0, out, target.length, source.length);
	            return out;
	        }
	    }
}
