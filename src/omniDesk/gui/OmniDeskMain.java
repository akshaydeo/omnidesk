/**
 * OmniDesk: Your Desktop Everywhere...
 * @omniDeksMain: To set up environment to run the application 
 * @author: vg ]
 */
package omniDesk.gui;

import java.io.IOException;

import omniDesk.net.rdp.Common;
import omniDesk.net.rdp.ConnectionManager;
import omniDesk.net.rdp.ImageViewer;
import omniDesk.net.rdp.OmniDeskException;
import omniDesk.net.rdp.Options;

import omniDesk.net.rdp.Rdp;
import omniDesk.net.rdp.SpecialKeyDialog;
import omniDesk.net.rdp.WrappedImage;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class OmniDeskMain extends Activity implements
android.view.GestureDetector.OnGestureListener,
android.view.GestureDetector.OnDoubleTapListener {

	String[] status = { "RDP5 Channel created", "Connecting to remote host",
			"Connected to RDP Server", "Drawing Desktop" };
	public ConnectionManager connection;
	Thread connectionManagerThread;
	GestureDetector gestureDetector;
	int REQUESTCODE = 1;
	private boolean Long_Press = false;
	private boolean MouseDragged = false;

	private int tempx, tempy;
	private Button zoom;
	private int dragx = 0, dragy = 0;
	
	private int lastTapX=0,lastTapY=0;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		Common.acitvity_context = this;
		Common.main_activity = this;
		makeFullScreen();
		setContentView(R.layout.main);

	
		//  Count-down timer for demo version
		 
		 		
		new CountDownTimer(120000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				//Toast.makeText(OmniDeskMain.this, "time left : " + (millisUntilFinished/1000),1).show();
			}

			@Override
			public void onFinish() {
				Toast.makeText(OmniDeskMain.this, "demo version...!!!",1).show();
				Common.ConnectionStatus=false;
				finish();

			}
		}.start();
		
		
		zoom = (Button) findViewById(R.id.Button01);
		zoom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Common.Fullscreen) {
					Common.Fullscreen = false;
					zoom.setBackgroundResource(R.drawable.fullscreen_unpressed);
					int newX=0,newY=0;
					
					if(!Common.TouchMode)
					{
						newX = (int)(Common.mouseX - Common.canvasWidth / 2);
						newY = (int)(Common.mouseY - Common.canvasHeight / 2);
					}
					else
					{
						newX = (int)(lastTapX - Common.canvasWidth / 2) ;
						newY = (int)(lastTapY - Common.canvasHeight / 2) ;
					}
					
					
					if (newX >= 0) {
						Common.x = newX;
						if (Common.bitmapWidth - Common.x < Common.canvasWidth)
							Common.x = Common.bitmapWidth - Common.canvasWidth;
					}

					else
						Common.x = 0;

					
					if (newY >= 0) {
						Common.y = newY;
						if (Common.bitmapHeight - Common.y < Common.canvasHeight)
							Common.y = Common.bitmapHeight
							- Common.canvasHeight;
					} else
						Common.y = 0;

					if (Common.bitmapHeight - Common.y < Common.canvasHeight)
						Common.y = Common.bitmapHeight - Common.canvasHeight;

				} else {
					Common.Fullscreen = true;
					zoom.setBackgroundResource(R.drawable.fullscreen_pressed);
				}
				Common.currentImageViewer.postInvalidate();

			}
		});

		Common.currentImageViewer = (ImageViewer) findViewById(R.id.android_canvas);

		gestureDetector = new GestureDetector(this);

		Common.connectionProgress = new ProgressDialog(Common.acitvity_context);// .show(this,"OmniDesk: Your desktop everywhere..","Please wait while connecting");
		Common.connectionProgress.setProgressStyle(1);
		Common.connectionProgress.setTitle("OmniDesk: Your desktop everywhere..");
		Common.connectionProgress.setMessage("setting environment...");
		Common.connectionProgress.setProgress(20);
		Common.connectionProgress.show();

		setUpEnvironment();

		
		/* 
		 *   Timer to check Connection time out
		 */
		new CountDownTimer(20000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				if (!Common.connected) {
					Toast.makeText(Common.acitvity_context,
							"failed to connect to the remote host...!!!", 1)
							.show();
					// connectionManagerThread.stop();
					finish();
				}

			}
		}.start();

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				connection = new ConnectionManager();
				connection.connect();

			}
		});

		t.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		String text;
		if (Common.TouchMode)
			text = "Mouse Mode";
		else
			text = "Touch Mode";

		menu.add(1, 1, 0, text);
		menu.add(1, 2, 0, "Disconnect");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		String text;
		if (Common.TouchMode)
			text = "Mouse Mode";
		else
			text = "Touch Mode";
		menu.findItem(1).setTitle(text);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case 1:

			if (Common.TouchMode) {
				Toast.makeText(Common.acitvity_context, "toggled to mouse..",
						Toast.LENGTH_SHORT).show();
				Common.TouchMode = false;
				Common.mouseX = Common.x + (Common.canvasWidth / 2);
				Common.mouseY = Common.y + (Common.canvasHeight / 2);
				Common.inputHandler.mouseReset(Common.mouseX, Common.mouseY);

			} else {
				Toast.makeText(Common.acitvity_context, "toggled to touch..",
						Toast.LENGTH_SHORT).show();
				Common.TouchMode = true;
				Common.mouseX = 0;
				Common.mouseY = 0;
				Common.inputHandler.mouseReset(Common.mouseX, Common.mouseY);
			}
			return true;

		case 2:
			Toast.makeText(this, "disconnected", 1);
			Common.ConnectionStatus = false;
			finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.v(" oncreatedialog", "in oncreatedialog");

		return new SpecialKeyDialog(this);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			Common.ConnectionStatus=false;
			return true;
		}
		
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			showDialog(R.layout.special);
			return true;
		}
		Log.d("mainactivity/keydown", "keydown event called");
		System.out
		.println("The key code pressed is " + event.getDisplayLabel());
		System.out.println("The Key code is : " + keyCode);
		System.out.println("The scan code is : " + event.getScanCode());

		try {
			Common.inputHandler.keyPressed(event);
		} catch (OmniDeskException e) {
			Log.d("mainActivity/onkeydown",
			"Exception in keydown handling event");
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return super.onKeyMultiple(keyCode, repeatCount, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d("main activity", "case of key up");
		try {
			Common.inputHandler.keyReleased(event);
		} catch (OmniDeskException e) {
			Log.d("mainActivity/onkeydown",
			"Exception in keydown handling event");
		}

		return super.onKeyUp(keyCode, event);
	}

	private void setUpEnvironment() {
		
	Options.rdp5_performanceflags= Rdp.RDP5_NO_CURSOR_SHADOW | Rdp.RDP5_NO_CURSORSETTINGS |
    Rdp.RDP5_NO_FULLWINDOWDRAG | Rdp.RDP5_NO_MENUANIMATIONS  ;
	
	if(Options.disable_theming)
		Options.rdp5_performanceflags  = Options.rdp5_performanceflags |  Rdp.RDP5_NO_THEMING ;
	if(Options.disable_wallpaper)
		Options.rdp5_performanceflags = Options.rdp5_performanceflags | Rdp.RDP5_NO_WALLPAPER;;
	
		
		Common.BITMAP_READY_TO_RENDER = false;
		connection = new ConnectionManager();
		WrappedImage.bi = Bitmap.createBitmap(Options.width, Options.height,
				android.graphics.Bitmap.Config.RGB_565);
		Common.canvas = new Canvas(WrappedImage.bi);

		Common.progressHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.arg1 == 4)
					zoom.setVisibility(0);
				Common.connectionProgress.setMessage(status[msg.arg1 - 1]);
				Common.connectionProgress.incrementProgressBy(20);
				super.handleMessage(msg);
			}

		};

		try {

			Common.in = this.getResources().getAssets().open("keymap_engb");
			Common.in2 = this.getResources().getAssets().open("keymap_engb");
			if (Common.in == null) {
				System.out.println("The input Stream is  null");

			} else
				System.out.println("The input Stream is not null");
		} catch (IOException e) {
			Log.d("main activity", "Could not open file");
			e.printStackTrace();
		}
	}

	private void makeFullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float distanceX,
			float distanceY) {

		return false;
	}

	@Override
	public void onLongPress(MotionEvent event) {

		Long_Press = true;
		dragx = dragy = 0;

		try {
			Common.inputHandler.mousePressed(event);
			tempx = (int) event.getX();
			tempy = (int) event.getY();
		} catch (OmniDeskException e1) {
			e1.printStackTrace();

		}
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1,
			float distanceX, float distanceY) {

		/*********************************** only scaled-screen(touched) mode ***************************/

		if (Common.TouchMode && !Common.Fullscreen) {
			Log.d("onScroll", "event arg0 " + arg0.getAction());
			Log.d("onScroll", "event arg1 " + arg1.getAction());

			if ((Common.x + distanceX) > 0) // to check if exceeding left
				if (Common.bitmapWidth - (Common.x + distanceX) < Common.canvasWidth
						* Common.scale) // to check if exceeding right
					Common.x = Common.bitmapWidth - Common.canvasWidth
					* (int) Common.scale; // assure minimum screen width
				else
					Common.x += distanceX; // normal addition
			else
				Common.x = 0;
			if ((Common.y + distanceY) > 0)
				if (Common.bitmapHeight - (Common.y + distanceY) < Common.canvasHeight
						* Common.scale)
					Common.y = Common.bitmapHeight - Common.canvasHeight
					* (int) Common.scale;
				else
					Common.y += distanceY;
			else

				Common.y = 0;
			Common.currentImageViewer.invalidate();
		}

		else if (!Common.TouchMode) {
			/********************* mouse mode (full screen) *******************/
			/********************* called when mouse moved... *******************/

			try {
				Common.inputHandler.mouseMoved(arg0, (int) distanceX,
						(int) distanceY);
			} catch (OmniDeskException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch (event.getAction()) {

		case MotionEvent.ACTION_UP:

			if (Long_Press == true
					&& (dragx >= -5 && dragx <= 5 && dragy >= -5 && dragy <= 5)) {

				Common.inputHandler.mouseRightClickPress(event);
				Common.inputHandler.mouseRightClickRelease(event);
				Long_Press = false;
			} else if (MouseDragged) {
				try {
					Common.inputHandler.mouseReleased(event);
					MouseDragged = false;
					Long_Press = false;

				} catch (OmniDeskException e1) {
					e1.printStackTrace();
				}
			}

			break;

			/************************ called when mouse dragged **********/

		case MotionEvent.ACTION_MOVE:
			if (Long_Press) {
				int newX = (int) event.getX();
				int newY = (int) event.getY();
				try {
					MouseDragged = true;
					dragx += tempx - newX;
					dragy += tempy - newY;
					System.out.println("oving");
					Common.inputHandler.mouseDragged(event, tempx - newX, tempy
							- newY);
					tempx = newX;
					tempy = newY;
				} catch (OmniDeskException e) {
					e.printStackTrace();
				}
			}
		}

		return gestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		try {
			Common.inputHandler.mousePressed(e);
			Common.inputHandler.mouseReleased(e);

			Common.inputHandler.mousePressed(e);
			Common.inputHandler.mouseReleased(e);
		} catch (OmniDeskException e1) {
			e1.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		try {
			 lastTapX =(int) (e.getX() * Common.bitmapWidth / Common.canvasWidth);
			 lastTapY = (int) (e.getY() * Common.bitmapHeight / Common.canvasHeight);
			Common.inputHandler.mousePressed(e);
			Common.inputHandler.mouseReleased(e);
		} catch (OmniDeskException e1) {
			e1.printStackTrace();
		}

		return false;
	}

}
