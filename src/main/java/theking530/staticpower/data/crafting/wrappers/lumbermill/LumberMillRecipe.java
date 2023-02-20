package theking530.staticpower.data.crafting.wrappers.lumbermill;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class LumberMillRecipe extends AbstractMachineRecipe {
	public static final String ID = "lumber_mill";
	public static final RecipeType<LumberMillRecipe> RECIPE_TYPE = new StaticPowerRecipeType<LumberMillRecipe>();

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem primaryOutput;
	private final StaticPowerOutputItem secondaryOutput;
	private final FluidStack outputFluid;

	public LumberMillRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem primaryOutput, StaticPowerOutputItem secondaryOutput, FluidStack outputFluid, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.input = input;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
		this.outputFluid = outputFluid;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public StaticPowerOutputItem getPrimaryOutput() {
		return primaryOutput;
	}

	public StaticPowerOutputItem getSecondaryOutput() {
		return secondaryOutput;
	}

	public List<ItemStack> getRawOutputItems() {
		List<ItemStack> output = new LinkedList<ItemStack>();
		output.add(getPrimaryOutput().getItemStack());
		if (hasSecondaryOutput()) {
			output.add(getSecondaryOutput().getItemStack());
		}
		return output;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public boolean hasSecondaryOutput() {
		return !secondaryOutput.isEmpty();
	}

	public boolean hasOutputFluid() {
		return !outputFluid.isEmpty();
	}

	@Override
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
	public RecipeSerializer<LumberMillRecipe> getSerializer() {
		return LumberMillRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<LumberMillRecipe> getType() {
		return RECIPE_TYPE;
	}
}
