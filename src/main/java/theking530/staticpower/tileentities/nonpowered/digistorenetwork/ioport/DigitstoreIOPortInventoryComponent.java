package theking530.staticpower.tileentities.nonpowered.digistorenetwork.ioport;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;

public class DigitstoreIOPortInventoryComponent extends AbstractTileEntityComponent implements IItemHandler {

	public DigitstoreIOPortInventoryComponent(String name) {
		super(name);
	}

	@Override
	public int getSlots() {
		AtomicInteger output = new AtomicInteger(0);
		getDigistoreNetworkModule().ifPresent(network -> {
			output.set(network.getSlots());
		});
		return output.get();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		AtomicReference<ItemStack> output = new AtomicReference<ItemStack>(ItemStack.EMPTY);
		getDigistoreNetworkModule().ifPresent(network -> {
			output.set(network.getStackInSlot(slot));
		});
		return output.get();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		AtomicReference<ItemStack> output = new AtomicReference<ItemStack>(ItemStack.EMPTY);
		getDigistoreNetworkModule().ifPresent(network -> {
			output.set(network.insertItem(slot, stack, simulate));
		});
		return output.get();
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		AtomicReference<ItemStack> output = new AtomicReference<ItemStack>(ItemStack.EMPTY);
		getDigistoreNetworkModule().ifPresent(network -> {
			output.set(network.extractItem(slot, amount, simulate));
		});
		return output.get();
	}

	@Override
	public int getSlotLimit(int slot) {
		AtomicInteger output = new AtomicInteger(0);
		getDigistoreNetworkModule().ifPresent(network -> {
			output.set(network.getSlotLimit(slot));
		});
		return output.get();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		AtomicBoolean output = new AtomicBoolean(false);
		getDigistoreNetworkModule().ifPresent(network -> {
			output.set(network.isItemValid(slot, stack));
		});
		return output.get();
	}

	public Optional<DigistoreNetworkModule> getDigistoreNetworkModule() {
		// If on the client, always return empty.
		if (getWorld().isRemote) {
			return Optional.empty();
		}

		// Get the module if it exists.
		ServerCable cable = CableNetworkManager.get(getWorld()).getCable(getPos());
		if (cable.getNetwork().hasModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE)) {
			return Optional.of(cable.getNetwork().getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE));
		}

		// Otherwise, return empty.
		return Optional.empty();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return LazyOptional.of(() -> {
				return this;
			}).cast();
		}
		return LazyOptional.empty();
	}
}
