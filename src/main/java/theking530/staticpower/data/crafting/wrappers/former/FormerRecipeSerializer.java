package theking530.staticpower.data.crafting.wrappers.former;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.tileentities.powered.former.TileEntityFormer;
import theking530.staticpower.utilities.Reference;

public class FormerRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FormerRecipe> {
	public static final FormerRecipeSerializer INSTANCE = new FormerRecipeSerializer();

	private FormerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "former_recipe"));
	}

	@Override
	public FormerRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the input mold.
		JsonObject moldElement = JSONUtils.getJsonObject(json, "mold");
		Ingredient mold = Ingredient.deserialize(moldElement);

		// Get the output item.
		JsonObject outputElement = JSONUtils.getJsonObject(json, "output");
		ItemStack output = ShapedRecipe.deserializeItem(outputElement);

		// Start with the default processing values.
		int powerCost = TileEntityFormer.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityFormer.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Create the recipe.
		return new FormerRecipe(recipeId, processingTime, powerCost, output, input, mold);
	}

	@Override
	public FormerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, FormerRecipe recipe) {
	}
}
