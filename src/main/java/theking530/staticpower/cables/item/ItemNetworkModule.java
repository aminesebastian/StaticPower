package theking530.staticpower.cables.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.pathfinding.Path;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class ItemNetworkModule extends AbstractCableNetworkModule {
	private static final Logger LOGGER = LogManager.getLogger(ItemNetworkModule.class);
	private static long CurrentPacketId;
	private HashMap<BlockPos, LinkedList<ItemRoutingParcel>> ActiveParcels;

	public ItemNetworkModule() {
		super(CableNetworkModuleTypes.ITEM_NETWORK_MODULE);
		ActiveParcels = new HashMap<BlockPos, LinkedList<ItemRoutingParcel>>();
		CurrentPacketId = 0;
	}

	/**
	 * Ticks all the active packets.
	 */
	@Override
	public void tick(World world) {
		List<BlockPos> destinationsToRemove = new ArrayList<BlockPos>();

		// For each destination.
		for (Entry<BlockPos, LinkedList<ItemRoutingParcel>> entry : ActiveParcels.entrySet()) {
			// Get the parcels going to that destination.
			for (int i = entry.getValue().size() - 1; i >= 0; i--) {
				// Get the parcel.
				ItemRoutingParcel currentPacket = entry.getValue().get(i);
				// Increment the move time and stop tracking if the transfer returns true (in
				// the case of a final insert, network transfer, or re-routing).
				if (currentPacket.incrementMoveTimer()) {
					if (transferItemPacket(currentPacket)) {
						entry.getValue().remove(i);
					}
				}
			}

			// If the list of parcels is empty, mark that destination for purging.
			if (entry.getValue().isEmpty()) {
				destinationsToRemove.add(entry.getKey());
			}
		}

		// Remove any destinations marked for removal.
		for (BlockPos destToRemove : destinationsToRemove) {
			ActiveParcels.remove(destToRemove);
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
		// Mark the network manager as dirty.
		CableNetworkManager.get(Network.getWorld()).markDirty();

		// The source position can be null. This value is only used to not bounce items
		// back to the inventory it came from IF it comes from one.
		BlockPos sourcePosition = pulledFromDirection != null ? cablePosition.offset(pulledFromDirection) : null;

		// Calculate the path and see if its not null.
		Path path = getPathForItem(stack, cablePosition, sourcePosition);
		if (path != null) {
			return routeItem(stack, path, pulledFromDirection, cablePosition, simulate);
		}
		return stack;
	}

	/**
	 * Retrieves an {@link ItemStack} from any inventory in the network. The
	 * criteria for a matching {@link ItemStack} are Item, NBT, Damage, and
	 * capabilities.
	 * 
	 * @param stack               The stack of item to retrieve. Count doesn't
	 *                            matter.
	 * @param maxExtract          The maximum amount to retrieve.
	 * @param destinationPosition The destination to insert into.
	 * @return True if we were able to retrieve, false otherwise.
	 */
	public boolean retrieveItemStack(ItemStack stack, int maxExtract, BlockPos destinationPosition) {
		// Get all the destination wrappers that contain this item.
		List<RetrivalSourceWrapper> sources = getSourcesForItemRetrieval(stack, destinationPosition);
		if (sources.size() == 0) {
			return false;
		}

		// Preallocate the shortest values.
		Path shortestPath = null;
		int shortestPathLength = Integer.MAX_VALUE;
		RetrivalSourceWrapper targetSource = null;
		// Calculate the shortest path.
		for (RetrivalSourceWrapper source : sources) {
			// Get all the potential paths.
			List<Path> paths = Network.getPathCache().getPaths(source.getDestinationWrapper().getConnectedCable(), destinationPosition, CableNetworkModuleTypes.ITEM_NETWORK_MODULE);

			// Iterate through all the paths to the proposed tile entity.
			for (Path path : paths) {
				if (path.getLength() < shortestPathLength) {
					shortestPath = path;
					shortestPathLength = path.getLength();
					targetSource = source;
				}
			}
		}

		// If we have a path, get the source inventory.
		if (shortestPath != null) {
			IItemHandler sourceInv = targetSource.getDestinationWrapper().getTileEntity()
					.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, targetSource.getDestinationWrapper().getDestinationSide()).orElse(null);

			// If the source inventory is valid.
			if (sourceInv != null) {
				// Simulate pulling the maximum amount we can extract.
				ItemStack pulledAmount = sourceInv.extractItem(targetSource.getInventorySlot(), maxExtract, true);

				// Attempt to route that amount, and return the actual amount routed.
				ItemStack actuallyTransfered = routeItem(pulledAmount, shortestPath, targetSource.getDestinationWrapper().getDestinationSide().getOpposite(),
						targetSource.getDestinationWrapper().getConnectedCable(), false);

				// Then, extract the actual amount routed.
				sourceInv.extractItem(targetSource.getInventorySlot(), pulledAmount.getCount() - actuallyTransfered.getCount(), false);

				// If the actuallyTransfered amount is greater than 0, return success.
				return pulledAmount.getCount() > 0 && pulledAmount.getCount() != actuallyTransfered.getCount();
			}
		}
		return false;
	}

	/**
	 * This method should be called when an item cable is broken.
	 * 
	 * @param cablePosition
	 */
	public void onItemCableBroken(BlockPos cablePosition) {
		// For each destination.
		for (Entry<BlockPos, LinkedList<ItemRoutingParcel>> entry : ActiveParcels.entrySet()) {
			// Get the parcels going to that destination.
			for (int i = entry.getValue().size() - 1; i >= 0; i--) {
				// Get the parcel.
				ItemRoutingParcel currentPacket = entry.getValue().get(i);
				// If the position is equal to the remove cable's, drop the item only the floor.
				if (currentPacket.getCurrentEntry().getPosition().equals(cablePosition)) {
					WorldUtilities.dropItem(Network.getWorld(), cablePosition, currentPacket.getContainedItem());
					entry.getValue().remove(i);
				}
			}
		}
	}

	protected ItemStack routeItem(ItemStack stack, Path path, Direction pulledFromDirection, BlockPos sourceCablePosition, boolean simulate) {
		// Get the destination inventory.
		TileEntity destinationTe = Network.getWorld().getTileEntity(path.getDestinationLocation());
		IItemHandler destInventory = destinationTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, path.getDestinationDirection().getOpposite()).orElse(null);

		// Ensure the destination inventory is valid.
		if (destInventory != null) {
			// Simulate an insert.
			ItemStack simulatedStack = simulateInsertWithPrediction(stack, destInventory, destinationTe.getPos());

			// If simulating, just return the simulated insert result.
			if (simulate) {
				return simulatedStack;
			} else {
				// If the count of the simulate stack is lower than the count of the actual
				// stack, that means we were able to insert the in the amount of the difference
				// between the two.
				if (simulatedStack.getCount() < stack.getCount()) {
					// Create the stack to be transfered and set its size to the difference.
					ItemStack stackToTransfer = stack.copy();
					stackToTransfer.setCount(stack.getCount() - simulatedStack.getCount());

					// Create the new item routing packet and initialize it with the transfer speed
					// of the cable it was pulled out of.
					ItemRoutingParcel packet = new ItemRoutingParcel(CurrentPacketId, stackToTransfer, path, pulledFromDirection.getOpposite());
					packet.setMovementSpeed(getItemCableComponentAtPosition(sourceCablePosition).getTransferSpeed());

					// Add the packet to the list of active packets.
					addRoutingParcel(packet);

					// Tell the cable component at the cable's location that it is transferring an
					// item (for client rendering purposes only).
					getItemCableComponentAtPosition(sourceCablePosition).addTransferingItem(packet);
					return simulatedStack;
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
		// Get the next position.
		BlockPos nextPosition = packet.getNextEntry().getPosition();

		// If the next position is neither a cable in this network nor a destination,
		// re-route it.
		if (!Network.getGraph().getCables().containsKey(nextPosition) && !Network.getGraph().getDestinations().containsKey(nextPosition)) {
			rerouteOrTransferParcel(packet);
			return true;
		} else if (packet.isAtFinalCable()) {
			// If we're at the final cable, attempt to insert the parcel.
			TileEntity te = Network.getWorld().getTileEntity(nextPosition);

			// If the tile entity is there, attempt the insert, if not, reroute it.
			if (te != null && !te.isRemoved()) {
				// Get the inventory, if it is not valid, re-route the parcel. Otherwise, insert
				// as much as we can, and if there are any left overs, reroute them. Tell the
				// item cable at the current location that it should stop rendering the parcel.
				IItemHandler outputInventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, packet.getNextEntry().getDirectionOfEntry().getOpposite()).orElse(null);
				if (outputInventory != null) {
					ItemStack output = InventoryUtilities.insertItemIntoInventory(outputInventory, packet.getContainedItem(), false);
					getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).removeTransferingItem(packet.getId());
					if (!output.isEmpty()) {
						handlePartialInsert(output, packet);
					}
					return true;
				}
			}
			rerouteOrTransferParcel(packet);
			return true;
		} else {
			// Move the parcel one cable forward.
			getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).removeTransferingItem(packet.getId());
			packet.incrementCurrentPathIndex();
			packet.setMovementSpeed(getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).getTransferSpeed());
			getItemCableComponentAtPosition(packet.getCurrentEntry().getPosition()).addTransferingItem(packet);
		}

		return false;
	}

	/**
	 * Checks to see if the provided parcel is still part of this network.
	 * 
	 * @param parcel
	 * @return
	 */
	protected boolean isParcelStillInNetwork(ItemRoutingParcel parcel) {
		if (Network.getGraph().getCables().containsKey(parcel.getCurrentEntry().getPosition())) {
			return true;
		}
		return false;
	}

	/**
	 * Attempts to return the parcel to its sender. If not, attempts to send it to
	 * any other inventory. Worst case, it gets dropped on the floor. This may be
	 * called if the destination for the parcel is destroyed or the cable the parcel
	 * is currently in is transfered to another network.
	 * 
	 * @param remainingItems The amount of items that need to be re-routed. If this
	 *                       is straggling in the middle of transmission, the
	 *                       remainingItems should be equal to the parcel contents.
	 *                       But they may be different if this method is called in
	 *                       response to a partial insert into the destination.
	 * @param parcel
	 */
	protected void rerouteOrTransferParcel(ItemRoutingParcel parcel) {
		// First, remove this parcel from the current cable.
		if (getItemCableComponentAtPosition(parcel.getCurrentEntry().getPosition()) != null) {
			getItemCableComponentAtPosition(parcel.getCurrentEntry().getPosition()).removeTransferingItem(parcel.getId());
		} else {
			LOGGER.error(String.format("Encountered an invalid item cable at current parcel entry location: %1$s.", parcel.getCurrentEntry().getPosition()));
		}

		// Check if the parcel is still in this network. If it is, just reroute it or
		// drop it. If not, transfer it to another network if possible. If not, drop it.
		if (isParcelStillInNetwork(parcel)) {
			ItemStack transferedAmount = transferItemStack(parcel.getContainedItem(), parcel.getCurrentEntry().getPosition(), parcel.getCurrentEntry().getDirectionOfEntry(), false);
			// If no items were able to be transfered, just drop the parcel in to the world.
			if (transferedAmount.getCount() == parcel.getContainedItem().getCount()) {
				WorldUtilities.dropItem(Network.getWorld(), parcel.getCurrentEntry().getPosition(), transferedAmount);
			}
		} else {
			if (!transferParcelToAnotherNetwork(parcel)) {
				WorldUtilities.dropItem(Network.getWorld(), parcel.getCurrentEntry().getPosition(), parcel.getContainedItem());
			}
		}
	}

	/**
	 * We calculate the amount of items to send to the destination at extraction
	 * time. If the destination is filled or loses space between then and the time
	 * the parcel is inserted, we can only insert part of it. We have to route the
	 * rest somewhere else.
	 * 
	 * @param remainingItems
	 * @param parcel
	 */
	protected void handlePartialInsert(ItemStack remainingItems, ItemRoutingParcel parcel) {
		// First, remove this parcel from the current cable.
		getItemCableComponentAtPosition(parcel.getCurrentEntry().getPosition()).removeTransferingItem(parcel.getId());

		// Loop until we're able to fully transfer the parcel OR we run out of options.
		ItemStack lastTransferedAmount = ItemStack.EMPTY;
		ItemStack transferedAmount = remainingItems;
		while (lastTransferedAmount.getCount() != transferedAmount.getCount()) {
			lastTransferedAmount = transferedAmount;
			transferedAmount = transferItemStack(parcel.getContainedItem(), parcel.getCurrentEntry().getPosition(), parcel.getCurrentEntry().getDirectionOfEntry(), false);
			if (transferedAmount.isEmpty()) {
				break;
			}
		}

		// If there are still items remaining, drop them on the ground.
		if (!transferedAmount.isEmpty()) {
			WorldUtilities.dropItem(Network.getWorld(), parcel.getCurrentEntry().getPosition(), transferedAmount);
		}
	}

	/**
	 * Transfers this parcel to another network.
	 * 
	 * @param remainingItems
	 * @param parcel
	 * @return
	 */
	protected boolean transferParcelToAnotherNetwork(ItemRoutingParcel parcel) {
		// Check to see if the parcel's current location is still a cable.
		ServerCable otherNetworkCable = CableNetworkManager.get(Network.getWorld()).getCable(parcel.getCurrentEntry().getPosition());
		if (otherNetworkCable == null) {
			return false;
		}

		// Check to ensure that the other cable has a network.
		CableNetwork otherNetwork = otherNetworkCable.getNetwork();
		if (!otherNetwork.hasModule(CableNetworkModuleTypes.ITEM_NETWORK_MODULE)) {
			return false;
		}

		// Transfer this parcel to that network.
		ItemNetworkModule otherItemModule = otherNetwork.getModule(CableNetworkModuleTypes.ITEM_NETWORK_MODULE);

		// Loop until we're able to fully transfer the parcel OR we run out of options.
		ItemStack lastTransferedAmount = ItemStack.EMPTY;
		ItemStack transferedAmount = parcel.getContainedItem();
		while (lastTransferedAmount.getCount() != transferedAmount.getCount()) {
			lastTransferedAmount = transferedAmount;
			transferedAmount = otherItemModule.transferItemStack(parcel.getContainedItem(), parcel.getCurrentEntry().getPosition(), parcel.getCurrentEntry().getDirectionOfEntry(), false);
			if (transferedAmount.isEmpty()) {
				break;
			}
		}

		// If there are still items remaining, drop them on the ground.
		if (!transferedAmount.isEmpty()) {
			WorldUtilities.dropItem(Network.getWorld(), parcel.getCurrentEntry().getPosition(), transferedAmount);
		}
		return true;
	}

	/**
	 * Checks to see if we can insert into a destination through a cable. If false,
	 * this means there is a filter on that cable, or the cable is actively
	 * extracting as well.
	 * 
	 * @param stackToInsert
	 * @param cablePosition
	 * @param destinationSide
	 * @return
	 */
	protected boolean canInsertThroughCable(ItemStack stackToInsert, BlockPos cablePosition, Direction destinationSide) {
		ItemCableComponent cable = getItemCableComponentAtPosition(cablePosition);
		if (cable == null) {
			return false;
		}

		return cable.canInsertThroughSide(stackToInsert, destinationSide);
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
	 * @param item The itemstack to transfer.
	 * @paramcablePosition The position the itemstack is coming from.
	 * @param sourcePosition The position the itemstack was actually pulled from.
	 *                       Used to ensure we don't get a path back to the pulled
	 *                       from position. Can be null.
	 * @return The closest path if one exists.
	 */
	protected @Nullable Path getPathForItem(ItemStack item, BlockPos cablePosition, @Nullable BlockPos sourcePosition) {
		// Preallocate the shortest values.
		Path shortestPath = null;
		int shortestPathLength = Integer.MAX_VALUE;

		// Iterate through all the destinations in the graph.
		for (DestinationWrapper dest : Network.getGraph().getDestinations().values()) {
			// Skip any destinations that don't support item transfer.
			if (!dest.supportsType(DestinationType.ITEM)) {
				continue;
			}
			// Skip trying to go to the same position the item came from or is at.
			if (dest.getPos().equals(sourcePosition)) {
				continue;
			}

			// Get all the potential paths.
			List<Path> paths = Network.getPathCache().getPaths(cablePosition, dest.getPos(), CableNetworkModuleTypes.ITEM_NETWORK_MODULE);

			// Continue if no path is found.
			if (paths == null) {
				continue;
			}

			// Iterate through all the paths to the proposed tile entity.
			for (Path path : paths) {
				// Allocate an atomic bool to capture if a path is valid.
				AtomicBoolean isValid = new AtomicBoolean(false);

				// If we can't insert through that cable, skip this path.
				if (!canInsertThroughCable(item, path.getFinalCablePosition(), path.getFinalCableExitDirection())) {
					continue;
				}

				// If we're able to insert into that inventory, set the atomic boolean.
				dest.getTileEntity().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, path.getDestinationDirection().getOpposite()).ifPresent(inv -> {
					isValid.set(InventoryUtilities.canPartiallyInsertItemIntoInventory(inv, item));
				});

				// If the atomic boolean is valid, then we have a valid path and we return it.
				if (isValid.get()) {
					if (path.getLength() < shortestPathLength) {
						shortestPath = path;
						shortestPathLength = path.getLength();
					}
				}
			}
		}
		return shortestPath;
	}

	/**
	 * Gets a list of all destinations that contain the provided item (not counting
	 * the source position if provided).
	 * 
	 * @param item           The {@link ItemStack} to search for.
	 * @param sourcePosition The optional source position to ignore when performing
	 *                       the search.
	 * @return
	 */
	protected List<RetrivalSourceWrapper> getSourcesForItemRetrieval(ItemStack item, @Nullable BlockPos sourcePosition) {
		List<RetrivalSourceWrapper> validDestinations = new LinkedList<RetrivalSourceWrapper>();

		// Iterate through all the destinations in the graph.
		for (DestinationWrapper dest : Network.getGraph().getDestinations().values()) {
			// Skip any destinations that don't support item transfer.
			if (!dest.supportsType(DestinationType.ITEM)) {
				continue;
			}
			// Skip trying to go to the same position the item came from or is at.
			if (dest.getPos().equals(sourcePosition)) {
				continue;
			}

			// Allocate an atomic bool to capture if a path is valid.
			AtomicReference<List<RetrivalSourceWrapper>> wrappers = new AtomicReference<List<RetrivalSourceWrapper>>(validDestinations);

			// If we're able to insert into that inventory, set the atomic boolean.
			dest.getTileEntity().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dest.getDestinationSide()).ifPresent(inv -> {
				// If this inventory contains the provided item, add the destination to the
				// list.
				int slot = InventoryUtilities.getFirstSlotContainingItem(item, inv);
				if (slot >= 0) {
					wrappers.get().add(new RetrivalSourceWrapper(dest, slot));
				}
			});
		}
		return validDestinations;
	}

	protected void addRoutingParcel(ItemRoutingParcel parcel) {
		// If we aren't tracking and parcels for the destination, create an empty linked
		// list.
		if (!ActiveParcels.containsKey(parcel.getPath().getDestinationLocation())) {
			ActiveParcels.put(parcel.getPath().getDestinationLocation(), new LinkedList<ItemRoutingParcel>());
		}

		// Add the parcel.
		ActiveParcels.get(parcel.getPath().getDestinationLocation()).add(parcel);

		// Increment the parcel's current path inde, set its speedx and increment th
		// static parcel id.
		parcel.incrementCurrentPathIndex();
		CurrentPacketId++;
	}

	protected LinkedList<ItemStack> getItemsTravlingToDestination(BlockPos destination) {
		LinkedList<ItemStack> output = new LinkedList<ItemStack>();

		// Get all the active parcels for this destination if there are any.
		if (ActiveParcels.containsKey(destination)) {
			ActiveParcels.get(destination).forEach(parcel -> output.addFirst(parcel.getContainedItem()));
		}

		return output;
	}

	protected ItemStack simulateInsertWithPrediction(ItemStack itemToInsert, IItemHandler targetInventory, BlockPos targetLocation) {
		List<ItemStack> alreadyTravelingItems = getItemsTravlingToDestination(targetLocation);

		// Create a new item handler.
		IItemHandler dupInv = new ItemStackHandler(targetInventory.getSlots());
		for (int i = 0; i < targetInventory.getSlots(); i++) {
			dupInv.insertItem(i, targetInventory.getStackInSlot(i).copy(), false);
		}

		// Add the already traveling items to the new handler.
		for (ItemStack alreadyTravelingItem : alreadyTravelingItems) {
			if (!InventoryUtilities.insertItemIntoInventory(dupInv, alreadyTravelingItem, false).isEmpty()) {
				return itemToInsert;
			}
		}

		// Attempt to insert the provided item into the new handler.
		return InventoryUtilities.insertItemIntoInventory(dupInv, itemToInsert, false);
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		// Save the current parcel id.
		if (tag.contains("current_parcel_id")) {
			CurrentPacketId = tag.getLong("current_parcel_id");
		}

		ActiveParcels = new HashMap<BlockPos, LinkedList<ItemRoutingParcel>>();
		// Get the parcel NBT list and add the parcels.
		ListNBT parcelNBTList = tag.getList("parcels", Constants.NBT.TAG_COMPOUND);
		parcelNBTList.forEach(parcelTag -> {
			CompoundNBT parcelTagCompound = (CompoundNBT) parcelTag;
			ItemRoutingParcel parcel = new ItemRoutingParcel(parcelTagCompound);

			if (!ActiveParcels.containsKey(parcel.getPath().getDestinationLocation())) {
				ActiveParcels.put(parcel.getPath().getDestinationLocation(), new LinkedList<ItemRoutingParcel>());
			}
			// Add the parcel.
			ActiveParcels.get(parcel.getPath().getDestinationLocation()).add(parcel);
		});

	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.putLong("current_parcel_id", CurrentPacketId);

		// Get all the active parcels.
		List<ItemRoutingParcel> activeParcels = new ArrayList<ItemRoutingParcel>();
		for (Entry<BlockPos, LinkedList<ItemRoutingParcel>> entry : ActiveParcels.entrySet()) {
			// Get the parcels going to that destination.
			for (ItemRoutingParcel parcel : entry.getValue()) {
				activeParcels.add(parcel);
			}
		}

		// Serialize the parcels to the list.
		ListNBT parcelNBTList = new ListNBT();
		activeParcels.forEach(parcel -> {
			CompoundNBT parcelTag = new CompoundNBT();
			parcel.writeToNbt(parcelTag);
			parcelNBTList.add(parcelTag);
		});
		tag.put("parcels", parcelNBTList);

		return tag;
	}

}
