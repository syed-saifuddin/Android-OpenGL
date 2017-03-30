/*___________________________________________________________________
|
| File: OpenGLRenderer.java
|
| Description: Rendering class for slotmachine app.  
|
| TO DO: 1) Reload OpenGL resources (textures) on a context switch.
|__________________________________________________________________*/
package com.troden.test_3dmodel;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.troden.game.framework.impl.AccelerometerHandler;
import com.troden.game.framework.impl.GameAudio;
import com.troden.game.framework.impl.Music;
import com.troden.game.framework.impl.Sound;

/*___________________________________________________________________
|
| Class: OpenGLRenderer
|__________________________________________________________________*/
public class OpenGLRenderer implements Renderer
{
	// Used for runtime debug messages
	private static final String DEBUG_TAG="Project Logging";

	// App context
	Context mContext;
	// Handler to run code on UI thread
	Handler mHandler;

	// 3D models
	private Model model;

	// State variables
	private long mLastTime;
	private long spinTime = 0;

	// Audio variables
	GameAudio audio;
	Music song1;
	Sound sound_laugh;

	boolean touching = false;

	/*___________________________________________________________________
	|
	| Function: OpenGLRenderer (constructor)
	|
	| Description: Called when surface is created.  Do first-time initializations
	|	here.
	|__________________________________________________________________*/
	public OpenGLRenderer (Context context, Handler handler)
	{
		mContext = context;
		mHandler = handler;

		// Initialize the rectangle
		model = new Model (R.raw.test, mContext);
		//model = new Model (R.raw.tifa, mContext);

		// Init audio
		audio = new GameAudio (context, 10);
		song1 = audio.newMusic("music/song1.ogg");
		song1.setLooping(true);

		sound_laugh = audio.newSound ("sound/laugh.mp3");

		// Init accelerometer
		//ah = new AccelerometerHandler (context);
		//ah.enable();

		// Set timer
		mLastTime = System.currentTimeMillis() + 100;
	}

	/*___________________________________________________________________
	|
	| Function: onSurfaceCreated
	|
	| Description: Called when surface is created.  Do first-time initializations
	|	here.
	|__________________________________________________________________*/
	public void onSurfaceCreated (GL10 gl, EGLConfig config)
	{
//		Log.i(DEBUG_TAG, "onsurfaceCreated()");
		// Set the background color to black ( rgba )
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Enable Smooth Shading, default not really needed
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup
		gl.glClearDepthf(1.0f);
		// Enables depth testing
		gl.glEnable(GL10.GL_DEPTH_TEST);
		// The type of depth testing to do
		gl.glDepthFunc(GL10.GL_LEQUAL);
		// Really nice perspective calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
	}

	/*___________________________________________________________________
	|
	| Function: onSurfaceChanged
	|
	| Description: Called when device is changing between portrait and landscape
	|	mode.
	|__________________________________________________________________*/
	public void onSurfaceChanged (GL10 gl, int width, int height)
	{
//		Log.i(DEBUG_TAG, "onsurfaceChanged()");
		// Sets the current view port to the new size
		gl.glViewport (0, 0, width, height);
		// Select the projection matrix
		gl.glMatrixMode (GL10.GL_PROJECTION);
		// Reset the projection matrix
		gl.glLoadIdentity ();
		// Calculate the aspect ratio of the window
		GLU.gluPerspective (gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
		// Select the modelview matrix
		gl.glMatrixMode (GL10.GL_MODELVIEW);
		// Reset the modelview matrix
		gl.glLoadIdentity ();

		Log.i("Project Logging", gl.glGetString(GL10.GL_VERSION));
		//Log.i("Project Logging", gl.glGetString(GL10.GL_EXTENSIONS));
		String extensions = gl.glGetString(GL10.GL_EXTENSIONS);
		if (extensions.contains ("vertex_buffer_object"))
			Log.i("Project Logging", "VBOs are supported");
		else
			Log.i("Project Logging", "VBOs are not supported");

		model.initVBOs (gl);
		//model.loadBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.tifa_tex_d512));
		model.loadBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.tex));
	}

	/*___________________________________________________________________
	|
	| Function: onDrawFrame
	|
	| Description: Called each frame of drawing.  Can be called at varying
	|	frequencies so good idea to compute elapse time between calls and
	|	scale any animation accordingly.
	|__________________________________________________________________*/

	float drawangle = 0;
	float drawangle2=0;
	float drawangle3=0;
	float dir = 1;
	double dir2 = 1.5;
	double dir3 = 1.8;

	public void onDrawFrame (GL10 gl)
	{
		//long now = System.currentTimeMillis();
		//double elapsed_time = now - mLastTime;

		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Update the angle of rotation of the model
		if (! touching) {
			drawangle += (2 * dir);
			if (drawangle > 360)
				drawangle -= 360;
		}
		if (! touching) {
			drawangle2 += (2 * dir2);
			if (drawangle2 > 360)
				drawangle2 -= 360;
		}
		if (! touching) {
			drawangle3 += (2 * dir3);
			if (drawangle3 > 360)
				drawangle3 -= 360;
		}
		// Draw the rectangle
		gl.glLoadIdentity();
		gl.glTranslatef(0, -19, -65);
		gl.glRotatef(drawangle, 0, 1, 0);
		//gl.glScalef(7, 7, 7);

		gl.glEnable(GL10.GL_NORMALIZE); // must do this if drawing scaling as part of the xform - WARNING: PERFORMANCE PENALTY!
		model.drawVBOs(gl);
		gl.glDisable(GL10.GL_NORMALIZE);

		gl.glLoadIdentity();
		gl.glTranslatef(0, -4, -65);
		gl.glRotatef(drawangle2, 0, 1, 0);
		//gl.glScalef(7, 7, 7);

		gl.glEnable(GL10.GL_NORMALIZE); // must do this if drawing scaling as part of the xform - WARNING: PERFORMANCE PENALTY!
		model.drawVBOs(gl);
		gl.glDisable(GL10.GL_NORMALIZE);

		gl.glLoadIdentity();
		gl.glTranslatef(0, 11, -65);
		gl.glRotatef(drawangle3, 0, 1, 0);
		//gl.glScalef(7, 7, 7);

		gl.glEnable(GL10.GL_NORMALIZE); // must do this if drawing scaling as part of the xform - WARNING: PERFORMANCE PENALTY!
		model.drawVBOs(gl);
		gl.glDisable(GL10.GL_NORMALIZE);
	}

	/*___________________________________________________________________
      |
      | Function: doTouchEvent
      |
      | Description: Handles a touch event.  Returns true if the event was
      |		handled and consumed, else false.
      |
      | CAUTION: This method executes on the UI thread.  If changing any
      |	data used by the drawing thread, you may need to provide mutual
      |	exclusion so both threads don't try to make changes at the same
      |	time.
      |__________________________________________________________________*/
	public boolean doTouchEvent (MotionEvent event)
	{
		// Play a sound effect
		//sound_pull.play(1);

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touching = true;
			sound_laugh.playlooping(1);
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			touching = false;
			sound_laugh.stop();
		}
		return true;
	}

	/*___________________________________________________________________
    |
    | Function: onPause
    |
       | CAUTION: This method executes on the UI thread.  If changing any
      |	data used by the drawing thread, you may need to provide mutual
      |	exclusion so both threads don't try to make changes at the same
      |	time.
    |__________________________________________________________________*/
	public void onPause (boolean isFinishing)
	{
		synchronized(this) {
			if (isFinishing) {
				song1.free();
				sound_laugh.free ();
			}
			//else
			//	Assets.song1.stop ();
		}
	}

	/*___________________________________________________________________
    |
    | Function: onResume
    |
      | CAUTION: This method executes on the UI thread.  If changing any
      |	data used by the drawing thread, you may need to provide mutual
      |	exclusion so both threads don't try to make changes at the same
      |	time.
    |__________________________________________________________________*/
	public void onResume ()
	{
		synchronized(this) {
			song1.play ();
		}
	}
}
