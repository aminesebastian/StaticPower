package theking530.staticcore.productivity.product.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.productivity.ProductionCache;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.cacheentry.ItemProductionEntry;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.item.ItemUtilities;

public class ItemStackProductType extends ProductType<ItemStack> {

	public ItemStackProductType() {
		super(ItemStack.class, (isClientSide) -> new ProductionCache<ItemStack>(StaticCoreProductTypes.Item.get(), isClientSide));
	}

	@Override
	public String getUnlocalizedName(int amount) {
		if (amount > 1) {
			return "gui.staticcore.product.items";
		}
		return "gui.staticcore.product.item";
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

	@Override
	public boolean isValidProduct(ItemStack product) {
		return !product.isEmpty();
	}

	@Override
	public String getSerializedProduct(ItemStack product) {
		CompoundTag serialized = product.serializeNBT();
		serialized.remove("Count");
		return serialized.getAsString();
	}

	@Override
	public ItemStack deserializeProduct(String serializedProduct) {
		try {
			CompoundTag tag = TagParser.parseTag(serializedProduct);
			tag.putByte("Count", (byte) 1);
			return ItemStack.of(tag);
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to deserialize the serialized string: %1$s to an ItemStack.", serializedProduct));
		}
	}
}
