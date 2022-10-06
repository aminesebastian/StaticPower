package theking530.staticcore.productivity.cache;

import net.minecraft.world.item.ItemStack;
import theking530.staticcore.productivity.entry.ItemProductionEntry;
import theking530.staticcore.productivity.entry.ProductionEntry;

public class ItemProductionCache extends ProductionCache<ItemStack> {
	@Override
	protected ProductionEntry<ItemStack> createNewEntry(ItemStack product) {
		return new ItemProductionEntry(product);
	}

	@Override
	protected String getProductType() {
		return "item";
	}
}
