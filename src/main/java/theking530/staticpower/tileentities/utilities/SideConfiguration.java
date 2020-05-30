package theking530.staticpower.tileentities.utilities;

import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.utilities.SideModeList.Mode;
public class SideConfiguration {

	private final Mode[] configuration;
	
	public SideConfiguration() {
		configuration = new Mode[]{Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular};
	}
	
	public Mode[] getConfiguration() {
		return configuration;
	}
    public Mode getSideConfiguration(Direction facing) {
		if(facing == null) {
			return null;
		}
    	return configuration[facing.ordinal()];
    }
    public void setSideConfiguration(Mode newMode, Direction facing) {
    	if(facing == null) {
    		return;
    	}
    	configuration[facing.ordinal()] = newMode;
    }
    public void setToDefault() {
		setSideConfiguration(Mode.Input, Direction.UP);
		setSideConfiguration(Mode.Input, Direction.DOWN);
		
		setSideConfiguration(Mode.Output, Direction.EAST);
		setSideConfiguration(Mode.Output, Direction.WEST);
		setSideConfiguration(Mode.Output, Direction.NORTH);
		setSideConfiguration(Mode.Output, Direction.SOUTH);
    }
    public void reset() {
    	for(int i=0; i<6; i++) {
    		configuration[i] = Mode.Regular;
    	}
    }
	public void setAllToMode(Mode mode) {
		setSideConfiguration(mode, Direction.UP);
		setSideConfiguration(mode, Direction.DOWN);
		
		setSideConfiguration(mode, Direction.EAST);
		setSideConfiguration(mode, Direction.WEST);
		setSideConfiguration(mode, Direction.NORTH);
		setSideConfiguration(mode, Direction.SOUTH);
	}
}
