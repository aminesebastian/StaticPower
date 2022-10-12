package theking530.staticpower.cables.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.PacketDistributor;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.extractor.ExtractorAttachment;
import theking530.staticpower.cables.attachments.filter.FilterAttachment;
import theking530.staticpower.cables.attachments.retirever.RetrieverAttachment;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class ItemCableComponent extends AbstractCableProviderComponent {
	/**
	 * This is the slowest an item may travel.
	 */
	public static final int MAXIMUM_MOVE_TIME = 1000;
	public static final String ITEM_CABLE_MAX_TRANSFER_SPEED = "min_transfer_time";
	public static final String ITEM_CABLE_FRICTION_FACTOR_TAG = "friction_factor";
	public static final String ITEM_CABLE_ACCELERATION_FACTOR_TAG = "acceleration_factor";

	private final double maxTransferSpeed;
	private final double frictionFactor;
	private final double accelerationFactor;
	private final HashMap<Long, ItemRoutingParcelClient> containedPackets;
	private final ResourceLocation tier;
	private final Map<Direction, ItemCableSideWrapper> sideWrappers;

	public ItemCableComponent(String name, ResourceLocation tier, double maxTransferSpeed, double frictionFactor, double accelerationFactor) {
		super(name, ModCableModules.Item.get());
		containedPackets = new HashMap<Long, ItemRoutingParcelClient>();
		this.maxTransferSpeed = maxTransferSpeed;
		this.frictionFactor = frictionFactor;
		this.accelerationFactor = accelerationFactor;
		this.tier = tier;

		// Create and populate the side wrappers.
		this.sideWrappers = new HashMap<Direction, ItemCableSideWrapper>();
		for (Direction dir : Direction.values()) {
			sideWrappers.put(dir, new ItemCableSideWrapper(dir, this));
		}
		sideWrappers.put(null, new ItemCableSideWrapper(null, this));

		addValidAttachmentClass(ExtractorAttachment.class);
		addValidAttachmentClass(FilterAttachment.class);
		addValidAttachmentClass(RetrieverAttachment.class);
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		// Only do this on the client. The item module will do it on the server.
		// THIS IS FOR VISUAL SMOOTHNESS ONLY.
		if (isClientSide()) {
			for (ItemRoutingParcelClient packet : containedPackets.values()) {
				packet.incrementMoveTimer();
			}
		}
	}

	@Override
	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {
		// Only perform the following on the server.
		if (!isClientSide()) {
			// Get the network.
			CableNetwork network = CableNetworkManager.get(getLevel()).getCable(getPos()).getNetwork();
			if (network == null) {
				CableNetworkManager.get(getLevel()).removeCable(getPos());
				throw new RuntimeException(String.format("Encountered a null network for an ItemCableComponent at position: %1$s.", getPos()));
			}

			// Get the module.
			ItemNetworkModule itemNetworkModule = (ItemNetworkModule) network.getModule(ModCableModules.Item.get());

			// Tell the network module this cable was broken.
			itemNetworkModule.onItemCableBroken(getPos());
		}

		super.onOwningBlockEntityBroken(state, newState, isMoving);
	}

	public double getMaxTransferSpeed() {
		return maxTransferSpeed;
	}

	public void addTransferingItem(ItemRoutingParcelClient routingPacket) {
		containedPackets.put(routingPacket.getId(), routingPacket);
		if (!isClientSide()) {
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> getLevel().getChunkAt(getPos())),
					new ItemCableAddedPacket(this, routingPacket));
		}
		getTileEntity().setChanged();
	}

	public void removeTransferingItem(long parcelId) {
		containedPackets.remove(parcelId);
		if (!isClientSide()) {
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> getLevel().getChunkAt(getPos())),
					new ItemCableRemovedPacket(this, parcelId));
		}
		getTileEntity().setChanged();
	}

	public Collection<ItemRoutingParcelClient> getContainedItems() {
		return containedPackets.values();
	}

	public boolean canExtractFromSide(ItemStack stack, Direction destinationSide) {
		// If there is no attachment on that side, we can extract.
		if (!hasAttachment(destinationSide)) {
			return true;
		}

		// If there is a filter attachment, evaluate against it.
		if (getAttachment(destinationSide).getItem() instanceof FilterAttachment) {
			FilterAttachment filterAttachmentItem = (FilterAttachment) getAttachment(destinationSide).getItem();
			return filterAttachmentItem.doesItemPassFilter(getAttachment(destinationSide), stack, this);
		}

		// If there is an attachment, but its not a filter attachment, return false.
		return false;
	}

	public boolean canInsertThroughSide(ItemStack stack, Direction destinationSide) {
		// If there is no attachment on that side, we can insert.
		if (!hasAttachment(destinationSide)) {
			return true;
		}

		// If there is a filter attachment, evaluate against it.
		if (getAttachment(destinationSide).getItem() instanceof FilterAttachment) {
			FilterAttachment filterAttachmentItem = (FilterAttachment) getAttachment(destinationSide).getItem();
			return filterAttachmentItem.doesItemPassFilter(getAttachment(destinationSide), stack, this);
		}

		// If there is an attachment, but its not a filter attachment, return false.
		return false;
	}

	@Override
	protected void initializeCableProperties(ServerCable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		cable.getDataTag().putDouble(ITEM_CABLE_MAX_TRANSFER_SPEED, maxTransferSpeed);
		cable.getDataTag().putDouble(ITEM_CABLE_FRICTION_FACTOR_TAG, frictionFactor);
		cable.getDataTag().putDouble(ITEM_CABLE_ACCELERATION_FACTOR_TAG, accelerationFactor);
	}

	@Override
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Item.get());
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Only provide the item capability if we are not disabled on the provided side.
		if (cap == ForgeCapabilities.ITEM_HANDLER && side != null) {
			boolean disabled = false;
			if (side != null) {
				if (isClientSide()) {
					disabled = isSideDisabled(side);
				} else {
					// If the cable is not valid, just assume disabled. Could be that the cable is
					// not yet initailized server side.
					Optional<ServerCable> cable = getCable();
					disabled = cable.isPresent() ? cable.get().isDisabledOnSide(side) : true;
				}
			}

			if (!disabled) {
				return LazyOptional.of(() -> sideWrappers.get(side)).cast();
			}
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		if (fromUpdate) {
			// Serialize the contained item packets.
			ListTag itemPackets = new ListTag();
			containedPackets.values().forEach(parcel -> {
				CompoundTag packetTag = new CompoundTag();
				parcel.writeToNbt(packetTag);
				itemPackets.add(packetTag);
			});
			nbt.put("item_packets", itemPackets);
		}

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		if (fromUpdate) {
			// Clear any contained packets.
			containedPackets.clear();

			// Deserialize the packets.
			ListTag packets = nbt.getList("item_packets", Tag.TAG_COMPOUND);
			for (Tag packetTag : packets) {
				CompoundTag packetTagCompound = (CompoundTag) packetTag;
				ItemRoutingParcelClient newPacket = ItemRoutingParcelClient.create(packetTagCompound);
				if (!containedPackets.containsKey(newPacket.getId())) {
					containedPackets.put(newPacket.getId(), newPacket);
				}
			}
		}
	}

	public class ItemCableSideWrapper implements IItemHandler {
		private Direction side;
		private ItemCableComponent owningCable;

		public ItemCableSideWrapper(Direction side, ItemCableComponent owningCable) {
			this.side = side;
			this.owningCable = owningCable;
		}

		@Override
		public int getSlots() {
			return 1;
		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			return ItemStack.EMPTY;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (!stack.isEmpty()) {
				// Create a copy of the stack maxed to the slot limit of the cable.
				ItemStack insertStack = stack.copy();
				insertStack.setCount(Math.min(stack.getCount(), getSlotLimit(slot)));

				owningCable.<ItemNetworkModule>getNetworkModule(ModCableModules.Item.get()).ifPresent(network -> {
					// Attempt to insert the stack into the cable. We will use the default
					// extraction speed.
					ItemStack remainingAmount = network.transferItemStack(insertStack, getPos(), side.getOpposite(), false,
							StaticPowerConfig.getTier(tier).cableAttachmentConfiguration.cableExtractedItemInitialSpeed.get());
					if (remainingAmount.getCount() < insertStack.getCount()) {
						getTileEntity().setChanged();
						stack.setCount(stack.getCount() - insertStack.getCount() + remainingAmount.getCount());
					}
				});
			}
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return 64;
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return true;
		}
	}
}
