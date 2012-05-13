package omniDesk.net.rdp;

import android.util.Log;

//import org.apache.log4j.Logger;

public abstract class RdpPacket {
	// static Logger logger = Logger.getLogger(RdpPacket.class);

	/* constants for Packet */
	public static final int MCS_HEADER = 1;
	public static final int SECURE_HEADER = 2;
	public static final int RDP_HEADER = 3;
	public static final int CHANNEL_HEADER = 4;

	protected int mcs = -1;
	protected int secure = -1;
	protected int rdp = -1;
	protected int channel = -1;
	protected int start = -1;
	protected int end = -1;

	/**
	 * Read an 8-bit integer value from the packet (at current read/write
	 * position)
	 * 
	 * @return Value read from packet
	 */
	public abstract int get8();

	/**
	 * Read an 8-bit integer value from a specified offset in the packet
	 * 
	 * @param where
	 *            Offset to read location
	 * @return Value read from packet
	 */
	public abstract int get8(int where);

	/**
	 * Write 8-bit value to packet at current read/write position
	 * 
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void set8(int what);

	/**
	 * Write 8-bit value to packet at specified offset
	 * 
	 * @param where
	 *            Offset in packet to write location
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void set8(int where, int what);

	/**
	 * Read a 2-byte, little-endian integer value from the packet (at current
	 * read/write position)
	 * 
	 * @return Value read from packet
	 */
	public abstract int getLittleEndian16();

	/**
	 * Read a 2-byte, little-endian integer value from a specified offset in the
	 * packet
	 * 
	 * @param where
	 *            Offset to read location
	 * @return Value read from packet
	 */
	public abstract int getLittleEndian16(int where);

	/**
	 * Write a 2-byte, little-endian integer value to packet at current
	 * read/write position
	 * 
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void setLittleEndian16(int what);

	/**
	 * Write a 2-byte, little-endian integer value to packet at specified offset
	 * 
	 * @param where
	 *            Offset in packet to write location
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void setLittleEndian16(int where, int what);

	/**
	 * Read a 2-byte, big-endian integer value from the packet (at current
	 * read/write position)
	 * 
	 * @return Value read from packet
	 */
	public abstract int getBigEndian16();

	/**
	 * Read a 2-byte, big-endian integer value from a specified offset in the
	 * packet
	 * 
	 * @param where
	 *            Offset to read location
	 * @return Value read from packet
	 */
	public abstract int getBigEndian16(int where);

	/**
	 * Write a 2-byte, big-endian integer value to packet at current read/write
	 * position
	 * 
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void setBigEndian16(int what);

	/**
	 * Write a 2-byte, big-endian integer value to packet at specified offset
	 * 
	 * @param where
	 *            Offset in packet to write location
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void setBigEndian16(int where, int what);

	/**
	 * Read a 3-byte, little-endian integer value from the packet (at current
	 * read position)
	 * 
	 * @return Value read from packet
	 */
	public abstract int getLittleEndian32();

	/**
	 * Read a 3-byte, little-endian integer value from a specified offset in the
	 * packet
	 * 
	 * @param where
	 *            Offset to read location
	 * @return Value read from packet
	 */
	public abstract int getLittleEndian32(int where);

	/**
	 * Write a 3-byte, little-endian integer value to packet at current
	 * read/write position
	 * 
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void setLittleEndian32(int what);

	/**
	 * Write a 3-byte, little-endian integer value to packet at specified offset
	 * 
	 * @param where
	 *            Offset in packet to write location
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void setLittleEndian32(int where, int what);

	/**
	 * Read a 3-byte, big-endian integer value from the packet (at current
	 * read/write position)
	 * 
	 * @return Value read from packet
	 */
	public abstract int getBigEndian32();

	/**
	 * Read a 3-byte, big-endian integer value from a specified offset in the
	 * packet
	 * 
	 * @param where
	 *            Offset to read location
	 * @return Value read from packet
	 */
	public abstract int getBigEndian32(int where);

	/**
	 * Write a 3-byte, big-endian integer value to packet at current read/write
	 * position
	 * 
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void setBigEndian32(int what);

	/**
	 * Write a 3-byte, big-endian integer value to packet at specified offset
	 * 
	 * @param where
	 *            Offset in packet to write location
	 * @param what
	 *            Value to write to packet
	 */
	public abstract void setBigEndian32(int where, int what);

	/**
	 * Copy data from this packet to an array of bytes
	 * 
	 * @param array
	 *            Array of bytes to which data should be copied
	 * @param array_offset
	 *            Offset into array for start of data
	 * @param mem_offset
	 *            Offset into packet for start of data
	 * @param len
	 *            Length of data to be copied
	 */
	public abstract void copyToByteArray(byte[] array, int array_offset,
			int mem_offset, int len);

	/**
	 * Copy data to this packet from an array of bytes
	 * 
	 * @param array
	 *            Array of bytes containing source data
	 * @param array_offset
	 *            Offset into array for start of data
	 * @param mem_offset
	 *            Offset into packet for start of data
	 * @param len
	 *            Length of data to be copied
	 */
	public abstract void copyFromByteArray(byte[] array, int array_offset,
			int mem_offset, int len);

	/**
	 * Copy data from this packet to another packet
	 * 
	 * @param dst
	 *            Destination packet
	 * @param srcOffset
	 *            Offset into this packet (source) for start of data
	 * @param dstOffset
	 *            Offset into destination packet for start of data
	 * @param len
	 *            Length of data to be copied
	 */
	public abstract void copyToPacket(RdpPacket_Localised dst, int srcOffset,
			int dstOffset, int len);

	/**
	 * Copy data to this packet from another packet
	 * 
	 * @param src
	 *            Source packet
	 * @param srcOffset
	 *            Offset into source packet for start of data
	 * @param dstOffset
	 *            Offset into this packet (destination) for start of data
	 * @param len
	 *            Length of data to be copied
	 */
	public abstract void copyFromPacket(RdpPacket_Localised src, int srcOffset,
			int dstOffset, int len);

	/**
	 * Retrieve size of this packet
	 * 
	 * @return Packet size
	 */
	public abstract int size();

	/**
	 * Retrieve offset to current read/write position
	 * 
	 * @return Current read/write position (as byte offset from start)
	 */
	public abstract int getPosition();

	/**
	 * Set current read/write position
	 * 
	 * @param position
	 *            New read/write position (as byte offset from start)
	 */
	public abstract void setPosition(int position);

	/**
	 * Advance the read/write position
	 * 
	 * @param length
	 *            Number of bytes to advance read position by
	 */
	public abstract void incrementPosition(int length);

	/**
	 * Mark current read/write position as end of packet
	 */
	public void markEnd() {
		this.end = getPosition();
	}

	/**
	 * Retrieve capacity of this packet
	 * 
	 * @return Packet capacity (in bytes)
	 */
	public abstract int capacity();

	/**
	 * Mark specified position as end of packet
	 * 
	 * @param position
	 *            New end position (as byte offset from start)
	 */
	public void markEnd(int position) {
		if (position > capacity()) {
			throw new ArrayIndexOutOfBoundsException("Mark > size!");
		}
		this.end = position;
	}

	/**
	 * Retrieve location of packet end
	 * 
	 * @return Position of packet end (as byte offset from start)
	 */
	public int getEnd() {
		return this.end;
	}

	/**
	 * Reserve space within this packet for writing of headers for a specific
	 * communications layer. Move read/write position ready for adding data for
	 * a higher communications layer.
	 * 
	 * @param header
	 *            ID of header type
	 * @param increment
	 *            Required size to be reserved for header
	 * @throws RdesktopException
	 */
	public void pushLayer(int header, int increment) throws RdesktopException {
		Log.d("Rdppacket.java","pushLayer");
		this.setHeader(header);// mayuresh
		this.incrementPosition(increment);
		//this.setStart(this.getPosition());
	}

	/**
	 * Get location of the header for a specific communications layer
	 * 
	 * @param header
	 *            ID of header type
	 * @return Location of header, as byte offset from start of packet
	 * @throws RdesktopException
	 */
	public int getHeader(int header) throws RdesktopException {
		//Log.d("Rdppacket.java", "getHeader");
		switch (header) {
		case RdpPacket_Localised.MCS_HEADER:
			return this.mcs;
		case RdpPacket_Localised.SECURE_HEADER:
			return this.secure;
		case RdpPacket_Localised.RDP_HEADER:
			return this.rdp;
		case RdpPacket_Localised.CHANNEL_HEADER:
			return this.channel;
		default:
			Log.d("Rdppacket.java", "I am in default!!");
			throw new RdesktopException("Wrong Header!");
		}
	}

	/**
	 * Set current read/write position as the start of a layer header
	 * 
	 * @param header
	 *            ID of header type
	 * @throws RdesktopException
	 */
	public void setHeader(int header) throws RdesktopException {
	//	Log.d("Rdppacket.java", "setHeader");
		switch (header) {
		case RdpPacket_Localised.MCS_HEADER:
			this.mcs = this.getPosition();
			break;
		case RdpPacket_Localised.SECURE_HEADER:
			this.secure = this.getPosition();
			break;
		case RdpPacket_Localised.RDP_HEADER:
			this.rdp = this.getPosition();
			break;
		case RdpPacket_Localised.CHANNEL_HEADER:
			this.channel = this.getPosition();
			break;
		default:
			throw new RdesktopException("Wrong Header!");
		}
	//	Log.d("Rdppacket.java","exit setHeader");
	}

	/**
	 * Retrieve start location of this packet
	 * 
	 * @return Start location of packet (as byte offset from location 0)
	 */
	public int getStart() {
		return this.start;
	}

	/**
	 * Set start position of this packet
	 * 
	 * @param position
	 *            New start position (as byte offset from location 0)
	 */
	public void setStart(int position) {
	//	Log.d("Rdppacket.java","setStart");
		this.start = position;
	}

	/**
	 * Add a unicode string to this packet at the current read/write position
	 * 
	 * @param str
	 *            String to write as unicode to packet
	 * @param len
	 *            Desired length of output unicode string
	 */
	public void outUnicodeString(String str, int len) {
		int i = 0, j = 0;

		if (str.length() != 0) {
			char[] name = str.toCharArray();
			while (i < len) {
				this.setLittleEndian16((short) name[j++]);
				i += 2;
			}
			this.setLittleEndian16(0); // Terminating Null Character
		} else {
			this.setLittleEndian16(0);
		}
	}

	/**
	 * Write an ASCII string to this packet at current read/write position
	 * 
	 * @param str
	 *            String to be written
	 * @param length
	 *            Length in bytes to be occupied by string (may be longer than
	 *            string itself)
	 */
	public void out_uint8p(String str, int length) {
		byte[] bStr = str.getBytes();
		this.copyFromByteArray(bStr, 0, this.getPosition(), bStr.length);
		this.incrementPosition(length);
	}
}
