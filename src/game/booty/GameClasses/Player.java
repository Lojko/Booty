package game.booty.GameClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Player extends Observable implements Observer
{
	private Random m_GenerateBootyTrapSelection = new Random();
	private int[] m_BootyLocations, m_TrapLocations;
	private PlayerAI m_AI;
	
	public Player()
	{
		m_BootyLocations = new int[3];
		m_TrapLocations = new int[2];
		generateSelection();
	}
	
	//Set location for booty and traps
	public void SetSelection(int[] Booty, int[] Traps)
	{
		this.m_BootyLocations = Booty;
		this.m_TrapLocations = Traps;
	}
	
	public void SwitchLocations(int firstSwitchChoice, int secondSwitchChoice)
	{
		for(int i = 0; i < m_BootyLocations.length; i++)
		{
			if(m_BootyLocations[i] == firstSwitchChoice || m_BootyLocations[i] == secondSwitchChoice)
			{
				if(m_BootyLocations[i] == firstSwitchChoice)
				{
					m_BootyLocations[i] = secondSwitchChoice;
				}
				else
				{
					m_BootyLocations[i] = firstSwitchChoice;
				}
			}
		}
		
		for(int i = 0; i < m_TrapLocations.length; i++)
		{
			if(m_TrapLocations[i] == firstSwitchChoice || m_TrapLocations[i] == secondSwitchChoice)
			{
				if(m_TrapLocations[i] == firstSwitchChoice)
				{
					m_TrapLocations[i] = secondSwitchChoice;
				}
				else
				{
					m_TrapLocations[i] = firstSwitchChoice;
				}
			}
		}
	}
	
	//Generate locations for booty and traps
	private void generateSelection()
	{
		int numbersNeeded = 5;
		List<Integer> generated = new ArrayList<Integer>();
		
		for (int i = 0; i < numbersNeeded; i++)
		{
		    while(true)
		    {
		    	int next = m_GenerateBootyTrapSelection.nextInt(9);
		        if (!generated.contains(next))
		        {
		            // Done for this iteration
		            generated.add(next);
		            
		            if(i < 3)
		            {
		            	m_BootyLocations[i] = next;
		            }
		            else
		            {
		            	m_TrapLocations[i - 3] = next;
		            }
		            
		            break;
		        }
		    }
		}
		
		m_AI = new PlayerAI();
		m_AI.addObserver(this);
	}
	
	//Start AI booty selection
	public void makeSelection()
	{
		m_AI.StartSelection();
	}
	
	//Start AI booty selection
	public void makeSwitchSelection()
	{
		m_AI.StartSwitchSelection();
	}
	
	public int checkSelection(int playerSelection)
	{
		//Check booty
		for(int i = 0; i < m_BootyLocations.length; i++)
		{
			if(m_BootyLocations[i] == playerSelection)
			{
				//Found Booty! remove it from the array
				m_BootyLocations[i] = -1;
				return 2;
			}
		}
		
		//Check traps
		for(int i = 0; i < m_TrapLocations.length; i++)
		{
			if(m_TrapLocations[i] == playerSelection)
			{
				return 1;
			}
		}
		
		//Nothing found
		return 0;
	}
	
	public boolean hasAI()
	{
		if(m_AI != null)
		{
			return true;	
		}
		return false;
	}
	
	public void AddToFoundOpponentLocations(int location)
	{
		m_AI.AddToFoundOpponentLocations(location);
	}
	
	public void AddToFoundPlayerLocations(int location)
	{
		m_AI.AddToFoundPlayerLocations(location);
	}
	
	
	public void dispose()
	{
		m_AI.dispose();
	}

	public void update(Observable sender, Object selectionNumber) 
	{
		setChanged();
		notifyObservers(selectionNumber);
	}
}
