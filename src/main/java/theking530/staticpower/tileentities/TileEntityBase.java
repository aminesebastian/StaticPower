package theking530.staticpower.tileentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.components.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities;
import theking530.staticpower.tileentities.utilities.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.tileentities.utilities.interfaces.IBreakSerializeable;

public abstract class TileEntityBase extends TileEntity implements ITickableTileEntity, IBreakSerializeable, INamedContainerProvider {
	public static final Logger LOGGER = LogManager.getLogger(TileEntityBase.class);
	protected final static Random RANDOM = new Random();
	private boolean isValid;
	protected boolean DisableFaceInteraction;
	private HashMap<String, AbstractTileEntityComponent> Components;

	/**
	 * If true, on the next tick, the tile entity will be synced using the methods
	 * {@link #serializeUpdateNbt(CompoundNBT)} and
	 * {@link #deserializeUpdateNbt(CompoundNBT)}.
	 */
	private boolean updateQueued;

	public TileEntityBase(TileEntityType<?> teType) {
		super(teType);
		Components = new HashMap<String, AbstractTileEntityComponent>();
		updateQueued = false;
		isValid = true;
		disableFaceInteraction();
	}

	/**
	 * Disables pipe interaction with the face of this block.
	 */
	public void disableFaceInteraction() {
		DisableFaceInteraction = true;
	}

	@Override
	public void tick() {
		// Pre process all the components.
		preProcessUpdateComponents();

		// Call the process method for any inheritors to use.
		process();

		// If an update is queued, perform the update.
		if (updateQueued) {
			world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), 1 | 2);
			if(world.isRemote) {
				ModelDataManager.requestModelDataRefresh(this);
			}
			updateQueued = false;
		}

		// Post process all the components
		postProcessUpdateComponents();
	}

	@Override
	public void setWorldAndPos(World world, BlockPos pos) {
		super.setWorldAndPos(world, pos);
		onInitializedInWorld(world, pos);
	}

	@Override
	public void validate() {
		super.validate();
		for (AbstractTileEntityComponent component : Components.values()) {
			component.onOwningTileEntityValidate();
		}
	}

	@Override
	public void remove() {
		for (AbstractTileEntityComponent component : Components.values()) {
			component.onOwningTileEntityRemoved();
		}

		// Call the super AFTER everything has been cleaned up.
		super.remove();
	}

	/**
	 * This method is raised on tick after the component have had their
	 * {@link #AbstractTileEntityComponent.preProcessUpdate()} and before they've
	 * had their {@link #AbstractTileEntityComponent.postProcessUpdate()} called. Do
	 * NOT override {@link #tick()} unless explicitly required.
	 */
	public void process() {

	}

	public void markTileEntityForSynchronization() {
		updateQueued = true;
		markDirty();
	}

	public void onPlaced(BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (hasComponentOfType(SideConfigurationComponent.class)) {
			if (DisableFaceInteraction) {
				getComponent(SideConfigurationComponent.class).setWorldSpaceDirectionConfiguration(SideConfigurationUtilities.getDirectionFromSide(BlockSide.FRONT, getFacingDirection()), MachineSideMode.Never);
			}
		}
	}

	public ActionResultType onBlockActivated(BlockState currentState, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return ActionResultType.PASS;
	}

	public void onGuiEntered(BlockState currentState, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {

	}

	public void onBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		isValid = false;
	}

	public void onBlockReplaced(BlockState state, BlockState newState, boolean isMoving) {

	}

	public void onBlockLeftClicked(BlockState currentState, PlayerEntity player) {

	}

	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos) {
		for (AbstractTileEntityComponent comp : Components.values()) {
			comp.onNeighborChanged(currentState, neighborPos);
		}
	}

	public void onInitializedInWorld(World world, BlockPos pos) {

	}

	public void updatePostPlacement(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		for (AbstractTileEntityComponent comp : Components.values()) {
			comp.updatePostPlacement(state, direction, facingState, FacingPos);
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

	@OnlyIn(Dist.CLIENT)
	private static double getBlockReachDistanceClient() {
		return Minecraft.getInstance().playerController.getBlockReachDistance();
	}

	public void transferItemInternally(InventoryComponent fromInv, int fromSlot, InventoryComponent toInv, int toSlot) {
		toInv.insertItem(toSlot, fromInv.extractItem(fromSlot, 1, false), false);
	}

	public Direction getFacingDirection() {
		// If the world is null, return UP and log the error.
		if (getWorld() == null) {
			StaticPower.LOGGER.error("There was an attempt to get the facing direction before the block has been fully placed in the world! TileEntity: %1$s at position: %2$s.", getDisplayName().getFormattedText(), pos);
			return Direction.UP;
		}

		// Attempt to get the block state for horizontal facing.
		if (getWorld().getBlockState(pos).has(HorizontalBlock.HORIZONTAL_FACING)) {
			return getWorld().getBlockState(getPos()).get(HorizontalBlock.HORIZONTAL_FACING);
		} else {
			return Direction.UP;
		}
	}

	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	/* Serializeable */
	public CompoundNBT serializeOnBroken(CompoundNBT nbt) {
		write(nbt);
		return nbt;
	}

	public void deserializeOnPlaced(CompoundNBT nbt, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		read(nbt);
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return true;
	}

	@Override
	public boolean shouldDeserializeWhenPlaced(CompoundNBT nbt, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		return true;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		for (AbstractTileEntityComponent comp : Components.values()) {
			LazyOptional<T> capability = comp.provideCapability(cap, side);
			if (capability.isPresent()) {
				return capability;
			}
		}
		return super.getCapability(cap, side);
	}

	/* Components */
	/**
	 * Registers a {@link TileEntityComponent} to this {@link TileEntity}.
	 * 
	 * @param component The component to register.
	 */
	public void registerComponent(AbstractTileEntityComponent component) {
		Components.put(component.getComponentName(), component);
		component.onRegistered(this);
	}

	/**
	 * Removes a {@link TileEntityComponent} from this {@link TileEntity}.
	 * 
	 * @param component The component to remove.
	 * @return True if the component was removed, false otherwise.
	 */
	public boolean removeComponent(AbstractTileEntityComponent component) {
		return Components.remove(component.getComponentName()) != null;
	}

	/**
	 * Gets all the components registered to this tile entity.
	 * 
	 * @return The list of all components registered to this tile entity.
	 */
	public Collection<AbstractTileEntityComponent> getComponents() {
		return Components.values();
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
		for (AbstractTileEntityComponent component : Components.values()) {
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
		;
		if (Components.containsKey(componentName)) {
			return (T) Components.get(componentName);
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
		for (AbstractTileEntityComponent component : Components.values()) {
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
		for (AbstractTileEntityComponent component : Components.values()) {
			if (type.isInstance(component)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calls the pre-process methods on all the components.
	 */
	private void preProcessUpdateComponents() {
		for (AbstractTileEntityComponent component : Components.values()) {
			component.preProcessUpdate();
		}
	}

	/**
	 * Calls the post-process methods on all the components.
	 */
	private void postProcessUpdateComponents() {
		for (AbstractTileEntityComponent component : Components.values()) {
			component.postProcessUpdate();
		}
	}

	public List<MachineSideMode> getValidSideConfigurations() {
		List<MachineSideMode> modes = new ArrayList<MachineSideMode>();
		modes.add(MachineSideMode.Input);
		modes.add(MachineSideMode.Output);
		modes.add(MachineSideMode.Regular);
		modes.add(MachineSideMode.Disabled);
		return modes;
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
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		// Serialize each component to its own NBT tag and then add that to the master
		// tag. Catch errors on a per component basis to prevent one component from
		// breaking all the rest.
		for (AbstractTileEntityComponent component : Components.values()) {
			CompoundNBT componentTag = nbt.contains(component.getComponentName()) ? nbt.getCompound(component.getComponentName()) : new CompoundNBT();
			component.serializeUpdateNbt(componentTag, fromUpdate);
			nbt.put(component.getComponentName(), componentTag);
		}
		nbt.putBoolean("DISABLE_FACE", DisableFaceInteraction);
		return nbt;
	}

	/**
	 * This method should be used to deserialize any data that is sent by the
	 * {@link #serializeUpdateNbt(CompoundNBT)}.
	 * 
	 * @param nbt The NBT data to deserialize from from.
	 */
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		// Iterate through all the components and deserialize each one. Catch errors on
		// a per component basis to prevent one component from breaking all the rest.
		for (AbstractTileEntityComponent component : Components.values()) {
			if (nbt.contains(component.getComponentName())) {
				component.deserializeUpdateNbt(nbt.getCompound(component.getComponentName()), fromUpdate);
			}
		}
		DisableFaceInteraction = nbt.getBoolean("DISABLE_FACE");
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
	public CompoundNBT serializeSaveNbt(CompoundNBT nbt) {
		// Serialize each component to its own NBT tag and then add that to the master
		// tag. Catch errors on a per component basis to prevent one component from
		// breaking all the rest.
		for (AbstractTileEntityComponent component : Components.values()) {
			CompoundNBT componentTag = nbt.contains(component.getComponentName()) ? nbt.getCompound(component.getComponentName()) : new CompoundNBT();
			component.serializeSaveNbt(componentTag);
			nbt.put(component.getComponentName(), componentTag);
		}

		return nbt;
	}

	/**
	 * This method should be used to deserialize any data that is sent by
	 * {@link #serializeSaveNbt(CompoundNBT)}.
	 * 
	 * @param nbt The {@link CompoundNBT} to deserialize from.
	 */
	public void deserializeSaveNbt(CompoundNBT nbt) {
		// Iterate through all the components and deserialize each one. Catch errors on
		// a per component basis to prevent one component from breaking all the rest.
		for (AbstractTileEntityComponent component : Components.values()) {
			if (nbt.contains(component.getComponentName())) {
				component.deserializeSaveNbt(nbt.getCompound(component.getComponentName()));
			}
		}
	}

	/**
	 * Serializes an update packet to send to the client. This calls
	 * {@link #serializeUpdateNbt(CompoundNBT)}.
	 */
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbtTagCompound = new CompoundNBT();
		serializeUpdateNbt(nbtTagCompound, true);
		return new SUpdateTileEntityPacket(this.pos, 0, nbtTagCompound);
	}

	/**
	 * Handles a data packet from the server to update this local
	 * {@link TileEntity}. This calls {@link #deserializeUpdateNbt(CompoundNBT)}.
	 */
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		deserializeUpdateNbt(pkt.getNbtCompound(), true);
		this.markTileEntityForSynchronization();
	}

	/**
	 * Creates a tag containing all of the TileEntity information, used by vanilla
	 * to transmit from server to client. This calls both
	 * {@link #serializeUpdateNbt(CompoundNBT)} and
	 * {@link #serializeSaveNbt(CompoundNBT)} by calling
	 * {@link #write(CompoundNBT)}.
	 */
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbtTagCompound = super.getUpdateTag();
		write(nbtTagCompound);
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
	public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
		read(tag);
	}

	/**
	 * Serializes this {@link TileEntity} to the provided tag.
	 */
	@Override
	public CompoundNBT write(CompoundNBT parentNBTTagCompound) {
		super.write(parentNBTTagCompound);
		serializeSaveNbt(parentNBTTagCompound);
		return serializeUpdateNbt(parentNBTTagCompound, false);
	}

	/**
	 * Deserializes this {@link TileEntity} from the provided tag.
	 */
	@Override
	public void read(CompoundNBT parentNBTTagCompound) {
		super.read(parentNBTTagCompound);
		deserializeUpdateNbt(parentNBTTagCompound, false);
		deserializeSaveNbt(parentNBTTagCompound);
	}

	@Nonnull
	@Override
	public IModelData getModelData() {
		ModelDataMap.Builder builder = new ModelDataMap.Builder();
		for (AbstractTileEntityComponent component : Components.values()) {
			component.getModelData(builder);
		}
		return builder.build();
	}

	/**
	 * Create the container here. Null by default.
	 */
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		LOGGER.error(String.format("TileEntity: %1$s did not override the method #createMenu. The container for this TE is broken.", getDisplayName().getFormattedText()));
		return null;
	}

	/**
	 * Return the name of this tile entity.
	 */
	@Override
	public ITextComponent getDisplayName() {
		if (getBlockState() != null && getBlockState().getBlock() != null) {
			return new TranslationTextComponent(getBlockState().getBlock().getTranslationKey());
		}
		return new StringTextComponent("**ERROR**");
	}
}