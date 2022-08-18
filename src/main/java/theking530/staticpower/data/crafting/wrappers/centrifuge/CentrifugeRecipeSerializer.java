package theking530.staticpower.data.crafting.wrappers.centrifuge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class CentrifugeRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CentrifugeRecipe> {
	public static final CentrifugeRecipeSerializer INSTANCE = new CentrifugeRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "centrifuge_recipe");

	@Override
	public CentrifugeRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Get the minimum speed.
		int minimumSpeed = json.get("minimum_speed").getAsInt();

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.centrifugeProcessingTime.get(),
				StaticPowerConfig.SERVER.centrifugePowerUsage.get(), json);

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
		return new CentrifugeRecipe(recipeId, input, firstOutput, secondOutput, thirdOutput, minimumSpeed, processing);
	}

	@Override
	public CentrifugeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int speed = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output1 = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput output2 = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput output3 = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new CentrifugeRecipe(recipeId, input, output1, output2, output3, speed, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CentrifugeRecipe recipe) {
		buffer.writeInt(recipe.getMinimumSpeed());
		recipe.getInput().write(buffer);
		recipe.getOutput1().writeToBuffer(buffer);
		recipe.getOutput2().writeToBuffer(buffer);
		recipe.getOutput3().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
