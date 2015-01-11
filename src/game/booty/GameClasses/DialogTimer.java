package game.booty.GameClasses;

import java.util.Observable;

import game.booty.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class DialogTimer extends Observable
{
	private Context m_DialogContext;
	private LayoutInflater m_ParentLayoutInflater;
	private View m_ParentView;
	private CountDownTimer m_DialogTimer;
	private Dialog m_TimerDialog;
	private int m_Time, m_TimeElapsed;
	
	private final int ACHIEVEMENTTIMERTIME = 5000;
	
	public DialogTimer(LayoutInflater parentLayoutInflater, Context toastContext, Context dialogContext, View parentView, int timerTime) {
        m_ParentLayoutInflater = parentLayoutInflater; 
		m_DialogContext = dialogContext;
		m_ParentView = parentView;
		m_Time = timerTime;
		m_TimeElapsed = 0;
	}
	
	public void displayAchievement(String achievementName, int imageID)
	{				
		final Dialog dialogAchievement = new Dialog(m_DialogContext, R.style.DialogTranslateEntranceAnim);
		View layout = m_ParentLayoutInflater.inflate(R.layout.layoutachievement, (ViewGroup)m_ParentView);
		ImageView toastAchievementImage = (ImageView) layout.findViewById(R.id.imageAchievement);
		toastAchievementImage.setImageResource(imageID);
		TextView toastAchievementText = (TextView) layout.findViewById(R.id.textAchievement);
		toastAchievementText.setText(achievementName);
		
		dialogAchievement.setContentView(layout);
		dialogAchievement.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		//Location of the dialog
		dialogAchievement.getWindow().setGravity(Gravity.TOP);
		//Prevent displaying the 'dim background'
		dialogAchievement.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		//Prevent Dialog stopping user to interact with GUI
		dialogAchievement.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
		dialogAchievement.show();
		
    	new Handler().postDelayed(new Runnable() { 
    		public void run() { 
    			dialogAchievement.dismiss();
	    	}
		}, ACHIEVEMENTTIMERTIME);
	}
	
	public void startTimer()
	{
		m_TimerDialog = new Dialog(m_DialogContext);
		final View dialogLayout = m_ParentLayoutInflater.inflate(R.layout.layouttimer, (ViewGroup)m_ParentView);
		
        int backgroundImage = R.drawable.icontimer;
        if(m_Time > 20)
        {
        	backgroundImage = R.drawable.icontimerswitch;
        }

        ((ImageView) dialogLayout.findViewById(R.id.imageTimerBackground)).setImageResource(backgroundImage);
        
        constructNewDialog(Gravity.BOTTOM);
        m_TimerDialog.setContentView(dialogLayout);
        m_TimerDialog.show();
        
		m_DialogTimer = new CountDownTimer(((m_Time + 1) * 1000), 1000)
		{
			TextView dialogTimerText = (TextView) dialogLayout.findViewById(R.id.textTimer);
			ImageView background = (ImageView) dialogLayout.findViewById(R.id.imageTimerBackground);
			ImageView timerUp = (ImageView) dialogLayout.findViewById(R.id.imageTimerUp);
			
			public void onTick(long millisUntilFinished)
			{
				dialogTimerText.setText(Integer.toString(m_Time));
				if(m_Time != 0)
				{
					m_Time--;
				}
			};
			public void onFinish()
			{				
				timerUp.setVisibility(View.VISIBLE);
				background.setVisibility(View.INVISIBLE);
				dialogTimerText.setText("");
				timerUp.startAnimation(AnimationUtils.loadAnimation(m_DialogContext, R.anim.animationviewscaleup));
		    	new Handler().postDelayed(new Runnable() 
		    	{ 
		    		public void run() 
		    		{ 
		    			m_TimerDialog.dismiss();
		    			update();
			    	}
				}, 600);
			};
		}.start();
	}
	
	
	public void switchUpdate()
	{		    	
		m_DialogTimer.cancel();
		m_TimerDialog.dismiss();
		m_TimerDialog = new Dialog(m_DialogContext);
		
		constructNewDialog(Gravity.BOTTOM);
		
		View layout = m_ParentLayoutInflater.inflate(R.layout.layouttimer, (ViewGroup)m_ParentView);
		ImageView timerInterrupt = ((ImageView) layout.findViewById(R.id.imageTimerChange)); 
		timerInterrupt.setVisibility(View.VISIBLE);
		
        m_TimerDialog.setContentView(layout);
        m_TimerDialog.show();
        
        timerInterrupt.startAnimation(AnimationUtils.loadAnimation( m_DialogContext, R.anim.animationviewscaleup));
		
    	new Handler().postDelayed(new Runnable() 
    	{ 
    		public void run() 
    		{ 
    			m_TimerDialog.dismiss();
    			m_Time = m_Time - m_TimeElapsed;
    			if(m_Time > 8)
    			{
    				m_Time = (m_Time / 2);
    			}
    			startTimer();
	    	}
		}, 800);
	}
	
	public void stopTimer()
	{
		m_DialogTimer.cancel();
		m_TimerDialog.dismiss();
	}
	
	public void timerDispose()
	{
		stopTimer();
		m_DialogTimer = null;
	}
	
	//Observer update to Observing class
	public void update() 
	{
		setChanged();
		notifyObservers();
	}
	
	private void constructNewDialog(int gravity)
	{
		m_TimerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        m_TimerDialog.getWindow().setGravity(gravity);
		//Prevent displaying the 'dim background'
        m_TimerDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		//Prevent Dialog stopping user to interact with GUI
        m_TimerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        m_TimerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
}
