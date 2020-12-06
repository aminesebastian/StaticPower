package theking530.staticpower.data.crafting.wrappers.crucible;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.powered.squeezer.TileEntitySqueezer;

public class CrucibleRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CrucibleRecipe> {
	public static final CrucibleRecipeSerializer INSTANCE = new CrucibleRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(CrucibleRecipeSerializer.class);

	private CrucibleRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "crucible_recipe"));
	}

	@Override
	public CrucibleRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input.isEmpty()) {
			LOGGER.error(String.format("Encounetered a crucible recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Start with the default values.
		int powerCost = TileEntitySqueezer.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntitySqueezer.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the outputs object.
		JsonObject outputs = JSONUtils.getJsonObject(json, "outputs");

		// Get the item output if one is defined.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.EMPTY;
		if (JSONUtils.hasField(outputs, "item")) {
			itemOutput = ProbabilityItemStackOutput.parseFromJSON(JSONUtils.getJsonObject(outputs, "item"));
		}

		// Deserialize the fluid output if it exists.
		FluidStack fluidOutput = FluidStack.EMPTY;
		if (JSONUtils.hasField(outputs, "fluid")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(JSONUtils.getJsonObject(outputs, "fluid"));
		}

		// Get the minimum temperature.
		int minimumTemperature;
		if (json.has("minimum_temperature")) {
			minimumTemperature = json.get("minimum_temperature").getAsInt();
		} else {
			minimumTemperature = fluidOutput.getFluid().getAttributes().getTemperature(fluidOutput);
		}

		// Return null if the output fluid is null.
		if (fluidOutput == null) {
			LOGGER.error(String.format("Encounetered a null fluid output while deserializing a crucible recipe...skipping. %1$s", recipeId));
			return null;
		}

		// Create the recipe.
		return new CrucibleRecipe(recipeId, input, itemOutput, fluidOutput, minimumTemperature, processingTime, powerCost);
	}

	@Override
	public CrucibleRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int power = buffer.readInt();
		int time = buffer.readInt();
		int minimumTemperature = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack fluid = buffer.readFluidStack();

		return new CrucibleRecipe(recipeId, input, output, fluid, minimumTemperature, time, power);
	}

	@Override
	public void write(PacketBuffer buffer, CrucibleRecipe recipe) {
		buffer.writeInt(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeInt(recipe.getMinimumTemperature());
		recipe.getInput().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
