package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class FluidInfusionRecipe extends AbstractMachineRecipe {
	public static final String ID = "fluid_infusion";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<FluidInfusionRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
							StaticPowerIngredient.CODEC.fieldOf("input_item").forGetter(recipe -> recipe.getInput()),
							FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(recipe -> recipe.getRequiredFluid()),
							StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput()),
							MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection()))
					.apply(instance, FluidInfusionRecipe::new));

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;
	private final FluidIngredient inputFluid;

	public FluidInfusionRecipe(ResourceLocation id, StaticPowerIngredient input, FluidIngredient inputFluid, StaticPowerOutputItem output,
			MachineRecipeProcessingSection processing) {
		super(id, processing);
		this.input = input;
		this.output = output;
		this.inputFluid = inputFluid;
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

	public FluidIngredient getRequiredFluid() {
		return inputFluid;
	}

	@Override
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		boolean matched = true;

		// Check fluids.
		if (matchParams.shouldVerifyFluids()) {
			matched &= matchParams.hasFluids() && inputFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts());
		}

		// Check items.
		if (matched && matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && input.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<FluidInfusionRecipe> getSerializer() {
		return ModRecipeSerializers.FLUID_INFUSER_SERIALIZER.get();
	}

	@Override
	public RecipeType<FluidInfusionRecipe> getType() {
		return ModRecipeTypes.FLUID_INFUSION_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
