package theking530.staticpower.data.crafting.wrappers.lathe;

import com.google.gson.JsonArray;
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
import theking530.staticpower.tileentities.powered.lumbermill.TileEntityLumberMill;

public class LatheRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<LatheRecipe> {
	public static final LatheRecipeSerializer INSTANCE = new LatheRecipeSerializer();

	private LatheRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "lathe_recipe"));
	}

	@Override
	public LatheRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonArray inputsArray = JSONUtils.getJsonArray(json, "inputs");
		StaticPowerIngredient[] inputs = new StaticPowerIngredient[inputsArray.size()];
		for (int i = 0; i < inputsArray.size(); i++) {
			inputs[i] = StaticPowerIngredient.deserialize(inputsArray.get(i));
		}

		// Start with the default values.
		int powerCost = TileEntityLumberMill.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityLumberMill.DEFAULT_PROCESSING_TIME;

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
		return new LatheRecipe(recipeId, inputs, primaryOutput, secondaryOutput, fluidOutput, processingTime, powerCost);
	}

	@Override
	public LatheRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int power = buffer.readInt();
		int time = buffer.readInt();
		int inputCount = buffer.readInt();
		StaticPowerIngredient[] inputs = new StaticPowerIngredient[inputCount];
		for (int i = 0; i < inputCount; i++) {
			inputs[i] = StaticPowerIngredient.read(buffer);
		}

		ProbabilityItemStackOutput primary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput secondary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack outFluid = buffer.readFluidStack();

		return new LatheRecipe(recipeId, inputs, primary, secondary, outFluid, time, power);
	}

	@Override
	public void write(PacketBuffer buffer, LatheRecipe recipe) {
		buffer.writeInt(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeInt(recipe.getInputs().length);
		for (StaticPowerIngredient ing : recipe.getInputs()) {
			ing.write(buffer);
		}

		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
