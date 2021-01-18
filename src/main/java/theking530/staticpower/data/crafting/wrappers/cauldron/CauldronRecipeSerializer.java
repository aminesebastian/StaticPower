package theking530.staticpower.data.crafting.wrappers.cauldron;

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

public class CauldronRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CauldronRecipe> {
	public static final CauldronRecipeSerializer INSTANCE = new CauldronRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(CauldronRecipeSerializer.class);

	private CauldronRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "cauldron_recipe"));
	}

	@Override
	public CauldronRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input.isEmpty()) {
			LOGGER.error(String.format("Encounetered a cauldron recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Get the item output.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.parseFromJSON(JSONUtils.getJsonObject(json, "output"));

		// Get how long the item needs to be in the cauldron.
		int cauldronTime = json.get("time").getAsInt();

		// Indicates if the cauldron should empty after.
		boolean shouldDrainCauldron = false;
		if (json.has("empty_after_craft")) {
			shouldDrainCauldron = json.get("empty_after_craft").getAsBoolean();
		}

		// Deserialize the fluid input and output.
		FluidStack fluidInput = FluidStack.EMPTY;
		FluidStack fluidOutput = FluidStack.EMPTY;
		if (json.has("fluid")) {
			fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(JSONUtils.getJsonObject(json, "fluid"));
		}
		if (json.has("output_fluid")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(JSONUtils.getJsonObject(json, "output_fluid"));
		}

		// Create the recipe.
		return new CauldronRecipe(recipeId, input, itemOutput, fluidInput, fluidOutput, shouldDrainCauldron, cauldronTime);
	}

	@Override
	public CauldronRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int time = buffer.readInt();
		boolean shouldDrainCauldron = buffer.readBoolean();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack fluidInput = buffer.readFluidStack();
		FluidStack fluidOutput = buffer.readFluidStack();

		// Create the recipe.
		return new CauldronRecipe(recipeId, input, output, fluidInput, fluidOutput, shouldDrainCauldron, time);
	}

	@Override
	public void write(PacketBuffer buffer, CauldronRecipe recipe) {
		buffer.writeInt(recipe.getRequiredTimeInCauldron());
		buffer.writeBoolean(recipe.shouldDrainCauldron());
		recipe.getInput().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getRequiredFluid());
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
