package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

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

public class FluidInfusionRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FluidInfusionRecipe> {
	public static final FluidInfusionRecipeSerializer INSTANCE = new FluidInfusionRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(FluidInfusionRecipeSerializer.class);

	private FluidInfusionRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "fluid_infusion_recipe"));
	}

	@Override
	public FluidInfusionRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
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
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the item output.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.parseFromJSON(GsonHelper.getAsJsonObject(json, "result"));

		// Deserialize the fluid input.
		FluidStack fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(json, "fluid"));
		// Return null if the output fluid is null.
		if (fluidInput == null) {
			LOGGER.error(String.format("Encounetered a null fluid input while deserializing a fluid infuser recipe...skipping. %1$s", recipeId));
			return null;
		}

		// Create the recipe.
		return new FluidInfusionRecipe(recipeId, input, itemOutput, fluidInput, processingTime, powerCost);
	}

	@Override
	public FluidInfusionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		FluidStack fluidInput = buffer.readFluidStack();
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new FluidInfusionRecipe(recipeId, input, output, fluidInput, time, power);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FluidInfusionRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getInput().write(buffer);
		buffer.writeFluidStack(recipe.getRequiredFluid());
		recipe.getOutput().writeToBuffer(buffer);
	}
}
