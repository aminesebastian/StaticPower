package theking530.staticpower.tileentities.components;

import net.minecraft.nbt.CompoundNBT;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentities.utilities.RedstoneModeList.RedstoneMode;

public class RedstoneControlComponent extends AbstractTileEntityComponent {
	private RedstoneMode redstoneMode;

	public RedstoneControlComponent(String name, RedstoneMode defaultMode) {
		super(name);
		redstoneMode = defaultMode;
	}

	public RedstoneMode getRedstoneMode() {
		return redstoneMode;
	}

	public void setRedstoneMode(RedstoneMode newMode) {
		redstoneMode = newMode;
	}

	public boolean passesRedstoneCheck() {
		// If the owner is null, log it and return false.
		if (getTileEntity() == null) {
			StaticPower.LOGGER.error("Encountered invalid tile entity owner in %1$s of name: %2$s.", getClass().toString(), getComponentName());
			return false;
		}

		// Get the redstone signal at the block.
		int redstoneSignal = getTileEntity().getWorld().getStrongPower(getTileEntity().getPos());

		// If we're ignoring, just return true.
		if (redstoneMode == RedstoneMode.Ignore) {
			return true;
		}

		// If we're set to low mode, only return true if the current redstone signal is
		// low (0).
		if (redstoneMode == RedstoneMode.Low) {
			return redstoneSignal == 0;
		}

		// If we're set to high mode, only return true if the current redstone signal is
		// greater than high (> 0).
		if (redstoneMode == RedstoneMode.High) {
			return redstoneSignal > 0;
		}
		return false;
	}

	@Override
	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		nbt.putShort(getComponentName(), (short) redstoneMode.ordinal());
		return nbt;
	}

	@Override
	public void deserializeSaveNbt(CompoundNBT nbt) {
		redstoneMode = RedstoneMode.getModeFromInt(nbt.getShort(getComponentName()));
	}
}
