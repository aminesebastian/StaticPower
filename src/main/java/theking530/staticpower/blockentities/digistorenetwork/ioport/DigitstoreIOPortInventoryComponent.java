package theking530.staticpower.blockentities.digistorenetwork.ioport;

import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticpower.blockentities.components.AbstractBlockEntityComponent;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.init.cables.ModCableModules;

public class DigitstoreIOPortInventoryComponent extends AbstractBlockEntityComponent implements IItemHandler {

	public DigitstoreIOPortInventoryComponent(String name) {
		super(name);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		// Do nothing on the client.
		if (getLevel().isClientSide) {
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
		if (getLevel().isClientSide) {
			return Optional.empty();
		}

		// Get the module if it exists.
		ServerCable cable = CableNetworkManager.get(getLevel()).getCable(getPos());
		if (cable.getNetwork() != null && cable.getNetwork().hasModule(ModCableModules.Digistore.get())) {
			return Optional.of(cable.getNetwork().getModule(ModCableModules.Digistore.get()));
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
