package game.booty;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import game.booty.GameClasses.Game;
import game.booty.GameClasses.DialogTimer;
import game.booty.GameClasses.ViewAnimation;
import game.booty.GameClasses.ViewAnimation.Direction;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;

/**
 * Main game activity, other activities are executed and displayed off of this one
 * @author Michael Lojko
 */
public class BootyGameActivity extends Activity implements OnTouchListener, Observer
{
	//Game, animation, coinflip and toast member variable managers
	private Game m_Game;
    private ViewAnimation m_ViewAnimationGenerator;
    private FlipCoin handler;
    private DialogTimer m_TurnTimer;
    
    //Grid arrays
    private ImageView[] m_PlayerGridArray, m_OpponentGridArray;
    
    //Game text info, changed throughout the game
    private TextView m_PlayerTextGameInfo, m_OpponentTextGameInfo;
    
    //ViewFlipper Switch Constants
    private ViewFlipper m_GameFlipper;
    private final int SCRNPLAYER = 0;
    private final int SCRNOPPONENT = 1;
    
    //Game string arrays
    private String[] m_PlayerTurnText, m_OpponentTurnText, m_SwitchTurnText;
   
    //Primitives
    private float m_DownY;
    private int m_CurrentOpponentSelectedLocation, m_CurrentPlayerSelectedLocation, m_FirstPlayerSwitchSelection, m_SecondPlayerSwitchSelection,
    			m_FirstOpponentSwitchSelection;
    private boolean m_FirstSwitchSelected;
    
    private final int SWITCHTIMERTIME = 40;
	private final int COUNTDOWNTIMERTIME = 20;
    
    /** Called when the activity is first created. Initialize all variables to default state. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Create Game and retrieve Booty/Trap locations
        m_Game = new Game();
        m_Game.addObserver(this);
        
        Bundle locations = getIntent().getExtras();
        if(locations != null) {
        	m_Game.setPlayerBootyLocations(locations.getIntArray("BootyLocations"), locations.getIntArray("TrapLocations"));
        }
        
        //Set the Activity to full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.layoutgameplayer);
        
        m_GameFlipper = (ViewFlipper) findViewById(R.id.flipperTransitionalGame);
		m_GameFlipper.addView(View.inflate(this, R.layout.layoutgameopponent, null), SCRNOPPONENT);
        
		m_OpponentTextGameInfo = (TextView)this.findViewById(R.id.opponentTextGameInfo);
    	m_PlayerTextGameInfo = (TextView)this.findViewById(R.id.playerTextGameInfo);
    	m_PlayerTextGameInfo.setText(R.string.chooseCoin);
    	setupPlayerArray();
    	setupOpponentArray();
        m_Game.addObserver(this);
        
        //Default selection variables
        m_CurrentOpponentSelectedLocation = -1;
        m_CurrentPlayerSelectedLocation = -1;
        m_FirstPlayerSwitchSelection = -1;
        m_SecondPlayerSwitchSelection = -1;
        m_FirstOpponentSwitchSelection = -1;
        m_FirstSwitchSelected = false;
        
        m_PlayerTurnText = getResources().getStringArray(R.array.playerTurn);
        m_OpponentTurnText = getResources().getStringArray(R.array.opponentTurn);
        m_SwitchTurnText = getResources().getStringArray(R.array.switchTurn);
    }
    
    private void startGame() {
    	if(!m_Game.getFirstGo())
    	{
    		m_Game.makeOpponentSelection();
    	}
    	startTurnTimer(COUNTDOWNTIMERTIME);
    }
    
    private void displayPlayerReadyButton(int Visibility) {
    	ImageView ready = (ImageView)this.findViewById(R.id.buttonGamePlayerReady);
    	ready.setVisibility(Visibility);
    }
    
    private void displayOpponentReadyButton(int Visibility) {
    	ImageView ready = (ImageView)this.findViewById(R.id.buttonGameOpponentReady);
    	ready.setVisibility(Visibility);
    }
    
    public void setupGameScreen() {
    	ImageView opaqueBackground = (ImageView) findViewById(R.id.backgroundOpaque);
    	opaqueBackground.setVisibility(View.GONE);
    	ImageView coin = (ImageView) findViewById(R.id.iconCoin);
    	coin.setVisibility(View.GONE);
        
        //Hook the on touch listener to both layouts
        RelativeLayout playerLayout = (RelativeLayout) findViewById(R.id.BootyGamePlayerScreen);
        playerLayout.setOnTouchListener((OnTouchListener) this);
        
        RelativeLayout opponentLayout = (RelativeLayout) findViewById(R.id.BootyGameOpponentScreen);
        opponentLayout.setOnTouchListener((OnTouchListener) this);
        
        m_ViewAnimationGenerator = new ViewAnimation();
    }
    
    public void coinChoice_Click(View v) {
    	//Retrieve the guess number
    	if(v.getId() == R.id.buttonCoinHeads) {
    		m_Game.setCoinGuess(0);
    	}
    	else {
    		m_Game.setCoinGuess(1);
    	}
    	
    	//Attached click event to the coin
    	ImageView coin = (ImageView) findViewById(R.id.iconCoin);
        final Animation coinflip = AnimationUtils.loadAnimation(this, R.anim.animationcoinflip);
        handler = new FlipCoin(coin, coinflip, (ImageView) findViewById(R.id.imagecointoss));
        
        //Set the text for coin flip
        m_PlayerTextGameInfo.setText(R.string.flipCoin);
        
        //Remove the buttons
        findViewById(R.id.buttonCoinTails).setVisibility(View.GONE);
    	findViewById(R.id.buttonCoinHeads).setVisibility(View.GONE);
    }
    
    private void startCoinChoiceAnimation() {
    	final ImageView imageheadsortails = (ImageView) findViewById(R.id.imagecointoss);
    	final Animation coinchoice = AnimationUtils.loadAnimation(this, R.anim.animationviewscaleup);
    	
    	coinchoice.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				imageheadsortails.setVisibility(View.GONE);
			}
		});
    	
    	imageheadsortails.setVisibility(View.VISIBLE);
    	imageheadsortails.startAnimation(coinchoice);
    	
    	//Wait two seconds
    	new Handler().postDelayed(new Runnable() { 
    	    public void run() { 
    	        setupGameScreen();
    	        startGame();
    	    }
    	}, 2000);
    }
    
	public void update(Observable sender, Object selectionNumber) {
		//Time up!
		if(selectionNumber == null) {
			ChangeTurn();
		}
		//Opponent Selection
		else {
			if(m_Game.playerCanSwitch())
			{
				//End of AI Choice timer
				if((Integer) selectionNumber != -1 && m_FirstOpponentSwitchSelection == -1) {
					m_FirstOpponentSwitchSelection = (Integer) selectionNumber;
				}
				//Make the switch
				else if((Integer) selectionNumber != -1) {
					switchBooty(m_OpponentGridArray[m_FirstOpponentSwitchSelection], m_OpponentGridArray[(Integer) selectionNumber]);
					m_Game.opponentSwitchBooty(m_FirstOpponentSwitchSelection, (Integer) selectionNumber);
					makeSwitch();
				}
				else {
					m_FirstOpponentSwitchSelection = (Integer)selectionNumber;
				}
			}
			else {
				//End of AI Choice timer
				if((Integer) selectionNumber != -1) {
					//Add the select icon to the currently selected location
					if(m_CurrentOpponentSelectedLocation != -1) {
						m_PlayerGridArray[m_CurrentOpponentSelectedLocation].setImageResource(R.drawable.iconcard);
					}
					m_PlayerGridArray[(Integer) selectionNumber].setImageResource(R.drawable.iconcardselected);
					//Set the selected location, (default -1 when finished)
					m_CurrentOpponentSelectedLocation = (Integer) selectionNumber;
				}
				else {
					gameMakeBootySelection(m_Game.checkOpponentSelection(), "Opponent");
				}
			}
		}
	}
	
	private void gameMakeBootySelection(int selection, String player) {
		//Stop the timer
		m_TurnTimer.stopTimer();
		m_TurnTimer.deleteObservers();
		//Start Find Activity showing whether a trap, booty or nothing has been found
    	Intent findBooty = new Intent(this, BootyFindBootyActivity.class);
    	findBooty.putExtra("Selection", selection);
    	findBooty.putExtra("Player", player);
    	//Start Activity wait until its finished, pass what was found (trap, booty or nothing)
    	startActivityForResult(findBooty, 1);
    	displayPlayerReadyButton(View.INVISIBLE);
	}
	
	private void startTurnTimer(int timerTime) {
		if(m_TurnTimer != null) {
			m_TurnTimer.stopTimer();
		}
		m_TurnTimer = new DialogTimer(getLayoutInflater(), getApplicationContext(), this, findViewById(R.id.TimerToast), timerTime);
		m_TurnTimer.addObserver(this);
		m_TurnTimer.startTimer();
	}
	
	//Possible that method gets called twice, wait until the first call is finished
	private synchronized void makeSwitch() {
		
		if(m_Game.getSwitchMade()) {
			m_TurnTimer.switchUpdate();
		}
		else {
			//Stop the timer, change turn
			m_TurnTimer.stopTimer();
			m_TurnTimer.deleteObservers();
			ChangeTurn();
		}
	}
	
	public void readyButton_Click(View v) {
		
		if(m_Game.playerCanChoose()) {
			m_Game.setPlayerSelection(m_CurrentPlayerSelectedLocation);
			//Players choice to search for Booty
			gameMakeBootySelection(m_Game.checkPlayerSelection(), "Player");
		}
		else if(m_Game.playerCanSwitch()) {
			switchBooty(m_PlayerGridArray[m_FirstPlayerSwitchSelection], m_PlayerGridArray[m_SecondPlayerSwitchSelection]);
			m_Game.playerSwitchBooty(m_FirstPlayerSwitchSelection, m_SecondPlayerSwitchSelection);
			m_FirstPlayerSwitchSelection = -1;
			m_SecondPlayerSwitchSelection = -1;
			makeSwitch();
			displayPlayerReadyButton(View.INVISIBLE);
		}
	}
	
	public void switchBooty(ImageView firstBootySwitch, ImageView secondBootySwitch) {
		
		//Pass by reference int arrays for location of ImageViews
		int[] firstSelectionLocation = new int[2];
		int[] secondSelectionLocation = new int[2];
		firstBootySwitch.getLocationOnScreen(firstSelectionLocation);
		secondBootySwitch.getLocationOnScreen(secondSelectionLocation);
		
		//Hide the selection
		firstBootySwitch.setImageResource(R.drawable.iconcard);
		secondBootySwitch.setImageResource(R.drawable.iconcard);
		
		firstBootySwitch.startAnimation(m_ViewAnimationGenerator.SwitchAnimation(firstSelectionLocation, secondSelectionLocation));
		secondBootySwitch.startAnimation(m_ViewAnimationGenerator.SwitchAnimation(secondSelectionLocation, firstSelectionLocation));
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		int bootySearch = data.getExtras().getInt("Selection");
		String player = data.getExtras().getString("Player");
		
		if(bootySearch == 2) {
			
			if(player.equals("Opponent")) {
				
				int opponentFoundBooty = m_Game.GetOpponentFoundBooty();
				m_PlayerGridArray[m_CurrentOpponentSelectedLocation].setImageResource(R.drawable.iconcarddisabled);
				m_PlayerGridArray[m_CurrentOpponentSelectedLocation].setOnClickListener(null);
				//Remove the grid cell from the array
				m_PlayerGridArray[m_CurrentOpponentSelectedLocation] = null;
				
				if(opponentFoundBooty == 1) {
					((ImageView) findViewById(R.id.iconplayerfirstgamebooty)).setImageResource(R.drawable.iconusedchest);
				}
				else if(opponentFoundBooty == 2) {
					((ImageView) findViewById(R.id.iconplayersecondgamebooty)).setImageResource(R.drawable.iconusedchest);
				}
				else {
					//Opponent Won!
		        	//Intent opponentWinGame = new Intent(this, BootyWinLoseActivity.class);
		        	//startActivity(opponentWinGame);
		        	//End this activity
		        	this.finish();
				}
				
				clearOpponentArrayButtonImages();
			}
			else {
				
				int playerFoundBooty = m_Game.GetPlayerFoundBooty();
				m_OpponentGridArray[m_CurrentPlayerSelectedLocation].setImageResource(R.drawable.iconcarddisabled);
				m_OpponentGridArray[m_CurrentPlayerSelectedLocation].setOnClickListener(null);
				//Remove grid cell from the array
				m_OpponentGridArray[m_CurrentPlayerSelectedLocation] = null;
				
				if(playerFoundBooty == 1) {
					((ImageView) findViewById(R.id.iconopponentfirstgamebooty)).setImageResource(R.drawable.iconusedchest);
				}
				else if(playerFoundBooty == 2) {
					((ImageView) findViewById(R.id.iconopponentsecondgamebooty)).setImageResource(R.drawable.iconusedchest);
				}
				else {
		    		//Start Game
		        	//Intent playerWinGame = new Intent(this, BootyWinLoseActivity.class);
		        	//startActivity(playerWinGame);
		        	//End this activity
		        	this.finish();
				}
				
				clearPlayerArrayButtonImages();
			}
		}
		else if(bootySearch == 1) {
			//Opponent found a trap, player gets an extra turn!
		}
		
		ChangeTurn();
	}
	
    public void playerChoose_Click(View v) {
    	
    	if(m_Game.playerCanChoose()) {
    		
    		//Add the select icon to the currently selected location
    		if(m_CurrentPlayerSelectedLocation != -1) {
    			m_OpponentGridArray[m_CurrentPlayerSelectedLocation].setImageResource(R.drawable.iconcard);
    		}
    		
    		((ImageView)v).setImageResource(R.drawable.iconcardselected);
    		displayOpponentReadyButton(View.VISIBLE);
    		//Use clicked button tag as a selected index (0-8)
    		m_Game.setPlayerSelection(Integer.parseInt(((ImageView)v).getTag().toString()));
    		m_CurrentPlayerSelectedLocation = Integer.parseInt(((ImageView)v).getTag().toString());
    	}
    }
	
    public void playerChooseSwitch_Click(View v) {
    	
    	if(m_Game.playerCanSwitch()) {
    		int selectedImageView = Integer.parseInt(((ImageView)v).getTag().toString());
    		
    		if(m_FirstPlayerSwitchSelection == -1) {
    			m_FirstPlayerSwitchSelection = selectedImageView;
    			m_PlayerGridArray[m_FirstPlayerSwitchSelection].setImageResource(R.drawable.iconcardselected);
    		}
    		else if(m_SecondPlayerSwitchSelection == -1 && selectedImageView != m_FirstPlayerSwitchSelection) {
    			m_SecondPlayerSwitchSelection = selectedImageView;
    			m_PlayerGridArray[m_SecondPlayerSwitchSelection].setImageResource(R.drawable.iconcardselected);
    			displayPlayerReadyButton(View.VISIBLE);
    		}
    		else if(selectedImageView != m_FirstPlayerSwitchSelection && selectedImageView != m_SecondPlayerSwitchSelection) {
    			if(m_FirstSwitchSelected) {
    				m_PlayerGridArray[m_SecondPlayerSwitchSelection].setImageResource(R.drawable.iconcard);
    				m_SecondPlayerSwitchSelection = selectedImageView;
    				m_FirstSwitchSelected = false;
    				m_PlayerGridArray[m_SecondPlayerSwitchSelection].setImageResource(R.drawable.iconcardselected);
    			}
    			else {
    				m_PlayerGridArray[m_FirstPlayerSwitchSelection].setImageResource(R.drawable.iconcard);
    				m_FirstPlayerSwitchSelection = selectedImageView;
    				m_FirstSwitchSelected = true;
    				m_PlayerGridArray[m_FirstPlayerSwitchSelection].setImageResource(R.drawable.iconcardselected);
    			}
    		}
    	}
    }
    
    public void ChangeTurn()
    {
    	TurnReset();
    	m_Game.ChangeTurn();
    	
    	if(m_Game.playerCanSwitch()) {
    		//Set the text of both textfields on the game UI to a random string
    		m_OpponentTextGameInfo.setText(m_SwitchTurnText[new Random().nextInt(m_SwitchTurnText.length)]);
    		m_PlayerTextGameInfo.setText(m_SwitchTurnText[new Random().nextInt(m_SwitchTurnText.length)]);
    		m_Game.makeOpponentSwitchSelection();
    		startTurnTimer(SWITCHTIMERTIME);
    	}
    	else if(!m_Game.playerCanChoose()) {
    		
    		if(m_GameFlipper.getDisplayedChild() == SCRNOPPONENT) {
    			switchToPlayerScreen();
    		}
    		m_OpponentTextGameInfo.setText(m_OpponentTurnText[new Random().nextInt(m_OpponentTurnText.length)]);
    		m_PlayerTextGameInfo.setText(m_OpponentTurnText[new Random().nextInt(m_OpponentTurnText.length)]);
    		m_Game.makeOpponentSelection();
    		startTurnTimer(COUNTDOWNTIMERTIME);
    	}
    	else {
    		
    		if(m_GameFlipper.getDisplayedChild() == SCRNPLAYER) {
    			switchToOpponentScreen();
    		}
    		m_OpponentTextGameInfo.setText(m_PlayerTurnText[new Random().nextInt(m_PlayerTurnText.length)]);
    		m_PlayerTextGameInfo.setText(m_PlayerTurnText[new Random().nextInt(m_PlayerTurnText.length)]);
    		startTurnTimer(COUNTDOWNTIMERTIME);
    	}
    }
    
    /**
     * Turn reset method which resets selections and updates the UI for a new turn
     */
    public void TurnReset() {
		//Set the opponent selected location, (default -1 when finished)
    	m_CurrentPlayerSelectedLocation = -1;
		m_CurrentOpponentSelectedLocation = -1;
		//Reset the first selection
    	m_FirstSwitchSelected = false;
    	displayPlayerReadyButton(View.INVISIBLE);
    	displayOpponentReadyButton(View.INVISIBLE);
    	clearOpponentArrayButtonImages();
    	clearPlayerArrayButtonImages();
    }
	
    private void setupPlayerArray() {
        m_PlayerGridArray = new ImageView[9];
        m_PlayerGridArray[0] =  (ImageView)this.findViewById(R.id.buttonPlayerGridOne);
        m_PlayerGridArray[1] =  (ImageView)this.findViewById(R.id.buttonPlayerGridTwo);
        m_PlayerGridArray[2] =  (ImageView)this.findViewById(R.id.buttonPlayerGridThree);
        m_PlayerGridArray[3] =  (ImageView)this.findViewById(R.id.buttonPlayerGridFour);
        m_PlayerGridArray[4] =  (ImageView)this.findViewById(R.id.buttonPlayerGridFive);
        m_PlayerGridArray[5] =  (ImageView)this.findViewById(R.id.buttonPlayerGridSix);
        m_PlayerGridArray[6] =  (ImageView)this.findViewById(R.id.buttonPlayerGridSeven);
        m_PlayerGridArray[7] =  (ImageView)this.findViewById(R.id.buttonPlayerGridEight);
        m_PlayerGridArray[8] =  (ImageView)this.findViewById(R.id.buttonPlayerGridNine);
    }
    
    private void setupOpponentArray() {
        m_OpponentGridArray = new ImageView[9];
        m_OpponentGridArray[0] =  (ImageView)this.findViewById(R.id.buttonOpponentGridOne);
        m_OpponentGridArray[1] =  (ImageView)this.findViewById(R.id.buttonOpponentGridTwo);
        m_OpponentGridArray[2] =  (ImageView)this.findViewById(R.id.buttonOpponentGridThree);
        m_OpponentGridArray[3] =  (ImageView)this.findViewById(R.id.buttonOpponentGridFour);
        m_OpponentGridArray[4] =  (ImageView)this.findViewById(R.id.buttonOpponentGridFive);
        m_OpponentGridArray[5] =  (ImageView)this.findViewById(R.id.buttonOpponentGridSix);
        m_OpponentGridArray[6] =  (ImageView)this.findViewById(R.id.buttonOpponentGridSeven);
        m_OpponentGridArray[7] =  (ImageView)this.findViewById(R.id.buttonOpponentGridEight);
        m_OpponentGridArray[8] =  (ImageView)this.findViewById(R.id.buttonOpponentGridNine);
    }
    
    private void clearPlayerArrayButtonImages() {
		//Clear the buttons of images
		for(int i = 0; i < m_PlayerGridArray.length; i++) { 
			if(m_PlayerGridArray[i] != null) {
				m_PlayerGridArray[i].setImageResource(R.drawable.iconcard);
			}
		}
    }
    
    private void clearOpponentArrayButtonImages() {
		//Clear the buttons of images
		for(int i = 0; i < m_OpponentGridArray.length; i++) {
			//Ensure 'disabled' grid cells are not reset
			if(m_OpponentGridArray[i] != null) {
				m_OpponentGridArray[i].setImageResource(R.drawable.iconcard);
			}
		}
    }
    
    //Credit screen transition
    public void switchToOpponentScreen() {
    	m_GameFlipper.setInAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.IN, Direction.TOP, 500, Animation.RELATIVE_TO_PARENT));
    	m_GameFlipper.setOutAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.OUT, Direction.BOTTOM, 500, Animation.RELATIVE_TO_PARENT));
        m_GameFlipper.setDisplayedChild(SCRNOPPONENT);	
    }
    
    //Splash screen transition
    public void switchToPlayerScreen() {
        m_GameFlipper.setInAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.IN, Direction.BOTTOM, 500, Animation.RELATIVE_TO_PARENT));
        m_GameFlipper.setOutAnimation(m_ViewAnimationGenerator.GestureAnimation(Direction.OUT, Direction.TOP, 500, Animation.RELATIVE_TO_PARENT));
        m_GameFlipper.setDisplayedChild(SCRNPLAYER);
    }
    
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // Get the action that was done on this touch event
        switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                // store the Y value when the user's finger was pressed down
                m_DownY = arg1.getY();
                break;
            }

            case MotionEvent.ACTION_UP: {
                // Get the X value when the user released his/her finger
                float currentY = arg1.getY();
                // pull up
                if (m_DownY < currentY && m_GameFlipper.getDisplayedChild() == SCRNPLAYER) {
                	switchToOpponentScreen();
                }

                // pull down
                else if (m_DownY > currentY && m_GameFlipper.getDisplayedChild() == SCRNOPPONENT) {
                	switchToPlayerScreen();
                }
                break;
            }
        }
        
        return true;
       
    }    
    
    @Override
    public void onBackPressed() {
    	
    	AlertDialog backDialog = new AlertDialog.Builder(this)
    	.setView(getLayoutInflater().inflate(R.layout.layoutdialog, (ViewGroup)findViewById(R.id.DialogLayout)))
    	.setPositiveButton(R.string.quitDialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	closeGame();
            }
    	})
    	.setNegativeButton(R.string.cancelDialog, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
            }
    	}).show();
    	//Set x and y location
    	backDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,400);
    	//Set background to transparent
    	backDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
    
    private void closeGame() {
    	
        if(m_TurnTimer != null) {
        	m_TurnTimer.timerDispose();
        	m_Game.dispose();
        }
        this.finish();
    }
    
    /**
     * Internal flip coin class which handles the 'flipping coin animation'
     * @author Michael
     */
    class FlipCoin extends Handler {
		  private short m_CoinImageValue = 0;
		  private ImageView m_Coin;
		  private ImageView m_CoinChoice;
		  
		  FlipCoin(ImageView iconCoin, Animation animate, ImageView coinChoice) {
			  final Animation animation = animate;
			  m_Coin = iconCoin;
			  m_CoinChoice = coinChoice;
		        
		        m_Coin.setOnClickListener(new View.OnClickListener() {
		        	public void onClick(View view) {
		        		m_Coin.setOnClickListener(null);
		        		m_Coin.startAnimation(animation);
		        		handler.sendMessageDelayed(Message.obtain(handler, 0, 20), 50);
		        	}
		        });  
		  }

    	  /**
    	   * @see android.os.Handler#handleMessage(android.os.Message)
    	   */
    	  public void handleMessage(Message msg) {
    		  if(m_CoinImageValue == 0) {
    			  m_CoinImageValue = 1;
    		  }
    		  else {
    			  m_CoinImageValue = 0;
    		  }
    		  m_Coin.setImageLevel(m_CoinImageValue);

    		  // If there are still rolls available, roll another time.
    		  Integer flips = (Integer) msg.obj;
    		  if (flips > 0) {
    			  BootyGameActivity.this.handler.sendMessageDelayed(Message.obtain(BootyGameActivity.this.handler, 0, --flips), 50);
    		  }
    		  else {
    			  int coinflip = m_Game.getCoinFlip();
    			  
  		          m_Coin.setImageLevel(coinflip);
  		          
  		          //Get random text to inform player
  		          if(m_Game.getFirstGo()) {
  		        	  m_PlayerTextGameInfo.setText((getResources().getStringArray(R.array.playerFirst))[new Random().nextInt(5)]);
  		        	  m_OpponentTextGameInfo.setText((getResources().getStringArray(R.array.playerFirst))[new Random().nextInt(5)]);
  		          }
  		          else {
  		        	  m_OpponentTextGameInfo.setText((getResources().getStringArray(R.array.opponentFirst))[new Random().nextInt(5)]);
  		        	  m_PlayerTextGameInfo.setText((getResources().getStringArray(R.array.opponentFirst))[new Random().nextInt(5)]);	
  		          }
  		        		
  		          m_CoinChoice.setImageLevel(coinflip);
  		          startCoinChoiceAnimation();
    		  }
    	  }
    }
}

