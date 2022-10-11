package theking530.staticpower.data.crafting.wrappers.hammer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class HammerRecipeSerializer extends StaticPowerRecipeSerializer<HammerRecipe> {
	public static final HammerRecipeSerializer INSTANCE = new HammerRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "hammer_recipe");
	private final JsonElement hammerTag;

	private HammerRecipeSerializer() {
		hammerTag = GsonHelper.parse("{ \"tag\":\"staticpower:hammer\" }");
	}

	@Override
	public HammerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Create the hammer ingredient.
		Ingredient hammer = Ingredient.fromJson(hammerTag);

		// Get the item output.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.parseFromJSON(json.get("output").getAsJsonObject());

		// Create the recipe.
		if (json.has("input_item")) {
			return new HammerRecipe(recipeId, hammer, StaticPowerIngredient.deserialize(json.get("input_item")), itemOutput);
		} else if (json.has("input_block")) {
			return new HammerRecipe(recipeId, hammer, new ResourceLocation(json.get("input_block").getAsString()), itemOutput);
		} else {
			return null;
		}
	}

	@Override
	public HammerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		boolean isBlockType = buffer.readBoolean();
		ProbabilityItemStackOutput outputs = ProbabilityItemStackOutput.readFromBuffer(buffer);
		Ingredient hammer = Ingredient.fromNetwork(buffer);

		if (isBlockType) {
			ResourceLocation input = new ResourceLocation(buffer.readUtf());
			return new HammerRecipe(recipeId, hammer, input, outputs);
		} else {
			StaticPowerIngredient inputItem = StaticPowerIngredient.read(buffer);
			return new HammerRecipe(recipeId, hammer, inputItem, outputs);
		}
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, HammerRecipe recipe) {
		buffer.writeBoolean(recipe.isBlockType());
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getHammer().toNetwork(buffer);

		if (recipe.isBlockType()) {
			buffer.writeUtf(recipe.getRawInputTag().toString());
		} else {
			recipe.getInputItem().write(buffer);
		}
	}
}
