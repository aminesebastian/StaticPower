package theking530.staticpower.cables;

import java.util.HashSet;
import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.utilities.RedstoneMode;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class AbstractCableProviderComponent extends AbstractTileEntityComponent {
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<boolean[]> DISABLED_CABLE_SIDES = new ModelProperty<>();
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<CableConnectionState[]> CABLE_CONNECTION_STATES = new ModelProperty<>();
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<ResourceLocation[]> CABLE_ATTACHMENT_MODELS = new ModelProperty<>();
	/** The type of this cable. */
	private final HashSet<ResourceLocation> SupportedNetworkModules;
	/**
	 * Keeps track of which sides of the cable are disabled. This value is a client
	 * copy of the master value which exists on the server.
	 */
	private final boolean[] DisabledSides;
	/**
	 * Cache for the connection states. This is updated every time a new baked model
	 * is requested AND also, on first placement.
	 */
	protected final CableConnectionState[] ConnectionStates;
	/** Container for all the attachments on this cable. */
	protected final ItemStack[] Attachments;

	public AbstractCableProviderComponent(String name, ResourceLocation... supportedModules) {
		super(name);

		// Capture the types.
		SupportedNetworkModules = new HashSet<ResourceLocation>();
		for (ResourceLocation module : supportedModules) {
			SupportedNetworkModules.add(module);
		}

		// Initialize the disabled sides, connection states, and attachments arrays.
		DisabledSides = new boolean[] { false, false, false, false, false, false };
		ConnectionStates = new CableConnectionState[] { CableConnectionState.NONE, CableConnectionState.NONE, CableConnectionState.NONE, CableConnectionState.NONE, CableConnectionState.NONE,
				CableConnectionState.NONE };
		Attachments = new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY };
	}

	public void preProcessUpdate() {
		if (getWorld().isRemote || (!getWorld().isRemote && getNetwork() != null)) {
			for (Direction dir : Direction.values()) {
				if (!Attachments[dir.ordinal()].isEmpty()) {
					ItemStack attachment = Attachments[dir.ordinal()];
					((AbstractCableAttachment) attachment.getItem()).attachmentTick(attachment, dir, this);
				}
			}
		}
	}

	/**
	 * Checks to see if the provided side is disabled. This should only be called on
	 * the client. The server should query the {@link ServerCable} directly.
	 * 
	 * @param side
	 * @return
	 */
	public boolean isSideDisabled(Direction side) {
		return DisabledSides[side.ordinal()];
	}

	public void setSideDisabledState(Direction side, boolean disabledState) {
		DisabledSides[side.ordinal()] = disabledState;
	}

	/**
	 * Gets the connection state on the provided side.
	 * 
	 * @param side
	 * @return
	 */
	public CableConnectionState getConnectionState(Direction side) {
		return ConnectionStates[side.ordinal()];
	}

	/**
	 * Gets the connection states on all sides.
	 * 
	 * @return
	 */
	public CableConnectionState[] getConnectionStates() {
		return ConnectionStates;
	}

	/**
	 * Allows us to provide additional data about state of the cable for rendering
	 * purposes.
	 */
	@Override
	public void getModelData(ModelDataMap.Builder builder) {
		scanForAttachments();
		builder.withInitial(DISABLED_CABLE_SIDES, DisabledSides).withInitial(CABLE_CONNECTION_STATES, ConnectionStates).withInitial(CABLE_ATTACHMENT_MODELS, getAttachmentModels());
	}

	public HashSet<ResourceLocation> getSupportedNetworkModuleTypes() {
		return this.SupportedNetworkModules;
	}

	/**
	 * When the owning tile entity is validated, we check to see if there is a cable
	 * wrapper in the network for this cable. If not, we provide one.
	 */
	@Override
	public void onOwningTileEntityValidate() {
		super.onOwningTileEntityValidate();
		// If we're on the server, check to see if the cable network manager for this
		// world is tracking a cable at this position. If it is not, add this cable for
		// tracking.
		if (!getTileEntity().getWorld().isRemote) {
			CableNetworkManager manager = CableNetworkManager.get(getWorld());
			if (!manager.isTrackingCable(getTileEntity().getPos())) {
				ServerCable wrapper = new ServerCable(getWorld(), getPos(), SupportedNetworkModules);
				//if(!SupportedNetworkModules.contains(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE)) {
					manager.addCable(wrapper);
				//}

			}
		}
	}

	/**
	 * After placed, we update the connection states.
	 */
	@Override
	public void updatePostPlacement(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		scanForAttachments();
	}

	/**
	 * When the owning tile entity is removed from the world, we remove the cable
	 * wrapper for this tile entity as well.
	 */
	@Override
	public void onOwningTileEntityRemoved() {
		super.onOwningTileEntityRemoved();
		// If we're on the server, get the cable manager and remove the cable at the
		// current position.
		if (!getWorld().isRemote) {
			CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
			manager.removeCable(getTileEntity().getPos());

			for (Direction dir : Direction.values()) {
				if (this.hasAttachment(dir)) {
					ItemStack attachment = Attachments[dir.ordinal()];
					removeAttachment(dir);
					WorldUtilities.dropItem(getWorld(), getPos(), attachment);
				}
			}
		}
	}

	/**
	 * Attaches an attachment to the provided side. Returns true if the attachment
	 * was added. Returns false otherwise.
	 * 
	 * @param attachment The attachment itemstack to add.
	 * @param side       The side to attach it to.
	 * @return True if the attachment was applied, false otherwise.
	 */
	public boolean attachAttachment(ItemStack attachment, Direction side) {
		// Check if we can attach the attachment.
		if (!canAttachAttachment(attachment)) {
			return false;
		}

		// If there is no attachment on the provided side, add it.
		if (Attachments[side.ordinal()].isEmpty()) {
			Attachments[side.ordinal()] = attachment.copy();
			Attachments[side.ordinal()].setCount(1);

			// Raise the on added method on the attachment.
			AbstractCableAttachment attachmentItem = (AbstractCableAttachment) Attachments[side.ordinal()].getItem();
			attachmentItem.onAddedToCable(Attachments[side.ordinal()], side, this);

			getTileEntity().markTileEntityForSynchronization();
			return true;
		}
		return false;
	}

	/**
	 * Removes an attachment from the provided side and returns the itemstack for
	 * the attachment. If there is no attachment, returns an empty Itemstack.
	 * 
	 * @param side The side to remove from.
	 * @return
	 */
	public ItemStack removeAttachment(Direction side) {
		// Ensure we have an attachment on the provided side.
		if (hasAttachment(side)) {
			// Get the attachment for the side.
			ItemStack output = Attachments[side.ordinal()];

			// Raise the on removed method on the attachment.
			AbstractCableAttachment attachmentItem = (AbstractCableAttachment) output.getItem();
			attachmentItem.onRemovedFromCable(output, side, this);

			// Remove the attachment and return it.
			Attachments[side.ordinal()] = ItemStack.EMPTY;
			getTileEntity().markTileEntityForSynchronization();
			return output;
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Checks to see if an attachment is attached on that side.
	 * 
	 * @param side The side to check for.
	 * @return Returns true if a attachment is applied on the provided side.
	 */
	public boolean hasAttachment(Direction side) {
		return !Attachments[side.ordinal()].isEmpty();
	}

	/**
	 * Gets the attachment on the provided side. If no attachment is present,
	 * returns an empty itemstack.
	 * 
	 * @param side The side to check for.
	 * @return
	 */
	public ItemStack getAttachment(Direction side) {
		return Attachments[side.ordinal()];
	}

	/**
	 * Checks to see if the provided attachment passes the redstone test.
	 * 
	 * @param attachment
	 * @return
	 */
	public boolean doesAttachmentPassRedstoneTest(ItemStack attachment) {
		AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();
		return RedstoneMode.evaluateRedstoneMode(attachmentItem.getRedstoneMode(attachment), getWorld(), getPos());
	}

	/**
	 * Gets the network this cable is currently connected to. NOTE: This is a server
	 * only method. Calling this on the client will throw an error.
	 * 
	 * @return
	 */
	public CableNetwork getNetwork() {
		return CableNetworkManager.get(getWorld()).isTrackingCable(getPos()) ? CableNetworkManager.get(getWorld()).getCable(getPos()).getNetwork() : null;
	}

	/**
	 * Gets the module from this network if present. We have to wrap it in an
	 * optional because while we can guarantee once this component is validated that
	 * the network is valid, since this component exposes external methods, other
	 * tile entity that are made valid before us may call some of our methods.
	 * 
	 * @param <T>        The type of the module.
	 * @param moduleType The resource location model type.
	 * @return
	 */
	public <T extends AbstractCableNetworkModule> Optional<T> getNetworkModule(ResourceLocation moduleType) {
		CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
		ServerCable cable = manager.getCable(getTileEntity().getPos());
		if (cable.getNetwork() != null) {
			if (cable.getNetwork().hasModule(moduleType)) {
				return Optional.of(cable.getNetwork().getModule(moduleType));
			}
		}
		return Optional.empty();
	}

	public boolean shouldConnectionToCable(AbstractCableProviderComponent otherProvider, Direction side) {
		if (otherProvider.hasAttachment(side) || this.hasAttachment(side.getOpposite())) {
			return false;
		}
		for (ResourceLocation moduleType : otherProvider.getSupportedNetworkModuleTypes()) {
			if (SupportedNetworkModules.contains(moduleType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		// Serialize the disabled states.
		for (int i = 0; i < DisabledSides.length; i++) {
			nbt.putBoolean("disabledState" + i, DisabledSides[i]);
		}

		// Serialize the attachments.
		for (int i = 0; i < Attachments.length; i++) {
			CompoundNBT itemNbt = new CompoundNBT();
			Attachments[i].write(itemNbt);
			nbt.put("attachment" + i, itemNbt);
		}
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		// Deserialize the disabled states.
		for (int i = 0; i < DisabledSides.length; i++) {
			DisabledSides[i] = nbt.getBoolean("disabledState" + i);
		}

		// Deserialize the attachments.
		for (int i = 0; i < Attachments.length; i++) {
			CompoundNBT itemNbt = nbt.getCompound("attachment" + i);
			Attachments[i] = ItemStack.read(itemNbt);
		}
	}

	protected void scanForAttachments() {
		for (Direction dir : Direction.values()) {
			ConnectionStates[dir.ordinal()] = cacheConnectionState(dir, getTileEntity().getPos().offset(dir));
		}
	}

	protected ResourceLocation[] getAttachmentModels() {
		ResourceLocation[] output = new ResourceLocation[] { null, null, null, null, null, null };
		for (Direction dir : Direction.values()) {
			output[dir.ordinal()] = getAttachmentModelForSide(dir);
		}
		return output;
	}

	protected ResourceLocation getAttachmentModelForSide(Direction side) {
		if (!Attachments[side.ordinal()].isEmpty()) {
			ItemStack attachmentItemStack = Attachments[side.ordinal()];
			AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachmentItemStack.getItem();
			return attachmentItem.getModel(attachmentItemStack, this);
		}
		return null;
	}

	protected abstract boolean canAttachAttachment(ItemStack attachment);

	protected abstract CableConnectionState cacheConnectionState(Direction side, BlockPos blockPosition);
}
