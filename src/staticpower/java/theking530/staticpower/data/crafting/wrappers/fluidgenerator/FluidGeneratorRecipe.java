package theking530.staticpower.data.crafting.wrappers.fluidgenerator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class FluidGeneratorRecipe extends AbstractMachineRecipe {
	public static final String ID = "fluid_generator";

	public static final Codec<FluidGeneratorRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
							FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(recipe -> recipe.getFluid()),
							MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection()))
					.apply(instance, FluidGeneratorRecipe::new));

	private final FluidIngredient fluid;

	public FluidGeneratorRecipe(ResourceLocation id, FluidIngredient fluid, MachineRecipeProcessingSection processing) {
		super(id, processing);
		this.fluid = fluid;
	}

	public FluidIngredient getFluid() {
		return fluid;
	}

	public double getPowerGeneration() {
		return getProcessingSection().getPower();
	}

	@Override
	public RecipeSerializer<FluidGeneratorRecipe> getSerializer() {
		return ModRecipeSerializers.FLUID_GENERATOR_SERIALIZER.get();
	}

	@Override
	public RecipeType<FluidGeneratorRecipe> getType() {
		return ModRecipeTypes.FLUID_GENERATOR_RECIPE_TYPE.get();
	}

	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		if (!matchParams.shouldVerifyFluids()) {
			return true;
		}
		return fluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts());
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(1, 0, 0, 0);
	}
}
