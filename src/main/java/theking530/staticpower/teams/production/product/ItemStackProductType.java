package theking530.staticpower.teams.production.product;

import net.minecraft.world.item.ItemStack;
import theking530.staticpower.teams.production.ItemProductionEntry;
import theking530.staticpower.teams.production.ProductionTrackingToken;
import theking530.staticpower.utilities.ItemUtilities;

public class ItemStackProductType extends ProductType<ItemStack, ItemProductionEntry> {

	public ItemStackProductType() {
		super(ItemStack.class);
	}

	@Override
	public String getUnlocalizedName() {
		return "gui.staticpower.item";
	}

	@Override
	public ProductionTrackingToken<ItemStack> createProductivityToken() {
		return new ProductionTrackingToken<ItemStack>(this);
	}

	@Override
	public int getProductHashCode(ItemStack product) {
		return ItemUtilities.getItemStackHash(product);
	}

	@Override
	public ItemProductionEntry createProductionEntry(ItemStack product) {
		return new ItemProductionEntry(product);
	}
}
