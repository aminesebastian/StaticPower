package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.CapabilityDigistoreInventory;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.DigistoreInventory;
import theking530.staticpower.utilities.WorldUtilities;

public class DigistoreInventoryComponent extends AbstractTileEntityComponent {
	protected final DigistoreInventory inventory;

	public DigistoreInventoryComponent(String name, int slotCount, int maximumStorage) {
		super(name);
		this.inventory = new DigistoreInventory(slotCount, maximumStorage) {
			@Override
			public void onContentsChanged() {
				DigistoreInventoryComponent.this.getTileEntity().markTileEntityForSynchronization();
			}
		};
	}

	public DigistoreInventory getInventory() {
		return inventory;
	}

	public void dropAllContentsInWorld() {
		for (int i = 0; i < inventory.getMaximumUniqueItemTypeCount(); i++) {
			DigistoreItemTracker tracker = inventory.getItemTracker(i);
			while (tracker.getCount() > 0) {
				int countToDrop = Math.min(tracker.getStoredItem().getMaxStackSize(), tracker.getCount());
				WorldUtilities.dropItem(getWorld(), getTileEntity().getFacingDirection(), getPos(), tracker.getStoredItem(), countToDrop);
				inventory.extractItem(i, countToDrop, false);
			}
		}
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.put("inventory", inventory.serializeNBT());
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		inventory.deserializeNBT(nbt.getCompound("inventory"));
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap == CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY) {
			return LazyOptional.of(() -> {
				return inventory;
			}).cast();
		}
		return LazyOptional.empty();
	}
}
