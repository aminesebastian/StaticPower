package theking530.staticpower.tileentities.components;

import net.minecraft.nbt.CompoundNBT;
import theking530.staticpower.tileentities.TileEntityBase;

public interface ITileEntityComponent {
	default public void onRegistered(TileEntityBase owner) {
	}

	default public void onRemovedFromOwner(TileEntityBase owner) {
	}

	default public void preProcessUpdate() {
	}

	default public void postProcessUpdate() {
	}

	default public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		return nbt;
	}

	default public void deserializeSaveNbt(CompoundNBT nbt) {
	}

	default public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		return nbt;
	}

	default public void deserializeUpdateNbt(CompoundNBT nbt) {
	}

	public String getComponentName();

	public boolean isEnabled();

	public void setEnabled(boolean isEnabled);
}
