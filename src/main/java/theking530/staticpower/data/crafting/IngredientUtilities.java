package theking530.staticpower.data.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.utilities.ItemUtilities;

public class IngredientUtilities {
	public static CompoundNBT serializeIngredient(Ingredient ingredient) {
		ListNBT ingredientStacks = new ListNBT();
		for (ItemStack stack : ingredient.getMatchingStacks()) {
			CompoundNBT outputTag = new CompoundNBT();
			stack.write(outputTag);
			ingredientStacks.add(outputTag);
		}

		CompoundNBT output = new CompoundNBT();
		output.put("matching_stacks", ingredientStacks);
		return output;
	}

	public static Ingredient deserializeIngredient(CompoundNBT nbt) {
		ListNBT ingredientsNbt = nbt.getList("matching_stacks", Constants.NBT.TAG_COMPOUND);
		ItemStack[] ingredientStacks = new ItemStack[ingredientsNbt.size()];
		for (int i = 0; i < ingredientsNbt.size(); i++) {
			CompoundNBT outputTagNbt = (CompoundNBT) ingredientsNbt.get(i);
			ItemStack stack = ItemStack.read(outputTagNbt);
			ingredientStacks[i] = stack;
		}
		return Ingredient.fromStacks(ingredientStacks);
	}

	public static boolean areIngredientsEqual(Ingredient first, Ingredient second) {
		if (first.hasNoMatchingItems() || second.hasNoMatchingItems()) {
			return false;
		}
		if (first.getMatchingStacks().length != second.getMatchingStacks().length) {
			return false;
		}
		for (int i = 0; i < first.getMatchingStacks().length; i++) {
			if (!ItemUtilities.areItemStacksStackable(first.getMatchingStacks()[i], second.getMatchingStacks()[i])) {
				return false;
			}
		}
		return true;
	}
}
