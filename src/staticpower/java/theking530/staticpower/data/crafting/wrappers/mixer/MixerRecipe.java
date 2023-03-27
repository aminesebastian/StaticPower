package theking530.staticpower.data.crafting.wrappers.mixer;

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
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class MixerRecipe extends AbstractMachineRecipe {
	public static final String ID = "mixer";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<MixerRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.optionalFieldOf("input_item_1", StaticPowerIngredient.EMPTY).forGetter(recipe -> recipe.getPrimaryItemInput()),
					StaticPowerIngredient.CODEC.optionalFieldOf("input_item_2", StaticPowerIngredient.EMPTY).forGetter(recipe -> recipe.getSecondaryItemInput()),
					FluidIngredient.CODEC.fieldOf("input_fluid_1").forGetter(recipe -> recipe.getPrimaryFluidInput()),
					FluidIngredient.CODEC.optionalFieldOf("input_fluid_2", FluidIngredient.EMPTY).forGetter(recipe -> recipe.getSecondaryFluidInput()),
					JsonUtilities.FLUIDSTACK_CODEC.fieldOf("output_fluid").forGetter(recipe -> recipe.getOutput()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, MixerRecipe::new));

	private final StaticPowerIngredient input1;
	private final StaticPowerIngredient input2;
	private final FluidIngredient inputFluid1;
	private final FluidIngredient inputFluid2;
	private final FluidStack output;

	public MixerRecipe(ResourceLocation name, StaticPowerIngredient input1, StaticPowerIngredient input2, FluidIngredient inputFluid1, FluidIngredient inputFluid2,
			FluidStack output, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.input1 = input1;
		this.input2 = input2;
		this.inputFluid1 = inputFluid1;
		this.inputFluid2 = inputFluid2;
		this.output = output;
	}

	public StaticPowerIngredient getPrimaryItemInput() {
		return input1;
	}

	public boolean hasPrimaryItemInput() {
		return !input1.isEmpty();
	}

	public StaticPowerIngredient getSecondaryItemInput() {
		return input2;
	}

	public boolean hasSecondaryItemInput() {
		return !input2.isEmpty();
	}

	public int getRequiredItemIngredientCount() {
		int initial = input1.isEmpty() ? 0 : 1;
		return initial + (input2.isEmpty() ? 0 : 1);
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
		return (inputFluid1.isEmpty() ? 0 : 1) + (inputFluid2.isEmpty() ? 0 : 1);
	}

	public FluidStack getOutput() {
		return output;
	}

	@Override
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
		boolean matched = true;

		// Check fluids.
		if (matchParams.shouldVerifyFluids()) {
			// If we dont have enough fluids, return false.
			if (matchParams.getFluids().length < getRequiredFluidCount()) {
				return false;
			}

			// Check fluids either way.
			boolean straightMatch = true;
			straightMatch &= inputFluid1.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts());
			if (getRequiredFluidCount() == 2) {
				straightMatch &= inputFluid2.test(matchParams.getFluids()[1], matchParams.shouldVerifyFluidAmounts());
			}

			if (!straightMatch) {
				straightMatch &= inputFluid1.test(matchParams.getFluids()[1], matchParams.shouldVerifyFluidAmounts());
				if (getRequiredFluidCount() == 2) {
					straightMatch &= inputFluid2.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts());
				}
			}
		}

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			// If we dont have enough items, return false.
			if (matchParams.getItems().length < getRequiredItemIngredientCount()) {
				return false;
			}

			// Check items either way.
			boolean straightMatch = true;
			straightMatch &= matchParams.hasItems() && input1.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
			straightMatch &= matchParams.hasItems() && input2.test(matchParams.getItems()[1], matchParams.shouldVerifyItemCounts());

			// Check the alternate config.
			if (!straightMatch) {
				matched &= matchParams.hasItems() && input1.test(matchParams.getItems()[1], matchParams.shouldVerifyItemCounts());
				matched &= matchParams.hasItems() && input2.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<MixerRecipe> getSerializer() {
		return ModRecipeSerializers.MIXER_SERIALIZER.get();
	}

	@Override
	public RecipeType<MixerRecipe> getType() {
		return ModRecipeTypes.MIXER_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
