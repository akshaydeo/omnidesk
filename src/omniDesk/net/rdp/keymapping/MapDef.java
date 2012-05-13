/**
 * 
 */
package omniDesk.net.rdp.keymapping;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import android.util.Log;
import android.view.KeyEvent;

import omniDesk.net.rdp.*;;
/**
 * @author akshay
 *
 */
public class MapDef {
	// Flag masks for use in generating an integer modifiers value (for text definition output)
	private final int FLAG_SHIFT = 0x01; // flag mask for a shift modifier
	private final int FLAG_CTRL = 0x02; // flag mask for a control modifier
	private final int FLAG_ALT = 0x04; // flag mask for an alt modifier
	private final int FLAG_CAPSLOCK = 0x08; // flag mask for a capslock modifier
	
	  private int scancode;
	  private boolean ctrlDown;
	  private boolean shiftDown;
	  private boolean altDown;
	  private boolean capslockDown;
	  
	  private char keyChar;
	  private int keyCode;
	  private boolean characterDef;
	  
	  private int keyLocation;
	  	  
	  /**
	   * Constructor for a character-defined mapping definition
	   * 
	   * @param keyChar
	   * @param keyLocation
	   * @param scancode
	   * @param ctrlDown
	   * @param shiftDown
	   * @param altDown
	   * @param capslockDown
	   */
	  public MapDef(char keyChar, int keyLocation, int scancode, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean capslockDown) {
	  	this.keyChar = keyChar;
	  	this.characterDef = true;
	  	
	  	this.keyLocation = keyLocation;
	  	this.scancode = scancode;
	  	this.ctrlDown = ctrlDown;
	  	this.altDown = altDown;
	  	this.shiftDown = shiftDown;
	  	this.capslockDown = capslockDown;
	  }
	  
	  /**
	   * Constructor for a keycode-defined mapping definition
	   * 
	   * @param keyChar
	   * @param keyLocation
	   * @param scancode
	   * @param ctrlDown
	   * @param shiftDown
	   * @param altDown
	   * @param capslockDown
	   */
	  public MapDef(int keyCode, int keyLocation, int scancode, boolean ctrlDown, boolean shiftDown, boolean altDown, boolean capslockDown) {
	  	Log.d("mapdef.java","long contructor");
		  this.keyCode = keyCode;
	  	this.characterDef = false;
	  	
	  	this.keyLocation = keyLocation;
	  	this.scancode = scancode;
	  	this.ctrlDown = ctrlDown;
	  	this.altDown = altDown;
	  	this.shiftDown = shiftDown;
	  	this.capslockDown = capslockDown;
	  }
	  
	  public int getKeyCode(){ return keyCode; }
	  
	  public char getKeyChar(){ return keyChar; }
	  
	  /**
	   * Return the scancode associated with this mapping
	   * @return
	   */
	  public int getScancode(){ return scancode; }
	  
	  /**
	   * Return true if this mapping is defined by a character, false otherwise
	   * @return
	   */
	  public boolean isCharacterDef(){ return characterDef; }
	  
	  /**
	   * Return true if the keystroke defined in this mapping requires that the
	   * Control key be down
	   * 
	   * @return
	   */
	  public boolean isCtrlDown(){ return ctrlDown; }

	  /**
	   * Return true if the keystroke defined in this mapping requires that the
	   * Alt key be down
	   * 
	   * @return
	   */
	  public boolean isAltDown(){ return altDown; }
	  
	  /**
	   * Return true if the keystroke defined in this mapping requires that the
	   * Shift key be down
	   * 
	   * @return
	   */
	  public boolean isShiftDown(){ return shiftDown; }
	  
	  /**
	   * Return true if the keystroke defined in this mapping requires that
	   * Caps Lock is on
	   * 
	   * @return
	   */
	  public boolean isCapslockOn(){ return capslockDown; }	  
	  
	  /**
	   * Return the number of modifiers that would need to be changed to
	   * send the specified character/key using this particular mapping.
	   * 
	   * @param e Key event which was received by Java
	   * @param capslock Is the Caps Lock key down?
	   * @return The number of modifier changes to make
	   */
	  public int modifierDistance(KeyEvent e, boolean capslock){
		//boolean capslock = e.getComponent().getToolkit().getLockingKeyState( KeyEvent.VK_CAPS_LOCK);		

	  	if(!characterDef) return 0;
	  	
	  	int dist = 0;
	  	// neer Ctrl not available if(ctrlDown != e.isControlDown()) dist += 1;
	  	if(altDown != e.isAltPressed()) dist += 1;
	  	if(shiftDown != e.isShiftPressed()) dist += 1;
	  	if(capslockDown != capslock) dist += 1;
	  	return dist;
	  }
	  
	  public boolean appliesTo(char c){
	  	return ((characterDef) && (this.keyChar == c) && !(capslockDown));
	  }
	  
	  /**
	   * 
	   * Return true if this map definition applies to the supplied key event
	   * 
	   * @param e KeyEvent to check definition against
	   * @return
	   */
	  protected boolean appliesToTyped(KeyEvent e){	
  			return ((characterDef) && (this.keyChar == e.getNumber()));		
	  }

	  
	  protected boolean appliesToTyped(KeyEvent e, boolean capslock){
	  	
	  /*	if(Constants.OS == Constants.MAC){
	  		// Remap the hash key to �
	  		if(Options.remap_hash && (e.getKeyChar() == '�' ||'�' || '�')){
	  			return ((characterDef) && (this.keyChar == '#'));
	  		}
	  		
	  		
	  		// Handle unreported shifted capitals (with capslock) on a Mac
	  		if(capslock && Character.isLetter(e.getKeyChar()) && Character.isUpperCase(e.getKeyChar()) && e.isShiftDown()){
	  			char c = Character.toLowerCase(e.getKeyChar());
	  			return ((characterDef) && (this.keyChar == c));
	  		}
	  	}*/
	  	
		return ((characterDef) && (this.keyChar == e.getDisplayLabel()));// not sure neer .getKeyChar()));		
	  }

	  
	  /**
	   * 
	   * Return true if this map definition applies to the supplied key event
	   * 
	   * @param e KeyEvent to check definition against
	   * @return
	   */
	  protected boolean appliesToPressed(KeyEvent e){
	  	// only match special characters if the modifiers are consistent
		//  Log.i("mapdef.java","appliesToPressed");
	  	if(!characterDef){
	  	//cntrl not available android	if(!((ctrlDown && e.isControlDown()) || !ctrlDown)) return false;
	  		if(!((altDown && e.isAltPressed()) || !altDown)) return false;
	  	}
	  	
	  	return ((!characterDef) && (this.keyCode == e.getKeyCode()));
	  }
	  
	  /**
	   * Constructor for a mapping definition based on a given string
	   * representation (as would be output to a stream by the writeToStream method).
	   * 
	   * @param definition One-line definition string
	   * @throws KeyMapException Any parsing errors which may occur
	   */
	  public MapDef(String definition) throws KeyMapException{
		  Log.d("mapdef.java","Constructor");
	  	StringTokenizer st = new StringTokenizer(definition);
	  	try{
	  			// determine whether the definition is character-oriented
	  			characterDef = ( (Integer.parseInt(st.nextToken()) == 1) ? true  : false );
	  			
	  			// read in the character or keycode
	  			keyCode = Integer.parseInt(st.nextToken());
	  	//neer		if(characterDef) keyChar = (char) Integer.parseInt(st.nextToken());
	  	//neer		else keyCode = Integer.parseInt(st.nextToken());
	  			
	  			// read in the key location
	  			keyLocation = Integer.parseInt(st.nextToken());
	  			
	  			// read in the scancode (from a HEX string)
	  			scancode = Integer.decode(st.nextToken()).intValue();
	  			
	  			// read in the modifiers and interpret
	  			int modifiers = Integer.parseInt(st.nextToken());
	  			shiftDown = ((modifiers & this.FLAG_SHIFT) != 0);
	  			ctrlDown = ((modifiers & this.FLAG_CTRL) != 0);
	  			altDown = ((modifiers & this.FLAG_ALT) != 0);
	  			capslockDown = ((modifiers & this.FLAG_CAPSLOCK) != 0);
	  			 			
	  	}
	  	catch(NumberFormatException nfEx){ throw new KeyMapException("" + nfEx.getMessage() + " is not numeric"); }
	  	catch(NoSuchElementException nseEx){ throw new KeyMapException("Not enough parameters in definition"); }

	  }
	  
	  /**
	   * Output this mapping definition to a stream, formatted
	   * as a single line
	   * (characterDef character/keycode location scancode modifiers [description]) 
	   * 
	   * @param p Stream to write to
	   */
	  public void writeToStream(PrintStream p){
	  	
	  	// create definition string with first character 1 if the
	  	// mapping is character-defined, 0 otherwise
	  	String definition = "" + (characterDef?1:0);
	  	
	  	// add character or keycode
	  	definition += "\t";
	  	if(characterDef) definition += (int) keyChar;
		else definition += keyCode;
	  		
	    // add key location
	  	definition += "\t" + keyLocation;
	  	definition += "\t0x" + Integer.toHexString(scancode);
	  	
	  	// build and add modifiers as a set of flags in an integer value
	  	int modifiers = 0;
	  	modifiers |= (shiftDown?FLAG_SHIFT:0);
	  	modifiers |= (ctrlDown?FLAG_CTRL:0);
	  	modifiers |= (altDown?FLAG_ALT:0);
	  	modifiers |= (capslockDown?FLAG_CAPSLOCK:0);
	  	definition += "\t" + modifiers;
	  	
	  	// add additional information if available (and necessary)
	// neer  	if(!characterDef) definition += "\t" + KeyEvent.getKeyText(this.keyCode);
	  	
	  	// output the definition to the specified stream
	  	p.println(definition);
	  }
	  
}
