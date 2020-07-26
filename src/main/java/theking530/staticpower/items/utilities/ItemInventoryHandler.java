package theking530.staticpower.items.utilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

public class ItemInventoryHandler extends ItemStackHandler {
	public static final String ITEM_INVENTORY_TAG = "static_power_inventory";
	protected final ItemStack container;

	public ItemInventoryHandler(ItemStack container, int size) {
		super(size);
		this.container = container;
		if (container.hasTag() && container.getTag().contains(ITEM_INVENTORY_TAG)) {
			deserializeNBT(container.getTag().getCompound(ITEM_INVENTORY_TAG));
		}
	}

	@Override
	protected void onContentsChanged(int slot) {
		if (!container.hasTag()) {
			container.setTag(new CompoundNBT());
		}
		container.getTag().put(ITEM_INVENTORY_TAG, serializeNBT());
	}
}
