package theking530.staticpower.tileentities.components;

import net.minecraft.nbt.CompoundNBT;
import theking530.staticpower.tileentities.utilities.RedstoneMode;

public class RedstoneControlComponent extends AbstractTileEntityComponent {
	private RedstoneMode redstoneMode;

	public RedstoneControlComponent(String name, RedstoneMode defaultMode) {
		super(name);
		redstoneMode = defaultMode;
	}

	/**
	 * Gets the redstone control mode.
	 * 
	 * @return
	 */
	public RedstoneMode getRedstoneMode() {
		return redstoneMode;
	}

	/**
	 * Updates the redstone control mode.
	 * 
	 * @param newMode
	 */
	public void setRedstoneMode(RedstoneMode newMode) {
		redstoneMode = newMode;
	}

	/**
	 * Checks to see if the block at the current position passes the redstone
	 * control check.
	 * 
	 * @return True if the redstone check passes, false otherwise.
	 */
	public boolean passesRedstoneCheck() {
		return RedstoneMode.evaluateRedstoneMode(redstoneMode, getWorld(), getPos());
	}

	/**
	 * Writes the current setting to an NBT tag.
	 * 
	 * @param tag
	 * @return
	 */
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		nbt.putInt("redstone_mode", getRedstoneMode().ordinal());
		return nbt;
	}

	/**
	 * Reads the control setting from the provided NBT tag.
	 * 
	 * @param tag
	 */
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		setRedstoneMode(RedstoneMode.values()[nbt.getInt("redstone_mode")]);
	}
}
