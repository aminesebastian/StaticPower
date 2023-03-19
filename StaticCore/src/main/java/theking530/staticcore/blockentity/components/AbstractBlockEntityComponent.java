package theking530.staticcore.blockentity.components;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.BlockEntityUpdateRequest;
import theking530.staticcore.blockentity.components.serialization.SerializationUtilities;

/**
 * Abstract class for any {@link TileEntity} components. Each component recieves
 * its own {@link CompoundNBT} for serialization and deserialization, so no need
 * to prefix when writing to NBT.
 * 
 * @author Amine Sebastian
 *
 */
public abstract class AbstractBlockEntityComponent {

	private String name;
	private boolean isEnabled;
	private BlockEntityBase blockEntity;

	private final List<Field> saveSerializeableFields;
	private final List<Field> updateSerializeableFields;

	public AbstractBlockEntityComponent(String name) {
		this.name = name;
		this.isEnabled = true;
		this.saveSerializeableFields = SerializationUtilities.getSaveSerializeableFields(this);
		this.updateSerializeableFields = SerializationUtilities.getUpdateSerializeableFields(this);
	}

	public void onRegistered(BlockEntityBase owner) {
		this.blockEntity = owner;
	}

	public void onRemovedFromOwner(BlockEntityBase owner) {
	}

	/**
	 * This is called after the owning block entity is loaded every since time. This
	 * is NOT unique to just when the block is placed, it is also when loading the
	 * game or area.
	 * 
	 * @param level
	 * @param pos
	 * @param state
	 */
	public void onOwningBlockEntityLoaded(Level level, BlockPos pos, BlockState state) {
	}

	/**
	 * This is called after the owning entity is removed from the world. This is NOT
	 * unique to just when the block is broken. This is also when the area unloads
	 * or the game is closing.
	 */
	public void onOwningBlockEntityUnloaded() {

	}

	/**
	 * This method is called when the owning block entity is first placed in the
	 * world.
	 * 
	 * @param context TODO
	 * @param state
	 * @param placer
	 * @param stack
	 */
	public void onOwningBlockEntityFirstPlaced(BlockPlaceContext context, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

	}

	/**
	 * This method is called when the owning block entity is broken.
	 * 
	 * @param state
	 * @param newState
	 * @param isMoving
	 */
	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {

	}

	public void preProcessUpdate() {
	}

	public void postProcessUpdate() {
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
	public void getModelData(ModelData.Builder builder) {

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

	public void setEnabled(boolean isEnabled) {
		if (this.isEnabled != isEnabled) {
			this.isEnabled = isEnabled;
			getBlockEntity().addUpdateRequest(BlockEntityUpdateRequest.syncDataOnly(true), true);
			getBlockEntity().setChanged();
		}
	}

	public BlockEntityBase getBlockEntity() {
		return blockEntity;
	}

	public BlockPos getPos() {
		return getBlockEntity().getBlockPos();
	}

	public Level getLevel() {
		return getBlockEntity().getLevel();
	}

	public boolean isClientSide() {
		return getBlockEntity().getLevel().isClientSide();
	}
}
