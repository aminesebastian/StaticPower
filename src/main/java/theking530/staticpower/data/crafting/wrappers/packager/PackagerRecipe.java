package theking530.staticpower.data.crafting.wrappers.packager;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class PackagerRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<PackagerRecipe> RECIPE_TYPE = IRecipeType.register("packager");

	private final StaticPowerIngredient inputItem;
	private final ProbabilityItemStackOutput outputItem;
	private final int size;

	public PackagerRecipe(ResourceLocation name, int processingTime, long powerCost, int size, StaticPowerIngredient input, ProbabilityItemStackOutput outputItem) {
		super(name, processingTime, powerCost);
		this.size = size;
		this.inputItem = input;
		this.outputItem = outputItem;
	}

	public ProbabilityItemStackOutput getOutput() {
		return outputItem;
	}

	public StaticPowerIngredient getInputIngredient() {
		return inputItem;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check the size param.
		if (matchParams.hasCustomParameter("size")) {
			int paramSize = matchParams.getCustomParameterContainer().getInt("size");
			if (paramSize != size) {
				return false;
			}
		} else {
			return false;
		}

		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			matched &= matchParams.hasItems() && inputItem.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
		}

		return matched;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return PackageRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}