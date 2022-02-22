package theking530.api.digistore;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class DigistoreInventoryCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
	protected final ItemStack owningItemStack;
	protected final CompoundTag initialNbt;
	protected final DigistoreInventory inventory;

	public DigistoreInventoryCapabilityProvider(ItemStack owner, int uniqueItemTypes, int maxStoredItems, @Nullable CompoundTag nbt) {
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
	public CompoundTag serializeNBT() {
		return inventory.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		inventory.deserializeNBT(nbt);
	}
}
