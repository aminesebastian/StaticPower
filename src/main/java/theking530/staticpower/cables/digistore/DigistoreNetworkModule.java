package theking530.staticpower.cables.digistore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.items.cableattachments.digistoreterminal.DigistoreInventorySortType;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.CapabilityDigistoreInventory;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.IDigistoreInventory;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreNetworkModule extends AbstractCableNetworkModule {
	private final List<IDigistoreInventory> digistores;
	private boolean managerPresent;

	public DigistoreNetworkModule() {
		super(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
		digistores = new LinkedList<IDigistoreInventory>();
	}

	@Override
	public void tick(World world) {

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		super.onNetworkGraphUpdated(mapper);

		// Clear the initial values back to their defaults.
		digistores.clear();
		managerPresent = false;

		// Cache all the digistores in the network.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// Get the cable's tile entity.
			TileEntity te = Network.getWorld().getChunkAt(cable.getPos()).getTileEntity(cable.getPos(), Chunk.CreateEntityType.QUEUED);
			// If it's not null.
			if (te != null) {
				// Check if it has an inventory. Store it if it does.
				te.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY, null).ifPresent(digiInv -> {
					digistores.add(digiInv);
				});

				// If this is also a manager, set the manager present to true.
				if (te instanceof TileEntityDigistoreManager) {
					managerPresent = true;
				}
			}
		}
	}

	public boolean isManagerPresent() {
		if (!managerPresent) {
			Network.updateGraph(Network.getWorld(), Network.getOrigin());
		}
		return managerPresent;
	}

	public DigistoreInventoryWrapper getNetworkInventory(String filter, DigistoreInventorySortType sortType, boolean sortDescending) {
		return new DigistoreInventoryWrapper(this, filter, sortType, sortDescending);
	}

	public List<IDigistoreInventory> getAllDigistores() {
		return digistores;
	}

	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		if (managerPresent) {
			// Create a copy for the output stack.
			ItemStack stackToUse = stack.copy();

			// Allocate a list of all potential digistores to insert into.
			List<IDigistoreInventory> potentials = new ArrayList<IDigistoreInventory>();

			// Go through each digistore and add them to the potentials list if it can
			// accept the item. Only discard full digistores that do NOT have a void
			// upgrade.
			for (IDigistoreInventory digistore : digistores) {
				ItemStack insertSimulation = digistore.insertItem(stackToUse, true);
				boolean isFull = digistore.getTotalContainedCount() >= digistore.getMaxStoredAmount();
				if (insertSimulation.getCount() != stackToUse.getCount() && (!isFull || (isFull && digistore.shouldVoidExcess()))) {
					potentials.add(digistore);
				}
			}

			// If we found no matches, return early.
			if (potentials.size() == 0) {
				return stack;
			}

			// Sort the digistores so that we start by filling the most full first.
			Collections.sort(potentials, new Comparator<IDigistoreInventory>() {
				public int compare(IDigistoreInventory a, IDigistoreInventory b) {
					return a.getTotalContainedCount() == 0 ? 1 : a.getRemainingStorage(true) - b.getRemainingStorage(true);
				}
			});

			// Start filling, break if we finish the fill.
			for (IDigistoreInventory digistore : potentials) {
				// Skip empty digistores first time around.
				if (digistore.getTotalContainedCount() == 0) {
					continue;
				}

				// Update the remaining stack.
				stackToUse = digistore.insertItem(stack, simulate);
				if (stackToUse.isEmpty()) {
					break;
				}
			}

			if (!stackToUse.isEmpty()) {
				// Start filling, this time with empty digistores allowed. Break if we finish
				// the fill.
				for (IDigistoreInventory digistore : potentials) {
					// Skip non empty digistores, we hit those already.d
					if (digistore.getTotalContainedCount() > 0) {
						continue;
					}
					stackToUse = digistore.insertItem(stack, simulate);
					if (stackToUse.isEmpty()) {
						break;
					}
				}
			}

			// Return what remains.
			return stackToUse;
		}
		throw new RuntimeException("Attempted to insert an item into a network with no present manager.");
	}

	public ItemStack extractItem(ItemStack stack, int count, boolean simulate) {
		if (managerPresent) {
			// Allocate a list of all potential digistores to insert into.
			List<IDigistoreInventory> potentials = new ArrayList<IDigistoreInventory>();

			// Go through each digistore and add them to the potentials list if it can
			// accept the item.
			for (IDigistoreInventory digistore : digistores) {
				for (int i = 0; i < digistore.getMaximumUniqueItemTypeCount(); i++) {
					if (!digistore.getItemTracker(i).isEmpty() && ItemUtilities.areItemStacksStackable(stack, digistore.getItemTracker(i).getStoredItem())) {
						potentials.add(digistore);
					}
				}

			}

			// If we found no matches, return early.
			if (potentials.size() == 0) {
				return ItemStack.EMPTY;
			}

			// Sort the digistores so that we start by extracting from the emptiest first.
			Collections.sort(potentials, new Comparator<IDigistoreInventory>() {
				public int compare(IDigistoreInventory a, IDigistoreInventory b) {
					return a.getRemainingStorage(false) - b.getRemainingStorage(false);
				}
			});

			// Create a handle to the output.
			ItemStack output = ItemStack.EMPTY;

			// Start extracting, break if we finish the extract.
			for (IDigistoreInventory digistore : potentials) {
				// Extract up to the amount we need.
				ItemStack extracted = digistore.extractItem(stack, count - output.getCount(), simulate);

				// If this is our first iteration, set the output stack. Otherwise, just grow
				// it.
				if (output.isEmpty()) {
					output = extracted;
				} else {
					output.grow(extracted.getCount());
				}

				// If we have gathered enough, break;
				if (output.getCount() >= count) {
					break;
				}
			}

			// Return what remains.
			return output;
		}
		throw new RuntimeException("Attempted to extract an item from a network with no present manager.");
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {

	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		return tag;
	}
}
