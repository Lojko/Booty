package game.booty.GameClasses;

import game.booty.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.SparseIntArray;

/**
 * Class which is responsible for loading and playing the sound effect that are used
 * througout the Booty game. The class is a singleton instance and it loads the initial
 * sounds that are required by the main menu screen.
 * 
 * @author Michael Lojko
 */
public class Sound 
{
	private SoundPool m_SoundEffects;
	private MediaPlayer m_SoundBackground;
	private SparseIntArray m_SoundPoolMap;
	private static Context m_Context;
	
	private static Sound instance = null;
	
	private Sound(Context context) {
		m_SoundEffects = new SoundPool (4, AudioManager.STREAM_MUSIC, 0);
		m_SoundPoolMap = new SparseIntArray();
		m_Context = context;
	}
	
	public static Sound getInstance(Context context) {
		if(instance == null) {
			instance = new Sound(context);
		}
		else {
			m_Context = context;
		}
		
		return instance;
	}
	
	public void loadSplashSounds() {
		m_SoundBackground = MediaPlayer.create(m_Context, R.raw.oceanwaves);
		m_SoundPoolMap.put(0, m_SoundEffects.load(m_Context, R.raw.cannonfire, 1));	
	}
	          
	public void PlaySplashBackgroundSound() {
		//m_SoundBackground.start();
		//m_SoundBackground.setLooping(true);
	}
	
	public void PlaySplashButtonPress() {
		m_SoundEffects.play(m_SoundPoolMap.get(0), 1.0f, 1.0f, 1, 0, 1.0f);
	}
	
	/**
	 * Function which loads the sounds required by the {@
	 */
	public void loadHowToPlaySound() {
		
	}
	
	public void loadSetupSounds() {
		
	}
	
	public void loadGameSounds() {
		
	}
	
	public void clearSounds() {
		
	}
}
