package theking530.staticpower.tileentities.components;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.TileEntityUpdateRequest;
import theking530.staticpower.tileentities.components.serialization.SerializationUtilities;

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
	/** Indicates that the world and chunk have been loaded. */
	private boolean worldLoaded;

	private final List<Field> saveSerializeableFields;
	private final List<Field> updateSerializeableFields;

	public AbstractTileEntityComponent(String name) {
		this.name = name;
		this.isEnabled = true;
		worldLoaded = false;
		this.saveSerializeableFields = SerializationUtilities.getSaveSerializeableFields(this);
		this.updateSerializeableFields = SerializationUtilities.getUpdateSerializeableFields(this);
	}

	public void onRegistered(TileEntityBase owner) {
		this.tileEntity = owner;
	}

	public void onRemovedFromOwner(TileEntityBase owner) {
	}

	public void onOwningTileEntityValidate(boolean isInitialPlacement) {

	}

	public void onOwningTileEntityRemoved() {

	}

	public void onOwningBlockBroken(BlockState state, BlockState newState, boolean isMoving) {

	}

	public void preProcessUpdate() {
	}

	public void postProcessUpdate() {
	}

	public void onInitializedInWorld(World world, BlockPos pos, boolean firstTimePlaced) {
		worldLoaded = true;
	}

	protected boolean isWorldLoaded() {
		return worldLoaded;
	}

	/**
	 * WARNING: This method is raised on the render thread, this should only update
	 * visual values and not gameplay properties. Only called on the client.
	 * 
	 * @param partialTicks
	 */
	public void updateBeforeRendering(float partialTicks) {

	}

	/**
	 * This method is called only on the server when a neighbor changes.
	 * 
	 * @param currentState
	 * @param neighborPos
	 * @param isMoving
	 */
	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos, boolean isMoving) {

	}

	/**
	 * This method is called on the server AND client when a neighbor changes.
	 * 
	 * @param state
	 * @param direction
	 * @param facingState
	 * @param FacingPos
	 */
	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {

	}

	/**
	 * Adds any model data. Keep in mind, this runs on the render thread, not the
	 * game thread.
	 * 
	 * @param builder
	 */
	public void getModelData(ModelDataMap.Builder builder) {

	}

	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return 0;
	}

	public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return 0;
	}

	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		SerializationUtilities.serializeFieldsToNbt(nbt, saveSerializeableFields, this);
		return nbt;
	}

	public void deserializeSaveNbt(CompoundNBT nbt) {
		SerializationUtilities.deserializeFieldsToNbt(nbt, saveSerializeableFields, this);
	}

	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		SerializationUtilities.serializeFieldsToNbt(nbt, updateSerializeableFields, this);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		SerializationUtilities.deserializeFieldsToNbt(nbt, updateSerializeableFields, this);
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
		if (this.isEnabled != isEnabled) {
			this.isEnabled = isEnabled;
			getTileEntity().addUpdateRequest(TileEntityUpdateRequest.syncDataOnly(true), true);
			getTileEntity().markDirty();
		}
	}

	public TileEntityBase getTileEntity() {
		return tileEntity;
	}

	public BlockPos getPos() {
		return getTileEntity().getPos();
	}

	public World getWorld() {
		return getTileEntity().getWorld();
	}
}
