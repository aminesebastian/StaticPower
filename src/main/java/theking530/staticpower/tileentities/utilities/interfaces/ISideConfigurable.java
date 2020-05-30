package theking530.staticpower.tileentities.utilities.interfaces;

import java.util.List;

import net.minecraft.util.Direction;
import theking530.staticpower.tileentities.utilities.SideModeList;
import theking530.staticpower.tileentities.utilities.SideUtilities;
import theking530.staticpower.tileentities.utilities.SideModeList.Mode;
import theking530.staticpower.tileentities.utilities.SideUtilities.BlockSide;

public interface ISideConfigurable {

	public enum SideIncrementDirection {
		FORWARD, BACKWARDS;
	}

	public boolean isSideConfigurable();

	public Mode getSideConfiguration(BlockSide side);

	public Mode getSideConfiguration(Direction facing);

	public Mode[] getSideConfigurations();

	public List<Mode> getValidSideConfigurations();

	public void setSideConfiguration(Mode newMode, BlockSide side);

	public void setSideConfiguration(Mode newMode, Direction facing);

	public void incrementSideConfiguration(Direction side, SideIncrementDirection direction);

	public int getSideWithModeCount(Mode mode);
}
