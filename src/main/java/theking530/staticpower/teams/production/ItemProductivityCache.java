package theking530.staticpower.teams.production;

import java.sql.Connection;

import net.minecraft.world.item.ItemStack;

public class ItemProductivityCache extends AbstractProductivityCache<ItemStack> {

	public ItemProductivityCache(Connection database) {
		super(database);
	}

	@Override
	protected AbstractProductionEntry<ItemStack> createNewEntry(ItemStack product) {
		return new ItemProductionEntry(product);
	}

	@Override
	protected String getProductType() {
		return "item";
	}
}
