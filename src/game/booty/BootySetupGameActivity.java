package game.booty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Michael Lojko
 */
public class BootySetupGameActivity extends Activity 
{
	private int m_SetBooty;
	private int m_SetTraps;
	
	private ImageButton[] m_ButtonArray;
	private int[] m_SelectedBooty;
	private int[] m_SelectedTraps;
	
	public enum SetupState
	{
		SetBooty,
		SetTraps
	}
	
	private SetupState m_CurrentState = SetupState.SetBooty;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        //Set the Activity to full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        //Custom font?
        //applyFont(this, findViewById(R.id.SetBootyScreen), "assets/fonts/PirateFont.ttf");

        //Set the main screen
        setContentView(R.layout.layoutsetupgame);
        displayReadyButton(View.GONE);
        displayTraps(View.GONE);
        setupArrays();
    }
    
    //Credit Button function
    public void setup_Click(View v)
    {
		if(((ImageButton)v).getTag() != null)
		{
			return;
		}
		
		int clickedButton = -1;
    	
		//Find the button
		for(int i = 0; i < m_ButtonArray.length; i++)
		{
			if(m_ButtonArray[i] == (ImageButton)v)
			{
				clickedButton = i;
				break;
			}
		}
		
    	if(m_CurrentState == SetupState.SetBooty)
    	{
    		if(m_SelectedBooty[m_SetBooty] > -1)
    		{
    			m_ButtonArray[m_SelectedBooty[m_SetBooty]].setImageResource(0);
    			m_ButtonArray[m_SelectedBooty[m_SetBooty]].setTag(null);
    		}
    		
    		m_SelectedBooty[m_SetBooty] = clickedButton;
			m_ButtonArray[m_SelectedBooty[m_SetBooty]].setImageResource(R.drawable.iconchest);
			m_ButtonArray[m_SelectedBooty[m_SetBooty]].setTag("Booty");
			m_SetBooty++;
			
    		if(m_SetBooty == 1)
    		{
    			ImageView bootyImage = (ImageView)this.findViewById(R.id.iconfirstsetupbooty);
    			bootyImage.setImageResource(R.drawable.iconusedchest);
    		}
    		else if(m_SetBooty == 2)
    		{
    			ImageView bootyImage = (ImageView)this.findViewById(R.id.iconsecondsetupbooty);
    			bootyImage.setImageResource(R.drawable.iconusedchest);
    		}
    		else
    		{
    			m_SetBooty = 0;
    			displayReadyButton(View.VISIBLE);
    		}
    	}
    	else if(m_CurrentState == SetupState.SetTraps)
    	{
    		if(m_SelectedTraps[m_SetTraps] > -1)
    		{
    			m_ButtonArray[m_SelectedTraps[m_SetTraps]].setImageResource(0);
    			m_ButtonArray[m_SelectedTraps[m_SetTraps]].setTag(null);
    		}
    		
    		m_SelectedTraps[m_SetTraps] = clickedButton;
			m_ButtonArray[m_SelectedTraps[m_SetTraps]].setImageResource(R.drawable.icontrap);
			m_ButtonArray[m_SelectedTraps[m_SetTraps]].setTag("Traps");
			m_SetTraps++;
			
    		if(m_SetTraps == 1)
    		{
    			ImageView trapImage = (ImageView)this.findViewById(R.id.iconfirstsetuptrap);
    			trapImage.setImageResource(R.drawable.iconusedtrap);
    		}
    		else
    		{
    			m_SetTraps = 0;
    			displayReadyButton(View.VISIBLE);
    		}
    	}
    }
    
    public void readyButton_Click(View v)
    {
    	if(m_CurrentState == SetupState.SetBooty)
    	{
    		m_CurrentState = SetupState.SetTraps;
    		TextView textSetupInfo = (TextView)this.findViewById(R.id.textSetupInfo);
    		textSetupInfo.setText(R.string.setTraps);
    		displayReadyButton(View.GONE);
    		hideBooty();
            displayTraps(View.VISIBLE);
    	}
    	else
    	{
    		
    		//Start Game
        	Intent startGame = new Intent(this, BootyGameActivity.class);
        	
        	//Pass the location of booty and trap to the next Activity
        	startGame.putExtra("BootyLocations", m_SelectedBooty);
        	startGame.putExtra("TrapLocations", m_SelectedTraps);
        	
        	//Start Activity
        	startActivity(startGame);
        	
        	//End this one
        	this.finish();
    	}
    }
    
    private void displayReadyButton(int visibility)
    {
    	ImageButton ready = (ImageButton)this.findViewById(R.id.buttonSetupReady);
    	ready.setVisibility(visibility);
    }
    
    private void displayTraps(int visibility)
    {
    	ImageView icon = (ImageView)this.findViewById(R.id.iconfirstsetuptrap);
    	icon.setVisibility(visibility);
    	icon = (ImageView)this.findViewById(R.id.iconsecondsetuptrap);
    	icon.setVisibility(visibility);
    }
    
    private void hideBooty()
    {
    	ImageView icon = (ImageView)this.findViewById(R.id.iconfirstsetupbooty);
    	icon.setVisibility(View.GONE);
    	icon = (ImageView)this.findViewById(R.id.iconsecondsetupbooty);
    	icon.setVisibility(View.GONE);
    	icon = (ImageView)this.findViewById(R.id.iconthirdsetupbooty);
    	icon.setVisibility(View.GONE);
    }
    
    private void setupArrays()
    {
        m_ButtonArray = new ImageButton[9];
        m_ButtonArray[0] =  (ImageButton)this.findViewById(R.id.buttonSetupGridOne);
        m_ButtonArray[1] =  (ImageButton)this.findViewById(R.id.buttonSetupGridTwo);
        m_ButtonArray[2] =  (ImageButton)this.findViewById(R.id.buttonSetupGridThree);
        m_ButtonArray[3] =  (ImageButton)this.findViewById(R.id.buttonSetupGridFour);
        m_ButtonArray[4] =  (ImageButton)this.findViewById(R.id.buttonSetupGridFive);
        m_ButtonArray[5] =  (ImageButton)this.findViewById(R.id.buttonSetupGridSix);
        m_ButtonArray[6] =  (ImageButton)this.findViewById(R.id.buttonSetupGridSeven);
        m_ButtonArray[7] =  (ImageButton)this.findViewById(R.id.buttonSetupGridEight);
        m_ButtonArray[8] =  (ImageButton)this.findViewById(R.id.buttonSetupGridNine);
        
        m_SelectedBooty = new int[3];
        
        for(int i = 0; i < m_SelectedBooty.length; i++)
        {
        	m_SelectedBooty[i] = -1;
        }
        
        m_SelectedTraps = new int[2];
        
        for(int i = 0; i < m_SelectedTraps.length; i++)
        {
        	m_SelectedTraps[i] = -1;
        }
    }
    
    @Override
    public void onBackPressed() 
    {
    	this.finish();
    }
    
    public static void applyFont(final Context context, final View root, final String fontName) 
    {
        try {
        	if (root instanceof TextView)
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontName));
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
