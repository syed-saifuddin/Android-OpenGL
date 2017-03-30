package com.troden.game.framework.impl;

import android.media.SoundPool;

/*___________________________________________________________________
|
| Class: Sound
|
| Description: Encapsulates functionality for a single sound effect.
|__________________________________________________________________*/
public class Sound 
{
	int soundId;
	SoundPool soundPool;
	int playId;
	
	/*___________________________________________________________________
	|
	| Function: Sound (constructor)
	|__________________________________________________________________*/
	public Sound (SoundPool soundPool, int soundId)
	{
		this.soundId = soundId;
		this.soundPool = soundPool;
	}
	
	/*___________________________________________________________________
	|
	| Function: play
	|
	| Description: Starts playback.
	|__________________________________________________________________*/
	public void play (float volume)
	{
		playId = soundPool.play (soundId, volume, volume, 0, 0, 1);
	}
	
	/*___________________________________________________________________
	|
	| Function: playlooping
	|
	| Description: Starts playback looping forever.
	|__________________________________________________________________*/
	public void playlooping (float volume)
	{
		playId = soundPool.play (soundId, volume, volume, 0, -1, 1);
	}
	
	/*___________________________________________________________________
	|
	| Function: stop
	|
	| Description: Stops playback.
	|__________________________________________________________________*/
	public void stop ()
	{
		soundPool.stop (playId);
	}
	
	/*___________________________________________________________________
	|
	| Function: free
	|
	| Description: Frees the Sound object.
	|__________________________________________________________________*/
	public void free ()
	{
		soundPool.unload (soundId);
	}
}
