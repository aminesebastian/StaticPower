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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.pathfinding.Path;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.digistorenetwork.ioport.DigitstoreIOPortInventoryComponent;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.MetricConverter;
import theking530.staticpower.utilities.WorldUtilities;

public class ItemNetworkModule extends CableNetworkModule {
	private static final Logger LOGGER = LogManager.getLogger(ItemNetworkModule.class);
	protected HashMap<BlockPos, LinkedList<ItemRoutingParcel>> ActiveParcels;

	public ItemNetworkModule() {
		super(ModCableModules.Item.get());
		ActiveParcels = new HashMap<BlockPos, LinkedList<ItemRoutingParcel>>();
	}

	/**
	 * Ticks all the active packets.
	 */
	@Override
	public void tick(Level world) {
		// We have to capture these ahead of time because iterating using an iterator
		// may cause ConcurrentModificationException.
		List<BlockPos> positionsToQuery = ActiveParcels.keySet().stream().toList();
		List<BlockPos> destinationsToRemove = new ArrayList<BlockPos>();

		// For each destination.
		for (int j = positionsToQuery.size() - 1; j >= 0; j--) {
			LinkedList<ItemRoutingParcel> parcels = ActiveParcels.get(positionsToQuery.get(j));

			// Get the parcels going to that destination.
			for (int i = parcels.size() - 1; i >= 0; i--) {
				// Get the parcel.
				ItemRoutingParcel currentPacket = parcels.get(i);
				// Increment the move time and stop tracking if the transfer returns true (in
				// the case of a final insert, network transfer, or re-routing). If we are half
				// way through traversal (meaning, at a point where the item may visual exist
				// the cable even if there isn't a cable there), check to see if the next
				// position is still valid. If it is not, attempt to re-route.
				if (currentPacket.incrementMoveTimer()) {
					if (transferItemPacket(currentPacket)) {
						parcels.remove(i);
					}
				} else if (currentPacket.isHalfWayThroughPath()) {
					if (!isParcelPathValid(currentPacket)) {
						rerouteOrTransferParcel(currentPacket);
						parcels.remove(i);
					}
				}
			}

			// If the list of parcels is empty, mark that destination for purging.
			if (parcels.isEmpty()) {
				destinationsToRemove.add(positionsToQuery.get(j));
			}
		}

		// Remove any destinations marked for removal.
		for (BlockPos destToRemove : destinationsToRemove) {
			ActiveParcels.remove(destToRemove);
		}
	}

	/**
	 * When networks join, add all the parcels in transit from this network to the
	 * other one.
	 */
	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
		if (other.hasModule(ModCableModules.Item.get())) {
			ItemNetworkModule module = (ItemNetworkModule) other.getModule(ModCableModules.Item.get());

			for (Entry<BlockPos, LinkedList<ItemRoutingParcel>> entry : ActiveParcels.entrySet()) {
				if (!module.ActiveParcels.containsKey(entry.getKey())) {
					module.ActiveParcels.put(entry.getKey(), new LinkedList<ItemRoutingParcel>());
				}
				module.ActiveParcels.get(entry.getKey()).addAll(entry.getValue());
			}

			// Clear this module's parcels.
			ActiveParcels.clear();
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
	public ItemStack transferItemStack(ItemStack stack, BlockPos cablePosition, @Nullable Direction pulledFromDirection, boolean simulate, double blocksPerSecond) {
		return transferItemStack(stack, cablePosition, pulledFromDirection, simulate, simulate, blocksPerSecond);
	}

	/**
	 * INTERNAL USE ONLY: Creates a new transfer request for the provided
	 * {@link ItemStack} and returns the remains of what cannot be inserted. If an
	 * empty stack is return, that indicates the whole of the provided stack will be
	 * transfered.
	 * 
	 * @param stack               The stack to transfer.
	 * @param cablePosition       The position of the cable.
	 * @param pulledFromDirection The direction relative to the cable position that
	 *                            the stack was pulled from.
	 * @param simulate            If true, the process will stop once it is
	 *                            determined what amount of items can be transfered.
	 * @param startHalfWay        If true, the traversal beings half way through the
	 *                            transfer process.
	 * @return
	 */
	protected ItemStack transferItemStack(ItemStack stack, BlockPos cablePosition, @Nullable Direction pulledFromDirection, boolean simulate, boolean startHalfWay,
			double blocksPerSecond) {
		// Mark the network manager as dirty.
		CableNetworkManager.get(Network.getWorld()).setDirty();

		// The source position can be null. This value is only used to not bounce items
		// back to the inventory it came from IF it comes from one.
		BlockPos sourcePosition = pulledFromDirection != null ? cablePosition.relative(pulledFromDirection) : null;

		// Calculate the path and see if its not null.
		Path path = getPathForItem(stack, cablePosition, sourcePosition, pulledFromDirection, false);

		if (path != null) {
			return transferItemStack(stack, path, pulledFromDirection, simulate, startHalfWay, blocksPerSecond);
		}
		return stack;
	}

	/**
	 * INTERNAL USE ONLY: Creates a new transfer request with a provided path.
	 * 
	 * @param stack               The stack to transfer.
	 * @param path                The path to send the item on.
	 * @param pulledFromDirection The direction relative to the cable position that
	 *                            the stack was pulled from.
	 * @param simulate            If true, the process will stop once it is
	 *                            determined what amount of items can be transfered.
	 * @param startHalfWay        If true, the traversal beings half way through the
	 *                            transfer process.
	 * @return
	 */
	protected ItemStack transferItemStack(ItemStack stack, Path path, @Nullable Direction pulledFromDirection, boolean simulate, boolean startHalfWay, double blocksPerSecond) {
		// Mark the network manager as dirty.
		CableNetworkManager.get(Network.getWorld()).setDirty();

		// Route the item.
		return routeItem(stack, path, pulledFromDirection, path.getSourceCableLocation(), simulate, startHalfWay, blocksPerSecond);
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
	public boolean retrieveItemStack(ItemStack stack, int maxExtract, BlockPos destinationPosition, Direction requestingSide, double blocksPerSecond) {
		// Get all the destination wrappers that contain this item.
		List<RetrivalSourceWrapper> sources = getSourcesForItemRetrieval(stack, destinationPosition);
		if (sources.size() == 0) {
			return false;
		}

		// Preallocate the shortest values trackers.
		Path shortestPath = null;
		float shortestPathLength = Float.MAX_VALUE;
		RetrivalSourceWrapper targetSource = null;

		// Calculate the shortest path.
		for (RetrivalSourceWrapper source : sources) {
			// Get all the potential paths.
			List<Path> paths = Network.getPathCache().getPaths(source.getDestinationWrapper().getFirstConnectedCable(), destinationPosition, ModCableModules.Item.get());

			// Iterate through all the paths to the proposed tile entity.
			for (Path path : paths) {
				// Make sure the path goes to the same side.
				if (path.getDestinationDirection() != requestingSide) {
					continue;
				}

				// Check for the shortest path.
				if (path.getLength() < shortestPathLength) {
					shortestPath = path;
					shortestPathLength = path.getLength();
					targetSource = source;
				}
			}
		}

		// If we have a path, get the source inventory.
		if (shortestPath != null) {
			// The method #getSourcesForItemRetrieval should only return sources that have
			// tile entities, so no need to check that here.
			IItemHandler sourceInv = targetSource.getDestinationWrapper().getTileEntity()
					.getCapability(ForgeCapabilities.ITEM_HANDLER, targetSource.getDestinationWrapper().getFirstConnectedDestinationSide()).orElse(null);

			// If the source inventory is valid.
			if (sourceInv != null) {
				// Simulate pulling the maximum amount we can extract.
				ItemStack pulledAmount = sourceInv.extractItem(targetSource.getInventorySlot(), maxExtract, true);

				// Attempt to route that amount, and return the actual amount routed.
				ItemStack actuallyTransfered = routeItem(pulledAmount, shortestPath, targetSource.getDestinationWrapper().getFirstConnectedDestinationSide().getOpposite(),
						targetSource.getDestinationWrapper().getFirstConnectedCable(), false, false, blocksPerSecond);

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

	protected ItemStack routeItem(ItemStack stack, Path path, Direction pulledFromDirection, BlockPos sourceCablePosition, boolean simulate, boolean startHalfWay,
			double blocksPerSecond) {
		// Get the destination inventory.
		BlockEntity destinationTe = Network.getWorld().getBlockEntity(path.getDestinationLocation());
		IItemHandler destInventory = destinationTe.getCapability(ForgeCapabilities.ITEM_HANDLER, path.getDestinationDirection().getOpposite()).orElse(null);

		// Ensure the destination inventory is valid.
		if (destInventory != null) {
			// Simulate an insert.
			ItemStack simulatedStack = simulateInsertWithPrediction(stack, destInventory, destinationTe.getBlockPos());

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
					ItemRoutingParcel packet = new ItemRoutingParcel(CableNetworkManager.get(Network.getWorld()).getCurrentCraftingId(), stackToTransfer, path,
							pulledFromDirection);
					packet.setMovementTime((int) (20 / blocksPerSecond));

					// Add the packet to the list of active packets.
					addRoutingParcel(packet, startHalfWay);

					// Tell the cable component at the cable's location that it is transferring an
					// item (for client rendering purposes only).
					sendTransferingPacketToCable(sourceCablePosition, packet);
					return simulatedStack;
				}
			}
		}
		return stack;
	}

	protected boolean sendTransferingPacketToCable(BlockPos position, ItemRoutingParcel parcel) {
		ItemCableComponent cable = getItemCableComponentAtPosition(position);
		if (cable != null) {
			cable.addTransferingItem(parcel);
			return true;
		}
		LOGGER.warn(String.format("Attempted to send add item packet to item cable at position: %1$s that does not exist."), position);
		return false;
	}

	protected boolean sendRemoveTransferingPacketToCable(BlockPos position, long parcelId) {
		ItemCableComponent cable = getItemCableComponentAtPosition(position);
		if (cable != null) {
			cable.removeTransferingItem(parcelId);
			return true;
		}
		LOGGER.warn(String.format("Attempted to send update item removal update packet to item cable at position: %1$s that does not exist."), position);
		return false;
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
		if (!isParcelPathValid(packet)) {
			rerouteOrTransferParcel(packet);
			return true;
		} else if (packet.isAtFinalCable()) {
			// If we're at the final cable, attempt to insert the parcel.
			BlockEntity te = Network.getWorld().getBlockEntity(nextPosition);

			// If the tile entity is there, attempt the insert, if not, reroute it.
			if (te != null && !te.isRemoved()) {
				// Get the inventory, if it is not valid, re-route the parcel. Otherwise, insert
				// as much as we can, and if there are any left overs, reroute them. Tell the
				// item cable at the current location that it should stop rendering the parcel.
				IItemHandler outputInventory = te.getCapability(ForgeCapabilities.ITEM_HANDLER, packet.getNextEntry().getDirectionOfEntry().getOpposite())
						.orElse(null);
				if (outputInventory != null) {
					ItemStack output = InventoryUtilities.insertItemIntoInventory(outputInventory, packet.getContainedItem(), false);
					sendRemoveTransferingPacketToCable(packet.getCurrentEntry().getPosition(), packet.getId());
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
			sendRemoveTransferingPacketToCable(packet.getCurrentEntry().getPosition(), packet.getId());
			packet.incrementCurrentPathIndex();

			// Get the current and next max move times (how many ticks it takes to travele
			// through a single pipe).
			int currentMoveTime = packet.getCurrentMoveTime();
			int nextPipeMinMoveTime = (int) (20 / CableNetworkManager.get(Network.getWorld()).getCable(packet.getCurrentEntry().getPosition()).getDataTag()
					.getDouble(ItemCableComponent.ITEM_CABLE_MAX_TRANSFER_SPEED));
			nextPipeMinMoveTime = SDMath.clamp(nextPipeMinMoveTime, 1, Integer.MAX_VALUE);

			// If our current move time is lower than the move time allowed by the next
			// pipe, we have to slow down (apply friction). If we are taking longer than the
			// next pipe allows, that means we can speed up (accelerate). We clamp because
			// we cannot go faster than the next
			// pipe's min move time, and we cannot go slower than the maximum default move
			// time.
			if (currentMoveTime < nextPipeMinMoveTime) {
				currentMoveTime *= CableNetworkManager.get(Network.getWorld()).getCable(packet.getCurrentEntry().getPosition()).getDataTag()
						.getDouble(ItemCableComponent.ITEM_CABLE_FRICTION_FACTOR_TAG);
				currentMoveTime = SDMath.clamp(currentMoveTime, 2, nextPipeMinMoveTime);
			} else if (currentMoveTime > nextPipeMinMoveTime) {
				currentMoveTime *= CableNetworkManager.get(Network.getWorld()).getCable(packet.getCurrentEntry().getPosition()).getDataTag()
						.getDouble(ItemCableComponent.ITEM_CABLE_ACCELERATION_FACTOR_TAG);
				currentMoveTime = SDMath.clamp(currentMoveTime, nextPipeMinMoveTime, ItemCableComponent.MAXIMUM_MOVE_TIME);
			}

			packet.setMovementTime(currentMoveTime);
			sendTransferingPacketToCable(packet.getCurrentEntry().getPosition(), packet);
		}

		return false;
	}

	/**
	 * Checks to see if the provided parcel's path is valid. Only call this if you
	 * know the parcel is still has another cable to go to. If its in the last cable
	 * before the destination, calling this will cause an error.
	 * 
	 * @param parcel The parcel to check for.
	 * @return Returns true if the parcel path is valid, false otherwise.
	 */
	protected boolean isParcelPathValid(ItemRoutingParcel parcel) {
		// If the current position's tile entity is null, return false.
		if (Network.getWorld().getBlockEntity(parcel.getCurrentEntry().getPosition()) == null) {
			return false;
		}

		// Get the next position.
		BlockPos nextPosition = parcel.getNextEntry().getPosition();

		// If the next position's tile entity is null, return false.
		if (Network.getWorld().getBlockEntity(nextPosition) == null) {
			return false;
		}

		// If the next position is neither a
		// cable in this network nor a destination,
		// return false.
		if (!Network.getGraph().getCables().containsKey(nextPosition) && !Network.getGraph().getDestinations().containsKey(nextPosition)) {
			return false;
		}

		// If the next position is a cable, make sure its not disabled on the side we
		// need to enter it from.
		if (CableNetworkManager.get(Network.getWorld()).isTrackingCable(parcel.getNextEntry().getPosition())) {
			return !CableNetworkManager.get(Network.getWorld()).getCable(parcel.getNextEntry().getPosition()).isDisabledOnSide(parcel.getOutDirection().getOpposite());
		}

		// If we pass all the previous checks, return true.
		return true;
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
			sendRemoveTransferingPacketToCable(parcel.getCurrentEntry().getPosition(), parcel.getId());
		} else {
			LOGGER.error(String.format("Encountered an invalid item cable at current parcel entry location: %1$s.", parcel.getCurrentEntry().getPosition()));
		}

		// Check if the parcel's current position is still in this network. If it is,
		// just reroute it or
		// drop it. If not, transfer it to another network if possible. If not, drop it.
		if (isParcelStillInNetwork(parcel)) {
			// First try to reroute it to the closest valid location.
			ItemStack remainingAmount = transferItemStack(parcel.getContainedItem(), parcel.getCurrentEntry().getPosition(), parcel.getCurrentEntry().getDirectionOfEntry(), false,
					true, parcel.getCurrentMoveTime());

			// If no items were able to be transfered,to the original location, attempt to
			// route it back to the source location.
			if (remainingAmount.getCount() == parcel.getContainedItem().getCount()) {
				// Get a path back to the source location. If there is a path, start moving in
				// the opposite direction we came in from. This will be the direction back to
				// the source.
				Path pathToSource = getPathForItem(parcel.getContainedItem(), parcel.getCurrentEntry().getPosition(), parcel.getPath().getSourceCableLocation(), null, true);
				if (pathToSource != null) {
					remainingAmount = transferItemStack(parcel.getContainedItem(), pathToSource, parcel.getInDirection(), false, parcel.isHalfWayThroughPath(),
							parcel.getCurrentMoveTime());
				}

				// If we were unable to handle any items, drop the remaining amount into the
				// world.
				if (remainingAmount.getCount() == parcel.getContainedItem().getCount()) {
					WorldUtilities.dropItem(Network.getWorld(), parcel.getCurrentEntry().getPosition(), remainingAmount);
				}
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
		sendRemoveTransferingPacketToCable(parcel.getCurrentEntry().getPosition(), parcel.getId());

		// Loop until we're able to fully transfer the parcel OR we run out of options.
		ItemStack lastTransferedAmount = ItemStack.EMPTY;
		ItemStack transferedAmount = remainingItems;
		while (lastTransferedAmount.getCount() != transferedAmount.getCount()) {
			lastTransferedAmount = transferedAmount;
			transferedAmount = transferItemStack(parcel.getContainedItem(), parcel.getCurrentEntry().getPosition(), parcel.getCurrentEntry().getDirectionOfEntry(), false,
					parcel.getCurrentMoveTime());
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

		// Get the item we have to transfer.
		ItemStack remainingAmount = parcel.getContainedItem();

		// Check to ensure that the other cable has a network.
		CableNetwork otherNetwork = otherNetworkCable.getNetwork();
		if (otherNetwork != null) {
			if (!otherNetwork.hasModule(ModCableModules.Item.get())) {
				return false;
			}
			// Transfer this parcel to that network.
			ItemNetworkModule otherItemModule = otherNetwork.getModule(ModCableModules.Item.get());

			// Loop until we're able to fully transfer the parcel OR we run out of options.
			ItemStack lastTransferedAmount = ItemStack.EMPTY;
			while (lastTransferedAmount.getCount() != remainingAmount.getCount()) {
				lastTransferedAmount = remainingAmount;

				// Try to send the parcel to the nearest location.
				remainingAmount = otherItemModule.transferItemStack(parcel.getContainedItem(), parcel.getCurrentEntry().getPosition(),
						parcel.getCurrentEntry().getDirectionOfEntry(), false, false, parcel.getCurrentMoveTime());

				// If there are still remaining items, try to use the other network to return
				// the parcel to it's sender.
				if (!remainingAmount.isEmpty()) {
					Path pathToSource = otherItemModule.getPathForItem(parcel.getContainedItem(), parcel.getCurrentEntry().getPosition(), parcel.getPath().getSourceCableLocation(),
							null, true);
					if (pathToSource != null) {
						remainingAmount = otherItemModule.transferItemStack(parcel.getContainedItem(), pathToSource, parcel.getInDirection(), false, parcel.isHalfWayThroughPath(),
								parcel.getCurrentMoveTime());
					}

				}

				// If we transfered all the items, then return.
				if (remainingAmount.isEmpty()) {
					break;
				}
			}
		}

		// If there are still items remaining, drop them on the ground.
		if (!remainingAmount.isEmpty()) {
			WorldUtilities.dropItem(Network.getWorld(), parcel.getCurrentEntry().getPosition(), remainingAmount);
		}

		return true;
	}

	/**
	 * Checks to see if we can extract from a destination through a cable. If false,
	 * this means there is an attachment on the cable that is not a filter.
	 * 
	 * @param stackToInsert
	 * @param cablePosition
	 * @param destinationSide
	 * @return
	 */
	protected boolean canExtractFromCable(ItemStack stackToInsert, BlockPos cablePosition, Direction destinationSide) {
		ItemCableComponent cable = getItemCableComponentAtPosition(cablePosition);
		if (cable == null) {
			return false;
		}

		return cable.canExtractFromSide(stackToInsert, destinationSide);
	}

	/**
	 * Checks to see if we can insert into a destination through a cable. If false,
	 * this means there is a filter on that cable that we did not pass, or there is
	 * another attachment present.
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
		// TODO: This shouldn't be a thing (reaching across from Server to Component)
		if (Network.getWorld().getBlockEntity(position) instanceof BlockEntityBase) {
			return ((BlockEntityBase) Network.getWorld().getBlockEntity(position)).getComponent(ItemCableComponent.class);
		}
		return null;
	}

	/**
	 * Calculates the path for the provided item.
	 * 
	 * @param item The itemstack to transfer.
	 * @paramcablePosition The position the itemstack is coming from.
	 * @param sourcePosition          The position the itemstack was actually pulled
	 *                                from. Used to ensure we don't get a path back
	 *                                to the pulled from position. Can be null.
	 * @param ignoreCableRestrictions If true, this will not check if the cable can
	 *                                receive insert. This is only used when finding
	 *                                paths for items that need to be returned to an
	 *                                inventory they were extracted from.
	 * @return The closest path if one exists.
	 */
	protected @Nullable Path getPathForItem(ItemStack item, BlockPos cablePosition, @Nullable BlockPos sourcePosition, @Nullable Direction sourceDirection,
			boolean ignoreCableRestrictions) {
		// Preallocate the shortest values.
		Path shortestPath = null;
		float shortestPathLength = Float.MAX_VALUE;

		// Iterate through all the destinations in the graph.
		for (DestinationWrapper dest : Network.getGraph().getDestinations().values()) {
			// Skip any destinations that don't support item transfer.
			if (!dest.supportsType(ModCableDestinations.Item.get())) {
				continue;
			}

			// Get the shortest path to the destination.
			Path path = getShortestPathToDestination(item, cablePosition, dest.getPos(), ignoreCableRestrictions);

			// If this path is shorter, take this path.
			if (path != null && path.getLength() < shortestPathLength) {
				// Skip trying to go to the same position the item came from or is at and its
				// the same side. IF the original side is null, then we just check if the source
				// and dest are the same.
				if (dest.getPos().equals(sourcePosition) && (sourceDirection == null || path.getDestinationDirection().getOpposite().equals(sourceDirection))) {
					continue;
				} else {
					shortestPath = path;
					shortestPathLength = path.getLength();
				}
			}
		}
		return shortestPath;
	}

	/**
	 * Gets the shortest item network path between the source and destination
	 * position for the provided item.
	 * 
	 * @param stack                   The item to find a path for.
	 * @param sourcePosition          The source position to start from.
	 * @param destination             The position to travel to.
	 * @param ignoreCableRestrictions If true, this will not check if the cable can
	 *                                receive insert. This is only used when finding
	 *                                paths for items that need to be returned to an
	 *                                inventory they were extracted from.
	 * @return The path if one exists, null otherwise.
	 */
	public @Nullable Path getShortestPathToDestination(ItemStack stack, BlockPos sourcePosition, BlockPos destination, boolean ignoreCableRestrictions) {
		// Get all the potential paths.
		List<Path> paths = Network.getPathCache().getPaths(sourcePosition, destination, ModCableModules.Item.get());
		Path shortestPath = null;
		float shortestPathLength = Float.MAX_VALUE;

		// Continue if no path is found.
		if (paths == null) {
			return null;
		}

		// Iterate through all the paths to the proposed tile entity.
		for (Path path : paths) {
			// Allocate an atomic bool to capture if a path is valid.
			AtomicBoolean isValid = new AtomicBoolean(false);

			// If we can't insert through that cable, skip this path.
			if (!ignoreCableRestrictions) {
				if (!canInsertThroughCable(stack, path.getFinalCablePosition(), path.getFinalCableExitDirection())) {
					continue;
				}
			}

			// If we're able to insert into that inventory, set the atomic boolean.
			Network.getWorld().getBlockEntity(destination).getCapability(ForgeCapabilities.ITEM_HANDLER, path.getDestinationDirection().getOpposite())
					.ifPresent(inv -> {
						isValid.set(InventoryUtilities.canPartiallyInsertItemIntoInventory(inv, stack));
					});

			// If the atomic boolean is valid, then we have a valid path and we return it.
			if (isValid.get()) {
				if (path.getLength() < shortestPathLength) {
					shortestPath = path;
					shortestPathLength = path.getLength();
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
			if (!dest.supportsType(ModCableDestinations.Item.get())) {
				continue;
			}

			// Skip NON tile entity destinations.
			if (!dest.hasTileEntity()) {
				continue;
			}

			// Skip trying to go to the same position the item came from or is at.
			if (dest.getPos().equals(sourcePosition)) {
				continue;
			}

			// If we're able to insert into that inventory, set the atomic boolean.
			if (canExtractFromCable(item, dest.getFirstConnectedCable(), dest.getFirstConnectedDestinationSide().getOpposite())) {
				IItemHandler inv = dest.getTileEntity().getCapability(ForgeCapabilities.ITEM_HANDLER, dest.getFirstConnectedDestinationSide()).orElse(null);
				// If this inventory contains the provided item, add the destination to the
				// list.
				int slot = InventoryUtilities.getFirstSlotContainingItem(item, inv);
				if (slot >= 0) {
					validDestinations.add(new RetrivalSourceWrapper(dest, slot));
				}
			}
		}
		return validDestinations;
	}

	protected void addRoutingParcel(ItemRoutingParcel parcel, boolean startHalfWay) {
		// If we aren't tracking and parcels for the destination, create an empty linked
		// list.
		if (!ActiveParcels.containsKey(parcel.getPath().getDestinationLocation())) {
			ActiveParcels.put(parcel.getPath().getDestinationLocation(), new LinkedList<ItemRoutingParcel>());
		}

		// Add the parcel.
		ActiveParcels.get(parcel.getPath().getDestinationLocation()).add(parcel);

		// Increment the parcel's current path index, set its speed and increment the
		// static parcel id.
		parcel.incrementCurrentPathIndex(startHalfWay);

		CableNetworkManager.get(Network.getWorld()).incrementCurrentCraftingId();
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
		// Calculate the list of items already headed to the network.
		List<ItemStack> alreadyTravelingItems = getItemsTravlingToDestination(targetLocation);

		// If inserting into a digistore network, we leverage the prediction algorithm
		// from the transaction manager.
		if (targetInventory instanceof DigitstoreIOPortInventoryComponent) {
			// Get a reference to the digistore io port component.
			DigitstoreIOPortInventoryComponent digistoreComp = (DigitstoreIOPortInventoryComponent) targetInventory;
			// Create a reference to the provided itemstack.
			AtomicReference<ItemStack> output = new AtomicReference<ItemStack>(itemToInsert);

			// If the component has a valid network module, we use the transaction manager
			// from that module to simulate a predicted insert.
			digistoreComp.getDigistoreNetworkModule().ifPresent(module -> {
				output.set(module.getTransactionManager().simulatePredictedInsert(alreadyTravelingItems, itemToInsert));
			});

			// return the result.
			return output.get();
		} else {
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
	}

	@Override
	public void getReaderOutput(List<Component> output, BlockPos pos) {
		// Calculate the number of active parcels.
		int activeParcels = 0;
		for (LinkedList<ItemRoutingParcel> parcels : ActiveParcels.values()) {
			activeParcels += parcels.size();
		}

		String itemsInTransit = new MetricConverter(activeParcels).getValueAsString(true);
		output.add(Component.literal(String.format("Contains: %1$s items in transit.", itemsInTransit)));
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		ActiveParcels = new HashMap<BlockPos, LinkedList<ItemRoutingParcel>>();
		// Get the parcel NBT list and add the parcels.
		ListTag parcelNBTList = tag.getList("parcels", Tag.TAG_COMPOUND);
		parcelNBTList.forEach(parcelTag -> {
			CompoundTag parcelTagCompound = (CompoundTag) parcelTag;
			ItemRoutingParcel parcel = new ItemRoutingParcel(parcelTagCompound);

			if (!ActiveParcels.containsKey(parcel.getPath().getDestinationLocation())) {
				ActiveParcels.put(parcel.getPath().getDestinationLocation(), new LinkedList<ItemRoutingParcel>());
			}
			// Add the parcel.
			ActiveParcels.get(parcel.getPath().getDestinationLocation()).add(parcel);
		});

	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		// Get all the active parcels.
		List<ItemRoutingParcel> activeParcels = new ArrayList<ItemRoutingParcel>();
		for (Entry<BlockPos, LinkedList<ItemRoutingParcel>> entry : ActiveParcels.entrySet()) {
			// Get the parcels going to that destination.
			for (ItemRoutingParcel parcel : entry.getValue()) {
				activeParcels.add(parcel);
			}
		}

		// Serialize the parcels to the list.
		ListTag parcelNBTList = new ListTag();
		activeParcels.forEach(parcel -> {
			CompoundTag parcelTag = new CompoundTag();
			parcel.writeToNbt(parcelTag);
			parcelNBTList.add(parcelTag);
		});
		tag.put("parcels", parcelNBTList);

		return tag;
	}

}
