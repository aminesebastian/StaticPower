package theking530.staticpower.tileentity;

import net.minecraft.util.EnumFacing;
import theking530.staticpower.assists.utilities.SideModeList;
import theking530.staticpower.assists.utilities.SideModeList.Mode;

public class SideConfiguration {

	private SideModeList.Mode[] configuration = {Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular, Mode.Regular};
	
	public Mode[] getConfiguration() {
		return configuration;
	}
    public Mode getSideConfiguration(EnumFacing facing) {
    	return configuration[facing.ordinal()];
    }
    public void setSideConfiguration(Mode newMode, EnumFacing facing) {
    	configuration[facing.ordinal()] = newMode;
    }
    public void setToDefault() {
		setSideConfiguration(Mode.Input, EnumFacing.UP);
		setSideConfiguration(Mode.Input, EnumFacing.DOWN);
		
		setSideConfiguration(Mode.Output, EnumFacing.EAST);
		setSideConfiguration(Mode.Output, EnumFacing.WEST);
		setSideConfiguration(Mode.Output, EnumFacing.NORTH);
		setSideConfiguration(Mode.Output, EnumFacing.SOUTH);
    }
    public void reset() {
    	for(int i=0; i<6; i++) {
    		configuration[i] = Mode.Regular;
    	}
    }
}
