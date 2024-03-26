package theking530.staticpower.data.crafting.wrappers.evaporation;

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
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.utilities.JsonUtilities;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class EvaporatorRecipe extends AbstractMachineRecipe {
	public static final String ID = "evaporation";
	public static final int DEFAULT_PROCESSING_TIME = 10;

	public static final Codec<EvaporatorRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					FluidIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInputFluid()),
					JsonUtilities.FLUIDSTACK_CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutputFluid()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, EvaporatorRecipe::new));

	private final FluidIngredient inputFluid;
	private final FluidStack outputFluid;

	public EvaporatorRecipe(ResourceLocation name, FluidIngredient inputFluid, FluidStack outputFluid, MachineRecipeProcessingSection processing) {
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
	public RecipeSerializer<EvaporatorRecipe> getSerializer() {
		return ModRecipeSerializers.EVAPORATOR_SERIALIZER.get();
	}

	@Override
	public RecipeType<EvaporatorRecipe> getType() {
		return ModRecipeTypes.EVAPORATOR_RECIPE_TYPE.get();
	}

	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		if (matchParams.hasFluids() && inputFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts())) {
			if (matchParams.shouldVerifyFluidAmounts()) {
				return matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
			} else {
				return true;
			}
		}
		return false;
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, 0, 0, 0);
	}
}
