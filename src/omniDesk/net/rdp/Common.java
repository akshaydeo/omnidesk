

/**
 * 
 */
package omniDesk.net.rdp;

import java.io.InputStream;

import omniDesk.gui.OmniDeskMain;
import omniDesk.net.rdp.rdp5.Rdp5;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;

public class Common {
	public static  boolean Fullscreen = true;
	public static boolean TouchMode = true;
	public static boolean underApplet = false;
	public static Rdp5 rdp;
	public static Secure secure;
	public static MCS mcs;
	public static Boolean IS_STARTED=false;
	public static ImageViewer currentImageViewer;
	public static boolean BITMAP_READY_TO_RENDER;
	public static Rect dirtyRec;
	public static int x=0,y=0,scale=1,canvasWidth,canvasHeight;
	public static Handler uiHandler;
	public static int bitmapWidth;
	public static int bitmapHeight;
	public static String keyMapFileName= "\\system\\usr\\keychar\\qwerty.kcm";
	public static InputStream in=null,in2=null;
	public static Input inputHandler=null;
	public static String ipAddress;
	public static String Resolution;
	public static ProgressDialog connectionProgress;
	public static String Uname;
	public static String port;
	public static String password;
	public static boolean pan_mode=false;
	public static Canvas canvas;
	public static Context currentContext;
	public static int mouseX=0,mouseY=0;
	public static Thread ConnectionRequestResponseThread;
	public static boolean connected=false;
	public static OmniDeskMain acitvity_context;
	public static OmniDeskMain main_activity;
	public static Handler progressHandler;
	public static boolean ConnectionStatus;}
