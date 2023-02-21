package theking530.staticpower.data.crafting.wrappers.centrifuge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class CentrifugeRecipeSerializer extends StaticPowerRecipeSerializer<CentrifugeRecipe> {
	public static final CentrifugeRecipeSerializer INSTANCE = new CentrifugeRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "centrifuge_recipe");

	@Override
	public CentrifugeRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Get the minimum speed.
		int minimumSpeed = json.get("minimum_speed").getAsInt();

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.centrifugeProcessingTime,
				StaticPowerConfig.SERVER.centrifugePowerUsage, json);

		// Get the outputs.
		JsonArray outputs = json.getAsJsonArray("outputs");

		// Deserialize the first output and allocate the second and third.
		StaticPowerOutputItem firstOutput = StaticPowerOutputItem.parseFromJSON(outputs.get(0).getAsJsonObject());
		StaticPowerOutputItem secondOutput = StaticPowerOutputItem.EMPTY;
		StaticPowerOutputItem thirdOutput = StaticPowerOutputItem.EMPTY;

		// If there is a second output, deserialize it.
		if (outputs.size() > 1) {
			secondOutput = StaticPowerOutputItem.parseFromJSON(outputs.get(1).getAsJsonObject());
		}

		// If there is a third output, deserialize it.
		if (outputs.size() > 2) {
			thirdOutput = StaticPowerOutputItem.parseFromJSON(outputs.get(2).getAsJsonObject());
		}

		// Create the recipe.
		return new CentrifugeRecipe(recipeId, input, firstOutput, secondOutput, thirdOutput, minimumSpeed, processing);
	}

	@Override
	public CentrifugeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int speed = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output1 = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem output2 = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem output3 = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new CentrifugeRecipe(recipeId, input, output1, output2, output3, speed, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CentrifugeRecipe recipe) {
		buffer.writeInt(recipe.getMinimumSpeed());
		recipe.getInput().writeToBuffer(buffer);
		recipe.getOutput1().writeToBuffer(buffer);
		recipe.getOutput2().writeToBuffer(buffer);
		recipe.getOutput3().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
