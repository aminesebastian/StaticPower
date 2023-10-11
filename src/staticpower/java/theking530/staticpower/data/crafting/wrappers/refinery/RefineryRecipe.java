package theking530.staticpower.data.crafting.wrappers.refinery;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.utilities.JsonUtilities;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class RefineryRecipe extends AbstractMachineRecipe {
	public static final String ID = "refinery";
	public static final int DEFAULT_PROCESSING_TIME = 10;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<RefineryRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
			FluidIngredient.CODEC.fieldOf("input_fluid_1").forGetter(recipe -> recipe.getPrimaryFluidInput()),
			FluidIngredient.CODEC.optionalFieldOf("input_fluid_2", FluidIngredient.EMPTY)
					.forGetter(recipe -> recipe.getSecondaryFluidInput()),
			StaticPowerIngredient.CODEC.optionalFieldOf("catalyst", StaticPowerIngredient.EMPTY)
					.forGetter(recipe -> recipe.getCatalyst()),
			JsonUtilities.FLUIDSTACK_CODEC.fieldOf("output_fluid_1").forGetter(recipe -> recipe.getFluidOutput1()),
			JsonUtilities.FLUIDSTACK_CODEC.optionalFieldOf("output_fluid_2", FluidStack.EMPTY)
					.forGetter(recipe -> recipe.getFluidOutput2()),
			JsonUtilities.FLUIDSTACK_CODEC.optionalFieldOf("output_fluid_3", FluidStack.EMPTY)
					.forGetter(recipe -> recipe.getFluidOutput3()),
			MachineRecipeProcessingSection.CODEC.fieldOf("processing")
					.forGetter(recipe -> recipe.getProcessingSection()))
			.apply(instance, RefineryRecipe::new));

	private final StaticPowerIngredient catalyst;
	private final FluidIngredient inputFluid1;
	private final FluidIngredient inputFluid2;
	private final FluidStack output1;
	private final FluidStack output2;
	private final FluidStack output3;

	public RefineryRecipe(ResourceLocation name, FluidIngredient inputFluid1, FluidIngredient inputFluid2,
			StaticPowerIngredient catalyst, FluidStack output1, FluidStack output2, FluidStack output3,
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

	public FluidIngredient getPrimaryFluidInput() {
		return inputFluid1;
	}

	public boolean hasPrimaryFluidInput() {
		return !inputFluid1.isEmpty();
	}

	public FluidIngredient getSecondaryFluidInput() {
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
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
		boolean matched = true;

		// Check fluids.
		if (matchParams.shouldVerifyFluids()) {
			// If we dont have an exact match of fluids, return false. This is to prevent
			// cases where sending in oil and water will return the first recipe that
			// matches instead of the accurate one.
			if (matchParams.getFluids().length != getRequiredFluidCount()) {
				return false;
			}

			// Check fluids either way.
			if (!inputFluid1.isEmpty()) {
				matched &= inputFluid1.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts())
						|| matchParams.getFluids()[0] == ModFluids.WILDCARD;
			}

			if (!inputFluid2.isEmpty()) {
				matched &= inputFluid2.test(matchParams.getFluids()[1], matchParams.shouldVerifyFluidAmounts())
						|| matchParams.getFluids()[1] == ModFluids.WILDCARD;
			}

			if (!matched) {
				return false;
			}
		}

		// Check items.
		if (matchParams.shouldVerifyItems() && !catalyst.isEmpty()) {
			// Check items either way.
			if (!matchParams.hasItems()
					|| !catalyst.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts())) {
				return false;
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<RefineryRecipe> getSerializer() {
		return ModRecipeSerializers.REFINERY_SERIALIZER.get();
	}

	@Override
	public RecipeType<RefineryRecipe> getType() {
		return ModRecipeTypes.REFINERY_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
