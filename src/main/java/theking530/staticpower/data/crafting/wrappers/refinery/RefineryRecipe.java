package theking530.staticpower.data.crafting.wrappers.refinery;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class RefineryRecipe extends AbstractMachineRecipe {
	public static final RecipeType<RefineryRecipe> RECIPE_TYPE = RecipeType.register("refinery");

	private final StaticPowerIngredient catalyst;
	private final FluidStack inputFluid1;
	private final FluidStack inputFluid2;
	private final FluidStack output1;
	private final FluidStack output2;
	private final FluidStack output3;

	public RefineryRecipe(ResourceLocation name, StaticPowerIngredient catalyst, FluidStack inputFluid1, FluidStack inputFluid2, FluidStack output1, FluidStack output2, FluidStack output3,
			MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.catalyst = catalyst;
		this.inputFluid1 = inputFluid1;
		this.inputFluid2 = inputFluid2;
		this.output1 = output1;
		this.output2 = output2;
		this.output3 = output3;
	}

	public StaticPowerIngredient getCatalyst() {
		return catalyst;
	}

	public boolean hasCatalyst() {
		return !catalyst.isEmpty();
	}

	public FluidStack getPrimaryFluidInput() {
		return inputFluid1;
	}

	public boolean hasPrimaryFluidInput() {
		return !inputFluid1.isEmpty();
	}

	public FluidStack getSecondaryFluidInput() {
		return inputFluid2;
	}

	public boolean hasSecondaryFluidInput() {
		return !inputFluid2.isEmpty();
	}

	public int getRequiredFluidCount() {
		int initial = inputFluid1.isEmpty() ? 0 : 1;
		return initial + (inputFluid2.isEmpty() ? 0 : 1);
	}

	public FluidStack getFluidOutput1() {
		return output1;
	}

	public FluidStack getFluidOutput2() {
		return output2;
	}

	public FluidStack getFluidOutput3() {
		return output3;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check fluids.
		if (matchParams.shouldVerifyFluids()) {
			// If we dont have enough fluids, return false.
			if (matchParams.getFluids().length < getRequiredFluidCount()) {
				return false;
			}

			// Check fluids either way.
			boolean straightMatch = true;
			straightMatch &= matchParams.getFluids()[0].equals(inputFluid1);
			straightMatch &= matchParams.getFluids()[1].equals(inputFluid2);
			if (!straightMatch) {
				matched &= matchParams.getFluids()[1].equals(inputFluid1);
				matched &= matchParams.getFluids()[0].equals(inputFluid2);
			}

			// Verify the amounts.
			if (matched && matchParams.shouldVerifyFluidAmounts()) {
				if (straightMatch) {
					matched &= matchParams.getFluids()[0].getAmount() >= inputFluid1.getAmount();
					matched &= matchParams.getFluids()[1].getAmount() >= inputFluid2.getAmount();
				} else {
					matched &= matchParams.getFluids()[1].getAmount() >= inputFluid1.getAmount();
					matched &= matchParams.getFluids()[0].getAmount() >= inputFluid2.getAmount();
				}
			}
		}

		// Check items.
		if (matchParams.shouldVerifyItems() && !catalyst.isEmpty()) {
			// Check items either way.
			if (!matchParams.hasItems() && catalyst.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts())) {
				return false;
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RefineryRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}