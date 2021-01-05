package theking530.staticpower.data.crafting.wrappers.fusionfurnace;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FusionFurnaceRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FusionFurnaceRecipe> {
	public static final FusionFurnaceRecipeSerializer INSTANCE = new FusionFurnaceRecipeSerializer();

	private FusionFurnaceRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "fusion_furnace_recipe"));
	}

	@Override
	public FusionFurnaceRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonArray inputElement = json.getAsJsonArray("inputs");
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();

		// Deserialize the input ingredients.
		for (JsonElement element : inputElement) {
			StaticPowerIngredient input = StaticPowerIngredient.deserialize(element);
			inputs.add(input);
		}

		// Start with the default values.
		long powerCost = StaticPowerConfig.SERVER.fusionFurnacePowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.fusionFurnaceProcessingTime.get();

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the output.
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(json.getAsJsonObject("output"));

		// Craete the recipe.
		return new FusionFurnaceRecipe(recipeId, processingTime, powerCost, inputs, output);
	}

	@Override
	public FusionFurnaceRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();

		int inputCount = buffer.readByte();
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();
		for (int i = 0; i < inputCount; i++) {
			inputs.add(StaticPowerIngredient.read(buffer));
		}

		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Craete the recipe.
		return new FusionFurnaceRecipe(recipeId, time, power, inputs, output);
	}

	@Override
	public void write(PacketBuffer buffer, FusionFurnaceRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeByte(recipe.getInputs().size());

		for (StaticPowerIngredient ing : recipe.getInputs()) {
			ing.write(buffer);
		}

		recipe.getOutput().writeToBuffer(buffer);
	}
}
