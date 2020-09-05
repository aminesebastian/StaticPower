package theking530.staticpower.cables.digistore;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticpower.cables.attachments.digistore.digistoreterminal.DigistoreInventorySortType;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.items.DigistorePatternCard.EncodedDigistorePattern;
import theking530.staticpower.tileentities.digistorenetwork.atomicconstructor.TileEntityAtomicConstructor;
import theking530.staticpower.tileentities.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.utilities.MetricConverter;

public class DigistoreNetworkModule extends AbstractCableNetworkModule {
	private final Queue<DigistoreCraftingRequest> craftingRequests;
	private final List<IDigistoreInventory> digistores;
	private final List<ServerCable> powerUsingDigistores;
	private final List<TileEntityAtomicConstructor> constructors;
	private final DigistoreNetworkTransactionManager transactionManager;
	private TileEntityDigistoreManager manager;

	public DigistoreNetworkModule() {
		super(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
		digistores = new LinkedList<IDigistoreInventory>();
		transactionManager = new DigistoreNetworkTransactionManager(this);
		powerUsingDigistores = new LinkedList<ServerCable>();
		constructors = new LinkedList<TileEntityAtomicConstructor>();
		craftingRequests = new LinkedList<DigistoreCraftingRequest>();
	}

	@Override
	public void tick(World world) {
		if (isManagerPresent()) {
			manager.energyStorage.useBulkPower(getPowerUsage());
			processCraftingReuqests();
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		super.onNetworkGraphUpdated(mapper);

		// Clear the initial values back to their defaults.
		digistores.clear();
		constructors.clear();
		powerUsingDigistores.clear();
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

				// Capture the constructors.
				if (te instanceof TileEntityAtomicConstructor) {
					constructors.add((TileEntityAtomicConstructor) te);
				}

				// If this cable has a power usage tag, capture it as a power user.
				if (cable.containsProperty(DigistoreCableProviderComponent.POWER_USAGE_TAG)) {
					powerUsingDigistores.add(cable);
				}

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

	public boolean addCraftingRequest(ItemStack outputItem) {
		ItemStack strippedItem = outputItem.copy();
		DigistoreInventorySnapshot.stripCraftableTag(strippedItem);

		DigistoreInventorySnapshot snapshot = getNetworkInventorySnapshot("", DigistoreInventorySortType.COUNT, false);
		List<EncodedDigistorePattern> patterns = snapshot.getAllPatternsForItem(strippedItem);
		EncodedDigistorePattern bestPattern = null;
		for (EncodedDigistorePattern candidate : patterns) {
			bestPattern = candidate;
		}

		if (bestPattern != null) {
			DigistoreCraftingRequest request = new DigistoreCraftingRequest(CableNetworkManager.get(Network.getWorld()).getAndIncrementCurrentCraftingId(), bestPattern);
			if (canCraftRequest(request)) {
				craftingRequests.add(request);
				return true;
			}
		}

		return false;
	}

	protected void processCraftingReuqests() {
		// If there are no requests, do nothing.
		if (craftingRequests.size() == 0) {
			return;
		}

		// Pop the top request and attempt to process it.
		DigistoreCraftingRequest topRequest = craftingRequests.peek();
		if (canCraftRequest(topRequest)) {
			if (craftRequest(topRequest)) {
				craftingRequests.poll();
			}
		}
	}

	protected boolean canCraftRequest(DigistoreCraftingRequest request) {
		// Check to see if we have the items required to craft with.
		for (Entry<ItemStack, Integer> requiredItem : request.getRequiredItems().entrySet()) {
			ItemStack simulatedExtract = extractItem(requiredItem.getKey(), requiredItem.getValue(), true);
			if (simulatedExtract.getCount() != requiredItem.getValue()) {
				return false;
			}
		}

		// Check to see if we have space to insert the crafted item.
		ItemStack remaining = this.insertItem(request.pattern.outputs[0].copy(), true);
		return remaining.isEmpty();
	}

	protected boolean craftRequest(DigistoreCraftingRequest request) {
		for (Entry<ItemStack, Integer> requiredItem : request.getRequiredItems().entrySet()) {
			extractItem(requiredItem.getKey(), requiredItem.getValue(), false);
		}
		insertItem(request.pattern.outputs[0], false);
		return true;
	}

	public List<IDigistoreInventory> getAllDigistores() {
		return digistores;
	}

	public int getItemCount(ItemStack stack) {
		return transactionManager.getItemCount(stack);
	}

	public int getPowerUsage() {
		int usage = 0;
		for (ServerCable cable : powerUsingDigistores) {
			usage += cable.getIntProperty(DigistoreCableProviderComponent.POWER_USAGE_TAG);
		}
		return usage;
	}

	public List<TileEntityAtomicConstructor> getConstructors() {
		return constructors;
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
		// Get the request NBT list and add the parcels.
		ListNBT requestNBTList = tag.getList("requests", Constants.NBT.TAG_COMPOUND);
		craftingRequests.clear();
		requestNBTList.forEach(requestTag -> {
			CompoundNBT requestNbtTag = (CompoundNBT) requestTag;
			craftingRequests.add(DigistoreCraftingRequest.read(requestNbtTag));
		});
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		// Serialize the requests to the list.
		ListNBT requestNBTList = new ListNBT();
		craftingRequests.forEach(request -> {
			requestNBTList.add(request.serializeToNBT());
		});
		tag.put("requests", requestNBTList);

		return tag;
	}
}
