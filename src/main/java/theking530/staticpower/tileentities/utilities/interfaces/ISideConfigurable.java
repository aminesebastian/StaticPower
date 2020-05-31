package theking530.staticpower.tileentities.utilities.interfaces;

import java.util.List;

import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideUtilities.BlockSide;

public interface ISideConfigurable {

	public enum SideIncrementDirection {
		FORWARD, BACKWARDS;
	}

	public boolean isSideConfigurable();

	public MachineSideMode getSideConfiguration(BlockSide side);

	public MachineSideMode getSideConfiguration(Direction facing);

	public MachineSideMode[] getSideConfigurations();

	public List<MachineSideMode> getValidSideConfigurations();

	public void setSideConfiguration(MachineSideMode newMode, BlockSide side);

	public void setSideConfiguration(MachineSideMode newMode, Direction facing);

	public void incrementSideConfiguration(Direction side, SideIncrementDirection direction);

	public int getSideWithModeCount(MachineSideMode mode);
}
