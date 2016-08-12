package theking530.staticpower.utils;

public class CoordinateUtils {

	public int xOffset;
	public int yOffset;
	public int zOffset;

	/**
	 * 
	 * {UP, DOWN, EAST, WEST, NORTH, SOUTH}
	 * 
	 * @param direction 
	 */
	public void dirOffset(int direction) {
		switch(direction) {
		case 0: xOffset=0;
				yOffset=1;
				zOffset=0;
		case 1: xOffset=0;
				yOffset=-1;
				zOffset=0;
		case 2: xOffset=1;
				yOffset=0;
				zOffset=0;
		case 3: xOffset=-1;
				yOffset=0;
				zOffset=0;
		case 4: xOffset=0;
				yOffset=0;
				zOffset=1;
		case 5: xOffset=0;
				yOffset=0;
				zOffset=-1;
			}
		}
	}
