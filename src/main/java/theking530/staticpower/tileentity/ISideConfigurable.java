package theking530.staticpower.tileentity;

import net.minecraft.util.EnumFacing;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.assists.utilities.SideUtilities.BlockSide;

public interface ISideConfigurable {

	public boolean isSideConfigurable();
	
	public Mode getSideConfiguration(BlockSide side);
    public Mode getSideConfiguration(EnumFacing facing);
    
    public Mode[] getSideConfigurations();
    
	public void setSideConfiguration(Mode newMode, BlockSide side);
    public void setSideConfiguration(Mode newMode, EnumFacing facing);
    
	public void incrementSideConfiguration(EnumFacing side);
}
