/**
 * 
 */
package omniDesk.net.rdp.rdp5;

import java.io.IOException;

import omniDesk.net.rdp.crypto.CryptoException;

import omniDesk.net.rdp.*;
/*import omniDesk.net.rdp.Common;
import omniDesk.net.rdp.Constants;
import omniDesk.net.rdp.OmniDeskException;
import omniDesk.net.rdp.Options;
import omniDesk.net.rdp.RdpPacket;
import omniDesk.net.rdp.RdpPacket_Localised;
import omniDesk.net.rdp.Secure;
import omniDesk.net.rdp.crypto.CryptoException;
*/
/**
 * @author akshay
 *
 */
public abstract class VChannel {
//protected static Logger logger = Logger.getLogger(Input.class);
	
	private int mcs_id = 0;
	
    /**
     * Provide the name of this channel
     * @return Channel name as string
     */
	public abstract String name();
	
    /**
     * Provide the set of flags specifying working options for this channel
     * @return Option flags
     */
    public abstract int flags();
    
    /**
     * Process a packet sent on this channel
     * @param data Packet sent to this channel
     * @throws RdesktopException
     * @throws IOException
     * @throws CryptoException
     */
	public abstract void process(RdpPacket data) throws OmniDeskException, IOException, CryptoException;
	public int mcs_id(){
		return mcs_id;
	}
	
    /**
     * Set the MCS ID for this channel
     * @param mcs_id New MCS ID
     */
	public void set_mcs_id(int mcs_id){
		this.mcs_id = mcs_id;
	}
	
    /**
     * Initialise a packet for transmission over this virtual channel
     * @param length Desired length of packet
     * @return Packet prepared for this channel
     * @throws RdesktopException 
     * @throws RdesktopException
     */
	public RdpPacket_Localised init(int length) throws OmniDeskException, RdesktopException{
		RdpPacket_Localised s;
		
		s = Common.secure.init(Options.encryption ? Secure.SEC_ENCRYPT : 0,length + 8);
		s.setHeader(RdpPacket.CHANNEL_HEADER);
		s.incrementPosition(8);
				
		return s;
	}
		
    /**
     * Send a packet over this virtual channel
     * @param data Packet to be sent
     * @throws RdesktopException
     * @throws IOException
     * @throws CryptoException
     * @throws RdesktopException 
     */
	public void send_packet(RdpPacket_Localised data) throws OmniDeskException, IOException, CryptoException, RdesktopException
	{
		if(Common.secure == null) return;
		int length = data.size();
		
		int data_offset = 0;
		int packets_sent = 0;
		int num_packets = (length/VChannels.CHANNEL_CHUNK_LENGTH);
		num_packets += length - (VChannels.CHANNEL_CHUNK_LENGTH)*num_packets;
		
		while(data_offset < length){
		
			int thisLength = Math.min(VChannels.CHANNEL_CHUNK_LENGTH, length - data_offset);
			
			RdpPacket_Localised s = Common.secure.init(Constants.encryption ? Secure.SEC_ENCRYPT : 0, 8 + thisLength);
			s.setLittleEndian32(length);
		
			int flags = ((data_offset == 0) ? VChannels.CHANNEL_FLAG_FIRST : 0);
			if(data_offset + thisLength >= length) flags |= VChannels.CHANNEL_FLAG_LAST;
			
			if ((this.flags() & VChannels.CHANNEL_OPTION_SHOW_PROTOCOL) != 0) flags |= VChannels.CHANNEL_FLAG_SHOW_PROTOCOL;
		
			s.setLittleEndian32(flags);
			s.copyFromPacket(data,data_offset,s.getPosition(),thisLength);
			s.incrementPosition(thisLength);
			s.markEnd();
			
			data_offset += thisLength;		
			
			if(Common.secure != null) Common.secure.send_to_channel(s, Constants.encryption ? Secure.SEC_ENCRYPT : 0, this.mcs_id());
			packets_sent++;
		}
	}
	
}
