package theking530.staticpower.data.crafting.wrappers.fusionfurnace;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FusionFurnaceRecipe extends AbstractMachineRecipe {
	public static final RecipeType<FusionFurnaceRecipe> RECIPE_TYPE = RecipeType.register("fusion_furnace");

	private final List<StaticPowerIngredient> inputs;
	private final ProbabilityItemStackOutput output;

	public FusionFurnaceRecipe(ResourceLocation name, int processingTime, long powerCost, List<StaticPowerIngredient> inputs, ProbabilityItemStackOutput output) {
		super(name, processingTime, powerCost);
		this.inputs = inputs;
		this.output = output;
	}

	public List<StaticPowerIngredient> getInputs() {
		return inputs;
	}

	public ProbabilityItemStackOutput getOutput() {
		return output;
	}

	public int getRequiredCountOfItem(ItemStack item) {
		for (StaticPowerIngredient input : inputs) {
			if (input.test(item)) {
				return input.getCount();
			}
		}
		return 0;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check if the input counts catch.
		if (matchParams.shouldVerifyItems() && matchParams.getItems().length < inputs.size()) {
			return false;
		}

		// Copy the inputs.
		List<ItemStack> inputCopies = new ArrayList<ItemStack>();
		for (ItemStack input : matchParams.getItems()) {
			inputCopies.add(input.copy());
		}

		// Check each item, if any fails, return false.
		int matches = 0;
		for (StaticPowerIngredient ing : inputs) {
			for (int i = 0; i < inputCopies.size(); i++) {
				// Check the match.
				boolean itemMatched = false;
				if (matchParams.shouldVerifyItemCounts()) {
					itemMatched = ing.testWithCount(inputCopies.get(i));
				} else {
					itemMatched = ing.test(inputCopies.get(i));
				}

				// Check if there was a match.
				if (itemMatched) {
					inputCopies.set(i, ItemStack.EMPTY);
					matches++;
					break;
				}
			}
		}
		// Return true if we had the correct amount of matches.
		return matches == inputs.size();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return FusionFurnaceRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
