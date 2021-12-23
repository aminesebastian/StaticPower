package theking530.staticpower.tileentities.components.items;

import net.minecraft.world.item.ItemStack;

public interface ItemStackHandlerFilter {

	public default boolean canInsertItem(int slot, ItemStack stack) {
		return true;
	}

	public default boolean canExtractItem(int slot, int amount) {
		return true;
	}

}
