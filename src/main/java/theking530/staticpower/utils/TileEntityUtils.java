package theking530.staticpower.utils;

import java.util.Random;

public class TileEntityUtils {

	public static final Random RANDOM = new Random();
	
	public static boolean diceRoll(float percentage) {
		if(percentage >= 1) {
			return true;
		}
		float randFloat = RANDOM.nextFloat();	
		return percentage > randFloat ? true : false;
	}
}
