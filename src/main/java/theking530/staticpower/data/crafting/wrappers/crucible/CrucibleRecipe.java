package theking530.staticpower.data.crafting.wrappers.crucible;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class CrucibleRecipe extends AbstractMachineRecipe {
	public static final String ID = "crucible";
	public static final RecipeType<CrucibleRecipe> RECIPE_TYPE = new StaticPowerRecipeType<CrucibleRecipe>();

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;
	private final FluidStack outputFluid;
	private final int minimumTemperature;

	public CrucibleRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack outputFluid, int minimumTemperature,
			MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.input = input;
		this.output = output;
		this.outputFluid = outputFluid;
		this.minimumTemperature = minimumTemperature;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public boolean hasItemOutput() {
		return !output.isEmpty();
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public int getMinimumTemperature() {
		return minimumTemperature;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
		}

		return matched;
	}

	@Override
	public RecipeSerializer<CrucibleRecipe> getSerializer() {
		return CrucibleRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<CrucibleRecipe> getType() {
		return RECIPE_TYPE;
	}
}
