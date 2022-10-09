package theking530.staticcore.productivity.product;

import net.minecraft.world.item.ItemStack;
import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.entry.ItemProductionEntry;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.utilities.ItemUtilities;

public class ItemStackProductType extends ProductType<ItemStack> {

	public ItemStackProductType() {
		super(ItemStack.class, () -> new ProductionCache<ItemStack>(ModProducts.Item.get()));
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
