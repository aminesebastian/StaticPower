package theking530.staticpower.cables.digistore;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticpower.cables.attachments.digistore.digistoreterminal.DigistoreInventorySortType;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.tileentities.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.utilities.MetricConverter;

public class DigistoreNetworkModule extends AbstractCableNetworkModule {
	private final List<IDigistoreInventory> digistores;
	private final DigistoreNetworkTransactionManager transactionManager;
	private TileEntityDigistoreManager manager;

	public DigistoreNetworkModule() {
		super(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
		digistores = new LinkedList<IDigistoreInventory>();
		transactionManager = new DigistoreNetworkTransactionManager(this);
	}

	@Override
	public void tick(World world) {

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		super.onNetworkGraphUpdated(mapper);

		// Clear the initial values back to their defaults.
		digistores.clear();
		manager = null;

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
					manager = (TileEntityDigistoreManager) te;
				}
			}
		}

		// Update the transaction manager.
		transactionManager.updateDigistoreList(digistores);
	}

	public boolean isManagerPresent() {
		if (manager == null) {
			Network.updateGraph(Network.getWorld(), Network.getOrigin());
		}
		return manager != null && manager.energyStorage.getStorage().getStoredPower() > 0;
	}

	public DigistoreInventorySnapshot getNetworkInventorySnapshot(String filter, DigistoreInventorySortType sortType, boolean sortDescending) {
		if (!isManagerPresent()) {
			return DigistoreInventorySnapshot.EMPTY;
		}
		return new DigistoreInventorySnapshot(this, filter, sortType, sortDescending);
	}

	public DigistoreNetworkTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public List<IDigistoreInventory> getAllDigistores() {
		return digistores;
	}

	public int getItemCount(ItemStack stack) {
		return transactionManager.getItemCount(stack);
	}

	public boolean containsItem(ItemStack stack) {
		return transactionManager.containsItem(stack);
	}

	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		if (isManagerPresent()) {
			return transactionManager.insertItem(stack, simulate);
		}
		throw new RuntimeException("Attempted to insert an item into a network with no present manager.");
	}

	public ItemStack extractItem(ItemStack stack, int count, boolean simulate) {
		if (isManagerPresent()) {
			return transactionManager.extractItem(stack, count, simulate);
		}
		throw new RuntimeException("Attempted to extract an item from a network with no present manager.");
	}

	@Override
	public void getReaderOutput(List<ITextComponent> output) {
		// Get the total amount of items.
		int items = 0;
		for (IDigistoreInventory inv : digistores) {
			items += inv.getTotalContainedCount();
		}

		// Inventories.
		String digistoreInventories = new MetricConverter(digistores.size()).getValueAsString(true);
		output.add(new StringTextComponent(String.format("Contains: %1$s digistore inventories.", digistoreInventories)));

		// ItemCount
		String itemCount = new MetricConverter(items).getValueAsString(true);
		output.add(new StringTextComponent(String.format("Contains: %1$s items.", itemCount)));
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {

	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		return tag;
	}
}
