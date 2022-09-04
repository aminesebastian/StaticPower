package theking530.staticpower.tileentities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.StaticPower;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.network.TileEntityBasicSyncPacket;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.serialization.SerializationUtilities;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;
import theking530.staticpower.tileentities.components.team.TeamComponent;
import theking530.staticpower.tileentities.interfaces.IBreakSerializeable;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class TileEntityBase extends BlockEntity implements MenuProvider, IBreakSerializeable {
	public static final Logger LOGGER = LogManager.getLogger(TileEntityBase.class);
	@UpdateSerialize
	/**
	 * This is set to true after the first tick this tile entity has experienced.
	 * This value is also saved, so it will only be false the first time this tile
	 * entity is placed.
	 */
	private boolean hasFirstPlacedMethodRun;
	/**
	 * This is set to true after the first tick this tile entity has experienced.
	 * This value is NOT saved, so it will be false the first time this tile entity
	 * is placed and every subsequent time it is loaded.
	 */
	private boolean hasPostInitRun;
	/**
	 * Indicates whether or not this tile entity is valid.
	 */
	private boolean isValid;
	/**
	 * Collection of all the components on this tile entity.
	 */
	private LinkedHashMap<String, AbstractTileEntityComponent> components;
	/**
	 * Cached list of all fields that are save serializable.
	 */
	private final List<Field> saveSerializeableFields;
	/**
	 * Cached list of all fields that are update serializeable.
	 */
	private final List<Field> updateSerializeableFields;
	/**
	 * Queue of all update requests that are requested for this tile entity.
	 */
	private Queue<TileEntityUpdateRequest> updateRequestQueue;
	/**
	 * Indicates whether or not this tile entity should be marked dirty.
	 */
	private boolean shouldMarkDirty;
	/**
	 * Keeps track of which team placed this entity.
	 */
	private TeamComponent teamComponent;

	public TileEntityBase(BlockEntityTypeAllocator<? extends BlockEntity> allocator, BlockPos pos, BlockState state) {
		super(allocator.getType(), pos, state);
		components = new LinkedHashMap<String, AbstractTileEntityComponent>();
		updateRequestQueue = new LinkedList<TileEntityUpdateRequest>();
		isValid = true;
		hasFirstPlacedMethodRun = false;
		saveSerializeableFields = SerializationUtilities.getSaveSerializeableFields(this);
		updateSerializeableFields = SerializationUtilities.getUpdateSerializeableFields(this);
		registerComponent(teamComponent = new TeamComponent("team"));
	}

	public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (blockEntity instanceof TileEntityBase) {
			((TileEntityBase) blockEntity).tick();
		} else {
			StaticPower.LOGGER.error("Attempting to call TileEntityBase ticker for block entity that doesn not inherit from TileEntityBase.");
		}
	}

	public void tick() {
		if (!hasPostInitRun) {
			hasPostInitRun = true;
			postInit(level, worldPosition, level.getBlockState(worldPosition));
			for (AbstractTileEntityComponent comp : components.values()) {
				comp.onInitializedInWorld(level, worldPosition, !hasFirstPlacedMethodRun);
			}
			hasFirstPlacedMethodRun = true;
		}
		// Pre process all the components.
		preProcessUpdateComponents();

		// Call the process method for any inheritors to use.
		process();

		// If an update is queued, perform the update.
		if (updateRequestQueue.size() > 0) {
			// Debug the update.
			StaticPower.LOGGER.debug(String.format("Updating block at position: %1$s with name: %2$s with %3$d updates queued!", getBlockPos().toString(),
					getLevel().getBlockState(getBlockPos()), updateRequestQueue.size()));

			// Calculate the flag to use.
			int flags = 0;
			boolean shouldSync = false;
			boolean renderOnDataSync = false;
			while (!updateRequestQueue.isEmpty()) {
				TileEntityUpdateRequest request = updateRequestQueue.poll();
				flags |= request.getFlags();
				if (request.getShouldSyncData()) {
					shouldSync = true;
				}
				if (request.getShouldRenderOnDataSync()) {
					renderOnDataSync = true;
				}
			}

			// Update the block rendering state.
			if (getLevel().isClientSide()) {
				addRenderingUpdateRequest();
			}

			// Perform the block update.
			if (flags > 0) {
				level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), getBlockState(), getBlockState(), flags, 512);
			}

			// Perform a data sync if requested.
			if (shouldSync && !getLevel().isClientSide()) {
				synchronizeDataToPlayersInRadius(64, renderOnDataSync);
			}

			// If we also want to mark dirty, do so.
			if (shouldMarkDirty) {
				shouldMarkDirty = false;
				setChanged();
			}
		}

		// Post process all the components
		postProcessUpdateComponents();
	}

	protected void postInit(Level world, BlockPos pos, BlockState state) {
		for (AbstractTileEntityComponent component : components.values()) {
			component.onOwningTileEntityPostInit(hasFirstPlacedMethodRun);
		}
	}

	@Override
	public void clearRemoved() {
		super.clearRemoved();

	}

	@Override
	public void setRemoved() {
		for (AbstractTileEntityComponent component : components.values()) {
			component.onOwningTileEntityRemoved();
		}

		// Call the super AFTER everything has been cleaned up.
		super.setRemoved();
	}

	/**
	 * This method is raised on tick after the component have had their
	 * {@link #AbstractTileEntityComponent.preProcessUpdate()} and before they've
	 * had their {@link #AbstractTileEntityComponent.postProcessUpdate()} called. Do
	 * NOT override {@link #tick()} unless explicitly required.
	 */
	public void process() {

	}

	public void synchronizeDataToPlayersInRadius(int radius, boolean triggerRenderUpdate) {
		if (!getLevel().isClientSide()) {
			NetworkMessage msg = new TileEntityBasicSyncPacket(this, triggerRenderUpdate);
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getBlockPos(), radius, msg);
		} else {
			StaticPower.LOGGER.warn(String.format("Calling #synchronizeDataToPlayersInRadius() on the client is a no-op. Called at position: %1$s.", getBlockPos().toString()));
		}
	}

	public void synchronizeDataToContainerListener(ServerPlayer player) {
		if (!getLevel().isClientSide()) {
			NetworkMessage msg = new TileEntityBasicSyncPacket(this, false);
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), msg);
		} else {
			StaticPower.LOGGER.warn(String.format("Calling #synchronizeDataToContainerListener() on the client is a no-op. Called at position: %1$s.", getBlockPos().toString()));
		}
	}

	public void addUpdateRequest(TileEntityUpdateRequest request, boolean markDirty) {
		this.updateRequestQueue.add(request);
		if (markDirty) {
			shouldMarkDirty = true;
		}
	}

	public void addRenderingUpdateRequest() {
		if (getLevel().isClientSide()) {
			requestModelDataUpdate();
		} else {
			StaticPower.LOGGER.warn(String.format("Calling #addRenderingUpdateRequest() on the server is a no-op. Called at position: %1$s.", getBlockPos().toString()));
		}
	}

	public void onPlaced(BlockPlaceContext context, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		for (AbstractTileEntityComponent comp : components.values()) {
			comp.onPlaced(context, state, placer, stack);
		}
	}

	public InteractionResult onBlockActivated(BlockState currentState, Player player, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.PASS;
	}

	public void onGuiEntered(BlockState currentState, Player player, InteractionHand hand, BlockHitResult hit) {

	}

	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		for (AbstractTileEntityComponent comp : components.values()) {
			comp.onOwningBlockBroken(state, newState, isMoving);
		}
		isValid = false;

		// Add all the items that are currently in an inventory.
		if (worldPosition != null) {
			TileEntityBase baseTe = level.getBlockEntity(worldPosition) instanceof TileEntityBase ? (TileEntityBase) level.getBlockEntity(worldPosition) : null;
			if (baseTe != null) {
				for (InventoryComponent comp : baseTe.getComponents(InventoryComponent.class)) {
					// Skip components that should not drop their contents.
					if (!comp.shouldDropContentsOnBreak()) {
						continue;
					}
					// Capture all the items in the component.
					for (int i = 0; i < comp.getSlots(); i++) {
						ItemStack extracted = comp.extractItem(i, Integer.MAX_VALUE, false);
						if (!extracted.isEmpty()) {
							WorldUtilities.dropItem(level, worldPosition, extracted);
						}
					}
				}
			}
		}

	}

	public void onBlockReplaced(BlockState state, BlockState newState, boolean isMoving) {

	}

	public void onBlockLeftClicked(BlockState currentState, Player player) {

	}

	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos, boolean isMoving) {
		for (AbstractTileEntityComponent comp : components.values()) {
			comp.onNeighborChanged(currentState, neighborPos, isMoving);
		}
	}

	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		for (AbstractTileEntityComponent comp : components.values()) {
			comp.onNeighborReplaced(state, direction, facingState, FacingPos);
		}
	}

	/**
	 * Checks to see if this tile entity is still valid. This value is set to true
	 * when the tile entity is created and set to false when
	 * {@link #onBlockBroken(BlockState, BlockState, boolean)} or
	 * {@link #onBlockReplaced(BlockState, BlockState, boolean)} are called. This is
	 * useful as {@link #isRemoved()} is not set until after those methods have been
	 * called.
	 * 
	 * @return
	 */
	public boolean isValid() {
		return isValid;
	}

	public boolean isOnClientSide() {
		return getLevel().isClientSide();
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	private static double getBlockReachDistanceClient() {
		return Minecraft.getInstance().gameMode.getPickRange();
	}

	public void transferItemInternally(InventoryComponent fromInv, int fromSlot, InventoryComponent toInv, int toSlot) {
		transferItemInternally(1, fromInv, fromSlot, toInv, toSlot);
	}

	public void transferItemInternally(int count, InventoryComponent fromInv, int fromSlot, InventoryComponent toInv, int toSlot) {
		toInv.insertItem(toSlot, fromInv.extractItem(fromSlot, count, false), false);
	}

	public Direction getFacingDirection() {
		// If the world is null, return UP and log the error.
		if (getLevel() == null) {
			LOGGER.error("There was an attempt to get the facing direction before the block has been fully placed in the world! TileEntity: %1$s at position: %2$s.",
					getDisplayName().getString(), worldPosition);
			return Direction.UP;
		}

		// Attempt to get the block state for horizontal facing.
		if (getLevel().getBlockState(worldPosition).hasProperty(HorizontalDirectionalBlock.FACING)) {
			return getLevel().getBlockState(getBlockPos()).getValue(HorizontalDirectionalBlock.FACING);
		} else {
			return Direction.UP;
		}
	}

	public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	/* Components */
	/**
	 * Registers a {@link TileEntityComponent} to this {@link TileEntity}.
	 * 
	 * @param component The component to register.
	 */
	public void registerComponent(AbstractTileEntityComponent component) {
		components.put(component.getComponentName(), component);
		component.onRegistered(this);
	}

	/**
	 * Registers a {@link TileEntityComponent} to this {@link TileEntity} at the top
	 * of the component stack. This will override capability behaviour of lower
	 * components.
	 * 
	 * @param component The component to register.
	 */
	public void registerComponentOverride(AbstractTileEntityComponent component) {
		LinkedHashMap<String, AbstractTileEntityComponent> temp = new LinkedHashMap<String, AbstractTileEntityComponent>();
		temp.put(component.getComponentName(), component);
		temp.putAll(components);
		components = temp;
		component.onRegistered(this);
	}

	/**
	 * Removes a {@link TileEntityComponent} from this {@link TileEntity}.
	 * 
	 * @param component The component to remove.
	 * @return True if the component was removed, false otherwise.
	 */
	public boolean removeComponent(AbstractTileEntityComponent component) {
		return components.remove(component.getComponentName()) != null;
	}

	/**
	 * Gets all the components registered to this tile entity.
	 * 
	 * @return The list of all components registered to this tile entity.
	 */
	public Collection<AbstractTileEntityComponent> getComponents() {
		return components.values();
	}

	/**
	 * Gets all the components registered to this tile entity of the specified type.
	 * 
	 * @param <T>  The type of the tile entity component.
	 * @param type The class of the tile entity component.
	 * @return A list of tile entity components that inherit from the provided type.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTileEntityComponent> List<T> getComponents(Class<T> type) {
		List<T> output = new ArrayList<>();
		for (AbstractTileEntityComponent component : components.values()) {
			if (type.isInstance(component)) {
				output.add((T) component);
			}
		}
		return output;
	}

	/**
	 * Gets a component using the component's name.
	 * 
	 * @param <T>           The type of the tile entity component.
	 * @param type          The class of the tile entity component.
	 * @param componentName The name of the component.
	 * @return The component with the provided name if one exists, null otherwise.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTileEntityComponent> T getComponent(Class<T> type, String componentName) {
		if (components.containsKey(componentName)) {
			return (T) components.get(componentName);
		}
		return null;
	}

	/**
	 * Gets the first component of the provided type. This is useful for trying to
	 * access components that there should only really be one of (ex.
	 * {@link RedstoneControlComponent}).
	 * 
	 * @param <T>  The type of the tile entity component.
	 * @param type The class of the tile entity component.
	 * 
	 * @return A reference to the component if found, or null otherwise.
	 */
	@SuppressWarnings("unchecked")
	public <T extends AbstractTileEntityComponent> T getComponent(Class<T> type) {
		for (AbstractTileEntityComponent component : components.values()) {
			if (type.isInstance(component)) {
				return (T) component;
			}
		}
		return null;
	}

	/**
	 * Indicates if this tile entity has a component of the provided type.
	 * 
	 * @param type The type of the tile entity component.
	 * @return True if this tile entity has at least one component of that type,
	 *         false otherwise.
	 */
	public <T extends AbstractTileEntityComponent> boolean hasComponentOfType(Class<T> type) {
		for (AbstractTileEntityComponent component : components.values()) {
			if (type.isInstance(component)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasPostInitRun() {
		return hasPostInitRun;
	}

	/**
	 * Calls the pre-process methods on all the components.
	 */
	private void preProcessUpdateComponents() {
		for (AbstractTileEntityComponent component : components.values()) {
			component.preProcessUpdate();
		}
	}

	/**
	 * Calls the post-process methods on all the components.
	 */
	private void postProcessUpdateComponents() {
		for (AbstractTileEntityComponent component : components.values()) {
			component.postProcessUpdate();
		}
	}

	public List<InventoryComponent> getPriorityOrderedInventories() {
		// Get all the inventories.
		List<InventoryComponent> inventories = getComponents(InventoryComponent.class);

		// Remove any inventories that are not enabled for shift click.
		for (int i = inventories.size() - 1; i >= 0; i--) {
			if (!inventories.get(i).isShiftClickEnabled()) {
				inventories.remove(i);
			}
		}

		// Sort the inventories.
		Comparator<InventoryComponent> inventoryComparator = Comparator.comparingInt(InventoryComponent::getShiftClickPriority).reversed();
		inventories.sort(inventoryComparator);

		// Return the sorted list.
		return inventories;
	}

	public int getWeakPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		int output = 0;
		for (AbstractTileEntityComponent comp : components.values()) {
			output = Math.max(output, comp.getWeakPower(blockState, blockAccess, pos, side));
		}
		return output;
	}

	public int getStrongPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		int output = 0;
		for (AbstractTileEntityComponent comp : components.values()) {
			output = Math.max(output, comp.getStrongPower(blockState, blockAccess, pos, side));
		}
		return output;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		for (AbstractTileEntityComponent comp : components.values()) {
			LazyOptional<T> capability = comp.provideCapability(cap, side);
			if (capability.isPresent()) {
				return capability;
			}
		}
		return super.getCapability(cap, side);
	}

	public TeamComponent getTeamComponent() {
		return teamComponent;
	}

	@Override
	public CompoundTag serializeOnBroken(CompoundTag nbt) {
		serializeSaveNbt(nbt);
		return serializeUpdateNbt(nbt, false);
	}

	@Override
	public void deserializeOnPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		deserializeSaveNbt(nbt);
		deserializeUpdateNbt(nbt, false);
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return false;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		return false;
	}

	/**
	 * This method can be overridden to serialize any data that needs to be
	 * serialized each block update. This method, alongside
	 * {@link #serializeSaveNbt(CompoundNBT)}, is called in the parent
	 * {@link TileEntity}'s {@link #write(CompoundNBT)} method. So values should
	 * either appear in this method, or in the 'save' variant.
	 * 
	 * @param nbt The {@link CompoundNBT} to serialize to.
	 * @return The same {@link CompoundNBT} that was provided.
	 */
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		// Serialize each component to its own NBT tag and then add that to the master
		// tag. Catch errors on a per component basis to prevent one component from
		// breaking all the rest.
		for (AbstractTileEntityComponent component : components.values()) {
			CompoundTag componentTag = nbt.contains(component.getComponentName()) ? nbt.getCompound(component.getComponentName()) : new CompoundTag();
			component.serializeUpdateNbt(componentTag, fromUpdate);
			nbt.put(component.getComponentName(), componentTag);
		}
		SerializationUtilities.serializeFieldsToNbt(nbt, updateSerializeableFields, this);
		return nbt;
	}

	/**
	 * This method should be used to deserialize any data that is sent by the
	 * {@link #serializeUpdateNbt(CompoundNBT)}.
	 * 
	 * @param nbt The NBT data to deserialize from from.
	 */
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		// Iterate through all the components and deserialize each one. Catch errors on
		// a per component basis to prevent one component from breaking all the rest.
		for (AbstractTileEntityComponent component : components.values()) {
			if (nbt.contains(component.getComponentName())) {
				component.deserializeUpdateNbt(nbt.getCompound(component.getComponentName()), fromUpdate);
			}
		}
		SerializationUtilities.deserializeFieldsToNbt(nbt, updateSerializeableFields, this);
	}

	/**
	 * This method should be used to serialize any data that does NOT need to be
	 * serialized and synchronized each block update. This is useful for serialize
	 * information that is already synchronized with a packet (eg. Redstone Control
	 * State/Side Configuration) and should only be used when storing to disk/saving
	 * a chunk.This method, alongside {@link #serializeUpdateNbt(CompoundNBT)}, is
	 * called in the parent {@link TileEntity}'s {@link #write(CompoundNBT)} method.
	 * So values should either appear in this method, or in the 'update' variant.
	 * 
	 * @param nbt The {@link CompoundNBT} to serialize to.
	 * @return The same {@link CompoundNBT} that was provided.
	 */
	public CompoundTag serializeSaveNbt(CompoundTag nbt) {
		// Serialize each component to its own NBT tag and then add that to the master
		// tag. Catch errors on a per component basis to prevent one component from
		// breaking all the rest.
		for (AbstractTileEntityComponent component : components.values()) {
			CompoundTag componentTag = nbt.contains(component.getComponentName()) ? nbt.getCompound(component.getComponentName()) : new CompoundTag();
			component.serializeSaveNbt(componentTag);
			nbt.put(component.getComponentName(), componentTag);
		}
		SerializationUtilities.serializeFieldsToNbt(nbt, saveSerializeableFields, this);
		return nbt;
	}

	/**
	 * This method should be used to deserialize any data that is sent by
	 * {@link #serializeSaveNbt(CompoundNBT)}.
	 * 
	 * @param nbt The {@link CompoundNBT} to deserialize from.
	 */
	public void deserializeSaveNbt(CompoundTag nbt) {
		// Iterate through all the components and deserialize each one. Catch errors on
		// a per component basis to prevent one component from breaking all the rest.
		for (AbstractTileEntityComponent component : components.values()) {
			if (nbt.contains(component.getComponentName())) {
				component.deserializeSaveNbt(nbt.getCompound(component.getComponentName()));
			}
		}
		SerializationUtilities.deserializeFieldsToNbt(nbt, saveSerializeableFields, this);
	}

	/**
	 * Serializes an update packet to send to the client. This calls
	 * {@link #serializeUpdateNbt(CompoundNBT)}.
	 */
	@Override
	@Nullable
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag nbtTagCompound = new CompoundTag();
		// Make sure we only run this AFTER the post init has run.
		// Otherwise what happens is we send this update packet with data that MAY be
		// modified
		// during post init and therefore, for a tick or two, the client has the wrong
		// data.
		if (hasPostInitRun) {
			serializeUpdateNbt(nbtTagCompound, true);
		}
		return ClientboundBlockEntityDataPacket.create(this, (entity) -> nbtTagCompound);
	}

	/**
	 * Handles a data packet from the server to update this local
	 * {@link TileEntity}. This calls {@link #deserializeUpdateNbt(CompoundNBT)}.
	 */
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		super.onDataPacket(net, pkt);
		// Because we wait for post init from the #getUpdatePacket() method, we must
		// wait here too.
		if (hasPostInitRun && pkt.getTag() != null) {
			deserializeUpdateNbt(pkt.getTag(), true);
		}

		// Call mark and notify locally.
		getLevel().markAndNotifyBlock(getBlockPos(), getLevel().getChunkAt(getBlockPos()), getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE, 512);
	}

	/**
	 * Creates a tag containing all of the TileEntity information, used by vanilla
	 * to transmit from server to client. This calls both
	 * {@link #serializeUpdateNbt(CompoundNBT)} and
	 * {@link #serializeSaveNbt(CompoundNBT)} by calling
	 * {@link #write(CompoundNBT)}.
	 */
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtTagCompound = super.getUpdateTag();
		saveAdditional(nbtTagCompound);
		return nbtTagCompound;
	}

	/**
	 * Populates this TileEntity with information from the tag, used by vanilla to
	 * transmit from server to client. This calls both
	 * {@link #deserializeUpdateNbt(CompoundNBT)} and
	 * {@link #deserializeSaveNbt(CompoundNBT)} by calling
	 * {@link #read(CompoundNBT)}.
	 */
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		load(tag);
	}

	/**
	 * Serializes this {@link TileEntity} to the provided tag.
	 */
	@Override
	public void saveAdditional(CompoundTag parentNBTTagCompound) {
		super.saveAdditional(parentNBTTagCompound);
		serializeSaveNbt(parentNBTTagCompound);
		serializeUpdateNbt(parentNBTTagCompound, false);
	}

	/**
	 * Deserializes this {@link TileEntity} from the provided tag.
	 */
	@Override
	public void load(CompoundTag parentNBTTagCompound) {
		super.load(parentNBTTagCompound);
		deserializeUpdateNbt(parentNBTTagCompound, false);
		deserializeSaveNbt(parentNBTTagCompound);
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		for (AbstractTileEntityComponent component : components.values()) {
			component.getModelData(builder);
		}
		getAdditionalModelData(builder);
		return builder.build();
	}

	protected void getAdditionalModelData(ModelDataMap.Builder builder) {

	}

	/**
	 * Create the container here. Null by default.
	 */
	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		LOGGER.error(String.format("TileEntity: %1$s did not override the method #createMenu. The container for this TE is missing.", getDisplayName().getString()));
		return null;
	}

	/**
	 * In most cases, we should only return the position of this BE, but in the case
	 * where you want to remotely access another BE from this one, return the
	 * position of the other BE.
	 * 
	 * @return
	 */
	public BlockPos getContainerReferencedBlockPos() {
		return getBlockPos();
	}

	/**
	 * Return the name of this tile entity.
	 */
	@Override
	public Component getDisplayName() {
		if (getBlockState() != null && getBlockState().getBlock() != null) {
			return new TranslatableComponent(getBlockState().getBlock().getDescriptionId());
		}
		return new TextComponent("**ERROR**");
	}
}