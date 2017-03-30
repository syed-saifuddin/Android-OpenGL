package com.troden.game.framework;

import java.util.List;

/*___________________________________________________________________
|
| Interface: Input
|
| Description: A generic interface for Android game input.
|__________________________________________________________________*/
public interface Input 
{
	/*___________________________________________________________________
	|
	| Class: KeyEvent
	|__________________________________________________________________*/
	public static class KeyEvent 
	{
        public static final int KEY_DOWN = 0;
        public static final int KEY_UP   = 1;

        public int  type, keyCode;
        public char keyChar;	// unicode character for the key

        public String toString() {
            StringBuilder desc = new StringBuilder ();
            if (type == KEY_DOWN)
            	desc.append ("key down, ");
            else
            	desc.append ("key up, ");
            desc.append (keyCode);
            desc.append (",");
            desc.append (keyChar);
            return (desc.toString ());
        }
    }

	/*___________________________________________________________________
	|
	| Class: TouchEvent
	|__________________________________________________________________*/
	public static class TouchEvent 
	{
        public static final int TOUCH_DOWN    = 0;
        public static final int TOUCH_UP      = 1;
        public static final int TOUCH_DRAGGED = 2;

        public int type, x, y, finger;
       
        public String toString() {
            StringBuilder desc = new StringBuilder ();
            if (type == TOUCH_DOWN)
            	desc.append ("touch down, ");
            else if (type == TOUCH_DRAGGED)
            	desc.append("touch dragged, ");
            else
            	desc.append ("touch up, ");
            desc.append (finger);
            desc.append (",");
            desc.append (x);
            desc.append (",");
            desc.append (y);
            return (desc.toString ());
        }
    }

	/*___________________________________________________________________
	|
	| Functions
	|__________________________________________________________________*/
    
	// Keyboard functions
	public boolean isKeyPressed (int keyCode);
	public List<KeyEvent> getKeyEvents ();
	
	// Touch functions
    public boolean isTouchDown (int finger);
    public int getTouchX (int finger);
    public int getTouchY (int finger);
    public List<TouchEvent> getTouchEvents ();
    
    // Accelerometer functions
    public void enableAccelProcessing (boolean flag);
    public float getAccelX ();
    public float getAccelY ();
    public float getAccelZ ();
}
