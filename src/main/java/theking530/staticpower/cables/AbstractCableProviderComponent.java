package theking530.staticpower.cables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.BlockEntityUpdateRequest;
import theking530.staticpower.blockentities.components.AbstractTileEntityComponent;
import theking530.staticpower.blockentities.components.control.redstonecontrol.RedstoneMode;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.utilities.ItemUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class AbstractCableProviderComponent extends AbstractTileEntityComponent {
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<CableRenderingState> CABLE_RENDERING_STATE = new ModelProperty<>();
	/** The type of this cable. */
	private final HashSet<ResourceLocation> supportedNetworkModules;
	/**
	 * Keeps track of which sides of the cable are disabled. This value is a client
	 * copy of the master value which exists on the server.
	 */
	private final boolean[] disabledSides;
	/**
	 * Cache for the connection states. This is updated every time a new baked model
	 * is requested AND also, on first placement.
	 */
//	protected final CableConnectionState[] connectionStates;
	/** Container for all the attachments on this cable. */
	protected final ItemStack[] attachments;
	/** Container for all the covers on this cable. */
	protected final ItemStack[] covers;
	/** List of valid attachment classes. */
	private final HashSet<Class<? extends AbstractCableAttachment>> validAttachments;
	private final Map<Long, SparseCableLink> sparseLinks;
	private boolean isInitializeWithCableManager;

	public AbstractCableProviderComponent(String name, ResourceLocation... supportedModules) {
		super(name);
		// Capture the types.
		supportedNetworkModules = new HashSet<ResourceLocation>();
		for (ResourceLocation module : supportedModules) {
			supportedNetworkModules.add(module);
		}

		// Initialize the valid attachments set.
		validAttachments = new HashSet<Class<? extends AbstractCableAttachment>>();

		// Initialize the disabled sides, connection states, and attachments arrays.
		disabledSides = new boolean[] { false, false, false, false, false, false };
		attachments = new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY };
		covers = new ItemStack[] { ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY };
		sparseLinks = new HashMap<Long, SparseCableLink>();
		isInitializeWithCableManager = false;
	}

	@Override
	public void preProcessUpdate() {
		if (getLevel().isClientSide || (!getLevel().isClientSide && getNetwork() != null)) {
			for (Direction dir : Direction.values()) {
				if (!attachments[dir.ordinal()].isEmpty()) {
					ItemStack attachment = attachments[dir.ordinal()];
					((AbstractCableAttachment) attachment.getItem()).attachmentTick(attachment, dir, this);
				}
			}
		}
	}

	@Override
	public void onInitializedInWorld(Level world, BlockPos pos, boolean firstTimePlaced) {
		super.onInitializedInWorld(world, pos, firstTimePlaced);
		// Update the rendering state on all connected blocks.
		if (getLevel().isClientSide()) {
			updateRenderingStateOnAllAdjacent();
		}
	}

	@Override
	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos, boolean isMoving) {
		super.onNeighborChanged(currentState, neighborPos, isMoving);

		// Update the network graph.
		if (!getLevel().isClientSide()) {
			ServerCable cable = CableNetworkManager.get((ServerLevel) getLevel()).getCable(getPos());
			if (cable != null && cable.getNetwork() != null) {
				cable.getNetwork().updateGraph((ServerLevel) getLevel(), getPos(), true);
			}
		}
	}

	/**
	 * After placed, we update the connection states.
	 */
	@Override
	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		super.onNeighborReplaced(state, direction, facingState, FacingPos);
		if (state != facingState) {
			updateRenderingStateForCable();
		}
	}

	@Override
	public void onOwningBlockBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onOwningBlockBroken(state, newState, isMoving);
		// Drop the covers and attachments.
		for (Direction dir : Direction.values()) {
			// Drop any attachments.
			if (hasAttachment(dir)) {
				ItemStack attachment = getAttachment(dir);
				if (((AbstractCableAttachment) attachment.getItem()).shouldDropOnOwningCableBreak(attachment)) {
					WorldUtilities.dropItem(getLevel(), getPos(), removeAttachment(dir));
				}
			}

			// Drop the covers too.
			if (hasCover(dir)) {
				WorldUtilities.dropItem(getLevel(), getPos(), removeCover(dir));
			}
		}

		// If we're on the server, get the cable manager and remove the cable at the
		// current position.
		if (!isClientSide()) {
			CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getLevel());
			manager.removeCable(getTileEntity().getBlockPos());
		}
	}

	/**
	 * Checks to see if the provided side is disabled. This only checks the client
	 * synced value, so we must ensure the server value is synced to the client
	 * always.
	 * 
	 * @param side
	 * @return
	 */
	public boolean isSideDisabled(Direction side) {
		return disabledSides[side.ordinal()];
	}

	/**
	 * On the client, this sets the local disabled state. On the server, this sets
	 * the {@link ServerCable}'s disabled state.
	 * 
	 * @param side
	 * @param disabledState
	 */
	public void setSideDisabledState(Direction side, boolean disabledState) {
		if (disabledState != disabledSides[side.ordinal()]) {
			disabledSides[side.ordinal()] = disabledState;
			getTileEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighbors(), true);
			getTileEntity().requestModelDataUpdate();
			if (!isClientSide()) {
				CableNetworkManager.get(getLevel()).getCable(getPos()).setDisabledStateOnSide(side, disabledState);
			}
		}
	}

	/**
	 * This is a hacky way for us to be able to set the initial disabled state. If
	 * calling this, it's likely you'll have to also call
	 * {@link #ServerCable.setDisabledStateOnSide(Direction, boolean)} on the
	 * {@link ServerCable} as well.
	 * 
	 * @param side
	 * @param disabledState
	 */
	protected void silentlySetSideDisabledState(Direction side, boolean disabledState) {
		if (isInitializeWithCableManager) {
			throw new RuntimeException(
					"Setting the disabled state with fromInitialization set to true should only be done before the cable is initialized with the cable manager.");
		}
		disabledSides[side.ordinal()] = disabledState;
	}

	/**
	 * Gets the connection state on the provided side.
	 * 
	 * @param side
	 * @return
	 */
	public CableConnectionState getConnectionState(Direction side) {
		return getUncachedConnectionState(side, getLevel().getBlockEntity(getPos().relative(side)), getPos().relative(side), false);
	}

	/**
	 * Gets the connection states on all sides.
	 * 
	 * @return
	 */
	public CableConnectionState[] getConnectionStates() {
		CableConnectionState[] output = new CableConnectionState[6];
		for (Direction dir : Direction.values()) {
			output[dir.ordinal()] = getConnectionState(dir);
		}
		return output;
	}

	/**
	 * Allows us to provide additional data about state of the cable for rendering
	 * purposes.
	 */
	@Override
	public void getModelData(ModelDataMap.Builder builder) {
		builder.withInitial(CABLE_RENDERING_STATE, getRenderingState());
	}

	public Collection<SparseCableLink> getSparseLinks() {
		if (!isClientSide()) {
			ServerCable trakcedCable = this.getCable().get();
			return trakcedCable.getSparseLinks();
		} else {
			return sparseLinks.values();
		}
	}

	public CableRenderingState getRenderingState() {
		return new CableRenderingState(getConnectionStates(), getAttachmentModels(), attachments, covers, disabledSides, getPos());
	}

	public HashSet<ResourceLocation> getSupportedNetworkModuleTypes() {
		return this.supportedNetworkModules;
	}

	/**
	 * When the owning tile entity is validated, we check to see if there is a cable
	 * wrapper in the network for this cable. If not, we provide one.
	 */
	@Override
	public void onOwningTileEntityPostInit(boolean isInitialPlacement) {
		super.onOwningTileEntityPostInit(isInitialPlacement);

		// If we're on the server, check to see if the cable network manager for this
		// world is tracking a cable at this position. If it is not, add this cable for
		// tracking.
		if (!getTileEntity().getLevel().isClientSide) {
			// Get the network manager.
			CableNetworkManager manager = CableNetworkManager.get(getLevel());

			// If we are not tracking this cable, then this is a new one.
			if (!manager.isTrackingCable(getPos())) {
				// Create and initialize it, then add it to the network manager.
				ServerCable wrapper = createCable();
				initializeCableProperties(wrapper);
				manager.addCable(wrapper);

				// Raise an event on this component for any additional work that needs to be
				// done.
				onCableFirstAddedToNetwork(wrapper);
			} else {
				// Since we are tracking this cable, just add the initialization step.
				// Whatever we set here may be overloaded by the saved nbt data on the cable, so
				// this is just a failsafe.
				initializeCableProperties(manager.getCable(getPos()));
			}
		}

		isInitializeWithCableManager = true;
	}

	/**
	 * This method is called when we want to synchronize variables over from the
	 * server to the client. The implementation of how this is handled is left to
	 * the implementer. This is called from the {@link ServerCable}.
	 */
	public void gatherCableStateSynchronizationValues(ServerCable cable, CompoundTag tag) {
		// Serialize the sparse links..
		ListTag sparseLinkTag = new ListTag();
		cable.getSparseLinks().forEach(link -> {
			sparseLinkTag.add(link.serialize());
		});
		tag.put("sparse_links", sparseLinkTag);
	}

	public void syncCableStateFromServer(CompoundTag tag) {
		sparseLinks.clear();
		ListTag sparseLinkTags = tag.getList("sparse_links", Tag.TAG_COMPOUND);
		for (Tag sparseLinkTag : sparseLinkTags) {
			SparseCableLink link = SparseCableLink.fromTag((CompoundTag) sparseLinkTag);
			sparseLinks.put(link.linkId(), link);
		}
	}

	public boolean isSparselyConnectedTo(BlockPos location) {
		if (!isClientSide()) {
			ServerCable trakcedCable = CableNetworkManager.get(getLevel()).getCable(location);
			if (trakcedCable != null) {
				return trakcedCable.isLinkedTo(location);
			}
		} else {
			for (SparseCableLink link : sparseLinks.values()) {
				if (link.linkToPosition().equals(location)) {
					return true;
				}
			}
		}
		return false;
	}

	public SparseCableLink addSparseConnection(BlockPos location, CompoundTag tag) {
		if (!isClientSide()) {
			ServerCable tracked = getCable().get();
			if (tracked != null) {
				SparseCableLink addedLink = tracked.addSparseLink(location, tag);
				if (addedLink != null) {
					sparseConnectionAdded(addedLink);
				}
				return addedLink;
			}
		}
		return null;
	}

	protected void sparseConnectionAdded(SparseCableLink link) {

	}

	public List<SparseCableLink> removeSparseConnections(BlockPos location) {
		if (!isClientSide()) {
			ServerCable tracked = getCable().get();
			if (tracked != null) {
				List<SparseCableLink> removedLinks = tracked.removeSparseLinks(location);
				sparseConnectionsRemoved(removedLinks);
				return removedLinks;
			}
		}
		return null;
	}

	public List<SparseCableLink> breakAllSparseLinks() {
		if (!isClientSide()) {
			ServerCable tracked = getCable().get();
			if (tracked != null) {
				List<SparseCableLink> result = tracked.breakAllSparseLinks();
				sparseConnectionsRemoved(result);
				return result;
			}
		}
		return null;
	}

	protected void sparseConnectionsRemoved(List<SparseCableLink> links) {

	}

	/**
	 * This method updates the rendering state of this cable alongside all connected
	 * cables.
	 */
	public void updateRenderingStateOnAllAdjacent() {
		// Only execute on the client.
		if (getLevel().isClientSide()) {
			getTileEntity().requestModelDataUpdate();
			// Update the rendering state on all connected blocks.
			for (Direction dir : Direction.values()) {
				AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getLevel(), getPos().relative(dir));
				if (otherProvider != null) {
					otherProvider.getTileEntity().addRenderingUpdateRequest();
				}
			}
			StaticPower.LOGGER.debug(String.format("Performing cable rendering state update at position: %1$s and all adjacent cables.", getPos().toString()));
		} else {
			StaticPower.LOGGER.warn(String.format("Calling #updateRenderingStateOnAllAdjacent() on the server is a no-op. Called at position: %1$s.", getPos().toString()));
		}
	}

	public void updateRenderingStateForCable() {
		// Only execute on the client.
		if (getLevel().isClientSide()) {
			getTileEntity().addRenderingUpdateRequest();
			StaticPower.LOGGER.debug(String.format("Performing cable rendering state update at position: %1$s.", getPos().toString()));
		}
	}

	/**
	 * Adds a valid attachment class that can be attached to this cable.
	 * 
	 * @param attachmentClass
	 */
	protected void addValidAttachmentClass(Class<? extends AbstractCableAttachment> attachmentClass) {
		validAttachments.add(attachmentClass);
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
		if (attachments[side.ordinal()].isEmpty()) {
			attachments[side.ordinal()] = attachment.copy();
			attachments[side.ordinal()].setCount(1);

			// Raise the on added method on the attachment.
			AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachments[side.ordinal()].getItem();
			attachmentItem.onAddedToCable(attachments[side.ordinal()], side, this);

			// Initialize the data container on the server.
			if (!getLevel().isClientSide) {
				ServerCable cable = CableNetworkManager.get(getLevel()).getCable(getPos());
				cable.addAttachmentDataForSide(side, attachmentItem.getRegistryName());
				attachmentItem.initializeServerDataContainer(attachments[side.ordinal()], side, this, cable.getAttachmentDataContainerForSide(side));
			}

			// Re-sync the tile entity.
			getTileEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighbors(), true);
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
		if (hasAttachment(side) && canRemoveAttachment(side)) {
			// Get the attachment for the side.
			ItemStack output = attachments[side.ordinal()];

			// Raise the on removed method on the attachment.
			AbstractCableAttachment attachmentItem = (AbstractCableAttachment) output.getItem();
			attachmentItem.onRemovedFromCable(output, side, this);

			// Remove the attachment and return it.
			attachments[side.ordinal()] = ItemStack.EMPTY;
			// Clear the attachment data from the server.
			if (!isClientSide()) {
				CableNetworkManager.get(getLevel()).getCable(getPos()).clearAttachmentDataForSide(side);
			}

			getTileEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighbors(), true);
			getLevel().getChunkSource().getLightEngine().checkBlock(getPos());
			return output;
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Indicates whether or not the attachment on the provided side can be removed.
	 * 
	 * @param side
	 * @return
	 */
	public boolean canRemoveAttachment(Direction side) {
		return true;
	}

	/**
	 * Attaches a cover to the provided side. Returns true if the cover was added.
	 * Returns false otherwise.
	 * 
	 * @param attachment The cover itemstack to add.
	 * @param side       The side to attach it to.
	 * @return True if the cover was applied, false otherwise.
	 */
	public boolean attachCover(ItemStack attachment, Direction side) {
		// If there is no cover on the provided side, add it.
		if (covers[side.ordinal()].isEmpty()) {
			covers[side.ordinal()] = attachment.copy();
			covers[side.ordinal()].setCount(1);

			// Re-sync the tile entity.
			getTileEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighbors(), true);
			return true;
		}
		return false;
	}

	/**
	 * Removes a cover from the provided side and returns the itemstack for the
	 * cover. If there is no cover, returns an empty Itemstack.
	 * 
	 * @param side The side to remove from.
	 * @return
	 */
	public ItemStack removeCover(Direction side) {
		// Ensure we have an attachment on the provided side.
		if (hasCover(side)) {
			// Get the attachment for the side.
			ItemStack output = covers[side.ordinal()];

			// Remove the attachment and return it.
			covers[side.ordinal()] = ItemStack.EMPTY;

			// Re-sync the tile entity.
			getTileEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighbors(), true);
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
		return !attachments[side.ordinal()].isEmpty();
	}

	/**
	 * Checks to see if this component has any attachments of the provided type.
	 * 
	 * @param attachmentClass The attachment class to check.
	 * @return True if the cable has at least one attachment of this type.
	 */
	public boolean hasAttachmentOfType(Class<? extends AbstractCableAttachment> attachmentClass) {
		for (Direction dir : Direction.values()) {
			if (attachmentClass.isInstance(attachments[dir.ordinal()].getItem())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets a list of all attachments of the provided class.
	 * 
	 * @param attachmentClass The attachment class to check for.
	 * @return
	 */
	public List<ItemStack> getAttachmentsOfType(Class<? extends AbstractCableAttachment> attachmentClass) {
		List<ItemStack> outputs = new ArrayList<ItemStack>();
		for (Direction dir : Direction.values()) {
			if (attachmentClass.isInstance(attachments[dir.ordinal()].getItem())) {
				outputs.add(attachments[dir.ordinal()]);
			}
		}
		return outputs;
	}

	/**
	 * Checks to see if a cover is attached on that side.
	 * 
	 * @param side The side to check for.
	 * @return Returns true if a cover is applied on the provided side.
	 */
	public boolean hasCover(Direction side) {
		return !covers[side.ordinal()].isEmpty();
	}

	/**
	 * Gets the cover on the provided side. If no cover is present, returns an empty
	 * itemstack.
	 * 
	 * @param side The side to check for.
	 * @return
	 */
	public ItemStack getCover(Direction side) {
		return covers[side.ordinal()];
	}

	/**
	 * Gets the attachment on the provided side. If no attachment is present,
	 * returns an empty itemstack.
	 * 
	 * @param side The side to check for.
	 * @return
	 */
	public ItemStack getAttachment(Direction side) {
		return attachments[side.ordinal()];
	}

	/**
	 * Checks to see if the provided attachment passes the redstone test.
	 * 
	 * @param attachment
	 * @return
	 */
	public boolean doesAttachmentPassRedstoneTest(ItemStack attachment) {
		AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();
		return RedstoneMode.evaluateRedstoneMode(attachmentItem.getRedstoneMode(attachment), getLevel(), getPos());
	}

	/**
	 * Gets the network this cable is currently connected to. NOTE: This is a server
	 * only method. Calling this on the client will throw an error.
	 * 
	 * @return
	 */
	public CableNetwork getNetwork() {
		return CableNetworkManager.get(getLevel()).isTrackingCable(getPos()) ? CableNetworkManager.get(getLevel()).getCable(getPos()).getNetwork() : null;
	}

	/**
	 * Gets the module from this network if present. We have to wrap it in an
	 * optional because while we can guarantee once this component is validated that
	 * the network is valid, since this component exposes external methods, other
	 * tile entity that are made valid before us may call some of our methods. When
	 * called from the client, we always return an empty optional.
	 * 
	 * @param <T>        The type of the module.
	 * @param moduleType The resource location model type.
	 * @return
	 */
	public <T extends AbstractCableNetworkModule> Optional<T> getNetworkModule(ResourceLocation moduleType) {
		if (!getLevel().isClientSide) {
			Optional<ServerCable> cable = getCable();
			if (cable.isPresent()) {
				if (cable.get().getNetwork().hasModule(moduleType)) {
					return Optional.of(cable.get().getNetwork().getModule(moduleType));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Gets the cable this component represents from this network if present. We
	 * have to wrap it in an optional because while we can guarantee once this
	 * component is validated that the network is valid, since this component
	 * exposes external methods, other tile entity that are made valid before us may
	 * call some of our methods. When called from the client, we always return an
	 * empty optional.
	 * 
	 * @return
	 */
	public Optional<ServerCable> getCable() {
		if (!getLevel().isClientSide) {
			CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getLevel());
			ServerCable cable = manager.getCable(getTileEntity().getBlockPos());
			if (cable != null && cable.getNetwork() != null) {
				return Optional.of(cable);
			}
		}
		return Optional.empty();
	}

	public boolean areCableCompatible(AbstractCableProviderComponent otherProvider, Direction side) {
		if (otherProvider.hasAttachment(side) || this.hasAttachment(side.getOpposite())) {
			return false;
		}
		for (ResourceLocation moduleType : otherProvider.getSupportedNetworkModuleTypes()) {
			if (supportedNetworkModules.contains(moduleType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		// Serialize the disabled states.
		for (int i = 0; i < disabledSides.length; i++) {
			nbt.putBoolean("disabledState" + i, disabledSides[i]);
		}

		// Serialize the attachments.
		for (int i = 0; i < attachments.length; i++) {
			CompoundTag itemNbt = new CompoundTag();
			attachments[i].save(itemNbt);
			nbt.put("attachment" + i, itemNbt);
		}

		// Serialize the covers.
		for (int i = 0; i < covers.length; i++) {
			CompoundTag itemNbt = new CompoundTag();
			covers[i].save(itemNbt);
			nbt.put("cover" + i, itemNbt);
		}

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		// Deserialize the disabled states.
		for (int i = 0; i < disabledSides.length; i++) {
			disabledSides[i] = nbt.getBoolean("disabledState" + i);
		}

		// Deserialize the attachments.
		for (int i = 0; i < attachments.length; i++) {
			CompoundTag itemNbt = nbt.getCompound("attachment" + i);
			ItemStack incomingAttachment = ItemStack.of(itemNbt);

			// If the attachments have changed on the server, just check for the lights to
			// be safe (digistore lights for example). TODO Watch the performance of this.
			if (!ItemUtilities.areItemStacksStackable(incomingAttachment, attachments[i])) {
				if (getLevel() != null) {
					getLevel().getChunkSource().getLightEngine().checkBlock(getPos());
				}
			}

			// Update the local attachment.
			attachments[i] = ItemStack.of(itemNbt);
		}

		// Deserialize the covers.
		for (int i = 0; i < covers.length; i++) {
			CompoundTag itemNbt = nbt.getCompound("cover" + i);
			covers[i] = ItemStack.of(itemNbt);
		}
	}

	protected ServerCable createCable() {
		return new ServerCable(getLevel(), getPos(), false, getSupportedNetworkModuleTypes());
	}

	protected void onCableFirstAddedToNetwork(ServerCable cable) {

	}

	protected void initializeCableProperties(ServerCable cable) {

	}

	protected ResourceLocation[] getAttachmentModels() {
		ResourceLocation[] output = new ResourceLocation[] { null, null, null, null, null, null };
		for (Direction dir : Direction.values()) {
			output[dir.ordinal()] = getAttachmentModelForSide(dir);
		}
		return output;
	}

	protected ResourceLocation getAttachmentModelForSide(Direction side) {
		if (!attachments[side.ordinal()].isEmpty()) {
			ItemStack attachmentItemStack = attachments[side.ordinal()];
			AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachmentItemStack.getItem();
			return attachmentItem.getModel(attachmentItemStack, this);
		}
		return null;
	}

	protected boolean canAttachAttachment(ItemStack attachment) {
		if (attachment.isEmpty()) {
			return false;
		}
		if (validAttachments.contains(attachment.getItem().getClass())) {
			return true;
		}
		return false;
	}

	protected abstract CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded);
}
