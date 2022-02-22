package theking530.staticpower.cables.attachments.digistore.craftingterminal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreCraftingTerminalHistory implements INBTSerializable<CompoundTag> {
	protected Queue<DigistoreCraftingTerminalHistoryEntry> entries;
	protected int maxHistory;

	public DigistoreCraftingTerminalHistory(int maxHistory) {
		this.entries = new LinkedList<DigistoreCraftingTerminalHistoryEntry>();
		this.maxHistory = maxHistory;
	}

	public void addCraft(ItemStack[] recipe, ItemStack output) {
		// Make sure this is a new unique entry.
		boolean wasDifferent = true;
		for (DigistoreCraftingTerminalHistoryEntry history : entries) {
			// Check the outputs. IF they are the same, just update the recipe.
			if (ItemUtilities.areItemStacksStackable(history.output, output)) {
				wasDifferent = false;
				history.recipe = recipe;
				break;
			}
		}

		// If this is a totally new entry, add it.
		if (wasDifferent) {
			if (entries.size() >= maxHistory) {
				entries.remove();
			}
			entries.add(new DigistoreCraftingTerminalHistoryEntry(recipe, output));
		}
	}

	public List<DigistoreCraftingTerminalHistoryEntry> getHistory() {
		return new ArrayList<DigistoreCraftingTerminalHistoryEntry>(entries);
	}

	@Override
	public CompoundTag serializeNBT() {
		// Allocate the final serialized result.
		CompoundTag serialized = new CompoundTag();
		serialized.putInt("max_history", maxHistory);

		// Serialize the history.
		ListTag entryTags = new ListTag();
		for (DigistoreCraftingTerminalHistoryEntry entry : entries) {
			entryTags.add(entry.serializeNBT());
		}
		serialized.put("entries", entryTags);
		return serialized;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// Clear the original entries list.
		entries.clear();

		// Capture the max history.
		maxHistory = nbt.getInt("max_history");

		// Deserialize the history.
		ListTag serializedEntries = nbt.getList("entries", Tag.TAG_COMPOUND);
		for (Tag entryTag : serializedEntries) {
			CompoundTag entryTagCompound = (CompoundTag) entryTag;
			DigistoreCraftingTerminalHistoryEntry entry = new DigistoreCraftingTerminalHistoryEntry();
			entry.deserializeNBT(entryTagCompound);
			entries.add(entry);
		}
	}

	@Override
	public String toString() {
		return "DigistoreCraftingTerminalHistory [entries=" + entries + ", maxHistory=" + maxHistory + "]";
	}

	public class DigistoreCraftingTerminalHistoryEntry implements INBTSerializable<CompoundTag> {
		public ItemStack output;
		public ItemStack[] recipe;

		protected DigistoreCraftingTerminalHistoryEntry() {

		}

		public DigistoreCraftingTerminalHistoryEntry(@Nonnull ItemStack[] recipe, ItemStack output) {
			this.output = output;
			this.recipe = recipe;
		}

		@Override
		public String toString() {
			return "DigistoreCraftingTerminalHistoryEntry [recipe=" + recipe + ", output=" + output + "]";
		}

		@Override
		public CompoundTag serializeNBT() {
			// Allocate the final serialized result.
			CompoundTag serialized = new CompoundTag();

			// Serialize the output item.
			CompoundTag outputTag = new CompoundTag();
			output.save(outputTag);
			serialized.put("output", outputTag);

			// Serialize the recipe.
			ListTag recipeTags = new ListTag();
			for (ItemStack recipeItem : recipe) {
				CompoundTag itemTag = new CompoundTag();
				recipeItem.save(itemTag);
				recipeTags.add(itemTag);
			}
			serialized.put("recipe", recipeTags);
			serialized.putInt("recipe_size", recipe.length);

			return serialized;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			// Deserialize the output.
			output = ItemStack.of(nbt.getCompound("output"));

			// Deserialize the recipe.
			recipe = new ItemStack[nbt.getInt("recipe_size")];
			ListTag serializedRecipe = nbt.getList("recipe", Tag.TAG_COMPOUND);
			for (int i = 0; i < serializedRecipe.size(); i++) {
				Tag recipeTag = serializedRecipe.get(i);
				CompoundTag recipeTagCompound = (CompoundTag) recipeTag;
				recipe[i] = ItemStack.of(recipeTagCompound);
			}
		}
	}
}
