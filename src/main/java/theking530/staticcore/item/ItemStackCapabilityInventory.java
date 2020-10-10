package theking530.staticcore.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackCapabilityInventory extends ItemStackHandler implements IItemMultiCapability {
	protected static final String ITEM_INVENTORY_TAG = "static_power_inventory";
	protected final ItemStack container;
	protected final String name;

	public ItemStackCapabilityInventory(String name, ItemStack container, int size) {
		super(size);
		this.container = container;
		this.name = name;

		if (container.hasTag() && container.getTag().contains(getName())) {
			deserializeNBT(container.getTag().getCompound(getName()));
		}
	}

	@Override
	public String getName() {
		return name + "_" + ITEM_INVENTORY_TAG;
	}

	@Override
	public Capability<?>[] getCapabilityTypes() {
		return new Capability<?>[] { CapabilityItemHandler.ITEM_HANDLER_CAPABILITY };
	}
}
