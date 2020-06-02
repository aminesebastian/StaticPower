package theking530.staticpower.crafting.wrappers.grinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.utilities.Reference;

public class GrinderRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GrinderRecipe> {
	GrinderRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "grinder_recipe"));
	}

	@Override
	public GrinderRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Reads the input. Accepts items, tags, and anything else that
		// Ingredient.deserialize can understand.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "input"));

		// Reads the output. The common utility method in ShapedRecipe is what all
		// vanilla
		// recipe classes use for this.
		JsonElement outputElement = JSONUtils.isJsonArray(json, "output") ? JSONUtils.getJsonArray(json, "output") : JSONUtils.getJsonObject(json, "output");
		if (outputElement.isJsonArray()) {
			JsonArray outputArray = outputElement.getAsJsonArray();
			GrinderRecipe.GrinderOutput[] outputs = new GrinderRecipe.GrinderOutput[outputArray.size()];
			for (int i = 0; i < outputArray.size(); i++) {
				outputs[i] = GrinderRecipe.GrinderOutput.parseFromJSON(outputArray[i]);
			}
		} else {

		}

		return new GrinderRecipe(recipeId, input, output, block);
	}

	@Override
	public GrinderRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, GrinderRecipe recipe) {
		// TODO Auto-generated method stub

	}

}
