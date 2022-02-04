package theking530.staticpower.tileentities.digistorenetwork.ioport;

import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
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
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY	;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		// Do nothing on the client.
		if (getWorld().isClientSide) {
			return stack;
		}

		DigistoreNetworkModule module = getDigistoreNetworkModule().orElse(null);
		if (module != null && module.isManagerPresent()) {
			return module.insertItem(stack, simulate);
		}

		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
		return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
	}

	public Optional<DigistoreNetworkModule> getDigistoreNetworkModule() {
		// If on the client, always return empty.
		if (getWorld().isClientSide) {
			return Optional.empty();
		}

		// Get the module if it exists.
		ServerCable cable = CableNetworkManager.get(getWorld()).getCable(getPos());
		if (cable.getNetwork() != null && cable.getNetwork().hasModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE)) {
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
