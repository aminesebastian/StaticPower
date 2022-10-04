package theking530.staticpower.teams.production;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.utilities.ItemUtilities;

public class ItemProductionEntry extends AbstractProductionEntry<ItemStack> {

	public ItemProductionEntry(ItemStack product) {
		super(product);
	}

	@Override
	public int getProductHashCode() {
		return ItemUtilities.getItemStackHash(product);
	}

	@Override
	public String getSerializedProduct() {
		CompoundTag serialized = product.serializeNBT();
		serialized.remove("Count");
		return serialized.getAsString();
	}
}
