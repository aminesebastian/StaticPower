package theking530.staticpower.tileentities.cables.network.modules;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.tileentities.cables.ServerCable;
import theking530.staticpower.tileentities.cables.network.NetworkMapper;
import theking530.staticpower.tileentities.cables.network.modules.factories.CableNetworkModuleTypes;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreNetworkModule extends AbstractCableNetworkModule implements IItemHandler {
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
		if(!managerPresent) {
			Network.updateGraph(Network.getWorld(), Network.getOrigin());
		}
		return managerPresent;
	}

	public List<TileEntityDigistore> getAllDigistores() {
		return digistores;
	}

	@Override
	public int getSlots() {
		return managerPresent ? digistores.size() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (managerPresent) {
			return digistores.get(slot).getStoredItem();
		}
		throw new RuntimeException("Attempted to get the stack in the digistore slot with a network with no present manager.");
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (managerPresent) {
			return digistores.get(slot).insertItem(stack, simulate);
		}
		throw new RuntimeException("Attempted to insert an item into a network with no present manager.");
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if (managerPresent) {
			return digistores.get(slot).extractItem(amount, simulate);
		}
		throw new RuntimeException("Attempted to extract an item from a network with no present manager.");
	}

	@Override
	public int getSlotLimit(int slot) {
		if (managerPresent) {
			return digistores.get(slot).getMaxStoredAmount();
		}
		throw new RuntimeException("Attempted to get the slot size limit for a network with no present manager.");
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (managerPresent) {
			return ItemUtilities.areItemStacksStackable(getStackInSlot(slot), stack);
		}
		throw new RuntimeException("Attempted to check if an item is valid for a network with no present manager.");
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {

	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		return tag;
	}
}
