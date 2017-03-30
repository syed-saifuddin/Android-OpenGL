package com.troden.game.framework.impl;

import java.util.ArrayList;
import java.util.List;
import android.view.MotionEvent;
import android.view.View;

import com.troden.game.framework.Pool;
import com.troden.game.framework.Pool.PoolObjectFactory;
import com.troden.game.framework.Input.TouchEvent;

/*___________________________________________________________________
|
| Class: SingleTouchHandler
|
| Description: Used for Android 1.5 and earlier, which does not
|	support multi-touch.
|__________________________________________________________________*/
public class SingleTouchHandler implements TouchHandler 
{
    boolean isTouched;
    int touchX, touchY;
    Pool<TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents       = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
    float scaleX, scaleY;
    
	/*___________________________________________________________________
	|
	| Function: SingleTouchHandler (constructor)
	|__________________________________________________________________*/
    public SingleTouchHandler (View view, float scaleX, float scaleY) 
    {
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }            
        };
        // Init member variables
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        // Create a pool of reusable objects
        touchEventPool = new Pool<TouchEvent>(factory, 100);
        // Start listening for touch events
        view.setOnTouchListener(this);
    }
    
	/*___________________________________________________________________
	|
	| Function: onTouch
	|
	| Description: Called back OS when screen is touched.  Touch data is
	|	stored in the touchEventsBuffer.
	|__________________________________________________________________*/
    @Override
    public boolean onTouch (View v, MotionEvent event) 
    {
        synchronized(this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                touchEvent.type = TouchEvent.TOUCH_DOWN;
	                isTouched = true;
	                break;
	            case MotionEvent.ACTION_MOVE:
	                touchEvent.type = TouchEvent.TOUCH_DRAGGED;
	                isTouched = true;
	                break;
	            case MotionEvent.ACTION_CANCEL:                
	            case MotionEvent.ACTION_UP:
	                touchEvent.type = TouchEvent.TOUCH_UP;
	                isTouched = false;
	                break;
            }
            
            touchEvent.x = touchX = (int)(event.getX() * scaleX);
            touchEvent.y = touchY = (int)(event.getY() * scaleY);
            touchEventsBuffer.add(touchEvent);                        
            
            return true;
        }
    }

	/*___________________________________________________________________
	|
	| Function: isTouchDown
	|
	| Description: Returns true if finger is currently touching screen.
	|__________________________________________________________________*/
    @Override
    public boolean isTouchDown (int finger) 
    {
        synchronized(this) {
            if (finger == 0)
                return isTouched;
            else
                return false;
        }
    }

	/*___________________________________________________________________
	|
	| Function: getTouchX
	|
	| Description: Returns current X coordinate of finger touch.
	|__________________________________________________________________*/
    @Override
    public int getTouchX (int finger) 
    {
        synchronized(this) {
            return touchX;
        }
    }

	/*___________________________________________________________________
	|
	| Function: getTouchY
	|
	| Description: Returns current Y coordinate of finger touch.
	|__________________________________________________________________*/
    @Override
    public int getTouchY (int finger) 
    {
        synchronized(this) {
            return touchY;
        }
    }

	/*___________________________________________________________________
	|
	| Function: getTouchEvents
	|
	| Description: Returns list of buffered touch events that have occurred
	|	since last call to this function.
	|__________________________________________________________________*/
    @Override
    public List<TouchEvent> getTouchEvents () 
    {
        synchronized(this) {     
            int len = touchEvents.size();
            for (int i=0; i<len; i++)
                touchEventPool.freeObject(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return (touchEvents);
        }
    }
}
