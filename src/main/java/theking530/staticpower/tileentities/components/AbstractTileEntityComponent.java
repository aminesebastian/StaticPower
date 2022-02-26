package theking530.staticpower.tileentities.components;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
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

	public void onInitializedInWorld(Level world, BlockPos pos, boolean firstTimePlaced) {
		worldLoaded = true;
	}

	protected boolean isWorldLoaded() {
		return worldLoaded;
	}

	/**
	 * This method is called when the owning tile entity is first placed in the
	 * world.
	 * 
	 * @param state
	 * @param placer
	 * @param stack
	 */
	public void onPlaced(BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

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

	public int getWeakPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		return 0;
	}

	public int getStrongPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		return 0;
	}

	public CompoundTag serializeSaveNbt(CompoundTag nbt) {
		SerializationUtilities.serializeFieldsToNbt(nbt, saveSerializeableFields, this);
		return nbt;
	}

	public void deserializeSaveNbt(CompoundTag nbt) {
		SerializationUtilities.deserializeFieldsToNbt(nbt, saveSerializeableFields, this);
	}

	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		SerializationUtilities.serializeFieldsToNbt(nbt, updateSerializeableFields, this);
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
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

	protected Direction getOwningTileEntityFacing() {
		BlockState currentBlockState = getTileEntity().getBlockState();
		if (currentBlockState.hasProperty(StaticPowerTileEntityBlock.FACING)) {
			return currentBlockState.getValue(StaticPowerTileEntityBlock.FACING);
		}
		return null;
	}

	public void setEnabled(boolean isEnabled) {
		if (this.isEnabled != isEnabled) {
			this.isEnabled = isEnabled;
			getTileEntity().addUpdateRequest(TileEntityUpdateRequest.syncDataOnly(true), true);
			getTileEntity().setChanged();
		}
	}

	public TileEntityBase getTileEntity() {
		return tileEntity;
	}

	public BlockPos getPos() {
		return getTileEntity().getBlockPos();
	}

	public Level getWorld() {
		return getTileEntity().getLevel();
	}
}
