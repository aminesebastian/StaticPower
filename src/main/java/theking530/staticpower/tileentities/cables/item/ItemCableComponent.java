package theking530.staticpower.tileentities.cables.item;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.items.cableattachments.BasicExtractorAttachment;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.cables.network.CableNetwork;
import theking530.staticpower.tileentities.cables.network.CableNetworkManager;
import theking530.staticpower.tileentities.cables.network.factories.modules.CableNetworkModuleTypes;
import theking530.staticpower.tileentities.cables.network.modules.ItemCableAddedPacket;
import theking530.staticpower.tileentities.cables.network.modules.ItemCableRemovedPacket;
import theking530.staticpower.tileentities.cables.network.modules.ItemNetworkModule;

public class ItemCableComponent extends AbstractCableProviderComponent {
	private int itemTransferSpeed = 30;
	private int extractionTimer = 0;
	private int extractionTime = 5;
	private HashMap<Long, ItemRoutingParcelClient> containedPackets;

	public ItemCableComponent(String name, ResourceLocation type) {
		super(name, type);
		containedPackets = new HashMap<Long, ItemRoutingParcelClient>();
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		// Only do this on the client.
		if (getWorld().isRemote) {
			for (ItemRoutingParcelClient packet : containedPackets.values()) {
				packet.incrementMoveTimer();
			}
		}
	}

	@Override
	public void onOwningTileEntityRemoved() {
		// Only perform the following on the server.
		if (!getWorld().isRemote) {
			// Get the network.
			CableNetwork network = CableNetworkManager.get(getWorld()).getCable(getPos()).getNetwork();
			ItemNetworkModule itemNetworkModule = (ItemNetworkModule) network.getModule(CableNetworkModuleTypes.ITEM_NETWORK_ATTACHMENT);
			if (network == null || itemNetworkModule == null) {
				throw new RuntimeException(String.format("Encountered a null network for an ItemCableComponent at position: %1$s.", getPos()));
			}

			// Tell the network module this cable was broken.
			itemNetworkModule.onItemCableBroken(getPos());
		}

		super.onOwningTileEntityRemoved();
	}

	public int getTransferSpeed() {
		return itemTransferSpeed;
	}

	public void addTransferingItem(ItemRoutingParcelClient routingPacket) {
		containedPackets.put(routingPacket.getId(), routingPacket);
		if (!getWorld().isRemote) {
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> getWorld().getChunkAt(getPos())), new ItemCableAddedPacket(this, routingPacket));
		}
		getTileEntity().markDirty();
	}

	public void removeTransferingItem(long parcelId) {
		containedPackets.remove(parcelId);
		if (!getWorld().isRemote) {
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> getWorld().getChunkAt(getPos())), new ItemCableRemovedPacket(this, parcelId));
		}
		getTileEntity().markDirty();
	}

	public Collection<ItemRoutingParcelClient> getContainedItems() {
		return containedPackets.values();
	}

	public boolean canInsertOnDirection(Direction dir) {
		return false;
	}

	@Override
	protected void processAttachment(Direction side, ItemStack attachment) {
		// Process the extractor attachment.
		if (!getWorld().isRemote && attachment.getItem() instanceof BasicExtractorAttachment) {
			processExtractorAttachment(side, attachment);
		}
	}

	protected void processExtractorAttachment(Direction side, ItemStack attachment) {
		// Increment the extraction timer.
		if (extractionTimer < extractionTime) {
			extractionTimer++;
		}

		// If we have passed the extraction time, extract. Otherwise, return early.
		if (extractionTimer < extractionTime) {
			return;
		}

		// Reset the extraction timer.
		extractionTimer = 0;

		// Get the network.
		CableNetwork network = CableNetworkManager.get(getWorld()).getCable(getPos()).getNetwork();
		ItemNetworkModule itemNetworkModule = (ItemNetworkModule) network.getModule(CableNetworkModuleTypes.ITEM_NETWORK_ATTACHMENT);
		if (network == null || itemNetworkModule == null) {
			throw new RuntimeException(String.format("Encountered a null network for an ItemCableComponent at position: %1$s.", getPos()));
		}

		// Get the tile entity on the pulling side, return if it is null.
		TileEntity te = getWorld().getTileEntity(getPos().offset(side));
		if (te == null || te.isRemoved()) {
			return;
		}

		// Attempt to extract an item.
		te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(inv -> {
			for (int i = 0; i < inv.getSlots(); i++) {
				// Simulate an extract.
				ItemStack extractedItem = inv.extractItem(i, 2, true);

				// If the extracted item is empty, continue.
				if (extractedItem.isEmpty()) {
					continue;
				}

				ItemStack remainingAmount = itemNetworkModule.transferItemStack(extractedItem, getPos(), side, false);
				if (remainingAmount.getCount() < extractedItem.getCount()) {
					inv.extractItem(i, extractedItem.getCount() - remainingAmount.getCount(), false);
					getTileEntity().markDirty();
					break;
				}
			}
		});
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, BlockPos blockPosition) {
		AbstractCableProviderComponent overProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (overProvider != null && overProvider.getCableType() == getCableType()) {
			return CableConnectionState.CABLE;
		} else if (getWorld().getTileEntity(blockPosition) != null) {
			TileEntity te = getWorld().getTileEntity(blockPosition);
			if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).isPresent()) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return true;
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);

		if (fromUpdate) {
			// Serialize the contained item packets.
			ListNBT itemPackets = new ListNBT();
			containedPackets.values().forEach(parcel -> {
				CompoundNBT packetTag = new CompoundNBT();
				parcel.writeToNbt(packetTag);
				itemPackets.add(packetTag);
			});
			nbt.put("item_packets", itemPackets);
		}

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);

		if (fromUpdate) {
			// Clear any contained packets.
			containedPackets.clear();

			// Deserialize the packets.
			ListNBT packets = nbt.getList("item_packets", Constants.NBT.TAG_COMPOUND);
			for (INBT packetTag : packets) {
				CompoundNBT packetTagCompound = (CompoundNBT) packetTag;
				ItemRoutingParcelClient newPacket = ItemRoutingParcelClient.create(packetTagCompound);
				if (!containedPackets.containsKey(newPacket.getId())) {
					containedPackets.put(newPacket.getId(), newPacket);
				}
			}
		}
	}
}
