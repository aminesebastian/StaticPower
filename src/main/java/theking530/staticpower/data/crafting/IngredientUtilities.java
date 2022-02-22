package theking530.staticpower.data.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticpower.utilities.ItemUtilities;

public class IngredientUtilities {
	public static CompoundTag serializeIngredient(Ingredient ingredient) {
		ListTag ingredientStacks = new ListTag();
		for (ItemStack stack : ingredient.getItems()) {
			CompoundTag outputTag = new CompoundTag();
			stack.save(outputTag);
			ingredientStacks.add(outputTag);
		}

		CompoundTag output = new CompoundTag();
		output.put("matching_stacks", ingredientStacks);
		return output;
	}

	public static Ingredient deserializeIngredient(CompoundTag nbt) {
		ListTag ingredientsNbt = nbt.getList("matching_stacks", Tag.TAG_COMPOUND);
		ItemStack[] ingredientStacks = new ItemStack[ingredientsNbt.size()];
		for (int i = 0; i < ingredientsNbt.size(); i++) {
			CompoundTag outputTagNbt = (CompoundTag) ingredientsNbt.get(i);
			ItemStack stack = ItemStack.of(outputTagNbt);
			ingredientStacks[i] = stack;
		}
		return Ingredient.of(ingredientStacks);
	}

	public static boolean areIngredientsEqual(Ingredient first, Ingredient second) {
		if (first.isEmpty() || second.isEmpty()) {
			return false;
		}
		if (first.getItems().length != second.getItems().length) {
			return false;
		}
		for (int i = 0; i < first.getItems().length; i++) {
			if (!ItemUtilities.areItemStacksStackable(first.getItems()[i], second.getItems()[i])) {
				return false;
			}
		}
		return true;
	}
}
