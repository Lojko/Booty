package game.booty.GameClasses;

import java.util.Observable;
import java.util.Random;

import android.os.CountDownTimer;
import android.os.Handler;

public class PlayerAI extends Observable
{
	private Random m_RandomNumberGenerator;
	private int m_RandSelectionTime, m_RandTotalSelectionTime;
	private int[] m_FoundOpponentLocations, m_FoundPlayerLocations;
	private CountDownTimer m_SelectionGeneratorTimer;
	private final int MINSELECTIONTIME = 5;
	private final int MAXSELECTIONTIME = 20;
	
	//Representing a value to be less than for 0-9 random number gen (e.g. 6 is 70%)
	private final int SWITCHCHANCE = 7;
	private final int MAXSWITCHCHANCEDECISIONTIME = 5;
	private final int MAXNOSWITCHDECISIONTIME = 15;
	
	public PlayerAI() {
		m_RandomNumberGenerator = new Random();
		m_FoundOpponentLocations = new int[] {-1, -1};
		m_FoundPlayerLocations = new int[] {-1, -1};
	}
	
	/*Start the opponent AI's random selection process
	 * Initial selection is a random 0-8 integer generation,
	 * A random total 'time to take' selecting a position is generated between MAX and MINTIME (30 and 7 seconds)
	 * A random time to take making an individual selection is generated between the total time and 1
	 * The random selection time is subtracted from the total time
	 * Multiply the selection time by 1000 to change to milliseconds*/
	public void StartSelection() {
		ChoiceSelection(m_RandomNumberGenerator.nextInt(9));
		m_RandTotalSelectionTime = m_RandomNumberGenerator.nextInt(MAXSELECTIONTIME - MINSELECTIONTIME) + MINSELECTIONTIME;
		m_RandSelectionTime = m_RandomNumberGenerator.nextInt(m_RandTotalSelectionTime - 1) + 1;
		m_RandTotalSelectionTime -= m_RandSelectionTime;
		m_RandSelectionTime *= 1000;
		new CountDownTimer(m_RandSelectionTime, m_RandSelectionTime)
		{
			public void onTick(long millisUntilFinished){};
			public void onFinish()
			{
				ChoiceSelection(m_RandomNumberGenerator.nextInt(9));
				if(m_RandTotalSelectionTime >= 2)
				{
					ChangeSelection();
				}
			};
		}.start();
	}
	
	//Recursive change selection method
	private void ChangeSelection() {
		m_RandSelectionTime = m_RandomNumberGenerator.nextInt(m_RandTotalSelectionTime - 1) + 1;
		m_RandTotalSelectionTime -= m_RandSelectionTime;
		m_RandSelectionTime *= 1000;
		m_SelectionGeneratorTimer = new CountDownTimer(m_RandSelectionTime, m_RandSelectionTime)
		{
			public void onTick(long millisUntilFinished){};
			public void onFinish()
			{
				ChoiceSelection(m_RandomNumberGenerator.nextInt(9));
				if(m_RandTotalSelectionTime >= 2)
				{
					ChangeSelection();
				}
				else
				{
					ChoiceSelection(-1);
				}
			};
		}.start();
	}
	
	public void ChoiceSelection(int Selection) {
		if(Selection != -1)
		{
			//Ensure that a selection is made on a location that has been removed
			while(Selection == m_FoundOpponentLocations[0] || Selection == m_FoundOpponentLocations[1])
			{
				Selection = m_RandomNumberGenerator.nextInt(9);
			}
		}
		setChanged();
		notifyObservers(Selection);
	}
	
	//Select two available places to switch
	public void StartSwitchSelection() {
		//Random 1-5 second decision time
    	new Handler().postDelayed(new Runnable() 
    	{ 
    		public void run() 
    		{ 
    			//Decide whether or not a switch is actually going to happen
    			if(m_RandomNumberGenerator.nextInt(10) < SWITCHCHANCE)
    			{
    				//Make first random selection
    				int firstSelection = m_RandomNumberGenerator.nextInt(9);
    				checkSwitchSelection(firstSelection);
    				setChanged();
    				notifyObservers(firstSelection);
    				
    				//Make second random selection after 1-5 seconds
    		    	new Handler().postDelayed(new Runnable() 
    		    	{ 
    		    		public void run() 
    		    		{ 
    	    				int secondSelection = m_RandomNumberGenerator.nextInt(9);
    	    				checkSwitchSelection(secondSelection);
    	    				setChanged();
    	    				notifyObservers(secondSelection);
    	    				setChanged();
    	    				//Ready
    	    				notifyObservers(-1);
    			    	}
    				}, m_RandomNumberGenerator.nextInt((MAXSWITCHCHANCEDECISIONTIME + 1) * 1000));
    			}
    			else
    			{
    				//Make second random selection after 1-5 seconds
    				new Handler().postDelayed(new Runnable() 
    				{ 
    					public void run() 
    					{ 
    						setChanged();
    						notifyObservers(-1);
    				    }
    			    }, m_RandomNumberGenerator.nextInt((MAXNOSWITCHDECISIONTIME + 1) * 1000));
    			}
	    	}
    	}, m_RandomNumberGenerator.nextInt((MAXSWITCHCHANCEDECISIONTIME + 1) * 1000));
	}
	
	private int checkSwitchSelection(int selection) {
		//Ensure that a selection is made on a location that has been removed
		while(selection == m_FoundPlayerLocations[0] || selection == m_FoundPlayerLocations[1]) {
			selection = m_RandomNumberGenerator.nextInt(9);
		}
		
		return selection;
	}
	
	public void AddToFoundOpponentLocations(int location) {
		if(m_FoundOpponentLocations[0] == -1) {
			m_FoundOpponentLocations[0] = location;
		}
		else {
			m_FoundOpponentLocations[1] = location;
		}
	}
	
	public void AddToFoundPlayerLocations(int location) {
		if(m_FoundPlayerLocations[0] == -1) {
			m_FoundPlayerLocations[0] = location;
		}
		else {
			m_FoundPlayerLocations[1] = location;
		}
	}
	
	public void dispose() {
		if(m_SelectionGeneratorTimer != null) {
			m_SelectionGeneratorTimer.cancel();
		}
	}
}
