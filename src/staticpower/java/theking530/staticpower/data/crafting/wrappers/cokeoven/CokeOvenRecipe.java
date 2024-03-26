package theking530.staticpower.data.crafting.wrappers.cokeoven;

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
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class CokeOvenRecipe extends AbstractMachineRecipe {
	public static final String ID = "coke_oven";
	public static final int DEFAULT_PROCESSING_TIME = 60 * 20;

	public static final Codec<CokeOvenRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
							StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInput()),
							StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput()),
							FluidStack.CODEC.fieldOf("output_fluid").forGetter(recipe -> recipe.getOutputFluid()),
							Codec.FLOAT.fieldOf("experience").forGetter(recipe -> recipe.getExperience()),
							MachineRecipeProcessingSection.CODEC.fieldOf("processing")
									.forGetter(recipe -> recipe.getProcessingSection()))
					.apply(instance, CokeOvenRecipe::new));

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;
	private final FluidStack outputFluid;

	public CokeOvenRecipe(ResourceLocation id, StaticPowerIngredient input, StaticPowerOutputItem output,
			FluidStack outputFluid, float experience, MachineRecipeProcessingSection processing) {
		super(id, experience, processing);
		this.input = input;
		this.output = output;
		this.outputFluid = outputFluid;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	@Override
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		// Check if the input counts catch.
		if (matchParams.shouldVerifyItems() && matchParams.getItems().length != 1) {
			return false;
		}

		return input.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
	}

	@Override
	public RecipeSerializer<CokeOvenRecipe> getSerializer() {
		return ModRecipeSerializers.COKE_OVEN_SERIALIZER.get();
	}

	@Override
	public RecipeType<CokeOvenRecipe> getType() {
		return ModRecipeTypes.COKE_OVEN_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(() -> DEFAULT_PROCESSING_TIME, () -> 0.0, () -> 0.0f,
				() -> 0.0f);
	}
}
