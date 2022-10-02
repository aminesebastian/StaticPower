package theking530.staticpower.teams.production;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import theking530.staticpower.teams.Team;
import theking530.staticpower.utilities.ItemUtilities;
import theking530.staticpower.utilities.NBTUtilities;

public class ProductionManager {
	private final Team team;
	private final Map<Integer, ProductionEntry<ItemStack>> itemProductivity;

	public ProductionManager(Team team) {
		this.team = team;
		itemProductivity = new HashMap<>();
	}

	public void tick(long gameTime) {
		for (ProductionEntry<ItemStack> entry : itemProductivity.values()) {
			entry.tick(gameTime);
		}
	}

	public void itemInserted(ItemStack stack) {
		if (!stack.isEmpty()) {
			int hash = ItemUtilities.getItemStackHash(stack);
			if (!itemProductivity.containsKey(hash)) {
				ItemStack singleItemStack = stack.copy();
				singleItemStack.setCount(1);
				itemProductivity.put(hash, new ItemProductionEntry(singleItemStack));
			}
			itemProductivity.get(hash).inserted(stack.getCount());
			team.markDirty(true);
		}
	}

	public void itemExtracted(ItemStack stack, int count) {
		if (!stack.isEmpty() && count > 0) {
			int hash = ItemUtilities.getItemStackHash(stack);
			if (!itemProductivity.containsKey(hash)) {
				ItemStack singleItemStack = stack.copy();
				singleItemStack.setCount(1);
				itemProductivity.put(hash, new ItemProductionEntry(singleItemStack));
			}
			itemProductivity.get(hash).extracted(count);
			team.markDirty(true);
		}
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.put("item", NBTUtilities.serialize(this.itemProductivity.values(), (productivity) -> productivity.serialize()));
		return output;
	}

	public void deserialize(CompoundTag tag) {
		List<ItemProductionEntry> itemEntries = NBTUtilities.deserialize(tag.getList("item", ListTag.TAG_COMPOUND), (serialized) -> {
			ItemProductionEntry entry = new ItemProductionEntry();
			entry.deserialize((CompoundTag) serialized);
			return entry;
		});
		for (ItemProductionEntry entry : itemEntries) {
			this.itemProductivity.put(entry.hashCode(), entry);
		}
	}
}
