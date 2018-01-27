package theking530.staticpower.tileentity;

import net.minecraft.util.EnumFacing;
import theking530.staticpower.utils.SideModeList;
import theking530.staticpower.utils.SideModeList.Mode;

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
}
