package theking530.staticpower.tileentities.components;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.ModelDataMap;
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

	public void onOwningTileEntityValidate() {

	}

	public void onOwningTileEntityRemoved() {

	}

	public void preProcessUpdate() {
	}

	public void postProcessUpdate() {
	}

	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos) {

	}

	public void updatePostPlacement(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {

	}

	/**
	 * Adds any model data. Keep in mind, this runs on the render thread, not the
	 * game thread.
	 * 
	 * @param builder
	 */
	public void getModelData(ModelDataMap.Builder builder) {

	}

	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		return nbt;
	}

	public void deserializeSaveNbt(CompoundNBT nbt) {
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
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

	public BlockPos getPos() {
		return tileEntity.getPos();
	}

	public World getWorld() {
		if (tileEntity.isRemoved()) {
			throw new RuntimeException("We cannot access the world if the tile entity has been removed! Occured at: " + getPos());
		}
		return tileEntity.getWorld();
	}
}
