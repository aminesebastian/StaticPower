package theking530.staticpower.data.crafting.wrappers.centrifuge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.tileentities.powered.centrifuge.TileEntityCentrifuge;

public class CentrifugeRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugeRecipe> {
	public static final CentrifugeRecipeSerializer INSTANCE = new CentrifugeRecipeSerializer();

	private CentrifugeRecipeSerializer() {
		setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "centrifuge_recipe"));
	}

	@Override
	public CentrifugeRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Get the minimum speed.
		int minimumSpeed = json.get("minimum_speed").getAsInt();

		// Start with the default values.
		int powerCost = TileEntityCentrifuge.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityCentrifuge.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the outputs.
		JsonArray outputs = json.getAsJsonArray("outputs");

		// Deserialize the first output and allocate the second and third.
		ProbabilityItemStackOutput firstOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.get(0).getAsJsonObject());
		ProbabilityItemStackOutput secondOutput = ProbabilityItemStackOutput.EMPTY;
		ProbabilityItemStackOutput thirdOutput = ProbabilityItemStackOutput.EMPTY;

		// If there is a second output, deserialize it.
		if (outputs.size() > 1) {
			secondOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.get(1).getAsJsonObject());
		}

		// If there is a third output, deserialize it.
		if (outputs.size() > 2) {
			thirdOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.get(2).getAsJsonObject());
		}

		// Create the recipe.
		return new CentrifugeRecipe(recipeId, processingTime, powerCost, input, firstOutput, secondOutput, thirdOutput, minimumSpeed);
	}

	@Override
	public CentrifugeRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int power = buffer.readInt();
		int time = buffer.readInt();
		int speed = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output1 = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput output2 = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput output3 = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new CentrifugeRecipe(recipeId, time, power, input, output1, output2, output3, speed);
	}

	@Override
	public void write(PacketBuffer buffer, CentrifugeRecipe recipe) {
		buffer.writeInt(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeInt(recipe.getMinimumSpeed());
		recipe.getInput().write(buffer);
		recipe.getOutput1().writeToBuffer(buffer);
		recipe.getOutput2().writeToBuffer(buffer);
		recipe.getOutput3().writeToBuffer(buffer);

	}
}
