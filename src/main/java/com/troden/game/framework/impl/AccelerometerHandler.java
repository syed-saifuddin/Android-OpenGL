
package com.troden.game.framework.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/*___________________________________________________________________
|
| Class: AccelerometerHandler
|
| Description: Manager for accelerometer input.
|__________________________________________________________________*/
public class AccelerometerHandler implements SensorEventListener
{
	float accelX, accelY, accelZ;
	boolean accelAvailable;
	boolean accelEnabled;
	SensorManager manager;
	Sensor accelerometer;
	
	/*___________________________________________________________________
	|
	| Function: AccelerometerHandler (constructor)
	|__________________________________________________________________*/
	public AccelerometerHandler (Context context) 
	{
		// Initialize accel values to default (device stationary)
		accelX = 0;
		accelY = 0;
		accelZ = -1;
		// Get handle to the sensor manager
		manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		// Any accelerometer in the system?
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
			// Get the first one
			accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			accelAvailable = true;
			accelEnabled   = false;
		}
	}
	
	/*___________________________________________________________________
	|
	| Function: available
	|
	| Description: Returns true if accelerometer is available.
	|__________________________________________________________________*/
	public boolean available ()
	{
		return (accelAvailable);
	}
	
	/*___________________________________________________________________
	|
	| Function: enableAccelProcessing
	|
	| Description: Enables/disables accelerometer processing.
	|__________________________________________________________________*/
	public void enableAccelProcessing ()
	{
		if (accelAvailable) {
			// Enable it
			if (!accelEnabled) {
				manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
				accelEnabled = true;
			}
			// Disable it
			else {
				manager.unregisterListener(this, accelerometer);
				accelEnabled = false;
			}
		}
	}
		
	/*___________________________________________________________________
	|
	| Function: onAccuracyChanged
	|
	| Description: Stub function.
	|__________________________________________________________________*/
	@Override
	public void onAccuracyChanged (Sensor sensor, int accuracy) {}
	
	/*___________________________________________________________________
	|
	| Function: onSensorChanged
	|
	| Description: Triggered on sensor changed.
	|__________________________________________________________________*/
	@Override
	public void onSensorChanged (SensorEvent event)
	{
		accelX = event.values[0];
		accelY = event.values[1];
		accelZ = event.values[2];
	}
	
	/*___________________________________________________________________
	|
	| Function: getAccelX
	|
	| Description: Returns current X-axis acceleration.
	|__________________________________________________________________*/
	public float getAccelX ()
	{
		return (accelX);
	}
	
	/*___________________________________________________________________
	|
	| Function: getAccelY
	|
	| Description: Returns current Y-axis acceleration.
	|__________________________________________________________________*/
	public float getAccelY ()
	{
		return (accelY);
	}

	/*___________________________________________________________________
	|
	| Function: getAccelZ
	|
	| Description: Returns current Z-axis acceleration.
	|__________________________________________________________________*/
	public float getAccelZ ()
	{
		return (accelZ);
	}
}
