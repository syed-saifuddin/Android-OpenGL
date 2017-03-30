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
| Class: MultiTouchHandler
|
| Description: Used for Android 1.6 and later, which supports multi-touch.
|__________________________________________________________________*/
public class MultiTouchHandler implements TouchHandler 
{
	static final int MAX_FINGER_IDS = 20;
    boolean[] isTouched = new boolean[MAX_FINGER_IDS];
    int[] touchX = new int[MAX_FINGER_IDS];
    int[] touchY = new int[MAX_FINGER_IDS];
    Pool<TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
    float scaleX, scaleY;

	/*___________________________________________________________________
	|
	| Function: MultiTouchHandler (constructor)
	|__________________________________________________________________*/
    public MultiTouchHandler (View view, float scaleX, float scaleY) 
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
    public boolean onTouch(View v, MotionEvent event) 
    {
        synchronized (this) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            int fingerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int fingerId = event.getPointerId(fingerIndex);
            TouchEvent touchEvent;

            if (fingerId < MAX_FINGER_IDS) {
	            switch (action) {
		            case MotionEvent.ACTION_DOWN:
		            case MotionEvent.ACTION_POINTER_DOWN:
		                touchEvent 			= touchEventPool.newObject();
		                touchEvent.type 	= TouchEvent.TOUCH_DOWN;
		                touchEvent.finger 	= fingerId;
		                touchEvent.x 		= touchX[fingerId] = (int)(event.getX(fingerIndex) * scaleX);
		                touchEvent.y 		= touchY[fingerId] = (int)(event.getY(fingerIndex) * scaleY);
		                isTouched[fingerId] = true;
		                touchEventsBuffer.add(touchEvent);
		                break;
		
		            case MotionEvent.ACTION_UP:
		            case MotionEvent.ACTION_POINTER_UP:
		            case MotionEvent.ACTION_CANCEL:
		                touchEvent 			= touchEventPool.newObject();
		                touchEvent.type 	= TouchEvent.TOUCH_UP;
		                touchEvent.finger 	= fingerId;
		                touchEvent.x 		= touchX[fingerId] = (int)(event.getX(fingerIndex) * scaleX);
		                touchEvent.y 		= touchY[fingerId] = (int)(event.getY(fingerIndex) * scaleY);
		                isTouched[fingerId] = false;
		                touchEventsBuffer.add(touchEvent);
		                break;
		
		            case MotionEvent.ACTION_MOVE:
		                int pointerCount = event.getPointerCount();
		                for (int i = 0; i < pointerCount; i++) {
		                	fingerIndex = i;
		                	fingerId = event.getPointerId(fingerIndex);
		
		                    touchEvent 			= touchEventPool.newObject();
		                    touchEvent.type 	= TouchEvent.TOUCH_DRAGGED;
		                    touchEvent.finger 	= fingerId;
		                    touchEvent.x 		= touchX[fingerId] = (int)(event.getX(fingerIndex) * scaleX);
		                    touchEvent.y 		= touchY[fingerId] = (int)(event.getY(fingerIndex) * scaleY);
		                    touchEventsBuffer.add(touchEvent);
		                }
		                break;
	            }
            }
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
        synchronized (this) {
            if (finger < 0 || finger >= MAX_FINGER_IDS)
                return false;
            else
                return isTouched[finger];
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
        synchronized (this) {
            if (finger < 0 || finger >= MAX_FINGER_IDS)
                return 0;
            else
                return touchX[finger];
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
        synchronized (this) {
            if (finger < 0 || finger >= MAX_FINGER_IDS)
                return 0;
            else
                return touchY[finger];
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
    public List<TouchEvent> getTouchEvents() 
    {
        synchronized (this) {
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
