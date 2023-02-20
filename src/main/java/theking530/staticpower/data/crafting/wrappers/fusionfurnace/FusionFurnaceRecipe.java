package theking530.staticpower.data.crafting.wrappers.fusionfurnace;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class FusionFurnaceRecipe extends AbstractMachineRecipe {
	public static final String ID = "fusion_furnace";
	public static final RecipeType<FusionFurnaceRecipe> RECIPE_TYPE = new StaticPowerRecipeType<FusionFurnaceRecipe>();

	private final List<StaticPowerIngredient> inputs;
	private final StaticPowerOutputItem output;

	public FusionFurnaceRecipe(ResourceLocation name, List<StaticPowerIngredient> inputs, StaticPowerOutputItem output, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.inputs = inputs;
		this.output = output;
	}

	public List<StaticPowerIngredient> getInputs() {
		return inputs;
	}

	public StaticPowerOutputItem getOutput() {
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
	public RecipeSerializer<FusionFurnaceRecipe> getSerializer() {
		return FusionFurnaceRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<FusionFurnaceRecipe> getType() {
		return RECIPE_TYPE;
	}
}
