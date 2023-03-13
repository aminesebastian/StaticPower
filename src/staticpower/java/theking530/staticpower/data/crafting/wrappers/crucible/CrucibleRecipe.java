package theking530.staticpower.data.crafting.wrappers.crucible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.JsonUtilities;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class CrucibleRecipe extends AbstractMachineRecipe {
	public static final String ID = "crucible";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<CrucibleRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input_item").forGetter(recipe -> recipe.getInput()),
					StaticPowerOutputItem.CODEC.fieldOf("slag_item").forGetter(recipe -> recipe.getOutput()),
					JsonUtilities.FLUIDSTACK_CODEC.fieldOf("output_fluid").forGetter(recipe -> recipe.getOutputFluid()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, CrucibleRecipe::new));

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;
	private final FluidStack outputFluid;

	public CrucibleRecipe(ResourceLocation id, StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack outputFluid, MachineRecipeProcessingSection processing) {
		super(id, processing);
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

	public boolean hasItemOutput() {
		return !output.isEmpty();
	}

	public FluidStack getOutputFluid() {
		return outputFluid;
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
		return ModRecipeSerializers.CRUCIBLE_SERIALIZER.get();
	}

	@Override
	public RecipeType<CrucibleRecipe> getType() {
		return ModRecipeTypes.CRUCIBLE_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
