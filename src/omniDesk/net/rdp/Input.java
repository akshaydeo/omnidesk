package omniDesk.net.rdp;

import omniDesk.net.rdp.keymapping.KeyCode;
import omniDesk.net.rdp.keymapping.KeyCode_FileBased;
import omniDesk.net.rdp.keymapping.KeyMapException;

import java.util.Vector;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class Input {

	KeyCode_FileBased newKeyMapper = null;

	protected Vector<Integer> pressedKeys;

	protected static boolean capsLockOn = false;
	protected static boolean numLockOn = false;
	protected static boolean scrollLockOn = false;

	protected static boolean serverAltDown = false;
	protected static boolean altDown = false;
	protected static boolean ctrlDown = false;

	protected static long last_mousemove = 0;

	// Using this flag value (0x0001) seems to do nothing, and after running
	// through other possible values, the RIGHT flag does not appear to be
	// implemented
	protected static final int KBD_FLAG_RIGHT = 0x0001;
	protected static final int KBD_FLAG_EXT = 0x0100;

	// QUIET flag is actually as below (not 0x1000 as in rdesktop)
	protected static final int KBD_FLAG_QUIET = 0x200;
	protected static final int KBD_FLAG_DOWN = 0x4000;
	protected static final int KBD_FLAG_UP = 0x8000;

	protected static final int RDP_KEYPRESS = 0;
	protected static final int RDP_KEYRELEASE = KBD_FLAG_DOWN | KBD_FLAG_UP;
	protected static final int MOUSE_FLAG_MOVE = 0x0800;

	protected static final int SCANCODE_ALT = 0x38;
	protected static final int SCANCODE_CTRL = 0x1d;
	protected static final int SCANCODE_SHIFT = 0x2a;

	protected static final int MOUSE_FLAG_BUTTON1 = 0x1000;
	protected static final int MOUSE_FLAG_BUTTON2 = 0x2000;
	protected static final int MOUSE_FLAG_BUTTON3 = 0x4000;

	protected static final int MOUSE_FLAG_BUTTON4 = 0x0280; // wheel up -
	// rdesktop 1.2.0
	protected static final int MOUSE_FLAG_BUTTON5 = 0x0380; // wheel down -
	// rdesktop 1.2.0
	protected static final int MOUSE_FLAG_DOWN = 0x8000;

	protected static final int RDP_INPUT_SYNCHRONIZE = 0;
	protected static final int RDP_INPUT_CODEPOINT = 1;
	protected static final int RDP_INPUT_VIRTKEY = 2;
	protected static final int RDP_INPUT_SCANCODE = 4;
	protected static final int RDP_INPUT_UNICODE = 5;
	protected static final int RDP_INPUT_MOUSE = 0x8001;

	protected static int time = 0;

	public KeyEvent lastKeyEvent = null;
	public boolean modifiersValid = false;
	public boolean keyDownWindows = false;

	protected Rdp rdp = null;
	KeyCode keys = null;

	public Input(Rdp r, String keymapFile) {
		Log.d("input.java", "Constructor");
		try {
			newKeyMapper = new KeyCode_FileBased_Localised(Common.in);
		} catch (KeyMapException kmEx) {
			Log.d("input.java", "new keymapper could not be instantiated");
			System.err.println(kmEx.getMessage());

		}

		rdp = r;

		pressedKeys = new Vector<Integer>();
	}

	public void keyPressed(KeyEvent e) throws OmniDeskException {

		/* to ckeck for PAN mode... */

		if (e.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
			return;
		}

		int var;
		lastKeyEvent = e;
		/*
		 * if(e.isShiftPressed()) System.out.println("Shift button pressed");
		 */

		if (e.isPrintingKey() || e.isAltPressed() || e.isShiftPressed()) {// e.isAltPressed())
			// {
			System.out.println("sending the unicode character");
			sendUnicode(e);
			return;
		}

		System.out.println("The symbol of the key is: " + (char) e.getNumber());
		modifiersValid = true;
		long time = getTime();
		Log.i("input.java", "Key pressed");
		System.out.println("The key code is " + e.getKeyCode());
		// Some java versions have keys that don't generate keyPresses -
		// here we add the key so we can later check if it happened
		pressedKeys.addElement(new Integer(e.getKeyCode()));

		Log.i("input.java", "PRESSED keychar='" + (char) e.getUnicodeChar()
				+ "' keycode=0x" + Integer.toHexString(e.getKeyCode())
				+ " char='" + ((char) e.getKeyCode()) + "'");

		if (rdp != null) {
			if (!handleSpecialKeys(time, e, true)) {
				if (newKeyMapper == null) {
					Log.i("input.java", "keyPressed... newKeyMapper is null");
				} else {
					if ((var = newKeyMapper.getKeyStrokes(e)) == -1) {
						System.out.println("No scan code found");
						return;
					}
					sendKeyPresses(var);
				}
			}
		} else {
			Log.i("input/keypressed", "The RDP layer hasn't initialised yet!!");
		}

	}

	/**
	 * Handle a keyTyped event, sending any relevant keypresses to the server
	 * 
	 * @throws OmniDeskException
	 */
	public void keyTyped(KeyEvent e) throws OmniDeskException {
		lastKeyEvent = e;
		modifiersValid = true;
		long time = getTime();
		Log.d("input.java/keytyped", "typed");
		// Some java versions have keys that don't generate keyPresses -
		// here we add the key so we can later check if it happened
		pressedKeys.addElement(new Integer(e.getKeyCode()));

		Log.d("input.java", "TYPED keychar='" + e.getUnicodeChar()
				+ "' keycode=0x" + Integer.toHexString(e.getKeyCode())
				+ " char='" + ((char) e.getKeyCode()) + "'");

		if (rdp != null) {
			if (!handleSpecialKeys(time, e, true))
				sendKeyPresses(newKeyMapper.getKeyStrokes(e));
		} else {
			Log.d("input/keytyped", "The RDP layer hasn't initialised yet!!");
		}
	}

	/**
	 * Handle a keyReleased event, sending any relevent key events to the server
	 * 
	 * @throws OmniDeskException
	 */
	public void keyReleased(KeyEvent e) throws OmniDeskException {
		// Some java versions have keys that don't generate keyPresses -
		// we added the key to the vector in keyPressed so here we check for
		// it
		if (e.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER)
			return;
		if (!e.isSystem())// e.isAltPressed())
			return;
		Log.d("input/keyreleased", "key released");
		Integer keycode = new Integer(e.getKeyCode());
		if (!pressedKeys.contains(keycode)) {
			this.keyPressed(e);
		}

		pressedKeys.removeElement(keycode);

		lastKeyEvent = e;
		modifiersValid = true;
		long time = getTime();

		Log.i("input.java", "RELEASED keychar='" + e.getUnicodeChar()
				+ "' keycode=0x" + Integer.toHexString(e.getKeyCode())
				+ " char='" + ((char) e.getKeyCode()) + "'");
		if (rdp != null) {
			if (!handleSpecialKeys(time, e, false))
				sendKeyPresses(newKeyMapper.getKeyStrokes(e));
		} else {
			Log
					.d("input/keyreleased",
							"The RDP layer hasn't initialised yet!!");
		}
	}

	// public void sendKeyPresses(String pressSequence) {
	public void sendKeyPresses(int scancode) {
		int flags;
		Log.i("input.java", "inside sendKeyPresses");
		Log.i("input.java", "scan code is:" + scancode);

		long t = getTime();
		if (lastKeyEvent.getAction() == KeyEvent.ACTION_DOWN) {
			Log.d("input.java", "down event" + lastKeyEvent.getDisplayLabel());
			flags = 0;
		} else {
			Log.d("input.java", "up event" + lastKeyEvent.getDisplayLabel());
			flags = 49152;
		}

		try {
			System.out.println("Scanc code:" + scancode);
			sendScancode(t, flags, scancode);
		} catch (OmniDeskException e) {
			// TODO Auto-generated catch block
			System.out.println("the sendScanCode method (0) failed");
		}

	}

	/**
	 * Retrieve the next "timestamp", by incrementing previous stamp (up to the
	 * maximum value of an integer, at which the timestamp is reverted to 1)
	 * 
	 * @return New timestamp value
	 */
	public static int getTime() {
		time++;
		if (time == Integer.MAX_VALUE)
			time = 1;
		return time;
	}

	/**
	 * Handle loss of focus to the main canvas. Clears all depressed keys
	 * (sending release messages to the server.
	 * 
	 * @throws OmniDeskException
	 */
	public void lostFocus() throws OmniDeskException {
		clearKeys();
		modifiersValid = false;
	}

	/**
	 * Handle the main canvas gaining focus. Check locking key states.
	 */
	public void gainedFocus() {
		doLockKeys(); // ensure lock key states are correct
	}

	/**
	 * Send a keyboard event to the server
	 * 
	 * @param time
	 *            Time stamp to identify this event
	 * @param flags
	 *            Flags defining the nature of the event (eg:
	 *            press/release/quiet/extended)
	 * @param scancode
	 *            Scancode value identifying the key in question
	 * @throws OmniDeskException
	 */
	public void sendScancode(long time, int flags, int scancode)
			throws OmniDeskException {
		Log.i("input.java", "sendScanCode");
		System.out.println("the scancode=" + scancode);
		System.out.println("the flags=" + flags);
		if (scancode == 0x38) { // be careful with alt
			if ((flags & RDP_KEYRELEASE) != 0) {
				Log.v("input.java", "Alt release, serverAltDown = "
						+ serverAltDown);
				serverAltDown = false;
			}
			if ((flags == RDP_KEYPRESS)) {
				Log.v("input.java", "Alt press, serverAltDown = "
						+ serverAltDown);
				serverAltDown = true;
			}
		}

		if ((scancode & KeyCode.SCANCODE_EXTENDED) != 0) {
			Log.i("input.java/sendScanCode", "the scanccode extended");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rdp.sendInput((int) time, RDP_INPUT_SCANCODE, flags | KBD_FLAG_EXT,
					scancode & ~KeyCode.SCANCODE_EXTENDED, 0);

		} else {
			Log.d("input.java/sendScanCode", "the scanccode not extended");
			rdp.sendInput((int) time, RDP_INPUT_SCANCODE, flags, scancode, 0);
		}
	}

	public void sendUnicode(KeyEvent e) {
		// TODO Auto-generated method stub
		char ch = (char) e.getUnicodeChar();
		System.out.println("the char is " + ch);
		int flags = 0; // flags field of unicode event is ignored
		long time = getTime();
		try {
			rdp.sendInput((int) time, RDP_INPUT_UNICODE, flags, ch, 0);
		} catch (OmniDeskException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	/**
	 * Release any modifier keys that may be depressed.
	 * 
	 * @throws OmniDeskException
	 */
	public void clearKeys() throws OmniDeskException {
		if (!modifiersValid)
			return;

		altDown = false;
		ctrlDown = false;

		if (lastKeyEvent == null)
			return;

		if (lastKeyEvent.isShiftPressed())
			sendScancode(getTime(), RDP_KEYRELEASE, 0x2a); // shift
		if (lastKeyEvent.isAltPressed() || serverAltDown) {
			sendScancode(getTime(), RDP_KEYRELEASE, 0x38); // ALT
			sendScancode(getTime(), RDP_KEYPRESS | KBD_FLAG_QUIET, 0x38); // ALT
			sendScancode(getTime(), RDP_KEYRELEASE | KBD_FLAG_QUIET, 0x38); // l.alt
		}

	}

	/**
	 * Send keypress events for any modifier keys that are currently down
	 * 
	 * @throws OmniDeskException
	 */
	public void setKeys() throws OmniDeskException {
		if (!modifiersValid)
			return;

		if (lastKeyEvent == null)
			return;

		if (lastKeyEvent.isShiftPressed())
			sendScancode(getTime(), RDP_KEYPRESS, 0x2a); // shift
		if (lastKeyEvent.isAltPressed())
			sendScancode(getTime(), RDP_KEYPRESS, 0x38); // l.alt

	}

	public boolean handleShortcutKeys(long time, KeyEvent e, boolean pressed)
			throws OmniDeskException {
		Log.d("input.java", "inside handle shortkut keys");
		if (!e.isAltPressed()) {
			Log.d("input.java", "Alt key not pressed");
			return false;
		}

		if (!altDown)
			return false; // all of the below have ALT on
		Log.w("input.java/handleshortkutkeys",
				"pageup,pagedown,insert,delete,end,home");
		switch (e.getKeyCode()) {

		case KeyEvent.KEYCODE_ENTER:
			sendScancode(time, RDP_KEYRELEASE, 0x38);
			altDown = false;
			Log.d("input.java", "Alt+Enter not implemented");
			// ((RdesktopFrame_Localised)
			// canvas.getParent()).toggleFullScreen();
			break;

		case KeyEvent.KEYCODE_TAB: // Alt+Tab received, quiet combination

			sendScancode(time, (pressed ? RDP_KEYPRESS : RDP_KEYRELEASE)
					| KBD_FLAG_QUIET, 0x0f);
			if (!pressed) {
				sendScancode(time, RDP_KEYRELEASE | KBD_FLAG_QUIET, 0x38); // Release
				// Alt
			}

			if (pressed)
				Log.d("input.java",
						"Alt + Tab pressed, ignoring, releasing tab");
			break;

		case KeyEvent.KEYCODE_MINUS: // Ctrl + Alt + Minus (on NUM KEYPAD) =
			// Alt+PrtSc
			if (ctrlDown) {
				if (pressed) {
					sendScancode(time, RDP_KEYRELEASE, 0x1d); // Ctrl
					sendScancode(time, RDP_KEYPRESS,
							0x37 | KeyCode.SCANCODE_EXTENDED); // PrtSc
					Log.d("input.java", "shortcut pressed: sent ALT+PRTSC");
				} else {
					sendScancode(time, RDP_KEYRELEASE,
							0x37 | KeyCode.SCANCODE_EXTENDED); // PrtSc
					sendScancode(time, RDP_KEYPRESS, 0x1d); // Ctrl
				}
			}
			break;
		case KeyEvent.KEYCODE_PLUS: // Ctrl + ALt + Plus (on NUM KEYPAD) = PrtSc
		case KeyEvent.KEYCODE_EQUALS: // for laptops that can't do Ctrl-Alt+Plus
			if (ctrlDown) {
				if (pressed) {
					sendScancode(time, RDP_KEYRELEASE, 0x38); // Alt
					sendScancode(time, RDP_KEYRELEASE, 0x1d); // Ctrl
					sendScancode(time, RDP_KEYPRESS,
							0x37 | KeyCode.SCANCODE_EXTENDED); // PrtSc
					Log.d("input.java/handleshortkutkeys",
							"shortcut pressed: sent PRTSC");
				} else {
					sendScancode(time, RDP_KEYRELEASE,
							0x37 | KeyCode.SCANCODE_EXTENDED); // PrtSc
					sendScancode(time, RDP_KEYPRESS, 0x1d); // Ctrl
					sendScancode(time, RDP_KEYPRESS, 0x38); // Alt
				}
			}
			break;
		default:
			return false;
		}
		return true;
	}

	/**
	 * Deal with modifier keys as control, alt or caps lock
	 * 
	 * @param time
	 *            Time stamp for key event
	 * @param e
	 *            Key event to check for special keys
	 * @param pressed
	 *            True if key was pressed, false if released
	 * @return
	 * @throws OmniDeskException
	 */
	public boolean handleSpecialKeys(long time, KeyEvent e, boolean pressed)
			throws OmniDeskException {
		return false;

	}

	/**
	 * Turn off any locking key, check states if available
	 */
	public void triggerReadyToSend() {
		capsLockOn = false;
		numLockOn = false;
		scrollLockOn = false;
		doLockKeys(); // ensure lock key states are correct
	}

	protected void doLockKeys() {
	}

	public void mousePressed(MotionEvent e) throws OmniDeskException {
		System.out.println("the mouse pressed method called");

		int time = getTime();
		int x, y;

		if (Common.TouchMode) {
			if (Common.Fullscreen) {
				x = (int) (e.getX() * Common.bitmapWidth / Common.canvasWidth);
				y = (int) (e.getY() * Common.bitmapHeight / Common.canvasHeight);
			} else {
				x = ((int) e.getX() + Common.x);
				y = (int) e.getY() + Common.y;
			}
		} else {
			x = Common.mouseX;
			y = Common.mouseY;
		}
		if (rdp != null) {
			Log.d("input/mousePressed", "Mouse Button 1 Pressed.");
			rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON1
					| MOUSE_FLAG_DOWN, x, y);

		}
		System.out.println("the mouse input data packet has been sent");
	}

	public void mouseReleased(MotionEvent e) throws OmniDeskException {
		System.out.println("the mouse released method called");
		int time = getTime();
		int x, y;
		
		if (Common.TouchMode) {
			if (Common.Fullscreen) {
				x = (int) (e.getX() * Common.bitmapWidth / Common.canvasWidth);
				y = (int) (e.getY() * Common.bitmapHeight / Common.canvasHeight);
			} else {
				x = ((int) e.getX() + Common.x);
				y = (int) e.getY() + Common.y;
			}
		} else {
			x = Common.mouseX;
			y = Common.mouseY;
		}
		if (rdp != null) {
			Log.d("input/mousePressed", "mouse button1 released");
			rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON1, x, y);

		}
	}

	public void mouseMoved(MotionEvent e, int xDiff, int yDiff)
			throws OmniDeskException {
		System.out.println("the mouse pressed method called");

		/********* to change the mouse co-ordinates *******/

		Common.mouseX -= xDiff;
		if (Common.mouseX > Options.width)
			Common.mouseX = Options.width;
		else if (Common.mouseX < 0)
			Common.mouseX = 0;

		Common.mouseY -= yDiff;
		if (Common.mouseY > Options.height)
			Common.mouseY = Options.height;
		else if (Common.mouseY < 0)
			Common.mouseY = 0;

		/********* to pan the screen if necessary *********/

		if (!Common.Fullscreen) {
			if (Common.mouseX > Common.x + Common.canvasWidth) {

				Common.x = Common.mouseX - Common.canvasWidth;// Common.canvasWidth
				// /2 ;
				if (Common.bitmapWidth - Common.x < Common.canvasWidth)
					Common.x = Common.bitmapWidth - Common.canvasWidth;
			} else if (Common.mouseX < Common.x)

				Common.x = Common.mouseX;

			if (Common.mouseY > Common.y + Common.canvasHeight) {
				Common.y = Common.mouseY - Common.canvasHeight;
				if (Common.bitmapHeight - Common.y < Common.canvasHeight)
					Common.y = Common.bitmapHeight - Common.canvasHeight;
			} else if (Common.mouseY < Common.y)

				Common.y = Common.mouseY;
		}

		int time = getTime();
		if (rdp != null) {
			Log.d("input/mousePressed", "Mouse Button 1 Pressed.");
			try {
				rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_MOVE
						| MOUSE_FLAG_DOWN, Common.mouseX, Common.mouseY);
			} catch (OmniDeskException e1) {
				e1.printStackTrace();
			}
		}
		Common.currentImageViewer.invalidate();
	}

	public void mouseReset(int x, int y) {
		int time = getTime();
		if (rdp != null) {
			Log.d("input/mousePressed", "Mouse Button 1 Pressed.");
			try {
				rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_MOVE
						| MOUSE_FLAG_DOWN, x, y);
			} catch (OmniDeskException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void mouseDragged(MotionEvent e, int xDiff, int yDiff)
			throws OmniDeskException {

		int newX, newY;
		if (Common.TouchMode) {
			if(Common.Fullscreen)
			{
				newX = (int) (e.getX() * Common.bitmapWidth / Common.canvasWidth);
				newY = (int) (e.getY() * Common.bitmapHeight / Common.canvasHeight);
			}
			else
			{
				newX=(int) e.getX() + Common.x;
				newY=(int) e.getY() + Common.y;
			}
		
		} else {
			newX = Common.mouseX - xDiff;
			newY = Common.mouseY - yDiff;
		}

		if (newX > Options.width)
			newX = Options.width;
		else if (newX < 0)
			newX = 0;

		if (newY > Options.height)
			newY = Options.height;
		else if (newY < 0)
			newY = 0;

		if (!Common.TouchMode) {
			Common.mouseX = newX;
			Common.mouseY = newY;
		}

		// ********* to pan the screen if necessary *********

		if (!Common.Fullscreen) {
			if (newX > Common.x + Common.canvasWidth) {
				Common.x = newX - Common.canvasWidth;// Common.canvasWidth
				// /2 ;
				if (Common.bitmapWidth - Common.x < Common.canvasWidth)
					Common.x = Common.bitmapWidth - Common.canvasWidth;
			} else if (newX < Common.x)

				Common.x = newX;

			if (newY > Common.y + Common.canvasHeight) {
				Common.y = newY - Common.canvasHeight;
				if (Common.bitmapHeight - Common.y < Common.canvasHeight)
					Common.y = Common.bitmapHeight - Common.canvasHeight;
			} else if (newY < Common.y)
				Common.y = newY;
		}
		int time = getTime();
		if (rdp != null) {
			Log.d("input/mousePressed", "Mouse Button 1 Pressed.");
			try {
				rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_MOVE
						| MOUSE_FLAG_DOWN, newX, newY);
			} catch (OmniDeskException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void mouseRightClickPress(MotionEvent e) {

		int x, y;
		if (Common.TouchMode) {
			if (Common.Fullscreen) {
				x = (int) (e.getX() * Common.bitmapWidth / Common.canvasWidth);
				y = (int) (e.getY() * Common.bitmapHeight / Common.canvasHeight);
			} else {
				x = ((int) e.getX() + Common.x);
				y = (int) e.getY() + Common.y;
			}
		} else {
			x = Common.mouseX;
			y = Common.mouseY;
		}

		int time = getTime();
		if (rdp != null) {
			try {
				if (Common.TouchMode)
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON2
							| MOUSE_FLAG_DOWN, x, y);
				else
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON2
							| MOUSE_FLAG_DOWN, Common.mouseX, Common.mouseY);
			} catch (OmniDeskException e1) {
				e1.printStackTrace();
			}

		}

	}

	public void mouseRightClickRelease(MotionEvent e) {

		System.out.println("the mouse released method called");
		int time = getTime();

		int x, y;
		if (Common.TouchMode) {
			if (Common.Fullscreen) {
				x = (int) (e.getX() * Common.bitmapWidth / Common.canvasWidth);
				y = (int) (e.getY() * Common.bitmapHeight / Common.canvasHeight);
			} else {
				x = ((int) e.getX() + Common.x);
				y = (int) e.getY() + Common.y;
			}
		} else {
			x = Common.mouseX;
			y = Common.mouseY;
		}
		if (rdp != null) {
			try {
				if (Common.TouchMode)
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON2, x,
							y);
				else
					rdp.sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_BUTTON2,
							Common.mouseX, Common.mouseY);
			} catch (OmniDeskException e1) {
				e1.printStackTrace();
			}

		}

	}

	public void sendKeyCombinations(boolean alt, boolean ctl, boolean shift,
			int scanCode) {

		time = getTime();

		try {
			if (alt)
				sendScancode(time, RDP_KEYPRESS, SCANCODE_ALT);
			if (ctl)
				sendScancode(time, RDP_KEYPRESS, SCANCODE_CTRL);
			if (shift)
				sendScancode(time, RDP_KEYPRESS, SCANCODE_SHIFT);
			sendScancode(time, RDP_KEYPRESS, scanCode);

			time = getTime();

			if (alt)
				sendScancode(time, RDP_KEYRELEASE, SCANCODE_ALT);
			if (ctl)
				sendScancode(time, RDP_KEYRELEASE, SCANCODE_CTRL);
			if (shift)
				sendScancode(time, RDP_KEYRELEASE, SCANCODE_SHIFT);
			sendScancode(time, RDP_KEYRELEASE, scanCode);

		} catch (OmniDeskException e) {
			e.printStackTrace();
		}

	}

}// end class
