package theking530.staticpower.tileentities.cables.item;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.items.cableattachments.BasicExtractorAttachment;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.cables.network.CableNetwork;
import theking530.staticpower.tileentities.cables.network.CableNetworkManager;
import theking530.staticpower.tileentities.cables.network.factories.modules.CableNetworkModuleTypes;
import theking530.staticpower.tileentities.cables.network.modules.ItemNetworkModule;

public class ItemCableComponent extends AbstractCableProviderComponent {
	private int itemTransferSpeed = 20;
	private int extractionTimer = 0;
	private int extractionTime = 20;
	private List<ItemRoutingPacket> containedPackets;

	public ItemCableComponent(String name, ResourceLocation type) {
		super(name, type);
		containedPackets = new ArrayList<ItemRoutingPacket>();
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		for (ItemRoutingPacket packet : containedPackets) {
			packet.incrementMoveTimer();
		}
	}

	public int getTransferSpeed() {
		return itemTransferSpeed;
	}

	public void addTransferingItem(ItemRoutingPacket routingPacket) {
		containedPackets.add(routingPacket);
		getTileEntity().markTileEntityForSynchronization();
	}

	public void removeTransferingItem(ItemRoutingPacket routingPacket) {
		containedPackets.remove(routingPacket);
		getTileEntity().markTileEntityForSynchronization();
	}

	public List<ItemRoutingPacket> getContainedItems() {
		return containedPackets;
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
				ItemStack extractedItem = inv.extractItem(i, 1, true);

				// If the extracted item is empty, continue.
				if (extractedItem.isEmpty()) {
					continue;
				}

				ItemStack remainingAmount = itemNetworkModule.transferItemStack(extractedItem, getPos(), side, false);
				if (remainingAmount.getCount() < extractedItem.getCount()) {
					inv.extractItem(i, extractedItem.getCount() - remainingAmount.getCount(), false);
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
			containedPackets.forEach(packet -> {
				CompoundNBT packetTag = new CompoundNBT();
				packet.writeToNbt(packetTag);
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
				containedPackets.add(ItemRoutingPacket.create(packetTagCompound));
			}
		}
	}
}
