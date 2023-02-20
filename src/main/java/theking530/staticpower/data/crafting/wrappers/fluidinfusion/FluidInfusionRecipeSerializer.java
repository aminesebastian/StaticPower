package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class FluidInfusionRecipeSerializer extends StaticPowerRecipeSerializer<FluidInfusionRecipe> {
	public static final FluidInfusionRecipeSerializer INSTANCE = new FluidInfusionRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "fluid_infusion_recipe");
	private static final Logger LOGGER = LogManager.getLogger(FluidInfusionRecipeSerializer.class);

	@Override
	public FluidInfusionRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input.isEmpty()) {
			LOGGER.error(String.format("Encounetered a fluid infuser recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.fluidInfuserProcessingTime,
				StaticPowerConfig.SERVER.fluidInfuserPowerUsage, json);

		// Get the item output.
		StaticPowerOutputItem itemOutput = StaticPowerOutputItem.parseFromJSON(GsonHelper.getAsJsonObject(json, "result"));

		// Deserialize the fluid input.
		FluidStack fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(json, "fluid"));
		// Return null if the output fluid is null.
		if (fluidInput == null) {
			LOGGER.error(String.format("Encounetered a null fluid input while deserializing a fluid infuser recipe...skipping. %1$s", recipeId));
			return null;
		}

		// Create the recipe.
		return new FluidInfusionRecipe(recipeId, input, itemOutput, fluidInput, processing);
	}

	@Override
	public FluidInfusionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		FluidStack fluidInput = buffer.readFluidStack();
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new FluidInfusionRecipe(recipeId, input, output, fluidInput, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FluidInfusionRecipe recipe) {
		recipe.getInput().write(buffer);
		buffer.writeFluidStack(recipe.getRequiredFluid());
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
