package com.troden.game.framework.impl;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

/*___________________________________________________________________
|
| Class: GameAudio
|
| Description: Manager for music and sound effects.
|__________________________________________________________________*/
public class GameAudio 
{
	Context mContext;
	AssetManager assets;
	SoundPool soundPool;
	SpeechSynthesizer speechSynthesizer;
	
	/*___________________________________________________________________
	|
	| Function: GameAudio (constructor)
	|__________________________________________________________________*/
	public GameAudio (Context context, int max_sounds)
	{
		// Save context
		mContext = context;
		// Set volume control to the media stream
		//context.setVolumeControlStream (AudioManager.STREAM_MUSIC);
		this.assets = context.getAssets();
		this.soundPool = new SoundPool (max_sounds, AudioManager.STREAM_MUSIC, 0);
	}
	
	/*___________________________________________________________________
	|
	| Function: newMusic
	|
	| Description: Loads a music file for playback.
	|__________________________________________________________________*/
	public Music newMusic (String filename)
	{
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			return new Music(assetDescriptor);
		} catch (IOException e) {
			throw new RuntimeException("initMusic(): Error loading music file: " + filename);
		}
	}
	
	/*___________________________________________________________________
	|
	| Function: newSound
	|
	| Description: Loads a sound file for playback.
	|__________________________________________________________________*/
	public Sound newSound (String filename)
	{
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			int soundId = soundPool.load(assetDescriptor, 0);
			return new Sound(soundPool, soundId);
		} catch (IOException e) {
			throw new RuntimeException("initSound(): Error loading sound file: " + filename);
		}
	}
	/*___________________________________________________________________
	|
	| Function: startSpeechSynthesizer
	|
	| Description: Starts text to speech engine.
	|__________________________________________________________________*/
	public void startSpeechSynthesizer ()
	{
		if (speechSynthesizer == null) 
			speechSynthesizer = new SpeechSynthesizer (mContext);
	}
	
	/*___________________________________________________________________
	|
	| Function: stopSpeechSynthesizer
	|
	| Description: Starts text to speech engine.
	|__________________________________________________________________*/
	public void stopSpeechSynthesizer ()
	{
		if (speechSynthesizer != null) { 
			speechSynthesizer.free ();
			speechSynthesizer = null;
		}
	}
	
	
	
}
