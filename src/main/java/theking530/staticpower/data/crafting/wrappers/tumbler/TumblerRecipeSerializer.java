package theking530.staticpower.data.crafting.wrappers.tumbler;

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

public class TumblerRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TumblerRecipe> {
	public static final TumblerRecipeSerializer INSTANCE = new TumblerRecipeSerializer();

	private TumblerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "tumbler_recipe"));
	}

	@Override
	public TumblerRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the output.
		JsonObject outputElement = JSONUtils.getJsonObject(json, "output");
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement);

		// Start with the default processing values.
		long powerCost = StaticPowerConfig.SERVER.tumblerPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.tumblerProcessingTime.get();

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		return new TumblerRecipe(recipeId, processingTime, powerCost, input, output);
	}

	@Override
	public TumblerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		return new TumblerRecipe(recipeId, time, power, input, output);
	}

	@Override
	public void write(PacketBuffer buffer, TumblerRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getInputIngredient().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
	}
}
