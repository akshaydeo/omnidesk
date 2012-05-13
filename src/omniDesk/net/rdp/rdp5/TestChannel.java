/**
 * 
 */
package omniDesk.net.rdp.rdp5;

import java.io.IOException;

import omniDesk.net.rdp.OmniDeskException;
import omniDesk.net.rdp.RdpPacket;
import omniDesk.net.rdp.crypto.CryptoException;

import omniDesk.net.rdp.*;
/**
 * @author akshay
 *
 */
public abstract class TestChannel extends VChannel {

	public TestChannel(String name, int flags){
		this.name = name;
		this.flags = flags;
	}
		
	private String name;
	private int flags;
	
	public String name() {
		return name;
	}

	public int flags() {
		return flags;
	}




}
