package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

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
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class FluidInfusionRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluidInfusionRecipe> {
	public static final FluidInfusionRecipeSerializer INSTANCE = new FluidInfusionRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(FluidInfusionRecipeSerializer.class);

	private FluidInfusionRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "fluid_infusion_recipe"));
	}

	@Override
	public FluidInfusionRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input.isEmpty()) {
			LOGGER.error(String.format("Encounetered a fluid infuser recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Start with the default values.
		long powerCost = StaticPowerConfig.SERVER.fluidInfuserPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.fluidInfuserProcessingTime.get();

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the item output.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.parseFromJSON(JSONUtils.getJsonObject(json, "result"));

		// Deserialize the fluid input.
		FluidStack fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(JSONUtils.getJsonObject(json, "fluid"));
		// Return null if the output fluid is null.
		if (fluidInput == null) {
			LOGGER.error(String.format("Encounetered a null fluid input while deserializing a fluid infuser recipe...skipping. %1$s", recipeId));
			return null;
		}

		// Create the recipe.
		return new FluidInfusionRecipe(recipeId, input, itemOutput, fluidInput, processingTime, powerCost);
	}

	@Override
	public FluidInfusionRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		FluidStack fluidInput = buffer.readFluidStack();
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new FluidInfusionRecipe(recipeId, input, output, fluidInput, time, power);
	}

	@Override
	public void write(PacketBuffer buffer, FluidInfusionRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getInput().write(buffer);
		buffer.writeFluidStack(recipe.getRequiredFluid());
		recipe.getOutput().writeToBuffer(buffer);
	}
}
