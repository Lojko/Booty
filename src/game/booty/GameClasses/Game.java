package game.booty.GameClasses;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class Game extends Observable implements Observer
{
	private Random m_CoinFlip = new Random();
	private boolean m_FirstGo, m_SwitchMade;
	private int m_CoinGuess, m_PlayerSelection, m_OpponentSelection, m_PlayerFoundBooty, m_OpponentFoundBooty;
	
	private Player m_Player, m_Opponent;
	private GameState m_GameState;
	
	private enum GameState {
		StartGame,
		PlayerTurn,
		OpponentTurn,
		Switch
	}
	
	public Game() {
		m_PlayerFoundBooty = 0;
		m_OpponentFoundBooty = 0;
		m_FirstGo = false;
		m_SwitchMade = false;
		m_Player = new Player();
		m_Opponent = new Player();
		m_GameState = GameState.StartGame;
		m_Opponent.addObserver(this);
	}
	
	public int GetPlayerFoundBooty() {
		return m_PlayerFoundBooty;
	}
	
	public int GetOpponentFoundBooty() {
		return m_OpponentFoundBooty;
	}
	
	//Set the coin guess
	public void setCoinGuess(int CoinGuess) {
		this.m_CoinGuess = CoinGuess; 
	}
	
	public void setPlayerSelection(int playerSelection) {
		m_PlayerSelection = playerSelection;
	}
	
	//Get random value of 1 or 0 for first turn coin flip
	public int getCoinFlip() {
		int flip = m_CoinFlip.nextInt(2);
		
		if(flip == m_CoinGuess)
		{
			m_FirstGo = true;
			m_GameState = GameState.PlayerTurn;
		}
		else
		{
			m_GameState = GameState.OpponentTurn;
		}
		
		return flip;
	}
	
	//Check if player gets first go
	public boolean getFirstGo() {
		return this.m_FirstGo;
	}
	
	public boolean getSwitchMade() {
		return this.m_SwitchMade;
	}
	
	//Opponent AI Selection
	public void makeOpponentSelection() {
		m_Opponent.makeSelection();
	}
	
	//Opponent AI SwitchSelection
	public void makeOpponentSwitchSelection() {
		m_Opponent.makeSwitchSelection();
	}

	//Set the Booty Locations
	public void setPlayerBootyLocations(int[] BootyLocations, int[]TrapLocations) {
		m_Player.SetSelection(BootyLocations, TrapLocations);
	}
	
	public void playerSwitchBooty(int firstSwitchSelection, int secondSwitchSelection) {
		m_Player.SwitchLocations(firstSwitchSelection, secondSwitchSelection);
	}
	
	public void opponentSwitchBooty(int firstSwitchSelection, int secondSwitchSelection) {
		m_Opponent.SwitchLocations(firstSwitchSelection, secondSwitchSelection);
	}
	
	//Check if the player can search for opponents booty
	public boolean playerCanChoose() {
		if(m_GameState == GameState.PlayerTurn)
			return true;
		
		return false;

	}
	
	public boolean playerCanSwitch() {
		if(m_GameState == GameState.Switch)
			return true;
		return false;
	}
	
	public int checkPlayerSelection() {
		int playerFound = m_Opponent.checkSelection(m_PlayerSelection);
		if(playerFound == 2) {
			m_PlayerFoundBooty++;
			if(m_Opponent.hasAI()) {
				m_Opponent.AddToFoundPlayerLocations(m_PlayerSelection);
			}
		}
		
		return playerFound;
	}
	
	
	public int checkOpponentSelection() {
		int opponentFound = m_Player.checkSelection(m_OpponentSelection);
		if(opponentFound == 2) {
			m_OpponentFoundBooty++;
			if(m_Opponent.hasAI()) {
				m_Opponent.AddToFoundOpponentLocations(m_OpponentSelection);
			}
		}
		
		return opponentFound;
	}
	
	public void ChangeTurn() {
		if(m_FirstGo) {
			//if(switchMade)
			//{
				//switchMade = false;
			//}
			
			if(m_GameState == GameState.PlayerTurn) {
				m_GameState = GameState.OpponentTurn;
			}
			else if(m_GameState == GameState.OpponentTurn) {
				m_GameState = GameState.Switch;
			}
			else {
				m_GameState = GameState.PlayerTurn;
			}
		}
		else
		{
			if(m_GameState == GameState.OpponentTurn) {
				m_GameState = GameState.PlayerTurn;
			}
			else if(m_GameState == GameState.PlayerTurn) {
				m_GameState = GameState.Switch;
			}
			else {
				m_GameState = GameState.OpponentTurn;
			}
		}
	}

	//Observer update to Observing class
	public void update(Observable sender, Object selectionNumber)  {
		//If its not finished deciding hold onto value
		if((Integer) selectionNumber != -1) {
			m_OpponentSelection = (Integer)selectionNumber;
		}

		setChanged();
		notifyObservers(selectionNumber);
	}
	
	public void dispose() {
		m_Opponent.dispose();
	}
}
