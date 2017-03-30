package com.troden.game.framework.impl;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

/*___________________________________________________________________
|
| Class: Music
|
| Description:
|__________________________________________________________________*/
public class Music implements OnCompletionListener
{
	MediaPlayer mediaPlayer;
	boolean isPrepared = false;
	
	/*___________________________________________________________________
	|
	| Function: Music (constructor)
	|
	| Description: Creates a new MediaPlayer object and loads a file
	|	for playback.
	|__________________________________________________________________*/
	public Music (AssetFileDescriptor assetDescriptor)
	{
		mediaPlayer = new MediaPlayer ();
		try {
			mediaPlayer.setDataSource (assetDescriptor.getFileDescriptor(),
									   assetDescriptor.getStartOffset(),
									   assetDescriptor.getLength());
			mediaPlayer.prepare ();
			isPrepared = true;
			mediaPlayer.setOnCompletionListener(this);
		} catch (Exception e) {
			throw new RuntimeException ("Music(): Error loading music");
		}
	}
	
	/*___________________________________________________________________
	|
	| Function: free
	|
	| Description: Frees the Music object.
	|__________________________________________________________________*/
	public void free ()
	{
		if (mediaPlayer.isPlaying())
			mediaPlayer.stop();
		mediaPlayer.release();
	}
	
	/*___________________________________________________________________
	|
	| Function: isPlaying
	|
	| Description: Returns true if music is playing.
	|__________________________________________________________________*/
	public boolean isPlaying ()
	{
		return mediaPlayer.isPlaying ();
	}

	/*___________________________________________________________________
	|
	| Function: is Looping
	|
	| Description: Returns true if music is set to loop.
	|__________________________________________________________________*/
	public boolean isLooping ()
	{
		return mediaPlayer.isLooping ();
	}
	
	/*___________________________________________________________________
	|
	| Function: isStopped
	|
	| Description: Returns true if MediaPlayer object is stopped.
	|__________________________________________________________________*/
	public boolean isStopped ()
	{
		return (!isPrepared);
	}

	/*___________________________________________________________________
	|
	| Function: play
	|
	| Description: Starts playback.
	|__________________________________________________________________*/
	public void play ()
	{
		if (! mediaPlayer.isPlaying()) {
			try {
				synchronized(this) {
					if (!isPrepared) {
						mediaPlayer.prepare();
						isPrepared = true;	// pretty sure need to add this
					}
					mediaPlayer.start ();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*___________________________________________________________________
	|
	| Function: setLooping
	|
	| Description: Sets music to loop or not loop.
	|__________________________________________________________________*/
	public void setLooping (boolean isLooping)
	{
		mediaPlayer.setLooping(isLooping);
	}
	
	/*___________________________________________________________________
	|
	| Function: setVolume
	|
	| Description: Sets playback volume:
	|				0 = not audible
	|				1 = full volume
	|__________________________________________________________________*/
	public void setVolume (float volume)
	{
		// Make sure between 0-1
		if (volume < 0)
			volume = 0;
		else if (volume > 1)
			volume = 1;
		mediaPlayer.setVolume(volume, volume);
	}

	/*___________________________________________________________________
	|
	| Function: stop
	|
	| Description: Stops playback if playing.
	|__________________________________________________________________*/
	public void stop ()
	{
		mediaPlayer.stop();
		synchronized(this) {
			isPrepared = false;
		}
	}
	
	/*___________________________________________________________________
	|
	| Function: onCompletion
	|
	| Description: Callback function for onCompletionListener interface.
	|	Called when playback finishes.
	|__________________________________________________________________*/
	public void onCompletion (MediaPlayer player)
	{
		synchronized(this) {
			isPrepared = false;
		}
	}
	
}
