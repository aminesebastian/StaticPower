package theking530.staticpower.cables.attachments.digistore.craftingterminal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreCraftingTerminalHistory implements INBTSerializable<CompoundNBT> {
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
	public CompoundNBT serializeNBT() {
		// Allocate the final serialized result.
		CompoundNBT serialized = new CompoundNBT();
		serialized.putInt("max_history", maxHistory);

		// Serialize the history.
		ListNBT entryTags = new ListNBT();
		for (DigistoreCraftingTerminalHistoryEntry entry : entries) {
			entryTags.add(entry.serializeNBT());
		}
		serialized.put("entries", entryTags);
		return serialized;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		// Clear the original entries list.
		entries.clear();

		// Capture the max history.
		maxHistory = nbt.getInt("max_history");

		// Deserialize the history.
		ListNBT serializedEntries = nbt.getList("entries", Constants.NBT.TAG_COMPOUND);
		for (INBT entryTag : serializedEntries) {
			CompoundNBT entryTagCompound = (CompoundNBT) entryTag;
			DigistoreCraftingTerminalHistoryEntry entry = new DigistoreCraftingTerminalHistoryEntry();
			entry.deserializeNBT(entryTagCompound);
			entries.add(entry);
		}
	}

	@Override
	public String toString() {
		return "DigistoreCraftingTerminalHistory [entries=" + entries + ", maxHistory=" + maxHistory + "]";
	}

	public class DigistoreCraftingTerminalHistoryEntry implements INBTSerializable<CompoundNBT> {
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
		public CompoundNBT serializeNBT() {
			// Allocate the final serialized result.
			CompoundNBT serialized = new CompoundNBT();

			// Serialize the output item.
			CompoundNBT outputTag = new CompoundNBT();
			output.write(outputTag);
			serialized.put("output", outputTag);

			// Serialize the recipe.
			ListNBT recipeTags = new ListNBT();
			for (ItemStack recipeItem : recipe) {
				CompoundNBT itemTag = new CompoundNBT();
				recipeItem.write(itemTag);
				recipeTags.add(itemTag);
			}
			serialized.put("recipe", recipeTags);
			serialized.putInt("recipe_size", recipe.length);

			return serialized;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			// Deserialize the output.
			output = ItemStack.read(nbt.getCompound("output"));

			// Deserialize the recipe.
			recipe = new ItemStack[nbt.getInt("recipe_size")];
			ListNBT serializedRecipe = nbt.getList("recipe", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < serializedRecipe.size(); i++) {
				INBT recipeTag = serializedRecipe.get(i);
				CompoundNBT recipeTagCompound = (CompoundNBT) recipeTag;
				recipe[i] = ItemStack.read(recipeTagCompound);
			}
		}
	}
}
