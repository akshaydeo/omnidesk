/**
 * 
 */
package omniDesk.net.rdp;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import android.graphics.Paint;
import android.util.Log;

import omniDesk.net.rdp.crypto.CryptoException;
import omniDesk.net.rdp.rdp5.VChannels;

/**
 * @author akshay
 * 
 */
public class Rdp {

	public static int RDP5_DISABLE_NOTHING = 0x00;

	public static int RDP5_NO_WALLPAPER = 0x01;

	public static int RDP5_NO_FULLWINDOWDRAG = 0x02;

	public static int RDP5_NO_MENUANIMATIONS = 0x04;

	public static int RDP5_NO_THEMING = 0x08;

	public static int RDP5_NO_CURSOR_SHADOW = 0x20;

	public static int RDP5_NO_CURSORSETTINGS = 0x40;
	/*
	 * disables cursor blinking
	 */

	/* constants for RDP Layer */
	public static final int RDP_LOGON_NORMAL = 0x33;

	public static final int RDP_LOGON_AUTO = 0x8;

	public static final int RDP_LOGON_BLOB = 0x100;
	
	public static final int RDP_LOGON_LEAVE_AUDIO = 0x2000;

	// PDU Types
	private static final int RDP_PDU_DEMAND_ACTIVE = 1;

	private static final int RDP_PDU_CONFIRM_ACTIVE = 3;

	private static final int RDP_PDU_DEACTIVATE = 6;

	private static final int RDP_PDU_DATA = 7;

	// Data PDU Types
	private static final int RDP_DATA_PDU_UPDATE = 2;

	private static final int RDP_DATA_PDU_CONTROL = 20;

	private static final int RDP_DATA_PDU_POINTER = 27;

	private static final int RDP_DATA_PDU_INPUT = 28;

	private static final int RDP_DATA_PDU_SYNCHRONISE = 31;

	private static final int RDP_DATA_PDU_BELL = 34;

	private static final int RDP_DATA_PDU_LOGON = 38;

	private static final int RDP_DATA_PDU_FONT2 = 39;

	private static final int RDP_DATA_PDU_DISCONNECT = 47;

	// Control PDU types
	private static final int RDP_CTL_REQUEST_CONTROL = 1;

	@SuppressWarnings("unused")
	private static final int RDP_CTL_GRANT_CONTROL = 2;

	@SuppressWarnings("unused")
	private static final int RDP_CTL_DETACH = 3;

	private static final int RDP_CTL_COOPERATE = 4;

	// Update PDU Types
	private static final int RDP_UPDATE_ORDERS = 0;

	private static final int RDP_UPDATE_BITMAP = 1;

	private static final int RDP_UPDATE_PALETTE = 2;

	private static final int RDP_UPDATE_SYNCHRONIZE = 3;

	// Pointer PDU Types
	private static final int RDP_POINTER_SYSTEM = 1;

	private static final int RDP_POINTER_MOVE = 3;

	private static final int RDP_POINTER_COLOR = 6;

	private static final int RDP_POINTER_CACHED = 7;

	// System Pointer Types
	private static final int RDP_NULL_POINTER = 0;

	private static final int RDP_DEFAULT_POINTER = 0x7F00;

	// Input Devices
	private static final int RDP_INPUT_SYNCHRONIZE = 0;

	private static final int RDP_INPUT_CODEPOINT = 1;

	private static final int RDP_INPUT_VIRTKEY = 2;

	private static final int RDP_INPUT_SCANCODE = 4;

	private static final int RDP_INPUT_MOUSE = 0x8001;

	/* RDP capabilities */
	private static final int RDP_CAPSET_GENERAL = 1;

	private static final int RDP_CAPLEN_GENERAL = 0x18;

	private static final int OS_MAJOR_TYPE_UNIX = 4;

	private static final int OS_MINOR_TYPE_XSERVER = 7;

	private static final int RDP_CAPSET_BITMAP = 2;

	private static final int RDP_CAPLEN_BITMAP = 0x1C;

	private static final int RDP_CAPSET_ORDER = 3;

	private static final int RDP_CAPLEN_ORDER = 0x58;

	private static final int ORDER_CAP_NEGOTIATE = 2;

	private static final int ORDER_CAP_NOSUPPORT = 4;

	private static final int RDP_CAPSET_BMPCACHE = 4;

	private static final int RDP_CAPLEN_BMPCACHE = 0x28;

	private static final int RDP_CAPSET_CONTROL = 5;

	private static final int RDP_CAPLEN_CONTROL = 0x0C;

	private static final int RDP_CAPSET_ACTIVATE = 7;

	private static final int RDP_CAPLEN_ACTIVATE = 0x0C;

	private static final int RDP_CAPSET_POINTER = 8;

	private static final int RDP_CAPLEN_POINTER = 0x08;

	private static final int RDP_CAPSET_SHARE = 9;

	private static final int RDP_CAPLEN_SHARE = 0x08;

	private static final int RDP_CAPSET_COLCACHE = 10;

	private static final int RDP_CAPLEN_COLCACHE = 0x08;

	private static final int RDP_CAPSET_UNKNOWN = 13;

	private static final int RDP_CAPLEN_UNKNOWN = 0x9C;
	
	private static final int RDP_CAPLEN_VIRTUAL = 0x0c;	

	private static final int RDP_CAPSET_BMPCACHE2 = 19;

	private static final int RDP_CAPLEN_BMPCACHE2 = 0x28;

	private static final int BMPCACHE2_FLAG_PERSIST = (1 << 31);

	/* RDP bitmap cache (version 2) constants */
	public static final int BMPCACHE2_C0_CELLS = 0x78;

	public static final int BMPCACHE2_C1_CELLS = 0x78;

	public static final int BMPCACHE2_C2_CELLS = 0x150;

	public static final int BMPCACHE2_NUM_PSTCELLS = 0x9f6;

	private static final int RDP5_FLAG = 0x0030;

	private static final byte[] RDP_SOURCE = { (byte) 0x4D, (byte) 0x53,
		(byte) 0x54, (byte) 0x53, (byte) 0x43, (byte) 0x00 };
	// string MSTSC encoded as 7 byte US-Ascii

	protected Secure SecureLayer = null;

	private OmniDeskCanvas surface;
	public Input inputHandler;

	protected Orders orders = null;

	private Cache cache = null;

	private int next_packet = 0;

	private int rdp_shareid = 0;

	private boolean connected = false;

	private RdpPacket_Localised stream = null;

	/*
	 * private final byte[] canned_caps = { (byte)0x01, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x09, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x04,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x0C, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0C, (byte)0x00, (byte)0x08,
	 * (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0E,
	 * (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
	 * (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x34, (byte)0x00, (byte)0xFE,
	 * (byte)0x00, (byte)0x04, (byte)0x00, (byte)0xFE, (byte)0x00, (byte)0x04,
	 * (byte)0x00, (byte)0xFE, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0xFE,
	 * (byte)0x00, (byte)0x08, (byte)0x00, (byte)0xFE, (byte)0x00, (byte)0x10,
	 * (byte)0x00, (byte)0xFE, (byte)0x00, (byte)0x20, (byte)0x00, (byte)0xFE,
	 * (byte)0x00, (byte)0x40, (byte)0x00, (byte)0xFE, (byte)0x00, (byte)0x80,
	 * (byte)0x00, (byte)0xFE, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x40,
	 * (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x01, (byte)0x00,
	 * (byte)0x01, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x00 };
	 */
	private final byte[] canned_caps = { 0x01, 0x00, 0x00, 0x00, 0x09, 0x04,
			0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0C,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x0C, 0x00, 0x08, 0x00, 0x01, 0x00, 0x00, 0x00, 0x0E, 0x00,
			0x08, 0x00, 0x01, 0x00, 0x00, 0x00, 0x10, 0x00, 0x34, 0x00,
			(byte) 0xfe, 0x00, 0x04, 0x00, (byte) 0xfe, 0x00, 0x04, 0x00,
			(byte) 0xFE, 0x00, 0x08, 0x00, (byte) 0xFE, 0x00, 0x08, 0x00,
			(byte) 0xFE, 0x00, 0x10, 0x00, (byte) 0xFE, 0x00, 0x20, 0x00,
			(byte) 0xFE, 0x00, 0x40, 0x00, (byte) 0xFE, 0x00, (byte) 0x80,
			0x00, (byte) 0xFE, 0x00, 0x00, 0x01, 0x40, 0x00, 0x00, 0x08, 0x00,
			0x01, 0x00, 0x01, 0x02, 0x00, 0x00, 0x00 };

	// check the pg 133 onwards Input Capability Set
	// INPUT_FLAG_SCANCODES(2bytes) =0x0001
	// Pad (2 bytes)=0
	// keyboardLayout (4 bytes): must match to that specified in Client Core
	// Data
	// keyboardType (4 bytes): 0x00000004 IBM enhanced (101- or 102-key)
	// keyboard
	// keyboardSubType (4 bytes): must match Client Core data
	// keyboardFunctionKey (4 bytes): "------"
	// imeFileName (64 bytes): A 64-byte field. Input Method Editor (IME) file
	// name associated with
	// the input locale....looks like it is all 0
	static byte caps_0x0d[] = { 0x01, 0x00, 0x00, 0x00, 0x09, 0x04, 0x00, 0x00,
		0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0C, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	// Sound Capability Set pg 139
	static byte caps_0x0c[] = { 0x01, 0x00, 0x00, 0x00 };// Playing a beep sound
	// is supported.

	// font capability set
	static byte caps_0x0e[] = { 0x01, 0x00, 0x00, 0x00 };

	// Glypph capability set
	static byte caps_0x10[] = { (byte) 0xFE, 0x00, 0x04, 0x00, (byte) 0xFE,
		0x00, 0x04, 0x00, (byte) 0xFE, 0x00, 0x08, 0x00, (byte) 0xFE, 0x00,
		0x08, 0x00, (byte) 0xFE, 0x00, 0x10, 0x00, (byte) 0xFE, 0x00, 0x20,
		0x00, (byte) 0xFE, 0x00, 0x40, 0x00, (byte) 0xFE, 0x00,
		(byte) 0x80, 0x00, (byte) 0xFE, 0x00, 0x00, 0x01, 0x40, 0x00, 0x00,
		0x08, 0x00, 0x01, 0x00, 0x01,/* 0x02 */0x00, 0x00, 0x00, 0x00 };

	
	   //virtual channel capability set
    static byte caps_0x14[] = {0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x00};
	

	/**
	 * Process a general capability set
	 * 
	 * @param data
	 *            Packet containing capability set data at current read position
	 */
	static void processGeneralCaps(RdpPacket_Localised data) {
		int pad2octetsB; /* rdp5 flags? */

		data.incrementPosition(10); // in_uint8s(s, 10);
		pad2octetsB = data.getLittleEndian16(); // in_uint16_le(s, pad2octetsB);

		if (pad2octetsB != 0)
			Options.use_rdp5 = false;
	}

	/**
	 * Process a bitmap capability set
	 * 
	 * @param data
	 *            Packet containing capability set data at current read position
	 */
	static void processBitmapCaps(RdpPacket_Localised data) {
		int width, height, bpp;

		bpp = data.getLittleEndian16(); // in_uint16_le(s, bpp);
		data.incrementPosition(6); // in_uint8s(s, 6);

		width = data.getLittleEndian16(); // in_uint16_le(s, width);
		height = data.getLittleEndian16(); // in_uint16_le(s, height);

		/*
		 * The server may limit bpp and change the size of the desktop (for
		 * example when shadowing another session).
		 */
		if (Options.server_bpp != bpp) {
			// logger.warn("colour depth changed from " + Options.server_bpp
			// + " to " + bpp);
			Options.server_bpp = bpp;
		}
		if (Options.width != width || Options.height != height) {
			// logger.warn("screen size changed from " + Options.width + "x"
			// + Options.height + " to " + width + "x" + height);
			Options.width = width;
			Options.height = height;
			// ui_resize_window(); TODO: implement resize thingy
		}
	}

	/**
	 * Process server capabilities
	 * 
	 * @param data
	 *            Packet containing capability set data at current read position
	 */
	void processServerCaps(RdpPacket_Localised data, int length) {
		int n;
		int next, start;
		int ncapsets, capset_type, capset_length;

		start = data.getPosition();

		ncapsets = data.getLittleEndian16(); // in_uint16_le(s, ncapsets);
		data.incrementPosition(2); // in_uint8s(s, 2); /* pad */

		for (n = 0; n < ncapsets; n++) {
			if (data.getPosition() > start + length)
				return;

			capset_type = data.getLittleEndian16(); // in_uint16_le(s,
			// capset_type);
			capset_length = data.getLittleEndian16(); // in_uint16_le(s,
			// capset_length);

			next = data.getPosition() + capset_length - 4;

			switch (capset_type) {
			case RDP_CAPSET_GENERAL:
				processGeneralCaps(data);
				break;

			case RDP_CAPSET_BITMAP:
				processBitmapCaps(data);
				break;
			}

			data.setPosition(next);
		}
	}

	/**
	 * Process a disconnect PDU
	 * 
	 * @param data
	 *            Packet containing disconnect PDU at current read position
	 * @return Code specifying the reason for disconnection
	 */
	protected int processDisconnectPdu(RdpPacket_Localised data) {
		// logger.debug("Received disconnect PDU");
		return data.getLittleEndian32();
	}

	/**
	 * Initialise RDP comms layer, and register virtual channels
	 * 
	 * @param channels
	 *            Virtual channels to be used in connection
	 */
	public Rdp(VChannels channels) {
		this.SecureLayer = new Secure(channels);
		Common.secure = SecureLayer;
		//this.orders = new Orders();
		//this.cache = new Cache();
		//orders.registerCache(cache);

	}

	/**
	 * Connect to a server
	 * 
	 * @param username
	 *            Username for log on
	 * @param server
	 *            Server to connect to
	 * @param flags
	 *            Flags defining logon type
	 * @param domain
	 *            Domain for log on
	 * @param password
	 *            Password for log on
	 * @param command
	 *            Alternative shell for session
	 * @param directory
	 *            Initial working directory for connection
	 * @throws ConnectionException
	 * @throws OmniDeskException
	 */
	public void connect(String username, InetAddress server, int flags,
			String domain, String password, String command, String directory)
	throws ConnectionException, OmniDeskException {

		try {
			SecureLayer.connect(server, Options.port);
			this.connected = true;
			this.sendLogonInfo(flags, domain, username, password, command,
					directory);// Info Packet (TS_INFO_PACKET).. i THINK!!

		}
		// Handle an unresolvable hostname
		catch (UnknownHostException e) {
			throw new ConnectionException("Could not resolve host name: "
					+ server);
		}
		// Handle a refused connection
		catch (ConnectException e) {
			throw new ConnectionException(
					"Connection refused when trying to connect to " + server
					+ " on port " + Options.port);
		}
		// Handle a timeout on connecting
		catch (NoRouteToHostException e) {
			throw new ConnectionException(
					"Connection timed out when attempting to connect to "
					+ server);
		} catch (IOException e) {
			throw new ConnectionException("Connection Failed");
		} catch (RdesktopException e) {
			throw new ConnectionException(e.getMessage());
		} catch (OrderException e) {
			throw new ConnectionException(e.getMessage());
		} catch (CryptoException e) {
			throw new ConnectionException(e.getMessage());
		}

	}

	/**
	 * Send user logon details to the server
	 * 
	 * @param flags
	 *            Set of flags defining logon type
	 * @param domain
	 *            Domain for logon
	 * @param username
	 *            Username for logon
	 * @param password
	 *            Password for logon
	 * @param command
	 *            Alternative shell for session
	 * @param directory
	 *            Starting working directory for session
	 * @throws RdesktopException
	 * @throws IOException
	 * @throws CryptoException
	 * @throws OmniDeskException
	 */
	private void sendLogonInfo(int flags, String domain, String username,
			String password, String command, String directory)
	throws RdesktopException, IOException, CryptoException,
	OmniDeskException {

		int len_ip = 2 * "127.0.0.1".length();
		int len_dll = 2 * "C:\\WINNT\\System32\\mstscax.dll".length();
		int packetlen = 0;

		int sec_flags = Constants.encryption ? (Secure.SEC_LOGON_INFO | Secure.SEC_ENCRYPT)
				: Secure.SEC_LOGON_INFO;
		int domainlen = 2 * domain.length();
		int userlen = 2 * username.length();
		int passlen = 2 * password.length();
		int commandlen = 2 * command.length();
		int dirlen = 2 * directory.length();

		RdpPacket_Localised data;

		if (!Options.use_rdp5 || 1 == Options.server_rdp_version) {

			data = SecureLayer.init(sec_flags, 18 + domainlen + userlen
					+ passlen + commandlen + dirlen + 10);

			data.setLittleEndian32(0);
			data.setLittleEndian32(flags);
			data.setLittleEndian16(domainlen);
			data.setLittleEndian16(userlen);
			data.setLittleEndian16(passlen);
			data.setLittleEndian16(commandlen);
			data.setLittleEndian16(dirlen);
			data.outUnicodeString(domain, domainlen);
			data.outUnicodeString(username, userlen);
			data.outUnicodeString(password, passlen);
			data.outUnicodeString(command, commandlen);
			data.outUnicodeString(directory, dirlen);

		} else {
			flags |= RDP_LOGON_BLOB;

			packetlen = 4
			+ // Unknown uint32
			4
			+ // flags
			2
			+ // len_domain
			2
			+ // len_user
			((flags & RDP_LOGON_AUTO) != 0 ? 2 : 0)
			+ // len_password
			((flags & RDP_LOGON_BLOB) != 0 ? 2 : 0)
			+ // Length of BLOB
			2
			+ // len_program
			2
			+ // len_directory
			(0 < domainlen ? domainlen + 2 : 2)
			+ // domain
			userlen
			+ ((flags & RDP_LOGON_AUTO) != 0 ? passlen : 0)
			+ 0
			+ // We have no 512 byte BLOB. Perhaps we must?
			((flags & RDP_LOGON_BLOB) != 0
					&& (flags & RDP_LOGON_AUTO) == 0 ? 2 : 0)
					+ (0 < commandlen ? commandlen + 2 : 2)
					+ (0 < dirlen ? dirlen + 2 : 2) + 2 + // Unknown (2)
					2 + // Client ip length
					len_ip + // Client ip
					2 + // DLL string length
					len_dll + // DLL string
					2 + // Unknown
					2 + // Unknown
					64 + // Time zone #0
					20 + // Unknown
					64 + // Time zone #1
					32 + 6; // Unknown

			data = SecureLayer.init(sec_flags, packetlen); // s =
			// sec_init(sec_flags,
			// packetlen);

			data.setLittleEndian32(0); // out_uint32(s, 0); // Unknown
			data.setLittleEndian32(flags); // out_uint32_le(s, flags);
			data.setLittleEndian16(domainlen); // out_uint16_le(s, len_domain);
			data.setLittleEndian16(userlen); // out_uint16_le(s, len_user);
			if ((flags & RDP_LOGON_AUTO) != 0) {
				data.setLittleEndian16(passlen); // out_uint16_le(s,
				// len_password);
			}
			if ((flags & RDP_LOGON_BLOB) != 0
					&& ((flags & RDP_LOGON_AUTO) == 0)) {
				data.setLittleEndian16(0); // out_uint16_le(s, 0);
			}
			data.setLittleEndian16(commandlen); // out_uint16_le(s,
			// len_program);
			data.setLittleEndian16(dirlen); // out_uint16_le(s, len_directory);

			if (0 < domainlen)
				data.outUnicodeString(domain, domainlen); // rdp_out_unistr(s,
			// domain,
			// len_domain);
			else
				data.setLittleEndian16(0); // out_uint16_le(s, 0);

			data.outUnicodeString(username, userlen); // rdp_out_unistr(s,
			// user, len_user);
			if ((flags & RDP_LOGON_AUTO) != 0) {
				data.outUnicodeString(password, passlen); // rdp_out_unistr(s,
				// password,
				// len_password);
			}
			if ((flags & RDP_LOGON_BLOB) != 0 && (flags & RDP_LOGON_AUTO) == 0) {
				data.setLittleEndian16(0); // out_uint16_le(s, 0);
			}
			if (0 < commandlen) {
				data.outUnicodeString(command, commandlen); // rdp_out_unistr(s,
				// program,
				// len_program);
			} else {
				data.setLittleEndian16(0); // out_uint16_le(s, 0);
			}
			if (0 < dirlen) {
				data.outUnicodeString(directory, dirlen); // rdp_out_unistr(s,
				// directory,
				// len_directory);
			} else {
				data.setLittleEndian16(0); // out_uint16_le(s, 0);
			}
			data.setLittleEndian16(2); // out_uint16_le(s, 2);
			data.setLittleEndian16(len_ip + 2); // out_uint16_le(s, len_ip + 2);
			// // Length of client ip
			data.outUnicodeString("127.0.0.1", len_ip); // rdp_out_unistr(s,
			// "127.0.0.1",
			// len_ip);
			data.setLittleEndian16(len_dll + 2); // out_uint16_le(s, len_dll
			// + 2);
			data.outUnicodeString("C:\\WINNT\\System32\\mstscax.dll", len_dll); // rdp_out_unistr(s,
			// "C:\\WINNT\\System32\\mstscax.dll",
			// len_dll);
			data.setLittleEndian16(0xffc4); // out_uint16_le(s, 0xffc4);
			data.setLittleEndian16(0xffff); // out_uint16_le(s, 0xffff);
			data.outUnicodeString("GTB, normaltid", 2 * "GTB, normaltid"
					.length()); // rdp_out_unistr(s, "GTB, normaltid", 2 *
			// strlen("GTB, normaltid"));
			data.incrementPosition(62 - 2 * "GTB, normaltid".length()); // out_uint8s(s,
			// 62 -
			// 2 *
			// strlen("GTB,
			// normaltid"));

			data.setLittleEndian32(0x0a0000); // out_uint32_le(s, 0x0a0000);
			data.setLittleEndian32(0x050000); // out_uint32_le(s, 0x050000);
			data.setLittleEndian32(3); // out_uint32_le(s, 3);
			data.setLittleEndian32(0); // out_uint32_le(s, 0);
			data.setLittleEndian32(0); // out_uint32_le(s, 0);

			data.outUnicodeString("GTB, sommartid", 2 * "GTB, sommartid"
					.length()); // rdp_out_unistr(s, "GTB, sommartid", 2 *
			// strlen("GTB, sommartid"));
			data.incrementPosition(62 - 2 * "GTB, sommartid".length()); // out_uint8s(s,
			// 62 -
			// 2 *
			// strlen("GTB,
			// sommartid"));

			data.setLittleEndian32(0x30000); // out_uint32_le(s, 0x30000);
			data.setLittleEndian32(0x050000); // out_uint32_le(s, 0x050000);
			data.setLittleEndian32(2); // out_uint32_le(s, 2);
			data.setLittleEndian32(0); // out_uint32(s, 0);
			data.setLittleEndian32(0xffffffc4); // out_uint32_le(s, 0xffffffc4);
			data.setLittleEndian32(0xfffffffe); // out_uint32_le(s, 0xfffffffe);
			data.setLittleEndian32(Options.rdp5_performanceflags); // out_uint32_le(s,
			// 0x0f);
			data.setLittleEndian32(0); // out_uint32(s, 0);
		}

		data.markEnd();
		byte[] buffer = new byte[data.getEnd()];
		data.copyToByteArray(buffer, 0, 0, data.getEnd());
		SecureLayer.send(data, sec_flags);
	}

	/**
	 * Disconnect from an RDP session
	 */
	public void disconnect() {
		this.connected = false;
		SecureLayer.disconnect();
	}

	/**
	 * RDP receive loop
	 * 
	 * @param deactivated
	 *            On return, stores true in deactivated[0] if the session
	 *            disconnected cleanly
	 * @param ext_disc_reason
	 *            On return, stores the reason for disconnection in
	 *            ext_disc_reason[0]
	 * @throws IOException
	 * @throws RdesktopException
	 * @throws OrderException
	 * @throws CryptoException
	 * @throws OmniDeskException
	 */
	public void mainLoop(boolean[] deactivated, int[] ext_disc_reason)
	throws IOException, RdesktopException, OrderException,
	CryptoException, OmniDeskException {
		int[] type = new int[1];

		boolean disc = false; /* True when a disconnect PDU was received */
		boolean cont = true;

		RdpPacket_Localised data = null;

		while (Common.ConnectionStatus) {
			
			try {
				data = this.receive(type);
				if (data == null)
					return;
			} catch (EOFException e) {
				return;
			}

			switch (type[0]) {

			case (Rdp.RDP_PDU_DEMAND_ACTIVE):
				Log.d("Rdp.java/mainloop", "Rdp.RDP_PDU_DEMAND_ACTIVE");
			// get this after licence negotiation, just before the 1st
			// order...

			this.processDemandActive(data);
			// can use this to trigger things that have to be done before
			// 1st order

			ConnectionManager.readytosend = true;

			deactivated[0] = false;
			break;

			case (Rdp.RDP_PDU_DEACTIVATE):
				System.out.println("deacticate packet received....!!!!!!!!!");
				deactivated[0] = true;
			this.stream = null; // ty this fix
			break;

			case (Rdp.RDP_PDU_DATA):
				disc = this.processData(data, ext_disc_reason);

			break;

			case 0:
				break; // 32K keep alive fix, see receive() - rdesktop 1.2.0.

			default:
				throw new RdesktopException("Unimplemented type in main loop :"
						+ type[0]);
			}

			if (disc)
				return;
		}
		return;
	}

	/**
	 * Receive a packet from the RDP layer
	 * 
	 * @param type
	 *            Type of PDU received, stored in type[0]
	 * @return Packet received from RDP layer
	 * @throws IOException
	 * @throws RdesktopException
	 * @throws CryptoException
	 * @throws OrderException
	 * @throws OmniDeskException
	 */
	private RdpPacket_Localised receive(int[] type) throws IOException,
	RdesktopException, CryptoException, OrderException,
	OmniDeskException {

		int length = 0;

		if ((this.stream == null) || (this.next_packet >= this.stream.getEnd())) {
			this.stream = SecureLayer.receive();
			if (stream == null)
				return null;
			this.next_packet = this.stream.getPosition();
		} else {
			this.stream.setPosition(this.next_packet);// this is processing of
			// the shared
		} // control header pg 147 [MS-RDPBCGR] -Neer
		length = this.stream.getLittleEndian16();

		/* 32k packets are really 8, keepalive fix - rdesktop 1.2.0 */
		if (length == 0x8000) {
			Log.w("Rdp.java/receive", "32k packet keepalive fix");
			next_packet += 8;
			type[0] = 0;
			return stream;
		}
		type[0] = this.stream.getLittleEndian16() & 0xf;// the type is located
		// at least significant
		if (stream.getPosition() != stream.getEnd()) {// 4 bits hence ANDed by
			// 0xf
			stream.incrementPosition(2);// rest info is ignored
		}

		this.next_packet += length;
		return stream;
	}

	/**
	 * Process an activation demand from the server (received between licence
	 * negotiation and 1st order)
	 * 
	 * @param data
	 *            Packet containing demand at current read position
	 * @throws RdesktopException
	 * @throws IOException
	 * @throws CryptoException
	 * @throws OrderException
	 * @throws OmniDeskException
	 */
	private void processDemandActive(RdpPacket_Localised data)
	throws RdesktopException, IOException, CryptoException,
	OrderException, OmniDeskException {// processing the Demand Active
		// PDU sent by server

		int type[] = new int[1];// pg 72
		// these share ids form the part of T128 packets
		this.rdp_shareid = data.getLittleEndian32();// share id is 32bit
		// desccribed on pg 73 MSRDPBCGR
		this.sendConfirmActive();

		this.sendSynchronize();
		this.sendControl(RDP_CTL_COOPERATE);// co-operate request
		this.sendControl(RDP_CTL_REQUEST_CONTROL);// request control

		// server sync PDU. Not much info in it. IGNORED!!! Same goes for the
		// below 2
		this.receive(type); // Receive RDP_PDU_SYNCHRONIZE

		this.receive(type); // Receive RDP_CTL_COOPERATE
		this.receive(type); // Receive RDP_CTL_GRANT_CONTROL
		// there is also a Server Font Map PDU. client doesn't wait!! -Neer
		this.sendInput(0, RDP_INPUT_SYNCHRONIZE, 0, 0, 0);
		this.sendFonts(1);
		this.sendFonts(2);

		this.receive(type); // Receive an unknown PDU Code = 0x28

		System.out.println("The type is : " + Integer.toHexString(type[0]));

	}

	private void sendFonts(int seq) throws RdesktopException, IOException,
	CryptoException, OmniDeskException {
		Log.d("Rdp.java", " Inside sendFonts");
		RdpPacket_Localised data = this.initData(8);

		data.setLittleEndian16(0); /* number of fonts */
		data.setLittleEndian16(0x3e); /* unknown */
		data.setLittleEndian16(seq); /* unknown */
		data.setLittleEndian16(0x32); /* entry size */

		data.markEnd();

		this.sendData(data, RDP_DATA_PDU_FONT2);
	}

	/**
	 * Send a packet on the RDP layer
	 * 
	 * @param data
	 *            Packet to send
	 * @param data_pdu_type
	 *            Type of data
	 * @throws RdesktopException
	 * @throws IOException
	 * @throws CryptoException
	 * @throws OmniDeskException
	 */
	private void sendData(RdpPacket_Localised data, int data_pdu_type)
	throws RdesktopException, IOException, CryptoException,
	OmniDeskException {

		CommunicationMonitor.lock(this);

		int length;

		data.setPosition(data.getHeader(RdpPacket_Localised.RDP_HEADER));
		length = data.getEnd() - data.getPosition();

		data.setLittleEndian16(length);
		data.setLittleEndian16(RDP_PDU_DATA | 0x10);
		data.setLittleEndian16(SecureLayer.getUserID() + 1001);

		data.setLittleEndian32(this.rdp_shareid);
		data.set8(0); // pad
		data.set8(1); // stream id
		data.setLittleEndian16(length - 14);
		data.set8(data_pdu_type);
		data.set8(0); // compression type
		data.setLittleEndian16(0); // compression length

		SecureLayer.send(data, Constants.encryption ? Secure.SEC_ENCRYPT : 0);

		CommunicationMonitor.unlock(this);
	}

	public void sendInput(int time, int message_type, int device_flags,
			int param1, int param2) throws OmniDeskException {

		Log.d("Rdp.java", " Inside sendInput");
		RdpPacket_Localised data = null;
		try {
			data = this.initData(16);
		} catch (RdesktopException e) {
			ConnectionManager.error(e, this, false);
		}

		data.setLittleEndian16(1); /* number of events */
		data.setLittleEndian16(0); /* pad */

		data.setLittleEndian32(time);
		data.setLittleEndian16(message_type);
		data.setLittleEndian16(device_flags);
		data.setLittleEndian16(param1);
		data.setLittleEndian16(param2);

		data.markEnd();

		try {
			this.sendData(data, RDP_DATA_PDU_INPUT);
		} catch (RdesktopException r) {
			if (Common.rdp.isConnected())
				ConnectionManager.error(r, Common.rdp, /* Common.frame,. Neer */
						true);
			//Common.exit();
		} catch (CryptoException c) {
			if (Common.rdp.isConnected())
				ConnectionManager.error(c, Common.rdp, /* Common.frame,. Neer */
						true);
			//Common.exit();
		} catch (IOException i) {
			if (Common.rdp.isConnected())
				ConnectionManager.error(i, Common.rdp,/* Common.frame.. Neer, */
						true);
			//Common.exit();
		}
	}

	private void sendSynchronize() throws RdesktopException, IOException,
	CryptoException, OmniDeskException {

		RdpPacket_Localised data = this.initData(4);

		data.setLittleEndian16(1); // type
		data.setLittleEndian16(1002);

		data.markEnd();

		this.sendData(data, RDP_DATA_PDU_SYNCHRONISE);
	}

	private void sendControl(int action) throws RdesktopException, IOException,
	CryptoException, OmniDeskException {

		RdpPacket_Localised data = this.initData(8);

		data.setLittleEndian16(action);
		data.setLittleEndian16(0); // userid
		data.setLittleEndian32(0); // control id

		data.markEnd();

		this.sendData(data, RDP_DATA_PDU_CONTROL);
	}

	private void sendConfirmActive() throws RdesktopException, IOException,
	CryptoException, OmniDeskException {

		int caplen = RDP_CAPLEN_GENERAL + RDP_CAPLEN_BITMAP + RDP_CAPLEN_ORDER
		+ RDP_CAPLEN_BMPCACHE + RDP_CAPLEN_COLCACHE
		+ RDP_CAPLEN_ACTIVATE + RDP_CAPLEN_CONTROL + RDP_CAPLEN_POINTER
		+ RDP_CAPLEN_SHARE + RDP_CAPLEN_UNKNOWN + 4 + RDP_CAPLEN_VIRTUAL; // this is a fix
		// for W2k.
		// Purpose
		// unknown

		int sec_flags = Options.encryption ? (RDP5_FLAG | Secure.SEC_ENCRYPT)
				: RDP5_FLAG;

		RdpPacket_Localised data = SecureLayer.init(sec_flags, 6 + 14 + caplen
				+ RDP_SOURCE.length);

		data.setLittleEndian16(2 + 14 + caplen + RDP_SOURCE.length);// length

		data.setLittleEndian16((RDP_PDU_CONFIRM_ACTIVE | 0x10));// type
		data.setLittleEndian16(Common.mcs.getUserID() /* McsUserID() */+ 1001);// channel
		// id
		// described on pg148 BCGR

		data.setLittleEndian32(this.rdp_shareid);// this share id was sent by
		// the server in
		// demand active PDU which was stored while in method
		// processDemandActive
		data.setLittleEndian16(0x3ea); // user id or originator id which
		// is always set to 0x03ea described on pg77 BCGR- Neer
		data.setLittleEndian16(RDP_SOURCE.length);
		data.setLittleEndian16(caplen);
		// everything done here is explained on pg 77 of BCGR -NEER
		data.copyFromByteArray(RDP_SOURCE, 0, data.getPosition(),
				RDP_SOURCE.length);
		data.incrementPosition(RDP_SOURCE.length);
		data.setLittleEndian16(0xd); // num_caps
		data.incrementPosition(2); // pad

		// whatever follows this line are the capability sets denoted by the
		// structure
		// TS_CAPS_SET described on pg 73(BCGR) each of the individual sets are
		// described
		// from pg 118 onwards

		this.sendGeneralCaps(data);
		// ta.incrementPosition(this.RDP_CAPLEN_GENERAL);
		this.sendBitmapCaps(data);// this capability is ADVERTISED both by
		// client/server
		this.sendOrderCaps(data);// order capability set sent by both
		// client-server

		if (Options.use_rdp5 && Options.persistent_bitmap_caching) {

			this.sendBitmapcache2Caps(data);
		} else
			this.sendBitmapcacheCaps(data);// this is strange -Neer
		// if Persistent=true then version 2 else version1
		// FOUND NO details regarding this -NEER

		// STRANGE: This order of building the capability set isn't according to
		// the order
		// described in BCGR Pg 73
		this.sendColorcacheCaps(data);// MS_RDPEGDI dependent
		this.sendActivateCaps(data);// pg 140-141
		this.sendControlCaps(data);// pg140
		this.sendPointerCaps(data);// pg131
		this.sendShareCaps(data);// pg 141
		// this.sendUnknownCaps(data);

		// unknown?? WTF?? This is the Input Capability set described on pg 132
		// BCGR
		this.sendUnknownCaps(data, 0x0d, 0x58, caps_0x0d);
		// rdp_out_unknown_caps(s,
		// 0x0d, 0x58,
		// caps_0x0d); // international?

		// Sound capability set
		this.sendUnknownCaps(data, 0x0c, 0x08, caps_0x0c); // rdp_out_unknown_caps(s,
		// 0x0c, 0x08,
		// caps_0x0c);

		// font capability set
		this.sendUnknownCaps(data, 0x0e, 0x08, caps_0x0e); // rdp_out_unknown_caps(s,
		// 0x0e, 0x08,
		// caps_0x0e);

		// Glyph capability set
		this.sendUnknownCaps(data, 0x10, 0x34, caps_0x10); // rdp_out_unknown_caps(s,
		// 0x10, 0x34,
		// caps_0x10);
		// glyph cache?
		
	     //virtual channel capabiltity set
        this.sendUnknownCaps(data, 0x14, 0x0c, caps_0x14);
		
		

		data.markEnd();

		Common.secure.send(data, sec_flags);
	}

	/**
	 * Retrieve status of connection
	 * 
	 * @return True if connection to RDP session
	 */
	public boolean isConnected() {
		return this.connected;
	}

	/**
	 * Initialise a packet for sending data on the RDP layer
	 * 
	 * @param size
	 *            Size of RDP data
	 * @return Packet initialised for RDP
	 * @throws RdesktopException
	 * @throws OmniDeskException
	 */
	private RdpPacket_Localised initData(int size) throws RdesktopException,
	OmniDeskException {
		RdpPacket_Localised buffer = null;

		buffer = SecureLayer.init(
				Constants.encryption ? Secure.SEC_ENCRYPT : 0, size + 18);
		buffer.pushLayer(RdpPacket_Localised.RDP_HEADER, 18);
		// buffer.setHeader(RdpPacket_Localised.RDP_HEADER);
		// buffer.incrementPosition(18);
		// buffer.setStart(buffer.getPosition());
		return buffer;
	}

	private void sendBitmapCaps(RdpPacket_Localised data) {

		data.setLittleEndian16(RDP_CAPSET_BITMAP);
		data.setLittleEndian16(RDP_CAPLEN_BITMAP);

		data.setLittleEndian16(Options.server_bpp); /* Preferred BPP */
		data.setLittleEndian16(1); /* Receive 1 BPP */
		data.setLittleEndian16(1); /* Receive 4 BPP */
		data.setLittleEndian16(1); /* Receive 8 BPP */
		data.setLittleEndian16(Options.width); /* Desktop width =800 */
		data.setLittleEndian16(Options.height); /* Desktop height=600 */
		data.setLittleEndian16(0); /* Pad */
		data.setLittleEndian16(1); /* Allow resize */
		data.setLittleEndian16(Options.bitmap_compression ? 1 : 0); /*
		 * Support
		 * compression
		 */
		data.setLittleEndian16(0); /* Unknown high color flags Pg 123 BCGR-Neer */
		data.setLittleEndian16(1); /*
		 * Unknown multipleRectangleSupport
		 * flag=should be true
		 */
		data.setLittleEndian16(0); /* Pad */
	}

	private void sendGeneralCaps(RdpPacket_Localised data) {

		data.setLittleEndian16(RDP_CAPSET_GENERAL);
		data.setLittleEndian16(RDP_CAPLEN_GENERAL);

		data.setLittleEndian16(1); /* OS major type= Windows */
		// We are FOOLING the remote server!!!
		data.setLittleEndian16(3); /* OS minor type = Windows NT */
		data.setLittleEndian16(0x200); /* Protocol version */
		data.setLittleEndian16(Options.use_rdp5 ? 0x40d : 0);
		// data.setLittleEndian16(Options.use_rdp5 ? 0x1d04 : 0); // this seems
		/*
		 * Pad, according to T.128. 0x40d seems to trigger the server to start
		 * sending RDP5 packets. However, the value is 0x1d04 with W2KTSK and
		 * NT4MS. Hmm.. Anyway, thankyou, Microsoft, for sending such
		 * information in a padding field..
		 */

		// the above comment is important.. may have serious implications =Neer
		data.setLittleEndian16(0); /* Compression types */
		data.setLittleEndian16(0); /* Pad */// This is the extra flag field:
		// Used only by the server, 0 for client
		data.setLittleEndian16(0); /* Update capability */
		data.setLittleEndian16(0); /* Remote unshare capability */
		data.setLittleEndian16(0); /* Compression level */
		data.setLittleEndian16(0); /* Pad */// refreshRectSupport
		// +suppressOutputSupport:
		// A server only flag Not used by client
	}

	private void sendOrderCaps(RdpPacket_Localised data) {
		// this requires a detail study of [MS-RDPEGDI]
		byte[] order_caps = new byte[32];
		order_caps[0] = 0;// 1; /* dest blt */
		order_caps[1] = 0; /* pat blt */// nb no rectangle orders if this is 0
		order_caps[2] = 0;// 1; /* screen blt */
		order_caps[3] = 0;// (byte) (Options.bitmap_caching ? 1 : 0); /* memblt
		// */
		order_caps[4] = 0; /* triblt */
		order_caps[8] = 0; /* line */
		order_caps[9] = 0; /* line */
		order_caps[10] = 0; /* rect */
		order_caps[11] = 0;// (Constants.desktop_save ? 1 : 0); /* desksave */
		order_caps[13] = 0;// 1; /* memblt */
		order_caps[14] = 0;// 1; /* triblt */
		order_caps[20] = 0;// (byte) (Options.polygon_ellipse_orders ? 1 : 0);
		// /* polygon */
		order_caps[21] = 0;// (byte) (Options.polygon_ellipse_orders ? 1 : 0);
		// /* polygon2 */
		order_caps[22] = 0;// 1; /* polyline */
		order_caps[25] = 0;// (byte) (Options.polygon_ellipse_orders ? 1 : 0);
		// /* ellipse */
		order_caps[26] = 0;// (byte) (Options.polygon_ellipse_orders ? 1 : 0);
		// /* ellipse2 */
		order_caps[27] = 0; /* text2 */
		data.setLittleEndian16(RDP_CAPSET_ORDER);
		data.setLittleEndian16(RDP_CAPLEN_ORDER);

		data.incrementPosition(20); /*
		 * Terminal desc, pad - this is
		 * ignored!!-Neer
		 */
		data.setLittleEndian16(1); /* Cache X granularity */
		data.setLittleEndian16(20); /* Cache Y granularity */
		data.setLittleEndian16(0); /* Pad */
		data.setLittleEndian16(1); /* Max order level */
		data.setLittleEndian16(0x147); /* Number of fonts */
		data.setLittleEndian16(0x2a); /* Capability flags */// the values are not
		// as per the pdf pg125 BCGR-Neer
		data.copyFromByteArray(order_caps, 0, data.getPosition(), 32); /*
		 * Orders
		 * supported
		 */
		data.incrementPosition(32);
		data.setLittleEndian16(0x6a1); /* Text capability flags */// this is
		// said to
		// be
		// ignored
		// but the value seems genuine - pg 127 BCGR - Neer
		data.incrementPosition(6); /* 2 bytes orderSupportExFlags 4 bytes Pad */
		data.setLittleEndian32(Constants.desktop_save ? 0x38400 : 0); /*
		 * desktopsavesize
		 * Desktop
		 * cache
		 * size=
		 * 230400
		 * mentioned
		 * on pg128
		 * BCGR
		 */
		data.setLittleEndian32(0); /* Unknown */
		data.setLittleEndian32(0x4e4); /* textANSICodePage +pad */
	}

	/* Output bitmap cache v2 capability set */
	private void sendBitmapcache2Caps(RdpPacket_Localised data) {
		data.setLittleEndian16(RDP_CAPSET_BMPCACHE2);// value 19 //
		// out_uint16_le(s,
		// RDP_CAPSET_BMPCACHE2);
		data.setLittleEndian16(RDP_CAPLEN_BMPCACHE2); // out_uint16_le(s,
		// RDP_CAPLEN_BMPCACHE2);
		// This length field may change in our implementation!!!- We better
		// choose those
		// "final" constants wisely by calculating
		// data.setLittleEndian16(Options.persistent_bitmap_caching ? 2 : 0);
		data.setLittleEndian16(2); /*
		 * /* version Indicates that the client
		 * supports a cache waiting list . If a
		 * waiting list is supported , new bitmaps
		 * are cached on the second hit rather than
		 * the first ( bitmaps must be sent twice
		 * before they are cached ) . Pg 130 BCGR
		 */

		data.setBigEndian16(3); /* number of caches in this set */
		// reason for BIG-endian: first 8 bits are flags which are ignored!!
		// The next 1 byte(8 bit) denote the no. of caches. Here they set max.
		// no. of caches
		// to 3. The fields following these are called the BitmapCacheCell-n
		// info
		// n has to be <=3. Here 2 such "infos are described"

		/* max cell size for cache 0 is 16x16, 1 = 32x32, 2 = 64x64, etc */
		data.setLittleEndian32(BMPCACHE2_C0_CELLS); // out_uint32_le(s,
		// BMPCACHE2_C0_CELLS);

		// the values here is the no. of entries in each cell
		// According to BCGR-Pg131 the max value for first 2 cells is
		// "600 entries"
		data.setLittleEndian32(BMPCACHE2_C1_CELLS); // out_uint32_le(s,
		// BMPCACHE2_C1_CELLS);

		// Why are the lines below commented?????
		// No. of cache cells was defined 3. Only 2 cells are used

		// data.setLittleEndian32(PstCache.pstcache_init(2) ?
		// (BMPCACHE2_NUM_PSTCELLS | BMPCACHE2_FLAG_PERSIST) :
		// BMPCACHE2_C2_CELLS);

		// I see : either the cell-2 or a persistent cache is used
		if (PstCache.pstcache_init(2)) {
			Log.v("Rdp.java", "Persistent cache initialized");
			data.setLittleEndian32(BMPCACHE2_NUM_PSTCELLS
					| BMPCACHE2_FLAG_PERSIST);
		} else {
			Log.v("Rdp.java", "Persistent cache not initialized");
			data.setLittleEndian32(BMPCACHE2_C2_CELLS);
		}
		data.incrementPosition(20); // Only 3 cache cells are used
		// rest of them cell4 (4 bytes) cell5 (4 bytes) and Pad(12 bytes are set
		// to 0)
		// 4+4+12 = 20 ...NEER
		// out_uint8s(s, 20); /* other bitmap caches
		// not used */
	}

	/*
	 * Output bitmap cache v1 capability set the 2 versions are sent only from
	 * client to server
	 */
	private void sendBitmapcacheCaps(RdpPacket_Localised data) {

		data.setLittleEndian16(RDP_CAPSET_BMPCACHE);// the type of capability
		// sets
		data.setLittleEndian16(RDP_CAPLEN_BMPCACHE);// the length ... these all
		// our constants

		// All the description on pg129 BCGR
		data.incrementPosition(24); /* unused */// six 4-bytes of padding.. not
		// used
		data.setLittleEndian16(0x258); /* entries */
		data.setLittleEndian16(0x100); /* max cell size */
		data.setLittleEndian16(0x12c); /* entries */
		data.setLittleEndian16(0x400); /* max cell size */
		data.setLittleEndian16(0x106); /* entries */
		data.setLittleEndian16(0x1000); /* max cell size */
	}

	private void sendColorcacheCaps(RdpPacket_Localised data) {

		data.setLittleEndian16(RDP_CAPSET_COLCACHE);
		data.setLittleEndian16(RDP_CAPLEN_COLCACHE);

		data.setLittleEndian16(6); /* cache size */
		data.setLittleEndian16(0); /* pad */
	}

	private void sendActivateCaps(RdpPacket_Localised data) {

		data.setLittleEndian16(RDP_CAPSET_ACTIVATE);
		data.setLittleEndian16(RDP_CAPLEN_ACTIVATE);

		data.setLittleEndian16(0); /* Help key */
		data.setLittleEndian16(0); /* Help index key */
		data.setLittleEndian16(0); /* Extended help key */
		data.setLittleEndian16(0); /* Window activate */
		// All flags are FALSE!!! What's that about????????? -Neer
	}

	private void sendControlCaps(RdpPacket_Localised data) {

		data.setLittleEndian16(RDP_CAPSET_CONTROL);
		data.setLittleEndian16(RDP_CAPLEN_CONTROL);

		data.setLittleEndian16(0); /* Control capabilities */
		data.setLittleEndian16(0); /* Remote detach */
		data.setLittleEndian16(2); /* Control interest */
		data.setLittleEndian16(2); /* Detach interest */
	}

	private void sendPointerCaps(RdpPacket_Localised data) {

		data.setLittleEndian16(RDP_CAPSET_POINTER);
		data.setLittleEndian16(RDP_CAPLEN_POINTER);

		data.setLittleEndian16(0); /*
		 * Colour pointer flag = 0 i.e monochrome
		 * mouse cursor
		 */
		data.setLittleEndian16(20);// data.setLittleEndian16(20);
		/*
		 * Cache sizeThe number of available slots in the 24 bpp color pointer
		 * cache used to store data received in the Color Pointer Update
		 */
		// data.setLittleEndian16(0);//Pointer cache size
		// there is one more OPTIONAL field called the PointerCacheSize. This
		// field is not included
		// Therefore as per give in BCGR Pg132-
		// "If the value contained in this field is zero or
		// the Pointer Capability Set sent from the client
		// does not include this field, the server will not
		// use the New Pointer Update. "
	}

	private void sendShareCaps(RdpPacket_Localised data) {
		// The TS_SHARE_CAPABILITYSET structure is used to advertise
		// the channel ID of the sender and is
		// fully specified in [T128] section 8.2.12.
		// This capability is sent by both client and server.
		data.setLittleEndian16(RDP_CAPSET_SHARE);
		data.setLittleEndian16(RDP_CAPLEN_SHARE);

		data.setLittleEndian16(0); /*
		 * userid node id: A 16-bit, unsigned
		 * integer. This field SHOULD be set to 0 by
		 * the client //and to the server channel ID
		 * by the server //(in Microsoft RDP server
		 * implementations, this value is always
		 * 0x03EA).
		 */
		data.setLittleEndian16(0); /* pad */
	}

	private void sendUnknownCaps(RdpPacket_Localised data, int id, int length,
			byte[] caps) {

		data.setLittleEndian16(id /* RDP_CAPSET_UNKNOWN */);
		data.setLittleEndian16(length /* 0x58 */);

		data.copyFromByteArray(caps, 0, data.getPosition(), /* RDP_CAPLEN_UNKNOWN */
				length - 4);
		data.incrementPosition(/* RDP_CAPLEN_UNKNOWN */length - 4);
	}

	/**
	 * Process a data PDU received from the server
	 * 
	 * @param data
	 *            Packet containing data PDU at current read position
	 * @param ext_disc_reason
	 *            If a disconnect PDU is received, stores disconnection reason
	 *            at ext_disc_reason[0]
	 * @return True if disconnect PDU was received
	 * @throws OmniDeskException
	 * @throws OrderException
	 * @throws RdesktopException
	 */
	private boolean processData(RdpPacket_Localised data, int[] ext_disc_reason)
	throws OmniDeskException, OrderException, RdesktopException {

		int data_type, ctype, clen, len, roff, rlen;
		data_type = 0;

		data.incrementPosition(6); // skip shareid, pad, streamid
		len = data.getLittleEndian16();
		data_type = data.get8();
		ctype = data.get8(); // compression type
		clen = data.getLittleEndian16(); // compression length
		clen -= 18;

		switch (data_type) {

		case (Rdp.RDP_DATA_PDU_UPDATE):

			this.processUpdate(data);
		break;

		case RDP_DATA_PDU_CONTROL:

			break;

		case RDP_DATA_PDU_SYNCHRONISE:

			break;

		case (Rdp.RDP_DATA_PDU_POINTER):

			this.processPointer(data);
		break;
		case (Rdp.RDP_DATA_PDU_BELL):

			break;
		case (Rdp.RDP_DATA_PDU_LOGON):

			ConnectionManager.loggedon = true;
		break;
		case RDP_DATA_PDU_DISCONNECT:
			/*
			 * Normally received when user logs out or disconnects from a
			 * console session on Windows XP and 2003 Server
			 */
			ext_disc_reason[0] = processDisconnectPdu(data);
			System.out.println("reason : " + ext_disc_reason[0]);
			if (ext_disc_reason[0] == 0)
				return false;
			// /logger.debug(("Received disconnect PDU\n"));
			
			return true;

		default:
			// logger.warn("Unimplemented Data PDU type " + data_type);

		}
		return false;
	}

	private void processUpdate(RdpPacket_Localised data) throws OrderException,
	OmniDeskException, RdesktopException {

		Common.connectionProgress.dismiss();
		int update_type = 0;

		update_type = data.getLittleEndian16();

		switch (update_type) {

		case (Rdp.RDP_UPDATE_ORDERS):

			data.incrementPosition(2); // pad
		int n_orders = data.getLittleEndian16();
		data.incrementPosition(2); // pad
		this.orders.processOrders(data, next_packet, n_orders);
		break;
		case (Rdp.RDP_UPDATE_BITMAP):
			this.processBitmapUpdates(data);
		break;
		case (Rdp.RDP_UPDATE_PALETTE):
			this.processPalette(data);
		break;
		case (Rdp.RDP_UPDATE_SYNCHRONIZE):
			break;
		default:
			// logger.warn("Unimplemented Update type " + update_type);
		}
	}

	private void processPointer(RdpPacket_Localised data)
	throws OmniDeskException {
		int message_type = 0;
		int x = 0, y = 0;

		message_type = data.getLittleEndian16();
		data.incrementPosition(2);// pad... I am on pg 185 BCGR Neer
		switch (message_type) {

		case (Rdp.RDP_POINTER_MOVE):

			x = data.getLittleEndian16();
		y = data.getLittleEndian16();

		if (data.getPosition() <= data.getEnd()) {
		}// this case handles the movement of mouse cursor to a new position
		break;

		case (Rdp.RDP_POINTER_COLOR):

			process_colour_pointer_pdu(data);// drawing of the mouse pointer
		break;

		case (Rdp.RDP_POINTER_CACHED):

			process_cached_pointer_pdu(data);// just acceses the pointer cache
		break;

		case RDP_POINTER_SYSTEM:

			process_system_pointer_pdu(data);// hiddden or OS default
			break;

			// the case on new Pointer update is not handled
			// this update attempts to change the basic SHAPE of the cursor
		default:
			break;
		}
	}

	protected void processBitmapUpdates(RdpPacket_Localised data)
	throws OmniDeskException, RdesktopException {

		int n_updates = 0;
		int left = 0, top = 0, right = 0, bottom = 0, width = 0, height = 0;
		int cx = 0, cy = 0, bitsperpixel = 0, compression = 0, buffersize = 0, size = 0;
		byte[] pixel = null;

		int minX, minY, maxX, maxY;

		maxX = maxY = 0;
		minX = // surface.getWidth();//width of the android screen...mayur
			minY = // surface.getHeight();//height of the android screen...mayur

				n_updates = data.getLittleEndian16();// number of rectangles...mayur
		System.out.println("Number of rectangles: " + n_updates);
		if (n_updates > 1)
			Common.connectionProgress.dismiss();
		for (int i = 0; i < n_updates; i++) {

			left = data.getLittleEndian16();
			top = data.getLittleEndian16();
			right = data.getLittleEndian16();
			bottom = data.getLittleEndian16();
			width = data.getLittleEndian16();
			height = data.getLittleEndian16();
			bitsperpixel = data.getLittleEndian16();
			int Bpp = (bitsperpixel + 7) / 8;
			compression = data.getLittleEndian16(); // Flags
			buffersize = data.getLittleEndian16();// bitmap length... given on
			// [MS-RDPBCGR] pg 175/388

			cx = right - left + 1;// the actual comp screen
			cy = bottom - top + 1;// the actual comp screen

			if (minX > left)
				minX = left;
			if (minY > top)
				minY = top;
			if (maxX < right)
				maxX = right;
			if (maxY < bottom)
				maxY = bottom;

			/* Server may limit bpp - this is how we find out */
			if (Options.server_bpp != bitsperpixel) {
				// logger.warn("Server limited colour depth to " + bitsperpixel
				// + " bits");
				Options.set_bpp(bitsperpixel);
			}

			if (compression == 0) {
				// logger.info("compression == 0");

				pixel = new byte[width * height * Bpp];

				for (int y = 0; y < height; y++) {
					data.copyToByteArray(pixel, (height - y - 1)// The bitmap
							// data stream
							// is
							* (width * Bpp), data.getPosition(), width * Bpp);// bottom-up
					// left-right
					data.incrementPosition(width * Bpp);// sequence of pixel
					// info
				}// so this for loop starts to fill "pixel" array from bottom up

				surface.displayImage(Bitmap.convertImage(pixel, Bpp), width,
						height, left, top, cx, cy);

				continue;
			}

			if ((compression & 0x400) != 0) {
				// logger.info("compression & 0x400 != 0");
				size = buffersize;
			} else {
			
				data.incrementPosition(2); // pad
				size = data.getLittleEndian16();// The size in bytes of the
				// compressed bitmap data (which follows this header).
				// pg 177 MS RDPBCGR

				data.incrementPosition(4); // line size, final size

			}
			if (Bpp == 1) {
				pixel = Bitmap.decompress(width, height, size, data, Bpp);
				if (pixel != null) {
					surface.displayImage(Bitmap.convertImage(pixel, Bpp),
							width, height, left, top, cx, cy);

				}

				else {
				}

			} else {

				if (Options.bitmap_decompression_store == Options.INTEGER_BITMAP_DECOMPRESSION) {

					int[] pixeli = Bitmap.decompressInt(width, height, size,
							data, Bpp);
					if (pixeli != null)
						surface.displayImage(pixeli, width, height, left, top,
								cx, cy);
					else {
					}
					// logger.warn("Could not decompress bitmap");
				} else if (Options.bitmap_decompression_store == Options.BUFFEREDIMAGE_BITMAP_DECOMPRESSION) {

					// Image pix = Bitmap.decompressImg(width, height, size,
					// data,
					// Bpp, null);
					// neer

					android.graphics.Bitmap pix = Bitmap.decompressImg(width,
							height, size, data, Bpp);
					if (pix != null) {
						// surface.displayImage(pix, left, top); neer
						Paint paint = new Paint();

						// surface.drawBitmap(pix, left, top, paint);
					} else {
					}
					// logger.warn("Could not decompress bitmap");
				} else {
					surface.displayCompressed(left, top, width, height, size,
							data, Bpp);
				}
			}
		}
	    Common.currentImageViewer.postInvalidate();
	}

	protected void processPalette(RdpPacket_Localised data) {

	}

	protected void process_colour_pointer_pdu(RdpPacket_Localised data)
	throws OmniDeskException {
	}

	protected void process_cached_pointer_pdu(RdpPacket_Localised data)
	throws OmniDeskException {

		int cache_idx = data.getLittleEndian16();

	}

	private void process_system_pointer_pdu(RdpPacket_Localised data) {
		int system_pointer_type = 0;

		data.getLittleEndian16(system_pointer_type); // in_uint16(s,
		// system_pointer_type);
		switch (system_pointer_type) {
		case RDP_NULL_POINTER:
		break;
		default:
		}
	}

	public void registerDrawingSurface() {
		surface = new OmniDeskCanvas(Options.width, Options.height);
	}

	public void registerKeyboard() {
		inputHandler = new Input(this, Common.keyMapFileName);
		Common.inputHandler = inputHandler;

	}
}
