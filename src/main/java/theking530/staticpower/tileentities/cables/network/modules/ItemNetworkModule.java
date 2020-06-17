package theking530.staticpower.tileentities.cables.network.modules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.cables.item.ItemCableComponent;
import theking530.staticpower.tileentities.cables.item.ItemRoutingParcel;
import theking530.staticpower.tileentities.cables.network.factories.modules.CableNetworkModuleTypes;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class ItemNetworkModule extends AbstractCableNetworkModule {
	private static long CurrentPacketId;
	private final List<ItemRoutingParcel> ActivePackets;

	public ItemNetworkModule() {
		super(CableNetworkModuleTypes.ITEM_NETWORK_ATTACHMENT);
		ActivePackets = new ArrayList<ItemRoutingParcel>();
		CurrentPacketId = 0;
	}

	/**
	 * Ticks all the active packets.
	 */
	@Override
	public void tick(World world) {
		for (int i = ActivePackets.size() - 1; i >= 0; i--) {
			ItemRoutingParcel currentPacket = ActivePackets.get(i);
			if (currentPacket.incrementMoveTimer()) {
				if (transferItemPacket(currentPacket)) {
					ActivePackets.remove(i);
				}
			}
		}
	}

	/**
	 * Creates a new transfer request for the provided {@link ItemStack} and returns
	 * the remains of what cannot be inserted. If an empty stack is return, that
	 * indicates the whole of the provided stack will be transfered.
	 * 
	 * @param stack               The stack to transfer.
	 * @param cablePosition       The position of the cable.
	 * @param pulledFromDirection The direction relative to the cable position that
	 *                            the stack was pulled from.
	 * @param simulate            If true, the process will stop once it is
	 *                            determined what amount of items can be transfered.
	 * @return
	 */
	public ItemStack transferItemStack(ItemStack stack, BlockPos cablePosition, @Nullable Direction pulledFromDirection, boolean simulate) {
		// The source position can be null.
		BlockPos sourcePosition = pulledFromDirection != null ? cablePosition.offset(pulledFromDirection) : null;

		// Calculate the path and see if its not null.
		Path path;
		if ((path = getPathForItem(stack, cablePosition, sourcePosition)) != null) {
			// Get the destination inventory.
			TileEntity destinationTe = Network.getWorld().getTileEntity(path.getDestinationLocation());
			IItemHandler destInventory = destinationTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, path.getDestinationDirection().getOpposite()).orElse(null);

			// Ensure the destination inventory is valid.
			if (destInventory != null) {
				// Simulate an insert.
				ItemStack simulatedStack = InventoryUtilities.insertItemIntoInventory(destInventory, stack, true);

				// If simulating, just return the simulated insert result.
				if (simulate) {
					return simulatedStack;
				} else {
					// If the count of the simulate stack is lower than the count of the actual
					// stack, that means we were able to insert the difference's amount of items.
					if (simulatedStack.getCount() < stack.getCount()) {
						// Create the stack to be transfered.
						ItemStack stackToTransfer = stack.copy();
						stackToTransfer.setCount(stack.getCount() - simulatedStack.getCount());

						// Create the new item routing packet and initialize it with the transfer speed
						// of the cable it was pulled out of.
						ItemRoutingParcel packet = new ItemRoutingParcel(CurrentPacketId, stackToTransfer, path, pulledFromDirection.getOpposite());
						packet.setMoveTimer(getItemCableComponentAtPosition(path.getSourceLocation()).getTransferSpeed());

						// Add the packet to the list of active packets and increment the
						// CurrentPacketId.
						addRoutingParcel(packet);

						// Tell the cable component at the cable's location that it is trafnering an
						// item (for client rendering purposes only).
						getItemCableComponentAtPosition(path.getSourceLocation()).addTransferingItem(packet);
						return simulatedStack;
					}
				}
			}
		}
		return stack;
	}

	/**
	 * Transfers the item packet. Returning true removes the packet from the system
	 * entirely. Returning false implies that we still need to process this packet.
	 * 
	 * @param packet
	 * @return
	 */
	protected boolean transferItemPacket(ItemRoutingParcel packet) {
		if (packet.isAtFinalCable()) {
			BlockPos nextPosition = packet.getNextEntry().getPosition();
			TileEntity te = Network.getWorld().getTileEntity(nextPosition);
			if (te != null) {
				IItemHandler outputInventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, packet.getNextEntry().getDirectionOfEntry()).orElse(null);
				if (outputInventory != null) {
					ItemStack output = InventoryUtilities.insertItemIntoInventory(outputInventory, packet.getContainedItem(), false);
					if (getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()) != null) {
						getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).removeTransferingItem(packet.getId());
					}
					if (!output.isEmpty()) {
						ItemStack transferedAmount = transferItemStack(output, packet.getCurrentEntry().getPosition(), packet.getCurrentEntry().getDirectionOfEntry(), false);
						if(!transferedAmount.isEmpty()) {
							WorldUtilities.dropItem(Network.getWorld(), packet.getCurrentEntry().getPosition(), transferedAmount, transferedAmount.getCount());
						}
					}
					return true;
				}
			}
		} else {
			// If the next cable to go to is valid, transfer the item to it. Otherwise, drop
			// the item onto the ground. We shouldn't need to check for the current
			// position, that should be dropped if the block is broken. This is a temporrary
			// fix.
			if (getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()) != null && getItemCableComponentAtPosition(packet.getNextEntry().getPosition()) != null) {
				getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).removeTransferingItem(packet.getId());
				packet.incrementCurrentPathIndex();
				packet.setMoveTimer(getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).getTransferSpeed());
				getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).addTransferingItem(packet);
				return false;
			} else {
				WorldUtilities.dropItem(Network.getWorld(), packet.getCurrentEntry().getPosition(), packet.getContainedItem(), packet.getContainedItem().getCount());
				if (getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()) != null) {
					getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).removeTransferingItem(packet.getId());
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * Gets the item cable component at the provided position.
	 * 
	 * @param position
	 * @return
	 */
	protected ItemCableComponent getItemCableComponentAtPosition(BlockPos position) {
		if (Network.getWorld().getTileEntity(position) instanceof TileEntityBase) {
			return ((TileEntityBase) Network.getWorld().getTileEntity(position)).getComponent(ItemCableComponent.class);
		}
		return null;
	}

	/**
	 * Calculates the path for the provided item.
	 * 
	 * @param item           The itemstack to transfer.
	 * @param fromPosition   The position the itemstack is coming from.
	 * @param sourcePosition The position the itemstack was actually pulled from.
	 *                       Used to ensure we don't get a path back to the pulled
	 *                       from position. Can be null.
	 * @return The closest path if one exists.
	 */
	protected Path getPathForItem(ItemStack item, BlockPos fromPosition, @Nullable BlockPos sourcePosition) {
		// Iterate through all the destinations in the graph.
		for (TileEntity dest : Network.getGraph().getDestinations()) {
			// Skip trying to go to the same position the item came from or is at.
			if (dest.getPos().equals(sourcePosition)) {
				continue;
			}

			// Allocate an atomic bool to capture if a path is valid.
			AtomicBoolean isValid = new AtomicBoolean(false);

			// Iterate through all the paths to the proposed tile entity.
			for (Path path : Network.getPathCache().getPaths(fromPosition, dest.getPos())) {
				// If we're able to insert into that inventory, set the atomic boolean.
				dest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, path.getDestinationDirection()).ifPresent(inv -> {
					isValid.set(InventoryUtilities.canInsertItemIntoInventory(inv, item));
				});
				// If the atomic boolean is valid, then we have a valid path and we return it.
				if (isValid.get()) {
					return path;
				}
			}
		}
		return null;
	}

	protected void addRoutingParcel(ItemRoutingParcel parcel) {
		ActivePackets.add(parcel);
		parcel.incrementCurrentPathIndex();
		CurrentPacketId++;
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		if (tag.contains("current_packet_id")) {
			CurrentPacketId = tag.getLong("current_packet_id");
		}
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.putLong("current_packet_id", CurrentPacketId);
		return tag;
	}

}
