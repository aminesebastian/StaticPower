package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackInventoryCapabilityProvider implements ICapabilityProvider {
	protected final ItemStack owningItemStack;
	protected final int inventorySize;
	protected final CompoundNBT initialNbt;
	protected final ItemStackHandler inventory;

	public ItemStackInventoryCapabilityProvider(ItemStack owner, int size, @Nullable CompoundNBT nbt) {
		inventorySize = size;
		owningItemStack = owner;
		initialNbt = nbt;
		inventory = new ItemStackHandler(inventorySize);
		if (nbt != null) {
			inventory.deserializeNBT(nbt);
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return net.minecraftforge.common.util.LazyOptional.of(() -> inventory).cast();
		}
		return LazyOptional.empty();
	}
}
