package theking530.staticpower.data.crafting.wrappers.fusionfurnace;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FusionFurnaceRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FusionFurnaceRecipe> {
	public static final FusionFurnaceRecipeSerializer INSTANCE = new FusionFurnaceRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "fusion_furnace_recipe");

	@Override
	public FusionFurnaceRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonArray inputElement = json.getAsJsonArray("inputs");
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();

		// Deserialize the input ingredients.
		for (JsonElement element : inputElement) {
			StaticPowerIngredient input = StaticPowerIngredient.deserialize(element);
			inputs.add(input);
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.fusionFurnaceProcessingTime.get(),
				StaticPowerConfig.SERVER.fusionFurnacePowerUsage.get(), json);

		// Get the output.
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(json.getAsJsonObject("output"));

		// Craete the recipe.
		return new FusionFurnaceRecipe(recipeId, inputs, output, processing);
	}

	@Override
	public FusionFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int inputCount = buffer.readByte();
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();
		for (int i = 0; i < inputCount; i++) {
			inputs.add(StaticPowerIngredient.read(buffer));
		}

		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Craete the recipe.
		return new FusionFurnaceRecipe(recipeId, inputs, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FusionFurnaceRecipe recipe) {
		buffer.writeByte(recipe.getInputs().size());

		for (StaticPowerIngredient ing : recipe.getInputs()) {
			ing.write(buffer);
		}

		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
