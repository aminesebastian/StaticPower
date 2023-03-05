package theking530.staticpower.data.crafting.wrappers.condensation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.JsonUtilities;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class CondensationRecipe extends AbstractMachineRecipe {
	public static final String ID = "condensation";

	public static final int DEFAULT_PROCESSING_TIME = 40;

	public static final Codec<CondensationRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
							FluidIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInputFluid()),
							JsonUtilities.FLUIDSTACK_CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutputFluid()),
							MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection()))
					.apply(instance, CondensationRecipe::new));

	private final FluidIngredient inputFluid;
	private final FluidStack outputFluid;

	public CondensationRecipe(ResourceLocation name, FluidIngredient inputFluid, FluidStack outputFluid, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.inputFluid = inputFluid;
		this.outputFluid = outputFluid;
	}

	public FluidIngredient getInputFluid() {
		return inputFluid;
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	@Override
	public RecipeSerializer<CondensationRecipe> getSerializer() {
		return ModRecipeSerializers.CONEDNSATION_SERIALIZER.get();
	}

	@Override
	public RecipeType<CondensationRecipe> getType() {
		return ModRecipeTypes.CONDENSATION_RECIPE_TYPE.get();
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		if (matchParams.hasFluids() && inputFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts())) {
			return true;
		}
		return false;
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, 0, 0, 0);
	}
}
