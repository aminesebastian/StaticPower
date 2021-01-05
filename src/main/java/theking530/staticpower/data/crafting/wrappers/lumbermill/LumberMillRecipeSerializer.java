package theking530.staticpower.data.crafting.wrappers.lumbermill;

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

public class LumberMillRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<LumberMillRecipe> {
	public static final LumberMillRecipeSerializer INSTANCE = new LumberMillRecipeSerializer();

	private LumberMillRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "lumber_mill_recipe"));
	}

	@Override
	public LumberMillRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Start with the default values.
		long powerCost = StaticPowerConfig.SERVER.lumberMillPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.lumberMillProcessingTime.get();

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the outputs.
		JsonObject outputs = JSONUtils.getJsonObject(json, "outputs");
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
		return new LumberMillRecipe(recipeId, input, primaryOutput, secondaryOutput, fluidOutput, processingTime, powerCost);
	}

	@Override
	public LumberMillRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput primary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput secondary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack outFluid = buffer.readFluidStack();

		return new LumberMillRecipe(recipeId, input, primary, secondary, outFluid, time, power);
	}

	@Override
	public void write(PacketBuffer buffer, LumberMillRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getInput().write(buffer);
		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
