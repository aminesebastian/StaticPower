package theking530.staticpower.cables.digistore;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import theking530.staticpower.items.DigistorePatternCard.EncodedDigistorePattern;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreCraftingRequest {
	public final long id;
	public final EncodedDigistorePattern pattern;
	public final HashMap<ItemStack, Integer> requiredItems;

	public DigistoreCraftingRequest(long id, EncodedDigistorePattern pattern) {
		this.id = id;
		this.pattern = pattern;
		this.requiredItems = new HashMap<ItemStack, Integer>();
		for (ItemStack input : pattern.inputs) {
			// Skip empty items.
			if (input.isEmpty()) {
				continue;
			}

			// Check to see if we cached the item.
			boolean cached = false;

			// Check all required items to see if we already tracked this item. IF we do,
			// increment the count. Otherwise, add it.
			for (ItemStack key : requiredItems.keySet()) {
				if (ItemUtilities.areItemStacksStackable(key, input)) {
					int existingCount = requiredItems.get(key);
					existingCount += input.getCount();
					requiredItems.put(key, existingCount);
					cached = true;
				}
			}

			// If we haven't cached this before, add it.
			if (!cached) {
				requiredItems.put(input.copy(), input.getCount());
			}
		}
	}

	public long getId() {
		return id;
	}

	public EncodedDigistorePattern getPattern() {
		return pattern;
	}

	public HashMap<ItemStack, Integer> getRequiredItems() {
		return requiredItems;
	}

	public CompoundNBT serializeToNBT() {
		CompoundNBT output = new CompoundNBT();
		output.putLong("id", id);
		output.put("pattern", pattern.serialize());
		return output;
	}

	public static DigistoreCraftingRequest read(CompoundNBT nbt) {
		long id = nbt.getLong("id");
		EncodedDigistorePattern pattern = EncodedDigistorePattern.read(nbt.getCompound("pattern"));
		return new DigistoreCraftingRequest(id, pattern);
	}
}
