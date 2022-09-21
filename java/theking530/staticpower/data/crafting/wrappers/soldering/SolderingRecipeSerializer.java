package theking530.staticpower.data.crafting.wrappers.soldering;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class SolderingRecipeSerializer extends StaticPowerRecipeSerializer<SolderingRecipe> {
	public static final SolderingRecipeSerializer INSTANCE = new SolderingRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "soldering_recipe");
	private final JsonElement solderingIronTag;

	public SolderingRecipeSerializer() {
		solderingIronTag = GsonHelper.parse("{ \"tag\":\"staticpower:soldering_iron\" }");
	}

	public SolderingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		String s = GsonHelper.getAsString(json, "group", "");
		Map<String, Ingredient> map = SolderingRecipe.deserializeKey(GsonHelper.getAsJsonObject(json, "key"));
		String[] astring = SolderingRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern"));
		int i = astring[0].length();
		int j = astring.length;
		NonNullList<Ingredient> nonnulllist = SolderingRecipe.deserializeIngredients(astring, map, i, j);
		ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
		Ingredient solderingIron = Ingredient.fromJson(solderingIronTag);
		return new SolderingRecipe(recipeId, s, i, j, solderingIron, nonnulllist, itemstack);
	}

	public SolderingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int i = buffer.readVarInt();
		int j = buffer.readVarInt();
		String s = buffer.readUtf(32767);
		NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

		for (int k = 0; k < nonnulllist.size(); ++k) {
			nonnulllist.set(k, Ingredient.fromNetwork(buffer));
		}

		ItemStack itemstack = buffer.readItem();
		Ingredient solderingIron = Ingredient.fromNetwork(buffer);
		return new SolderingRecipe(recipeId, s, i, j, solderingIron, nonnulllist, itemstack);
	}

	public void toNetwork(FriendlyByteBuf buffer, SolderingRecipe recipe) {
		buffer.writeVarInt(recipe.recipeWidth);
		buffer.writeVarInt(recipe.recipeHeight);
		buffer.writeUtf(recipe.group);

		for (Ingredient ingredient : recipe.recipeItems) {
			ingredient.toNetwork(buffer);
		}

		buffer.writeItem(recipe.recipeOutput);
		recipe.getSolderingIron().toNetwork(buffer);
	}

	@Override
	public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
		return INSTANCE;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return ID;
	}
}