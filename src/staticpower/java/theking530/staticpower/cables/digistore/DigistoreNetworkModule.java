package theking530.staticpower.cables.digistore;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.utilities.MetricConverter;
import theking530.staticpower.blockentities.digistorenetwork.manager.BlockEntityDigistoreManager;
import theking530.staticpower.blockentities.digistorenetwork.patternstorage.BlockEntityPatternStorage;
import theking530.staticpower.cables.attachments.digistore.craftinginterface.DigistoreCraftingInterfaceAttachment;
import theking530.staticpower.cables.attachments.digistore.terminalbase.DigistoreInventorySortType;
import theking530.staticpower.cables.digistore.crafting.CraftingInterfaceWrapper;
import theking530.staticpower.cables.digistore.crafting.DigistoreNetworkCraftingManager;
import theking530.staticpower.init.cables.ModCableModules;

public class DigistoreNetworkModule extends CableNetworkModule {
	public static final int CRAFTING_TIME = 10;

	private final List<IDigistoreInventory> digistores;
	private final List<Cable> powerUsingDigistores;
	private final List<BlockEntityPatternStorage> patternStorages;
	private final List<CraftingInterfaceWrapper> craftingInterfaces;

	private final DigistoreNetworkTransactionManager transactionManager;
	private final DigistoreNetworkCraftingManager craftingManager;
	private BlockEntityDigistoreManager manager;
	private int craftingTimer;

	public DigistoreNetworkModule() {
		super(ModCableModules.Digistore.get());
		digistores = new LinkedList<IDigistoreInventory>();
		transactionManager = new DigistoreNetworkTransactionManager(this);
		powerUsingDigistores = new LinkedList<Cable>();
		craftingInterfaces = new LinkedList<CraftingInterfaceWrapper>();
		patternStorages = new LinkedList<BlockEntityPatternStorage>();
		craftingManager = new DigistoreNetworkCraftingManager(this);
	}

	@Override
	public void tick(Level world) {
		if (isManagerPresent()) {
			manager.energyStorage.drainPower(getPowerUsage(), false);

			craftingTimer++;
			if (craftingTimer >= CRAFTING_TIME) {
				craftingManager.processCrafting();
				craftingTimer = 0;
			}
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		super.onNetworkGraphUpdated(mapper, startingPosition);

		// Clear the initial values back to their defaults.
		digistores.clear();
		patternStorages.clear();
		powerUsingDigistores.clear();
		craftingInterfaces.clear();
		manager = null;

		// Cache all the digistores in the network.
		for (Cable cable : mapper.getDiscoveredCables()) {
			// Get the cable's tile entity.
			BlockEntity te = Network.getWorld().getChunkAt(cable.getPos()).getBlockEntity(cable.getPos(), LevelChunk.EntityCreationType.QUEUED);
			// If it's not null.
			if (te != null) {
				// Check if it has an inventory. Store it if it does.
				te.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY, null).ifPresent(digiInv -> {
					digistores.add(digiInv);
				});

				// Capture the constructors.
				if (te instanceof BlockEntityPatternStorage) {
					patternStorages.add((BlockEntityPatternStorage) te);
				}

				// If this cable has a power usage tag, capture it as a power user.
				if (cable.getDataTag().contains(DigistoreCableProviderComponent.POWER_USAGE_TAG)) {
					powerUsingDigistores.add(cable);
				}

				// If this cable has a crafting interface, cache it.
				ComponentUtilities.getComponent(DigistoreCableProviderComponent.class, te).ifPresent(comp -> {
					for (Direction dir : Direction.values()) {
						ItemStack attachment = comp.getAttachment(dir);
						if (attachment.getItem() instanceof DigistoreCraftingInterfaceAttachment) {
							craftingInterfaces.add(new CraftingInterfaceWrapper(attachment, dir, cable));
						}
					}
				});

				// If this is also a manager, set the manager present to true.
				// If we already had a manager set, ignore this.
				// TODO: Error message to show that there are multiple managers attached.
				if (te instanceof BlockEntityDigistoreManager) {
					if (manager == null) {
						manager = (BlockEntityDigistoreManager) te;
					}
				}
			}
		}

		// Update the transaction manager.
		transactionManager.updateDigistoreList(digistores);
	}

	public boolean isManagerPresent() {
		return manager != null && manager.energyStorage.getStoredPower() >= getPowerUsage();
	}

	public DigistoreInventorySnapshot getNetworkInventorySnapshotForDisplay(String filter, DigistoreInventorySortType sortType, boolean sortDescending) {
		if (!isManagerPresent()) {
			return DigistoreInventorySnapshot.EMPTY;
		}
		return new DigistoreInventorySnapshot(this, filter, sortType, sortDescending);
	}

	public DigistoreInventorySnapshot getSimulatedNetworkInventorySnapshot() {
		if (!isManagerPresent()) {
			return DigistoreInventorySnapshot.EMPTY;
		}
		return new DigistoreInventorySnapshot(this, "", null, false, true);
	}

	public DigistoreInventorySnapshot getNonSimulatedNetworkInventorySnapshot() {
		if (!isManagerPresent()) {
			return DigistoreInventorySnapshot.EMPTY;
		}
		return new DigistoreInventorySnapshot(this, "", null, false, false);
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

	public int getPowerUsage() {
		int usage = 0;
		for (Cable cable : powerUsingDigistores) {
			usage += cable.getDataTag().getInt(DigistoreCableProviderComponent.POWER_USAGE_TAG);
		}
		return usage;
	}

	public List<BlockEntityPatternStorage> getPatternStorageTileEntities() {
		return patternStorages;
	}

	public List<CraftingInterfaceWrapper> getCraftingInterfaces() {
		return craftingInterfaces;
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

	public DigistoreNetworkCraftingManager getCraftingManager() {
		return craftingManager;
	}

	@Override
	public void onJoinedOtherNetwork(CableNetwork other) {
		super.onJoinedOtherNetwork(other);
		if (other.hasModule(ModCableModules.Digistore.get())) {
			DigistoreNetworkModule module = (DigistoreNetworkModule) other.getModule(ModCableModules.Digistore.get());
			module.craftingManager.mergeWithOtherManager(craftingManager);
		}
	}

	@Override
	public void getReaderOutput(List<Component> output, BlockPos pos) {
		// Get the total amount of items.
		int items = 0;
		for (IDigistoreInventory inv : digistores) {
			items += inv.getTotalContainedCount();
		}

		// Inventories.
		String digistoreInventories = new MetricConverter(digistores.size()).getValueAsString(true);
		output.add(Component.literal(String.format("Contains: %1$s digistore inventories.", digistoreInventories)));

		// ItemCount
		String itemCount = new MetricConverter(items).getValueAsString(true);
		output.add(Component.literal(String.format("Contains: %1$s items.", itemCount)));
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		craftingManager.readFromNbt(tag);
		craftingTimer = tag.getInt("crafting_timer");
	}

	protected Optional<ITeam> getOwningTeam() {
		if (!isManagerPresent()) {
			return Optional.empty();
		}

		ITeam team = manager.getTeamComponent().getOwningTeam();
		return Optional.ofNullable(team);
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		craftingManager.writeToNbt(tag);
		tag.putInt("crafting_timer", craftingTimer);
		return tag;
	}
}
