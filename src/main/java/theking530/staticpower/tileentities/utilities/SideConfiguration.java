package theking530.staticpower.tileentities.utilities;

import javax.annotation.Nonnull;

import net.minecraft.util.Direction;

public class SideConfiguration {

	private MachineSideMode[] configuration;

	public SideConfiguration() {
		configuration = new MachineSideMode[6];
		setToDefault();
	}

	public MachineSideMode[] getConfiguration() {
		return configuration;
	}

	public MachineSideMode getSideConfiguration(Direction facing) {
		if (facing == null) {
			return null;
		}
		return configuration[facing.ordinal()];
	}

	public void setSideConfiguration(@Nonnull Direction facing, @Nonnull MachineSideMode newMode) {
		if (facing == null || newMode == null) {
			return;
		}
		configuration[facing.ordinal()] = newMode;
	}

	public void setToDefault() {
		setSideConfiguration(Direction.UP, MachineSideMode.Input);
		setSideConfiguration(Direction.DOWN, MachineSideMode.Input);

		setSideConfiguration(Direction.EAST, MachineSideMode.Output);
		setSideConfiguration(Direction.WEST, MachineSideMode.Output);
		setSideConfiguration(Direction.NORTH, MachineSideMode.Output);
		setSideConfiguration(Direction.SOUTH, MachineSideMode.Output);
	}

	public void reset() {
		for (int i = 0; i < 6; i++) {
			configuration[i] = MachineSideMode.Regular;
		}
	}

	public void setAllToMode(MachineSideMode mode) {
		setSideConfiguration(Direction.UP, mode);
		setSideConfiguration(Direction.DOWN, mode);

		setSideConfiguration(Direction.EAST, mode);
		setSideConfiguration(Direction.WEST, mode);
		setSideConfiguration(Direction.NORTH, mode);
		setSideConfiguration(Direction.SOUTH, mode);
	}
}
