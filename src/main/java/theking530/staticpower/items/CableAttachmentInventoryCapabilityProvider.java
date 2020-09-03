package theking530.staticpower.items;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.items.utilities.ItemInventoryHandler;

public class CableAttachmentInventoryCapabilityProvider extends ItemStackInventoryCapabilityProvider {
	protected final ItemInventoryHandler upgradeInventory;

	public CableAttachmentInventoryCapabilityProvider(ItemStack owner, int size, int upgradeInventorySize, @Nullable CompoundNBT nbt) {
		super(owner, size, nbt);
		upgradeInventory = new ItemInventoryHandler("upgrade", owner, size);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == null) {
			return net.minecraftforge.common.util.LazyOptional.of(() -> inventory).cast();
		} else if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return net.minecraftforge.common.util.LazyOptional.of(() -> upgradeInventory).cast();
		}
		return LazyOptional.empty();
	}
}
