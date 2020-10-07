package theking530.staticpower.items.utilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

public class ItemInventoryHandler extends ItemStackHandler {
	protected static final String ITEM_INVENTORY_TAG = "static_power_inventory";
	protected final ItemStack container;
	protected final String name;

	public ItemInventoryHandler(String name, ItemStack container, int size) {
		super(size);
		this.container = container;
		this.name = name;

		if (container.hasTag() && container.getTag().contains(getTag())) {
			deserializeNBT(container.getTag().getCompound(getTag()));
		}
	}

	public void updateToTag(CompoundNBT nbt) {
		nbt.put(getTag(), serializeNBT());
	}

	public void updateFromTag(CompoundNBT nbt) {
		deserializeNBT(nbt.getCompound(getTag()));
		System.out.println(nbt);
	}

	protected String getTag() {
		return name + "_" + ITEM_INVENTORY_TAG;
	}
}
