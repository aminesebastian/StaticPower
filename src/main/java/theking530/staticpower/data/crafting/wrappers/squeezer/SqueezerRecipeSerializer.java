package theking530.staticpower.data.crafting.wrappers.squeezer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class SqueezerRecipeSerializer extends StaticPowerRecipeSerializer<SqueezerRecipe> {
	public static final SqueezerRecipeSerializer INSTANCE = new SqueezerRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "squeezer_recipe");
	private static final Logger LOGGER = LogManager.getLogger(SqueezerRecipeSerializer.class);

	@Override
	public SqueezerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input.isEmpty()) {
			LOGGER.error(String.format("Encounetered a squeezer recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.squeezerProcessingTime.get(),
				StaticPowerConfig.SERVER.squeezerPowerUsage.get(), json);

		// Get the outputs object.
		JsonObject outputs = GsonHelper.getAsJsonObject(json, "outputs");

		// Get the item output if one is defined.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.EMPTY;
		if (GsonHelper.isValidNode(outputs, "item")) {
			itemOutput = ProbabilityItemStackOutput.parseFromJSON(GsonHelper.getAsJsonObject(outputs, "item"));
		}

		// Deserialize the fluid output if it exists.
		FluidStack fluidOutput = FluidStack.EMPTY;
		if (GsonHelper.isValidNode(outputs, "fluid")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(outputs, "fluid"));
		}

		// Return null if the output fluid is null.
		if (fluidOutput == null) {
			LOGGER.error(String.format("Encounetered a null fluid output while deserializing a squeezer recipe...skipping. %1$s", recipeId));
			return null;
		}

		// Return null if no outputs are defined.
		if ((itemOutput.isEmpty() && fluidOutput.isEmpty())) {
			LOGGER.error(String.format("Encounetered a squeezer recipe with no input output item OR fluid...skipping. %1$s", recipeId));
			return null;
		}

		// Create the recipe.
		return new SqueezerRecipe(recipeId, input, itemOutput, fluidOutput, processing);
	}

	@Override
	public SqueezerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack fluid = buffer.readFluidStack();

		return new SqueezerRecipe(recipeId, input, output, fluid, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SqueezerRecipe recipe) {
		recipe.getInput().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}

	@Override
	public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
		return INSTANCE;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return ID;
	}
}
