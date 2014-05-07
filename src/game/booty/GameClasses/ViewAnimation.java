package game.booty.GameClasses;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class ViewAnimation 
{
	//Gesture Animation which animates transitions between views
    public Animation GestureAnimation(String inout, String direction, int duration, int animationType) {
    	float xFrom = 0.0f, xTo = 0.0f, yFrom = 0.0f, yTo = 0.0f;
		
		//Check if its bottom or top
		if(direction.equals("BOTTOM") || direction.equals("TOP")) {
			xFrom = xTo = 0.0f;
			
			//Check Movement
			if(inout.equals("IN")){
				//Assign Value
				if(direction.equals("BOTTOM")) {
					yFrom = +1.0f;
				}
				else {
					yFrom = -1.0f;
				}
			}
			else {
				if(direction.equals("BOTTOM")) {
					yTo = +1.0f;
				}
				else {
					yTo = -1.0f;
				}
			}
		}
		else {
			yFrom = yTo = 0.0f;
			
			if(inout.equals("IN")) {
				if(direction.equals("RIGHT")) {
					xFrom = +1.0f;
				}
				else {
					xFrom = -1.0f;
				}
			}
			else {
				if(direction.equals("RIGHT")) {
					xTo = +1.0f;
				}
				else {
					xTo = -1.0f;
				}
			}
		}
    	
    	Animation animation = new TranslateAnimation (
    			animationType, xFrom, animationType, xTo, animationType, yFrom, animationType, yTo
    	);
    	
    	animation.setDuration(duration);
        animation.setInterpolator(new AccelerateInterpolator());
    	return animation;
    }
    
    //'Switch' animation which animates a switch between two booty locations ('ImageViews')
    public TranslateAnimation SwitchAnimation(int[] currentLocation, int[] translateLocation) {
    	TranslateAnimation translateAnimation = new TranslateAnimation(0,translateLocation[0] - currentLocation[0], 0, translateLocation[1] - currentLocation[1]);
    	translateAnimation.setDuration(2000);
    	translateAnimation.setInterpolator(new AccelerateInterpolator());
    	return translateAnimation;
    }

}
