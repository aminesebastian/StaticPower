package theking530.staticcore.blockentity.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticcore.StaticCore;
import theking530.staticcore.blockentity.BlockEntityUpdateRequest;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.cablenetwork.AbstractCableBlock;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.CableRenderingState;
import theking530.staticcore.cablenetwork.CableStateSyncRequestPacket;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.cablenetwork.ICableStateSyncTarget;
import theking530.staticcore.cablenetwork.SparseCableLink;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachment;
import theking530.staticcore.cablenetwork.data.CableConnectionState;
import theking530.staticcore.cablenetwork.data.CableConnectionState.CableConnectionType;
import theking530.staticcore.cablenetwork.data.ClientCableConnectionState;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.cablenetwork.manager.CableNetworkManager;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.world.WorldUtilities;

public abstract class AbstractCableProviderComponent extends AbstractBlockEntityComponent implements ICableStateSyncTarget {
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<CableRenderingState> CABLE_RENDERING_STATE = new ModelProperty<>();
	/** The type of this cable. */
	private final HashSet<CableNetworkModuleType> supportedNetworkModules;
	/** List of valid attachment classes. */
	private final HashSet<Class<? extends AbstractCableAttachment>> validAttachments;

	/** The client replicated list of sparse links. */
	private final Map<Long, SparseCableLink> clientSparseLinks;
	/** The client replicated list of connection states. */
	private final ClientCableConnectionState[] clientConnectionStates;

	public AbstractCableProviderComponent(String name, CableNetworkModuleType... supportedModules) {
		super(name);
		// Capture the module types.
		supportedNetworkModules = new HashSet<CableNetworkModuleType>();
		for (CableNetworkModuleType module : supportedModules) {
			supportedNetworkModules.add(module);
		}

		// Initialize the valid attachments set.
		validAttachments = new HashSet<Class<? extends AbstractCableAttachment>>();

		// Initialize the sided data. Initialize the disabled sides to true until we
		// recieve an update from the server.
		clientConnectionStates = new ClientCableConnectionState[6];
		for (Direction dir : Direction.values()) {
			clientConnectionStates[dir.ordinal()] = ClientCableConnectionState.createEmpty();
		}
		clientSparseLinks = new HashMap<Long, SparseCableLink>();
	}

	@Override
	public void preProcessUpdate() {
		// Tick the attachments.
		for (Direction dir : Direction.values()) {
			ItemStack attachment = ItemStack.EMPTY;
			if (isClientSide()) {
				attachment = clientConnectionStates[dir.ordinal()].getAttachment();
			} else {
				if (getCable().isPresent()) {
					attachment = getCable().get().getAttachmentOnSide(dir);
				}
			}

			if (!attachment.isEmpty()) {
				((AbstractCableAttachment) attachment.getItem()).attachmentTick(attachment, dir, this);
			}
		}
	}

	@Override
	public void onNeighborChanged(BlockState currentState, BlockPos neighborPos, boolean isMoving) {
		super.onNeighborChanged(currentState, neighborPos, isMoving);

		// Update the network graph.
		if (!getLevel().isClientSide()) {
			Cable cable = CableNetworkAccessor.get((ServerLevel) getLevel()).getCable(getPos());
			if (cable != null && cable.getNetwork() != null) {
				cable.getNetwork().updateGraph((ServerLevel) getLevel(), getPos(), true);
			}
		} else {
			updateRenderingStateForCable();
		}
	}

	/**
	 * After placed, we update the connection states.
	 */
	@Override
	public void onNeighborReplaced(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		super.onNeighborReplaced(state, direction, facingState, FacingPos);
		updateRenderingStateForCable();
	}

	@Override
	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {
		super.onOwningBlockEntityBroken(state, newState, isMoving);
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
			CableNetworkManager manager = CableNetworkAccessor.get(getBlockEntity().getLevel());
			manager.removeCable(getBlockEntity().getBlockPos());
		}
	}

	/**
	 * When the owning tile entity is validated, we check to see if there is a cable
	 * wrapper in the network for this cable. If not, we provide one.
	 */
	@Override
	public void onOwningBlockEntityLoaded(Level level, BlockPos pos, BlockState state) {
		super.onOwningBlockEntityLoaded(level, pos, state);

		// Update the rendering state on all connected blocks.
		if (isClientSide()) {
			requestServerCableSyncFromClient();
		}
	}

	@Override
	public void onOwningBlockEntityFirstPlaced(BlockPlaceContext context, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.onOwningBlockEntityFirstPlaced(context, state, placer, stack);

		// If we're on the server, check to see if the cable network manager for this
		// world is tracking a cable at this position. If it is not, add this cable for
		// tracking.
		if (!isClientSide()) {
			// Get the network manager.
			CableNetworkManager manager = CableNetworkAccessor.get(context.getLevel());

			// If we are not tracking this cable, then this is a new one.
			if (!manager.isTrackingCable(getPos())) {
				// Create and initialize it, then add it to the network manager.
				Cable cable = createCable();
				initializeCableProperties(cable, context, state, placer, stack);
				manager.addCable(cable);

				// Raise an event on this component for any additional work that needs to be
				// done.
				onCableFirstAddedToNetwork(cable, context, state, placer, stack);
			}
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
		if (!isClientSide()) {
			// Make sure the cable is still tracked by the server. Sometimes when a cable is
			// removed, we trigger the scan before the block entity is also removed.
			if (getCable().isPresent()) {
				return getCable().get().isDisabledOnSide(side);
			}
			return true;
		} else {
			return getBlockEntity().getBlockState().getValue(AbstractCableBlock.CONNECTION_TYPES.get(side)) == CableConnectionType.DISABLED;
		}
	}

	/**
	 * This should only be called on the server. Once set on the ServerCable, it
	 * gets synchronized back down to the client.
	 * 
	 * @param side
	 * @param disabledState
	 */
	public void setSideDisabledState(Direction side, boolean disabledState) {
		if (!isClientSide()) {
			CableNetworkAccessor.get(getLevel()).getCable(getPos()).setDisabledStateOnSide(side, disabledState);
		} else {
			StaticCore.LOGGER.warn(String.format(
					"AbstractCableProviderComponent#setSideDisabledState should only be called from the server! This is a no-op on the client. Called from Position: %1$s.",
					getPos()));
		}
	}

	/**
	 * Allows us to provide additional data about state of the cable for rendering
	 * purposes.
	 */
	@Override
	public void getModelData(ModelData.Builder builder) {
		builder.with(CABLE_RENDERING_STATE, getRenderingState());
	}

	public Collection<SparseCableLink> getSparseLinks() {
		if (!isClientSide()) {
			Cable trakcedCable = this.getCable().get();
			return trakcedCable.getSparseLinks();
		} else {
			return clientSparseLinks.values();
		}
	}

	public CableRenderingState getRenderingState() {
		ResourceLocation[] attachmentModels = new ResourceLocation[6];
		for (Direction dir : Direction.values()) {
			attachmentModels[dir.ordinal()] = getAttachmentModel(dir, clientConnectionStates[dir.ordinal()]);
		}
		return new CableRenderingState(clientConnectionStates, attachmentModels, getPos());
	}

	public Set<CableNetworkModuleType> getSupportedNetworkModuleTypes() {
		return this.supportedNetworkModules;
	}

	public boolean isSparselyConnectedTo(BlockPos location) {
		if (!isClientSide()) {
			Cable trakcedCable = CableNetworkAccessor.get(getLevel()).getCable(location);
			if (trakcedCable != null) {
				return trakcedCable.isLinkedTo(location);
			}
		} else {
			for (SparseCableLink link : clientSparseLinks.values()) {
				if (link.linkToPosition().equals(location)) {
					return true;
				}
			}
		}
		return false;
	}

	public SparseCableLink addSparseConnection(BlockPos location, CompoundTag tag) {
		if (!isClientSide()) {
			Cable tracked = getCable().get();
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
			Cable tracked = getCable().get();
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
			Cable tracked = getCable().get();
			if (tracked != null) {
				List<SparseCableLink> result = tracked.breakAllSparseLinks();
				sparseConnectionsRemoved(result);
				return result;
			}
		}
		return null;
	}

	protected void clientSparseLinkArrayChanged() {
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
			getBlockEntity().requestModelDataUpdate();
			// Update the rendering state on all connected blocks.
			for (Direction dir : Direction.values()) {
				AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getLevel(), getPos().relative(dir));
				if (otherProvider != null) {
					otherProvider.getBlockEntity().addRenderingUpdateRequest();
				}
			}
			StaticCore.LOGGER.debug(String.format("Performing cable rendering state update at position: %1$s and all adjacent cables.", getPos().toString()));
		} else {
			StaticCore.LOGGER
					.warn(String.format("Calling #updateRenderingStateOnAllAdjacent() on the server is a no-op. Called at position: %1$s.", getPos().toString()));
		}
	}

	public void updateRenderingStateForCable() {
		// Only execute on the client.
		if (getLevel().isClientSide()) {
			getBlockEntity().addRenderingUpdateRequest();
			StaticCore.LOGGER.debug(String.format("Performing cable rendering state update at position: %1$s.", getPos().toString()));
		}
	}

	public CableConnectionType getConnectionTypeOnSide(Direction side) {
		if (!isClientSide()) {
			if (getCable().isPresent()) {
				return getCable().get().getConnectionType(side);
			}
		}
		return getBlockEntity().getBlockState().getValue(AbstractCableBlock.CONNECTION_TYPES.get(side));
	}

	/**
	 * Attaches a cover to the provided side. Returns true if the cover was added.
	 * Returns false otherwise.
	 * 
	 * @param attachment The cover itemstack to add.
	 * @param side       The side to attach it to.
	 * @return True if the cover was applied, false otherwise.
	 */
	public boolean attachCover(ItemStack cover, Direction side) {
		if (!isClientSide()) {
			Cable cable = this.getCable().orElse(null);
			if (cable != null) {
				ItemStack singleCover = cover.copy();
				singleCover.setCount(1);
				return cable.addCoverToSide(side, singleCover);
			} else {
				StaticCore.LOGGER.error(String.format("Encountered null cable when attempting to place a cover at: %1$s.", getPos()));
			}
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
		if (!isClientSide()) {
			Cable cable = this.getCable().orElse(null);
			if (cable != null) {
				return cable.removeCoverFromSide(side);
			} else {
				StaticCore.LOGGER.error(String.format("Encountered null cable when attempting to remove a cover at: %1$s.", getPos()));
			}
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Checks to see if a cover is attached on that side.
	 * 
	 * @param side The side to check for.
	 * @return Returns true if a cover is applied on the provided side.
	 */
	public boolean hasCover(Direction side) {
		if (!isClientSide()) {
			Cable cable = this.getCable().orElse(null);
			if (cable != null) {
				return cable.hasCoverOnSide(side);
			}
		} else {
			return clientConnectionStates[side.ordinal()].hasCover();
		}
		return false;
	}

	/**
	 * Gets the cover on the provided side. If no cover is present, returns an empty
	 * itemstack.
	 * 
	 * @param side The side to check for.
	 * @return
	 */
	public ItemStack getCover(Direction side) {
		if (!isClientSide()) {
			Cable cable = this.getCable().orElse(null);
			if (cable != null) {
				return cable.getCoverOnSide(side);
			}
		} else {
			return clientConnectionStates[side.ordinal()].getCover();
		}
		return ItemStack.EMPTY;
	}

	/**
	 * Gets the network this cable is currently connected to. NOTE: This is a server
	 * only method. Calling this on the client will throw an error.
	 * 
	 * @return
	 */
	public CableNetwork getNetwork() {
		return CableNetworkAccessor.get(getLevel()).isTrackingCable(getPos()) ? CableNetworkAccessor.get(getLevel()).getCable(getPos()).getNetwork() : null;
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
	public <T extends CableNetworkModule> Optional<T> getNetworkModule(CableNetworkModuleType moduleType) {
		if (!isClientSide()) {
			Optional<Cable> cable = getCable();
			if (cable.isPresent()) {
				if (cable.get().getNetwork().hasModule(moduleType)) {
					return Optional.ofNullable(cable.get().getNetwork().getModule(moduleType));
				}
			}
		}
		return Optional.empty();
	}

	private Cable createCable() {
		Set<CableDestination> types = new HashSet<CableDestination>();
		getSupportedDestinationTypes(types);
		return new Cable(getLevel(), getPos(), isSpraseCable(), getSupportedNetworkModuleTypes(), types);
	}

	public boolean isSpraseCable() {
		return false;
	}

	protected abstract void getSupportedDestinationTypes(Set<CableDestination> types);

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
	public Optional<Cable> getCable() {
		if (!isClientSide()) {
			CableNetworkManager manager = CableNetworkAccessor.get(getBlockEntity().getLevel());
			Cable cable = manager.getCable(getBlockEntity().getBlockPos());
			if (cable != null && cable.getNetwork() != null) {
				return Optional.of(cable);
			}
		}
		return Optional.empty();
	}

	protected void initializeCableProperties(Cable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		for (Direction dir : Direction.values()) {
			BlockPos side = cable.getPos().relative(dir);
			if (CableNetworkAccessor.get(getLevel()).isTrackingCable(side)) {
				Cable adjacentCable = CableNetworkAccessor.get(getLevel()).getCable(side);
				if (adjacentCable.isDisabledOnSide(dir.getOpposite())) {
					cable.setDisabledStateOnSide(dir, true);
				}
			}
		}
	}

	protected void onCableFirstAddedToNetwork(Cable cable, BlockPlaceContext context, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

	}

	public boolean areCableCompatible(AbstractCableProviderComponent otherProvider, Direction side) {
		if (otherProvider.hasAttachment(side) || this.hasAttachment(side.getOpposite())) {
			return false;
		}
		for (CableNetworkModuleType moduleType : otherProvider.getSupportedNetworkModuleTypes()) {
			if (supportedNetworkModules.contains(moduleType)) {
				return true;
			}
		}
		return false;
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
		if (!isClientSide()) {
			// Check if we can attach the attachment.
			if (!canAttachAttachment(attachment)) {
				return false;
			}

			Cable cable = this.getCable().orElse(null);
			if (cable != null) {
				// Use a copy of the attachment to be safe.
				ItemStack attachmentCopy = attachment.copy();
				attachmentCopy.setCount(1);

				if (cable.addAttachmentToSide(side, attachmentCopy)) {
					// Raise the on added method on the attachment.
					AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachmentCopy.getItem();
					attachmentItem.onAddedToCable(attachmentCopy, side, this);
					cable.synchronizeServerState();
					return true;
				}
			} else {
				StaticCore.LOGGER.error(String.format("Encountered null cable when attempting to remove a cover at: %1$s.", getPos()));
			}
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
		if (!isClientSide()) {
			Cable cable = this.getCable().orElse(null);
			if (cable != null) {
				ItemStack removedAttachment = cable.removeAttachmentFromSide(side);
				if (!removedAttachment.isEmpty()) {
					// Raise the on added method on the attachment.
					AbstractCableAttachment attachmentItem = (AbstractCableAttachment) removedAttachment.getItem();
					attachmentItem.onRemovedFromCable(removedAttachment, side, this);
					return removedAttachment;
				}
			} else {
				StaticCore.LOGGER.error(String.format("Encountered null cable when attempting to remove a cover at: %1$s.", getPos()));
			}
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
	 * Checks to see if an attachment is attached on that side.
	 * 
	 * @param side The side to check for.
	 * @return Returns true if a attachment is applied on the provided side.
	 */
	public boolean hasAttachment(Direction side) {
		if (!isClientSide()) {
			Cable cable = this.getCable().orElse(null);
			if (cable != null) {
				return cable.hasAttachmentOnSide(side);
			}
		} else {
			return clientConnectionStates[side.ordinal()].hasAttachment();
		}
		return false;
	}

	/**
	 * Checks to see if this component has any attachments of the provided type.
	 * 
	 * @param attachmentClass The attachment class to check.
	 * @return True if the cable has at least one attachment of this type.
	 */
	public boolean hasAttachmentOfType(Class<? extends AbstractCableAttachment> attachmentClass) {
		if (!isClientSide()) {
			Cable cable = this.getCable().orElse(null);
			if (cable == null) {
				StaticCore.LOGGER.error(
						String.format("Encountered null cable when attempting to check for an attachment of type: %1$s at %2$s.", attachmentClass.toString(), getPos()));
				return false;
			}
			for (Direction dir : Direction.values()) {
				if (attachmentClass.isInstance(cable.getAttachmentOnSide(dir).getItem())) {
					return true;
				}
			}
		} else {
			for (Direction dir : Direction.values()) {
				if (attachmentClass.isInstance(getAttachment(dir).getItem())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the attachment on the provided side. If no attachment is present,
	 * returns an empty itemstack.
	 * 
	 * @param side The side to check for.
	 * @return
	 */
	public ItemStack getAttachment(Direction side) {
		if (!isClientSide()) {
			Cable cable = this.getCable().orElse(null);
			if (cable != null) {
				return cable.getAttachmentOnSide(side);
			}
		} else {
			return clientConnectionStates[side.ordinal()].getAttachment();
		}
		return ItemStack.EMPTY;
	}

	protected ResourceLocation getAttachmentModel(Direction side, ClientCableConnectionState connectionState) {
		if (!hasAttachment(side)) {
			return null;
		}

		AbstractCableAttachment item = (AbstractCableAttachment) getAttachment(side).getItem();
		return item.getModel(getAttachment(side), getLevel(), getPos());
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

	public boolean canAttachAttachment(ItemStack attachment) {
		if (attachment.isEmpty()) {
			return false;
		}
		if (validAttachments.contains(attachment.getItem().getClass())) {
			return true;
		}
		return false;
	}

	public void recieveCableSyncState(CompoundTag tag) {
		// Capture the existing ids.
		Set<Long> changeDetection = new HashSet<Long>();
		changeDetection.addAll(clientSparseLinks.values().stream().map(x -> x.linkId()).toList());

		// Clear out the sparse links and then accept the new data.
		clientSparseLinks.clear();
		ListTag sparseLinkTags = tag.getList("sparse_links", Tag.TAG_COMPOUND);
		for (Tag sparseLinkTag : sparseLinkTags) {
			SparseCableLink link = SparseCableLink.fromTag((CompoundTag) sparseLinkTag);
			clientSparseLinks.put(link.linkId(), link);

			if (changeDetection.contains(link.linkId())) {
				changeDetection.remove(link.linkId());
			} else {
				changeDetection.add(link.linkId());
			}
		}

		// Check to see if there was a change.
		if (!changeDetection.isEmpty()) {
			clientSparseLinkArrayChanged();
		}

		// Deserialize the sided data.
		ListTag sidedTags = tag.getList("sided_data", Tag.TAG_COMPOUND);
		for (int i = 0; i < sidedTags.size(); i++) {
			clientConnectionStates[i] = CableConnectionState.deserialize(sidedTags.getCompound(i));
		}

		getBlockEntity().addUpdateRequest(BlockEntityUpdateRequest.blockUpdateAndNotifyNeighborsAndRender(), false);
	}

	protected void requestServerCableSyncFromClient() {
		if (isClientSide()) {
			StaticCoreMessageHandler.sendToServer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, new CableStateSyncRequestPacket(getPos()));
		} else {
			StaticCore.LOGGER
					.error(String.format("Attempted to request cable state synchronization from the server at position: %1$s. This is a no-op.", getPos().toString()));
		}
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

	}

}
