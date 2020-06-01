package theking530.staticpower.tileentities.components;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticpower.tileentities.TileEntityBase;

/**
 * Abstract class for any {@link TileEntity} components. Each component recieves
 * its own {@link CompoundNBT} for serialization and deserialization, so no need
 * to prefix when writing to NBT.
 * 
 * @author Amine Sebastian
 *
 */
public abstract class AbstractTileEntityComponent {
	private String name;
	private boolean isEnabled;
	private TileEntityBase tileEntity;
	
	public AbstractTileEntityComponent(String name) {
		this.name = name;
	}

	public void onRegistered(TileEntityBase owner) {
		this.tileEntity = owner;
	}

	public void onRemovedFromOwner(TileEntityBase owner) {
	}

	public void preProcessUpdate() {
	}

	public void postProcessUpdate() {
	}

	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		return nbt;
	}

	public void deserializeSaveNbt(CompoundNBT nbt) {
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt) {
	}

	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		return LazyOptional.empty();
	}

	public String getComponentName() {
		return name;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public TileEntityBase getTileEntity() {
		return tileEntity;
	}
}
