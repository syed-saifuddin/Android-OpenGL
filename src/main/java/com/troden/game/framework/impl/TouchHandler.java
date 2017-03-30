package com.troden.game.framework.impl;

import java.util.List;
import android.view.View.OnTouchListener;

import com.troden.game.framework.Input.TouchEvent;

/*___________________________________________________________________
|
| Interface: TouchHandler
|
| Description: Needed to enable the use of 2 different TouchHandler
|	classes - one that supports only single touch (Android 1.5 and
|	earlier) and another that supports multi-touch (Android 1.6 and
|	later).
|__________________________________________________________________*/
public interface TouchHandler extends OnTouchListener 
{
    public boolean isTouchDown (int finger);
    
    public int getTouchX (int finger);
    
    public int getTouchY (int finger);
    
    public List<TouchEvent> getTouchEvents ();
}
