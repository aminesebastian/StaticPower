package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;

public class DigistoreInventoryComponent extends AbstractTileEntityComponent implements IItemHandler {

	public DigistoreInventoryComponent(String name) {
		super(name);
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		ItemStack output = getOwningDigistore().getStoredItem().copy();
		output.setCount(getOwningDigistore().getStoredAmount());
		return output;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return getOwningDigistore().insertItem(stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return getOwningDigistore().extractItem(amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return getOwningDigistore().getMaxStoredAmount();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return getOwningDigistore().canAcceptItem(stack);
	}

	private TileEntityDigistore getOwningDigistore() {
		return (TileEntityDigistore) getTileEntity();
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
