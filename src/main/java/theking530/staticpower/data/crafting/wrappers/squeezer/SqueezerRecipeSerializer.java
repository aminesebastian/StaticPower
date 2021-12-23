package theking530.staticpower.data.crafting.wrappers.squeezer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class SqueezerRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SqueezerRecipe> {
	public static final SqueezerRecipeSerializer INSTANCE = new SqueezerRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(SqueezerRecipeSerializer.class);

	private SqueezerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "squeezer_recipe"));
	}

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

		// Start with the default values.
		long powerCost = StaticPowerConfig.SERVER.squeezerPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.squeezerProcessingTime.get();

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

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
		return new SqueezerRecipe(recipeId, input, itemOutput, fluidOutput, processingTime, powerCost);
	}

	@Override
	public SqueezerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack fluid = buffer.readFluidStack();

		return new SqueezerRecipe(recipeId, input, output, fluid, time, power);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SqueezerRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getInput().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
