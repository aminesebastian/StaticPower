package theking530.staticpower.teams.production;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.utilities.ItemUtilities;

public class ItemProductionEntry extends ProductionEntry<ItemStack> {

	public ItemProductionEntry() {
		super();
	}

	public ItemProductionEntry(ItemStack product) {
		super(product);
	}

	@Override
	public int hashCode() {
		return ItemUtilities.getItemStackHash(getProduct());
	}

	@Override
	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		getProduct().save(output);
		return output;
	}

	@Override
	public void deserialize(CompoundTag tag) {
		product = ItemStack.of(tag);
	}
}
