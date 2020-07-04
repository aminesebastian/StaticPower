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
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreNetworkModule extends AbstractCableNetworkModule {
	private final List<TileEntityDigistore> digistores;
	private boolean managerPresent;

	public DigistoreNetworkModule() {
		super(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
		digistores = new LinkedList<TileEntityDigistore>();
	}

	@Override
	public void tick(World world) {

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		super.onNetworkGraphUpdated(mapper);
		digistores.clear();
		managerPresent = false;

		// Cache all the digistores in the network.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			TileEntity te = Network.getWorld().getChunkAt(cable.getPos()).getTileEntity(cable.getPos(), Chunk.CreateEntityType.QUEUED);
			if (te instanceof TileEntityDigistore) {
				digistores.add((TileEntityDigistore) te);
			} else if (te instanceof TileEntityDigistoreManager) {
				managerPresent = true;
			}
		}
	}

	public boolean isManagerPresent() {
		if (!managerPresent) {
			Network.updateGraph(Network.getWorld(), Network.getOrigin());
		}
		return managerPresent;
	}

	public DigistoreInventoryWrapper getNetworkInventory() {
		return new DigistoreInventoryWrapper(this);
	}

	public List<TileEntityDigistore> getAllDigistores() {
		return digistores;
	}

	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		if (managerPresent) {
			// Create a copy for the output stack.
			ItemStack stackToUse = stack.copy();

			// Allocate a list of all potential digistores to insert into.
			List<TileEntityDigistore> potentials = new ArrayList<TileEntityDigistore>();

			// Go through each digistore and add them to the potentials list if it can
			// accept the item.
			for (TileEntityDigistore digistore : digistores) {
				ItemStack insertSimulation = digistore.insertItem(stackToUse, true);
				if (insertSimulation.getCount() != stackToUse.getCount() || digistore.isVoidUpgradeInstalled()) {
					potentials.add(digistore);
				}
			}
			
			// If we found no matches, return early.
			if (potentials.size() == 0) {
				return stack;
			}

			// Sort the digistores so that we start by filling the most full first.
			Collections.sort(potentials, new Comparator<TileEntityDigistore>() {
				public int compare(TileEntityDigistore a, TileEntityDigistore b) {
					return b.getRemainingStorage(false) - a.getRemainingStorage(false);
				}
			});

			// Start filling, break if we finish the fill.
			for (TileEntityDigistore digistore : potentials) {
				stackToUse = digistore.insertItem(stack, simulate);
				if (stackToUse.isEmpty()) {
					break;
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
			List<TileEntityDigistore> potentials = new ArrayList<TileEntityDigistore>();

			// Go through each digistore and add them to the potentials list if it can
			// accept the item.
			for (TileEntityDigistore digistore : digistores) {
				if (!digistore.getStoredItem().isEmpty() && ItemUtilities.areItemStacksStackable(stack, digistore.getStoredItem())) {
					potentials.add(digistore);
				}
			}

			// If we found no matches, return early.
			if (potentials.size() == 0) {
				return ItemStack.EMPTY;
			}

			// Sort the digistores so that we start by extracting from the emptiest first.
			Collections.sort(potentials, new Comparator<TileEntityDigistore>() {
				public int compare(TileEntityDigistore a, TileEntityDigistore b) {
					return a.getRemainingStorage(false) - b.getRemainingStorage(false);
				}
			});

			// Create a handle to the output.
			ItemStack output = ItemStack.EMPTY;

			// Start extracting, break if we finish the extract.
			for (TileEntityDigistore digistore : potentials) {
				// Extract up to the amount we need.
				ItemStack extracted = digistore.extractItem(count - output.getCount(), false);

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
