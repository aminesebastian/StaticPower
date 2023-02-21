package theking530.staticpower.data.crafting.wrappers.packager;

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

public class PackagerRecipeSerializer extends StaticPowerRecipeSerializer<PackagerRecipe> {
	public static final PackagerRecipeSerializer INSTANCE = new PackagerRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "packager_recipe");

	@Override
	public PackagerRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.packagerProcessingTime,
				StaticPowerConfig.SERVER.packagerPowerUsage, json);

		// Get the recipe size.
		int size = json.get("size").getAsInt();

		// Get the item output if one is defined.
		StaticPowerOutputItem itemOutput = StaticPowerOutputItem.parseFromJSON(json.get("output").getAsJsonObject());

		// Create the recipe.
		return new PackagerRecipe(recipeId, size, input, itemOutput, processing);
	}

	@Override
	public PackagerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int size = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem outputs = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new PackagerRecipe(recipeId, size, input, outputs, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, PackagerRecipe recipe) {
		buffer.writeInt(recipe.getSize());
		recipe.getInputIngredient().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
