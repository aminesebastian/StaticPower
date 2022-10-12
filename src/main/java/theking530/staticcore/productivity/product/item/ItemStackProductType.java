package theking530.staticcore.productivity.product.item;

import net.minecraft.world.item.ItemStack;
import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.cacheentry.ItemProductionEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.init.ModProducts;
import theking530.staticpower.utilities.ItemUtilities;

public class ItemStackProductType extends ProductType<ItemStack> {

	public ItemStackProductType() {
		super(ItemStack.class, (isClientSide) -> new ProductionCache<ItemStack>(ModProducts.Item.get(), isClientSide));
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
