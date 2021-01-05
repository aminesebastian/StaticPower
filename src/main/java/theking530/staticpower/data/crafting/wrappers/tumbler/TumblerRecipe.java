package theking530.staticpower.data.crafting.wrappers.tumbler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class TumblerRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<TumblerRecipe> RECIPE_TYPE = IRecipeType.register("tumbler");

	private final ProbabilityItemStackOutput output;
	private final StaticPowerIngredient inputItem;

	public TumblerRecipe(ResourceLocation name, int processingTime, long powerCost, StaticPowerIngredient input, ProbabilityItemStackOutput output) {
		super(name, processingTime, powerCost);
		this.inputItem = input;
		this.output = output;
	}

	public ProbabilityItemStackOutput getOutput() {
		return output;
	}

	public ItemStack getRawOutputItem() {
		return output.getItem();
	}

	public StaticPowerIngredient getInputIngredient() {
		return inputItem;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && inputItem.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && inputItem.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return TumblerRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}