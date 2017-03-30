package com.troden.game.framework.impl;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.View.OnKeyListener;

import com.troden.game.framework.Pool;
import com.troden.game.framework.Pool.PoolObjectFactory;
import com.troden.game.framework.Input.KeyEvent;

/*___________________________________________________________________
|
| Class: KeyboardHandler
|
| Description: Manager for keyboard input.
|__________________________________________________________________*/
public class KeyboardHandler implements OnKeyListener
{
	boolean[] pressedKeys = new boolean[128];
	Pool<KeyEvent> keyEventPool;
	List<KeyEvent> keyEventsBuffer = new ArrayList<KeyEvent>();
	List<KeyEvent> keyEvents 	   = new ArrayList<KeyEvent>();
	
	/*___________________________________________________________________
	|
	| Function: KeyboardHandler (constructor)
	|__________________________________________________________________*/
	public KeyboardHandler (View view)
	{
		PoolObjectFactory<KeyEvent> factory = new PoolObjectFactory<KeyEvent>() {
			@Override
			public KeyEvent createObject() {
				return new KeyEvent();
			}
		};
		// Create a pool of reusable objects
		keyEventPool = new Pool<KeyEvent>(factory, 100);
		// Start listening for key events
		view.setOnKeyListener(this);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}
		
	/*___________________________________________________________________
	|
	| Function: onKey 
	|
	| Description: Called back by OS when a key is pressed.  If the key is
	|	an ASCII key it is stored in the keyEventsBuffer.
	|__________________________________________________________________*/
	@Override
	public boolean onKey (View v, int keyCode, android.view.KeyEvent event)
	{
		if (event.getAction() == android.view.KeyEvent.ACTION_MULTIPLE)
			return (false);
		
		synchronized(this) {
			KeyEvent keyEvent = keyEventPool.newObject ();
			keyEvent.keyCode = keyCode;
			keyEvent.keyChar = (char) event.getUnicodeChar ();
			if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
				keyEvent.type = KeyEvent.KEY_DOWN;
				if (keyCode > 0 && keyCode < 127)
					pressedKeys[keyCode] = true;
			}
			if (event.getAction() == android.view.KeyEvent.ACTION_UP) {
				keyEvent.type = KeyEvent.KEY_UP;
				if (keyCode > 0 && keyCode < 127)
					pressedKeys[keyCode] = false;
			}
			keyEventsBuffer.add (keyEvent);
		}
		return (true);
	}
	
	/*___________________________________________________________________
	|
	| Function: isKeyPressed 
	|
	| Description: Returns current state of a particular key.
	|__________________________________________________________________*/
	public boolean isKeyPressed (int keyCode) 
	{
        if (keyCode < 0 || keyCode > 127)
            return false;
        return (pressedKeys[keyCode]);
    }

	/*___________________________________________________________________
	|
	| Function: getKeyEvents 
	|
	| Description: Returns list of buffered key events that have occurred
	|	since last call to this function.
	|__________________________________________________________________*/
    public List<KeyEvent> getKeyEvents () 
    {
        synchronized (this) {
            int len = keyEvents.size ();
            for (int i=0; i<len; i++)
                keyEventPool.freeObject(keyEvents.get(i));
            keyEvents.clear();
            keyEvents.addAll(keyEventsBuffer);
            keyEventsBuffer.clear();
            return (keyEvents);
        }
    }
}
