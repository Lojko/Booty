package game.booty;

import game.booty.GameClasses.Achievements;
import game.booty.GameClasses.Sound;
import game.booty.GameClasses.DialogTimer;
import game.booty.GameClasses.ViewAnimation;
import game.booty.GameClasses.ViewAnimation.Direction;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import android.view.View.OnTouchListener;

/**
 * 
 * @author Michael Lojko
 */
public class BootySplashActivity extends Activity implements OnTouchListener 
{	
    private ViewFlipper m_SplashFlipper;
    private ViewAnimation m_ViewAnimationGenerator;
    private float m_DownY;
    private final int SCRNSPLASH = 0;
    private final int SCRNCREDITS = 1;
    private final int SCRNCHOOSEGAME = 2;
    
    private DialogTimer m_ToastTimer;
    private Sound m_GameAudio;
    private Achievements m_Achievements;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        //Set the Activity to full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set the main screen
        setContentView(R.layout.layoutsplash);
        
        //Set the flipper with both screens for animation
        m_SplashFlipper = (ViewFlipper) findViewById(R.id.flipperTransitional);
        m_SplashFlipper.addView(View.inflate(this, R.layout.layoutcredits, null), SCRNCREDITS);
        m_SplashFlipper.addView(View.inflate(this, R.layout.layoutchoosegame, null), SCRNCHOOSEGAME);
        
        m_GameAudio = Sound.getInstance(getApplicationContext());
        m_GameAudio.loadSplashSounds();
 
        //Hook the on touch listener to both layouts
        RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.SplashScreen);
        splashLayout.setOnTouchListener((OnTouchListener) this);
        
        RelativeLayout creditsLayout = (RelativeLayout) findViewById(R.id.CreditsScreen);
        creditsLayout.setOnTouchListener((OnTouchListener) this);
        
        RelativeLayout menuLayout = (RelativeLayout) findViewById(R.id.ChooseGameScreen);
        menuLayout.setOnTouchListener((OnTouchListener) this);
                
        m_ViewAnimationGenerator = new ViewAnimation();
        m_GameAudio.PlaySplashBackgroundSound();
        
        m_Achievements = Achievements.getInstance(getApplicationContext());
        m_Achievements.open();
        
        m_ToastTimer = new DialogTimer(getLayoutInflater(), getApplicationContext(), this, findViewById(R.id.TimerAchievement), 0);
    }
    
    public boolean onTouch(View arg0, MotionEvent arg1) 
    {
        // Get the action that was done on this touch event
        switch (arg1.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                // store the Y value when the user's finger was pressed down
                m_DownY = arg1.getY();
                break;
            }

            case MotionEvent.ACTION_UP:
            {
                // Get the X value when the user released his/her finger
                float currentY = arg1.getY();

                // pull up
                if (m_DownY < currentY && m_SplashFlipper.getDisplayedChild() == SCRNCREDITS)
                {
                	switchToSplashScreen();
                }

                // pull down
                else if (m_DownY > currentY && m_SplashFlipper.getDisplayedChild() == SCRNSPLASH)
                {
                	switchToCreditsScreen();
                }
                
                // pull down
                else if (m_DownY > currentY && m_SplashFlipper.getDisplayedChild() == SCRNCHOOSEGAME)
                {
                	returnToSplashScreen();
                }
                
                break;
            }
        }
        
        return true;
    }
    
    //Credit Button function
    public void buttonChooseGameScreenSwitch(View v)
    {
    	m_SplashFlipper.setInAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.IN, Direction.TOP, 500, Animation.RELATIVE_TO_PARENT));
        m_SplashFlipper.setOutAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.OUT, Direction.BOTTOM, 500, Animation.RELATIVE_TO_PARENT));
        m_SplashFlipper.setDisplayedChild(SCRNCHOOSEGAME);
        
        //Play sound
        m_GameAudio.PlaySplashButtonPress();
    }
    
    //Credit Button function
    public void creditsScreenSwitch_Click(View v)
    {
    	switchToCreditsScreen();
    	m_GameAudio.PlaySplashButtonPress();
    }
    
    //Return to splash screen from game menu
    public void returnToSplashScreen()
    {
        m_SplashFlipper.setInAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.IN, Direction.BOTTOM, 500, Animation.RELATIVE_TO_PARENT));
        m_SplashFlipper.setOutAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.OUT, Direction.TOP, 500, Animation.RELATIVE_TO_PARENT));
        m_SplashFlipper.setDisplayedChild(SCRNSPLASH);
    }
    
    //Credit screen transition
    public void switchToSplashScreen()
    {
    	m_SplashFlipper.setInAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.IN, Direction.TOP, 500, Animation.RELATIVE_TO_PARENT));
    	m_SplashFlipper.setOutAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.OUT, Direction.BOTTOM, 500, Animation.RELATIVE_TO_PARENT));
        m_SplashFlipper.setDisplayedChild(SCRNSPLASH);	
    }
    
    //Splash screen transition
    public void switchToCreditsScreen()
    {
        m_SplashFlipper.setInAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.IN, Direction.BOTTOM, 500, Animation.RELATIVE_TO_PARENT));
        m_SplashFlipper.setOutAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.OUT, Direction.TOP, 500, Animation.RELATIVE_TO_PARENT));
        m_SplashFlipper.setDisplayedChild(SCRNCREDITS);
    }
    
    public void startGameSetup_Click(View v)
    {
    	m_GameAudio.PlaySplashButtonPress();
    	Intent setupGame = new Intent(this, BootySetupGameActivity.class);
    	startActivity(setupGame);
    }
    
    public void howToPlay_Click(View v)
    {
    	m_GameAudio.PlaySplashButtonPress();
    	String achievementImage = m_Achievements.incrementAchievement("Ready to Booty!");
    	if(achievementImage != null)
    	{
    		m_ToastTimer.displayAchievement("Ready to Booty!", getResources().getIdentifier(achievementImage, "drawable", "game.booty"));
    	}
    	
		//Intent setupGame = new Intent(this, BootySetupGameActivity.class);
		//startActivity(setupGame);
    }
    
    @Override
    public void onBackPressed() 
    {
    	AlertDialog backDialog = new AlertDialog.Builder(this)
    	.setView(getLayoutInflater().inflate(R.layout.layoutdialog, (ViewGroup)findViewById(R.id.DialogLayout)))
    	.setPositiveButton(R.string.quitDialog, new DialogInterface.OnClickListener() 
    	{
            public void onClick(DialogInterface dialog, int id) 
            {
            	closeGame();
            }
    	})
    	.setNegativeButton(R.string.cancelDialog, new DialogInterface.OnClickListener() 
    	{
            public void onClick(DialogInterface dialog, int id) 
            {
                // sign in the user ...
            }
    	}).show();
    	//Set x and y location
    	backDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,400);
    	//Set background to transparent
    	backDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
    
    private void closeGame()
    {
    	this.finish();
    }
}