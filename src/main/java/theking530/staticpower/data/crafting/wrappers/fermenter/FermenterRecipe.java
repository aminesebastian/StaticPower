package theking530.staticpower.data.crafting.wrappers.fermenter;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class FermenterRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "fermenter";
	public static final RecipeType<FermenterRecipe> RECIPE_TYPE = new StaticPowerRecipeType<FermenterRecipe>();

	private final FluidStack outputFluidStack;
	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;

	public FermenterRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack fluid) {
		super(name);
		outputFluidStack = fluid;
		this.input = input;
		this.output = output;
	}

	public FluidStack getOutputFluidStack() {
		return outputFluidStack;
	}

	public StaticPowerOutputItem getResidualOutput() {
		return output;
	}

	public StaticPowerIngredient getInputIngredient() {
		return input;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && input.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<FermenterRecipe> getSerializer() {
		return FermenterRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<FermenterRecipe> getType() {
		return RECIPE_TYPE;
	}
}
