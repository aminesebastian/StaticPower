package theking530.staticpower.blockentities.components.control.sideconfiguration;

import net.minecraft.nbt.CompoundTag;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class SideConfiguration {
	private BlockSide side;
	private MachineSideMode mode;
	private boolean enabled;

	public SideConfiguration(BlockSide side, MachineSideMode mode, boolean enabled) {
		this.side = side;
		this.mode = mode;
		this.enabled = enabled;
	}

	public BlockSide getSide() {
		return side;
	}

	public void setSide(BlockSide side) {
		this.side = side;
	}

	public MachineSideMode getMode() {
		return mode;
	}

	public void setMode(MachineSideMode mode) {
		this.mode = mode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public SideConfiguration copy() {
		return new SideConfiguration(side, mode, enabled);
	}

	public void deserialize(CompoundTag tag) {
		side = BlockSide.values()[tag.getByte("side")];
		mode = MachineSideMode.values()[tag.getByte("mode")];
		enabled = tag.getBoolean("enabled");
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putByte("side", (byte) side.ordinal());
		tag.putByte("mode", (byte) mode.ordinal());
		tag.putBoolean("enabled", enabled);
		return tag;
	}
}
