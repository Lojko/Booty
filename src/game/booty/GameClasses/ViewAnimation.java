package game.booty.GameClasses;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * ViewAnimation helper class which translates a given {@link Direction} into/out of
 * the frame and direction to enter/exit and returns an appropriate {@link Animation}.	
 * 
 * @author Michael
 */
public class ViewAnimation 
{
	/**Enumeration which dictates direction */
	public enum Direction {
		TOP,
		BOTTOM,
		RIGHT,
		LEFT,
		IN,
		OUT
	}
	
	//Gesture Animation which animates transitions between views
    public Animation GestureAnimation(Direction inout, Direction direction, int duration, int animationType) {
    	float xFrom = 0.0f, xTo = 0.0f, yFrom = 0.0f, yTo = 0.0f;
		
		//Check if its bottom or top
		if (direction == Direction.BOTTOM || direction == Direction.TOP) {
			xFrom = xTo = 0.0f;
			
			//Check Movement
			if (inout == Direction.IN){
				//Assign Value
				if (direction == Direction.BOTTOM) {
					yFrom = +1.0f;
				} else {
					yFrom = -1.0f;
				}
			} else {
				if (direction == Direction.BOTTOM) {
					yTo = +1.0f;
				} else {
					yTo = -1.0f;
				}
			}
		} else {
			yFrom = yTo = 0.0f;
			
			if (inout == Direction.IN) {
				if (direction == Direction.RIGHT) {
					xFrom = +1.0f;
				} else {
					xFrom = -1.0f;
				}
			} else {
				if (direction == Direction.RIGHT) {
					xTo = +1.0f;
				} else {
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
