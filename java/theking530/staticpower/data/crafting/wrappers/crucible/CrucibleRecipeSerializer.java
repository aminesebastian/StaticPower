package theking530.staticpower.data.crafting.wrappers.crucible;

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

public class CrucibleRecipeSerializer extends StaticPowerRecipeSerializer<CrucibleRecipe> {
	public static final CrucibleRecipeSerializer INSTANCE = new CrucibleRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "crucible_recipe");
	private static final Logger LOGGER = LogManager.getLogger(CrucibleRecipeSerializer.class);

	@Override
	public CrucibleRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input.isEmpty()) {
			LOGGER.error(String.format("Encounetered a crucible recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.crucibleProcessingTime.get(),
				StaticPowerConfig.SERVER.cruciblePowerUsage.get(), json);

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
		return new CrucibleRecipe(recipeId, input, itemOutput, fluidOutput, minimumTemperature, processing);
	}

	@Override
	public CrucibleRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int minimumTemperature = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack fluid = buffer.readFluidStack();

		return new CrucibleRecipe(recipeId, input, output, fluid, minimumTemperature, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CrucibleRecipe recipe) {
		buffer.writeInt(recipe.getMinimumTemperature());
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
