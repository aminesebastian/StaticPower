package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.powered.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.utilities.Reference;

public class FluidInfusionRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluidInfusionRecipe> {
	public static final FluidInfusionRecipeSerializer INSTANCE = new FluidInfusionRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(FluidInfusionRecipeSerializer.class);

	private FluidInfusionRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "fluid_infusion_recipe"));
	}

	@Override
	public FluidInfusionRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		Ingredient input = Ingredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input == Ingredient.EMPTY) {
			LOGGER.error(String.format("Encounetered a fluid infuser recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Start with the default values.
		int powerCost = TileEntityFluidInfuser.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityFluidInfuser.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("time").getAsInt();
			processingTime = processingElement.get("power").getAsInt();
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, FluidInfusionRecipe recipe) {
		// TODO Auto-generated method stub

	}
}
