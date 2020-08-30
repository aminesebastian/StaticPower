package theking530.api.digistore;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class DigistoreInventoryCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
	protected final ItemStack owningItemStack;
	protected final CompoundNBT initialNbt;
	protected final DigistoreInventory inventory;

	public DigistoreInventoryCapabilityProvider(ItemStack owner, int uniqueItemTypes, int maxStoredItems, @Nullable CompoundNBT nbt) {
		owningItemStack = owner;
		initialNbt = nbt;
		inventory = new DigistoreInventory(uniqueItemTypes, maxStoredItems);
		if (nbt != null) {
			inventory.deserializeNBT(nbt);
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY) {
			return net.minecraftforge.common.util.LazyOptional.of(() -> inventory).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {
		return inventory.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		inventory.deserializeNBT(nbt);
	}
}
