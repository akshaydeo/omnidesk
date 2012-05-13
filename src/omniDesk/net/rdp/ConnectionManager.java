package omniDesk.net.rdp;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.Message;

import omniDesk.errorReporting.ErrorReport;
import omniDesk.net.rdp.keymapping.KeyCode_FileBased;
import omniDesk.net.rdp.keymapping.KeyMapException;
import omniDesk.net.rdp.rdp5.Rdp5;
import omniDesk.net.rdp.rdp5.VChannels;
import omniDesk.net.rdp.rdp5.rdpsnd.SoundChannel;

public class ConnectionManager{

	public static boolean readytosend;
	public static boolean loggedon;
	public void connect()
	{
		try {
			InetAddress rdpserver = InetAddress.getByName(Common.ipAddress);
			VChannels channels=new VChannels();
			Rdp5 RdpLayer = null;
			Common.rdp = RdpLayer;
			RdpLayer = new Rdp5(channels);
			Common.rdp = RdpLayer;
		
			Message msg=new Message();
			msg.arg1=1;
			Common.progressHandler.sendMessage(msg);
						
			RdpLayer.registerDrawingSurface();	
			
		
	
			KeyCode_FileBased keyMap=null;
			try {	
				keyMap=new KeyCode_FileBased_Localised(Common.keyMapFileName);
			} catch (KeyMapException e1) {
				ErrorReport.showErrorDialog(0, "Error in Mapping","Map file not found!!",Common.currentContext);
				e1.printStackTrace();
			}	
			Options.keylayout=keyMap.getMapCode();
			Common.in=Common.in2;
			RdpLayer.registerKeyboard();
			
			Message msg1=new Message();
			msg1.arg1=2;
			Common.progressHandler.sendMessage(msg1);
			
			try {
				Common.in2.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				ErrorReport.showErrorDialog(0, "Input stream Error","Inputstream closed after registering keyboard",Common.currentContext);
			}
			try {
	
				int logonflags = Rdp.RDP_LOGON_NORMAL;
				Options.port=3389;//Integer.parseInt(Common.port);
	
		  
				
				RdpLayer.connect(Options.username, rdpserver, logonflags, 
						Options.domain, Options.password, Options.command, Options.directory);

				
				boolean[] deactivated = new boolean[1];
				int[] ext_disc_reason = new int[1];
	
				
				
				RdpLayer.mainLoop(deactivated, ext_disc_reason);
	
				RdpLayer.disconnect();
				System.out.println("disconnected....!!!!!!!!!!!!!!!!!!!!!!!");
				
				Common.main_activity.finish();
			}catch(UnknownHostException e)
			{
				Common.connectionProgress.dismiss();
				ErrorReport.showErrorDialog(0, "Connection Error", "Unknown Host!!!", Common.currentContext);
			}
			catch(SocketException e)
			{
				Common.connectionProgress.dismiss();
				//ErrorReport.showErrorDialog(0, "Connection Error","Network unreachable..!!!", Common.currentContext);
			}
			catch (Exception e) {
			
			}			
		} catch (UnknownHostException e1) {

			ErrorReport.showErrorDialog(0, "Unknown host exception!!",
					"Remote computer is not reachable, ensure IP address and permissions", Common.currentContext);
		}
	}
	/**
	 * Displays details of the Exception e in an error dialog via the
	 * RdesktopFrame window and reports this through the logger, then prints a
	 * stack trace.
	 * <p>
	 * The application then exits iff sysexit == true
	 * 
	 * @param e
	 * @param RdpLayer
	 * @param window
	 * @param sysexit
	 */
	public static void error(Exception e, Rdp RdpLayer,boolean sysexit) {
		try {
			String msg1 = e.getClass().getName();
			String msg2 = e.getMessage();
			ErrorReport.showErrorDialog(0, msg1,msg2,Common.currentContext);
		} catch (Exception ex) {			
		}
	}
    
}