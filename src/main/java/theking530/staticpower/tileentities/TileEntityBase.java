package theking530.staticpower.tileentities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentities.components.ITileEntityComponent;
import theking530.staticpower.tileentities.components.TileEntityInventoryComponent;
import theking530.staticpower.tileentities.components.TileEntityRedstoneControlComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;
import theking530.staticpower.tileentities.utilities.RedstoneModeList.RedstoneMode;
import theking530.staticpower.tileentities.utilities.SideConfiguration;
import theking530.staticpower.tileentities.utilities.SideUtilities;
import theking530.staticpower.tileentities.utilities.SideUtilities.BlockSide;
import theking530.staticpower.tileentities.utilities.interfaces.IBreakSerializeable;
import theking530.staticpower.tileentities.utilities.interfaces.ISideConfigurable;
import theking530.staticpower.tileentities.utilities.interfaces.IUpgradeable;

public class TileEntityBase extends TileEntity implements ITickableTileEntity, ISideConfigurable, IUpgradeable, IBreakSerializeable, INamedContainerProvider {
	protected static final int DEFAULT_UPDATE_TIME = 2;
	protected final static Random RANDOM = new Random();

	protected boolean disableFaceInteraction;
	protected boolean wasWrenchedDoNotBreak;
	protected boolean wasPlaced;

	private HashSet<ITileEntityComponent> components;
	protected SideConfiguration ioSideConfiguration;

	@SuppressWarnings("unused")
	private int updateTimer = 0;
	@SuppressWarnings("unused")
	private int updateTime = DEFAULT_UPDATE_TIME;

	public final TileEntityRedstoneControlComponent redstoneControlComponent;
	public final TileEntityInventoryComponent upgradesInventoryComponent;

	/**
	 * If true, on the next tick, the tile entity will be synced using the methods
	 * {@link #serializeUpdateNbt(CompoundNBT)} and
	 * {@link #deserializeUpdateNbt(CompoundNBT)}.
	 */
	private boolean updateQueued;

	public TileEntityBase(TileEntityType<?> teType) {
		this(teType, 0);
	}

	public TileEntityBase(TileEntityType<?> teType, int upgradeSlotsCount) {
		super(teType);
		components = new HashSet<ITileEntityComponent>();
		wasPlaced = false;
		wasWrenchedDoNotBreak = false;
		ioSideConfiguration = new SideConfiguration();
		updateQueued = false;
		disableFaceInteraction();
		registerComponent(redstoneControlComponent = new TileEntityRedstoneControlComponent("RedstoneControl", RedstoneMode.Ignore));
		registerComponent(upgradesInventoryComponent = new TileEntityInventoryComponent("Upgrades", upgradeSlotsCount, MachineSideMode.Never));
	}

	/**
	 * Disables pipe interaction with the face of this block.
	 */
	public void disableFaceInteraction() {
		disableFaceInteraction = true;
	}

	@Override
	public void tick() {
		// If this tile entity is upgradeable, perform the ugprade tick.
		if (isUpgradeable()) {
			upgradeTick();
		}
		// If the redstone settings are valid, call the process method.
		if (redstoneControlComponent.passesRedstoneCheck()) {
			preProcessUpdateComponents();
			process();
			postProcessUpdateComponents();
		}
		// If an update is queued, perform the update.
		if (updateQueued) {
			world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), 1 | 2);
			updateQueued = false;
		}
		// Always mark this tile entity as dirty.
		markDirty();

		// Raise the on placed method if it was just placed.
		if (!wasPlaced) {
			onPlaced();
			wasPlaced = true;
		}
	}

	/**
	 * This method should be overriden for any processing.
	 */
	public void process() {
	}

	/**
	 * This method should be overriden to perform any on tick logic for any present
	 * upgrades.
	 */
	public void upgradeTick() {

	}

	public void markTileEntityForSynchronization() {
		updateQueued = true;
	}

	public void onPlaced() {
		if (isSideConfigurable()) {
			if (disableFaceInteraction) {
				setSideConfiguration(MachineSideMode.Never, BlockSide.FRONT);
			} else {
				setSideConfiguration(MachineSideMode.Disabled, BlockSide.FRONT);
			}

		}
	}

	@OnlyIn(Dist.CLIENT)
	private static double getBlockReachDistanceClient() {
		return Minecraft.getInstance().playerController.getBlockReachDistance();
	}

	public void transferItemInternally(ItemStackHandler fromInv, int fromSlot, ItemStackHandler toInv, int toSlot) {
		toInv.insertItem(toSlot, fromInv.extractItem(fromSlot, 1, false), false);
	}

	public Direction getFacingDirection() {
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
		return super.getCapability(cap, side);
	}

	/* Upgrade Handling */
	public boolean hasUpgrade(Item upgradeBase) {
		return !getUpgrade(upgradeBase).isEmpty();
	}

	public ItemStack getUpgrade(Item upgradeBase) {
		for (int i = 0; i < upgradesInventoryComponent.getSlots(); i++) {
			if (!upgradesInventoryComponent.getStackInSlot(i).isEmpty() && upgradesInventoryComponent.getStackInSlot(i).getItem() == upgradeBase) {
				return upgradesInventoryComponent.getStackInSlot(i);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public List<ItemStack> getAllUpgrades() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		ItemStack upgrade = ItemStack.EMPTY;
		int slot = -1;
		for (int i = 0; i < upgradesInventoryComponent.getSlots(); i++) {
			slot = i;
			upgrade = upgradesInventoryComponent.getStackInSlot(slot);
			if (!upgrade.isEmpty()) {
				list.add(upgrade);
			}
		}
		return list;
	}

	@Override
	public boolean canAcceptUpgrade(ItemStack upgrade) {
		return true;
	}

	@Override
	public boolean isUpgradeable() {
		return true;
	}

	/* Components */
	/**
	 * Registers a {@link TileEntityComponent} to this {@link TileEntity}.
	 * 
	 * @param component The component to register.
	 */
	public void registerComponent(ITileEntityComponent component) {
		components.add(component);
		component.onRegistered(this);
	}

	/**
	 * Removes a {@link TileEntityComponent} from this {@link TileEntity}.
	 * 
	 * @param component The component to remove.
	 * @return True if the component was removed, false otherwise.
	 */
	public boolean removeComponents(ITileEntityComponent component) {
		return components.remove(component);
	}

	/**
	 * Gets all the components registered to this tile entity.
	 * 
	 * @return The list of all components registered to this tile entity.
	 */
	public HashSet<ITileEntityComponent> getComponents() {
		return components;
	}

	/**
	 * Gets all the components registered to this tile entity of the specified type.
	 * 
	 * @param <T>  The type of the tile entity component.
	 * @param type The class of the tile entity component.
	 * @return A list of tile entity components that inherit from the provided type.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ITileEntityComponent> List<T> getComponentsOfType(Class<T> type) {
		List<T> output = new ArrayList<>();
		for (ITileEntityComponent component : components) {
			if (type.isInstance(component)) {
				output.add((T) component);
			}
		}
		return output;
	}

	/**
	 * Gets the first component of the provided type. This is useful for trying to
	 * access components that there should only really be one of (ex.
	 * {@link TileEntityRedstoneControlComponent}).
	 * 
	 * @param <T>  The type of the tile entity component.
	 * @param type The class of the tile entity component.
	 * @return A reference to the component if found, or null otherwise.
	 */
	@SuppressWarnings("unchecked")
	public <T extends ITileEntityComponent> T getFirstComponentOfType(Class<T> type) {
		for (ITileEntityComponent component : components) {
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
	public <T extends ITileEntityComponent> boolean hasComponentOfType(Class<T> type) {
		for (ITileEntityComponent component : components) {
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
		try {
			for (ITileEntityComponent component : components) {
				component.preProcessUpdate();
			}
		} catch (Exception e) {
			StaticPower.LOGGER.warn(String.format("An error occured while attempting to perform the TileEntityComponent's preprocess update for Tile Entity: $1%s at position: %2$s.",
					getDisplayName().getString(), getPos()), e);
		}
	}

	/**
	 * Calls the post-process methods on all the components.
	 */
	private void postProcessUpdateComponents() {
		try {
			for (ITileEntityComponent component : components) {
				component.postProcessUpdate();
			}
		} catch (Exception e) {
			StaticPower.LOGGER.warn(String.format("An error occured while attempting to perform the TileEntityComponent's postprocess update for Tile Entity: $1%s at position: %2$s.",
					getDisplayName().getString(), getPos()), e);
		}
	}

	/* Side Control */
	@Override
	public boolean isSideConfigurable() {
		return true;
	}

	@Override
	public MachineSideMode getSideConfiguration(BlockSide side) {
		Direction facing = SideUtilities.getDirectionFromSide(side, getFacingDirection());
		return getSideConfiguration(facing);
	}

	@Override
	public MachineSideMode getSideConfiguration(Direction facing) {
		return ioSideConfiguration.getSideConfiguration(facing);
	}

	@Override
	public MachineSideMode[] getSideConfigurations() {
		return ioSideConfiguration.getConfiguration();
	}

	@Override
	public void setSideConfiguration(MachineSideMode newMode, BlockSide side) {
		Direction facing = SideUtilities.getDirectionFromSide(side, getFacingDirection());
		setSideConfiguration(newMode, facing);
	}

	@Override
	public void setSideConfiguration(MachineSideMode newMode, Direction facing) {
		ioSideConfiguration.setSideConfiguration(facing, newMode);
	}

	public void onSidesConfigUpdate() {

	}

	public void resetSideConfiguration() {
		ioSideConfiguration.reset();
		onSidesConfigUpdate();
	}

	@Override
	public void incrementSideConfiguration(Direction side, SideIncrementDirection direction) {
		BlockSide blockSideEquiv = SideUtilities.getBlockSide(side, getFacingDirection());

		int newIndex = 0;
		do {
			MachineSideMode currentSideMode = getSideConfiguration(side);
			newIndex = direction == SideIncrementDirection.FORWARD ? currentSideMode.ordinal() + 1 : currentSideMode.ordinal() - 1;
			if (newIndex < 0) {
				newIndex += MachineSideMode.values().length;
			}
			newIndex %= MachineSideMode.values().length;
			setSideConfiguration(MachineSideMode.values()[newIndex], side);
		} while (!getValidSideConfigurations().contains(MachineSideMode.values()[newIndex]));

		if (disableFaceInteraction) {
			setSideConfiguration(MachineSideMode.Never, BlockSide.FRONT);
		}

		onSidesConfigUpdate();
		markTileEntityForSynchronization();
	}

	@Override
	public int getSideWithModeCount(MachineSideMode mode) {
		int count = 0;
		for (MachineSideMode sideMode : getSideConfigurations()) {
			if (sideMode == mode) {
				count++;
			}
		}
		return count;
	}

	public void setDefaultSideConfiguration(SideConfiguration configuration) {
		if (wasPlaced) {
			ioSideConfiguration = configuration;
			if (disableFaceInteraction) {
				setSideConfiguration(MachineSideMode.Disabled, BlockSide.FRONT);
			}
		} else {
			StaticPower.LOGGER.warn(String.format(
					"Tile Entity: %1$s attempted to change it's side configuration without having first been placed in the world. This is usually because an attempt was made to change the side configuration from within the constructor.",
					getDisplayName().getFormattedText()));
		}
	}

	@Override
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
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		for (ITileEntityComponent component : components) {
			component.serializeUpdateNbt(nbt);
		}
		if (isSideConfigurable()) {
			for (int i = 0; i < 6; i++) {
				nbt.putInt("SIDEMODE" + i, getSideConfiguration(Direction.values()[i]).ordinal());
			}
			nbt.putBoolean("DISABLE_FACE", disableFaceInteraction);
		}
		return nbt;
	}

	/**
	 * This method should be used to deserialize any data that is sent by the
	 * {@link #serializeUpdateNbt(CompoundNBT)}.
	 * 
	 * @param nbt The NBT data to deserialize from from.
	 */
	public void deserializeUpdateNbt(CompoundNBT nbt) {
		for (ITileEntityComponent component : components) {
			component.deserializeUpdateNbt(nbt);
		}
		if (isSideConfigurable()) {
			for (int i = 0; i < 6; i++) {
				setSideConfiguration(MachineSideMode.values()[nbt.getInt("SIDEMODE" + i)], Direction.values()[i]);
			}
			disableFaceInteraction = nbt.getBoolean("DISABLE_FACE");
		}
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
		for (ITileEntityComponent component : components) {
			component.serializeSaveNbt(nbt);
		}

		nbt.putBoolean("placed", wasPlaced);
		return nbt;
	}

	/**
	 * This method should be used to deserialize any data that is sent by
	 * {@link #serializeSaveNbt(CompoundNBT)}.
	 * 
	 * @param nbt The {@link CompoundNBT} to deserialize from.
	 */
	public void deserializeSaveNbt(CompoundNBT nbt) {
		for (ITileEntityComponent component : components) {
			component.deserializeSaveNbt(nbt);
		}
		if (nbt.contains("placed")) {
			wasPlaced = nbt.getBoolean("placed");
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
		serializeUpdateNbt(nbtTagCompound);
		return new SUpdateTileEntityPacket(this.pos, 0, nbtTagCompound);
	}

	/**
	 * Handles a data packet from the server to update this local
	 * {@link TileEntity}. This calls {@link #deserializeUpdateNbt(CompoundNBT)}.
	 */
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		deserializeUpdateNbt(pkt.getNbtCompound());
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
		CompoundNBT tag = serializeSaveNbt(parentNBTTagCompound);
		return serializeUpdateNbt(tag);
	}

	/**
	 * Deserializes this {@link TileEntity} from the provided tag.
	 */
	@Override
	public void read(CompoundNBT parentNBTTagCompound) {
		super.read(parentNBTTagCompound);
		deserializeUpdateNbt(parentNBTTagCompound);
		deserializeSaveNbt(parentNBTTagCompound);
	}

	/**
	 * Create the container here.
	 */
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return null;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new StringTextComponent("ERROR");
	}
}