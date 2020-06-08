package theking530.staticpower.tileentities.nonpowered.digistorenetwork.ioport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;

public class DigitstoreIOPortInventoryComponent extends AbstractTileEntityComponent implements IItemHandler {

	public DigitstoreIOPortInventoryComponent(String name) {
		super(name);
	}

	@Override
	public int getSlots() {
		return getDigistoreList().size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return getDigistoreList().get(slot).getStoredItem();
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return getDigistoreList().get(slot).insertItem(stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return getDigistoreList().get(slot).extractItem(amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return getDigistoreList().get(slot).getMaxStoredAmount();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		for (TileEntityDigistore teStore : getDigistoreList()) {
			if (teStore.canAcceptItem(stack)) {
				return true;
			}
		}
		return false;
	}

	private List<TileEntityDigistore> getDigistoreList() {
		if (getTileEntity() instanceof TileEntityDigistoreIOPort) {
			TileEntityDigistoreIOPort port = (TileEntityDigistoreIOPort) getTileEntity();
			return port.getManager().getNetwork().getAllNetworkTiles(TileEntityDigistore.class);
		}
		StaticPower.LOGGER.error(String.format("TileEntity at location: %1$s did not inherit from TileEntityDigistoreIOPort but is using a component of type DigitstoreIOPortInventoryComponent.",
				getTileEntity().getPos()));
		return new ArrayList<TileEntityDigistore>();
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
