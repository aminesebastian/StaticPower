package theking530.staticpower.data.crafting.wrappers.lumbermill;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class LumberMillRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<LumberMillRecipe> {
	public static final LumberMillRecipeSerializer INSTANCE = new LumberMillRecipeSerializer();

	private LumberMillRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "lumber_mill_recipe"));
	}

	@Override
	public LumberMillRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.lumberMillProcessingTime.get(),
				StaticPowerConfig.SERVER.lumberMillPowerUsage.get(), json);

		// Get the outputs.
		JsonObject outputs = GsonHelper.getAsJsonObject(json, "outputs");
		ProbabilityItemStackOutput primaryOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.getAsJsonObject("primary"));
		ProbabilityItemStackOutput secondaryOutput = ProbabilityItemStackOutput.EMPTY;
		FluidStack fluidOutput = FluidStack.EMPTY;

		// Deserialize the secondary output if it exists.
		if (outputs.has("secondary")) {
			secondaryOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.getAsJsonObject("secondary"));
		}

		// Deserialize the fluid output if it exsists.
		if (outputs.has("fluid")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(outputs.getAsJsonObject("fluid"));
		}

		// Craete the recipe.
		return new LumberMillRecipe(recipeId, input, primaryOutput, secondaryOutput, fluidOutput, processing);
	}

	@Override
	public LumberMillRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput primary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput secondary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack outFluid = buffer.readFluidStack();

		return new LumberMillRecipe(recipeId, input, primary, secondary, outFluid, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, LumberMillRecipe recipe) {
		recipe.getInput().write(buffer);
		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
