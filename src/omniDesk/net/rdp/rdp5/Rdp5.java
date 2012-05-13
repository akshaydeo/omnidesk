/**
 * 
 */
package omniDesk.net.rdp.rdp5;
/*
import omniDesk.net.rdp.OmniDeskException;
import omniDesk.net.rdp.OrderException;
import omniDesk.net.rdp.Rdp;
import omniDesk.net.rdp.RdpPacket_Localised;*/
import omniDesk.net.rdp.Rdp;
import omniDesk.net.rdp.crypto.CryptoException;

/**
 * @author akshay
 *
 */
public class Rdp5 extends Rdp {
	 private VChannels channels;

	    /**
	     * Initialise the RDP5 communications layer, with specified virtual channels
	     * 
	     * @param channels
	     *            Virtual channels for RDP layer
	     */
	    public Rdp5(VChannels channels) {
	        super(channels);
	        this.channels = channels;
	    }
}
