package com.troden.game.framework.impl;

import java.util.List;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

import com.troden.game.framework.Input;

/*___________________________________________________________________
|
| Class: GameInput
|
| Description: Integrates user input handling for keyboard, touch and
|	accelerometer.
|__________________________________________________________________*/
public class GameInput implements Input 
{   
	KeyboardHandler      keyHandler;
    TouchHandler 		 touchHandler;
    AccelerometerHandler accelHandler;
    
	/*___________________________________________________________________
	|
	| Function: GameInput (constructor)
	|__________________________________________________________________*/
    public GameInput (Context context, View view, float scaleX, float scaleY, boolean use_keyboard, boolean use_accelerometer) 
    {
    	// Init variables
    	keyHandler   = null;
    	touchHandler = null;
    	accelHandler = null;
    	
    	// Create the keyboard handler
    	if (use_keyboard)
    		keyHandler = new KeyboardHandler (view);
        // Create the touch handler - use multi-touch if Android 2.0+ (API level 5+)
        if (Integer.parseInt(VERSION.SDK) < 5) 
            touchHandler = new SingleTouchHandler (view, scaleX, scaleY);
        else
            touchHandler = new MultiTouchHandler (view, scaleX, scaleY);   
        // Create the accelerometer handler (starts with processing disabled)
        if (use_accelerometer)
        	accelHandler = new AccelerometerHandler (context);
    }

	/*___________________________________________________________________
	|
	| Function: isKeyPressed
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public boolean isKeyPressed (int keyCode) 
    {
    	if (keyHandler != null)
    		return keyHandler.isKeyPressed (keyCode);
    	else
    		return false;
    }

	/*___________________________________________________________________
	|
	| Function: getKeyEvents
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public List<KeyEvent> getKeyEvents () 
    {
    	if (keyHandler != null)
    		return keyHandler.getKeyEvents ();
    	else
    		return null;
    }
    
	/*___________________________________________________________________
	|
	| Function: isTouchDown
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public boolean isTouchDown (int finger) 
    {
        return touchHandler.isTouchDown (finger);
    }

	/*___________________________________________________________________
	|
	| Function: getTouchX
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public int getTouchX (int finger) 
    {
        return touchHandler.getTouchX (finger);
    }

	/*___________________________________________________________________
	|
	| Function: getTouchY
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public int getTouchY (int finger) 
    {
        return touchHandler.getTouchY (finger);
    }

	/*___________________________________________________________________
	|
	| Function: getTouchEvents
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public List<TouchEvent> getTouchEvents () 
    {
        return touchHandler.getTouchEvents ();
    }
    
	/*___________________________________________________________________
	|
	| Function: enableAccelProcessing
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public void enableAccelProcessing (boolean flag)
    {
    	if (accelHandler != null)
    		enableAccelProcessing (flag);
    }
	/*___________________________________________________________________
	|
	| Function: getAccelX
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public float getAccelX () 
    {
    	if (accelHandler != null)
    		return accelHandler.getAccelX ();
    	else
    		return 0;
    }

	/*___________________________________________________________________
	|
	| Function: getAccelY
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public float getAccelY () 
    {
    	if (accelHandler != null)
    		return accelHandler.getAccelY ();
    	else
    		return 0;
    }

	/*___________________________________________________________________
	|
	| Function: getAccelZ
	|
	| Description: 
	|__________________________________________________________________*/
    @Override
    public float getAccelZ () 
    {
    	if (accelHandler != null)
    		return accelHandler.getAccelZ ();
    	else
    		return -1;
    }
}
