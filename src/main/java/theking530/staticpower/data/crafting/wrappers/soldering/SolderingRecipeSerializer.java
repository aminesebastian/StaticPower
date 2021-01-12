package theking530.staticpower.data.crafting.wrappers.soldering;

import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.StaticPower;

public class SolderingRecipeSerializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SolderingRecipe> {
	public static final SolderingRecipeSerializer INSTANCE = new SolderingRecipeSerializer();
	private final JsonElement solderingIronTag;

	public SolderingRecipeSerializer() {
		setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "soldering_recipe"));
		solderingIronTag = JSONUtils.fromJson("{ \"tag\":\"staticpower:soldering_iron\" }");
	}

	public SolderingRecipe read(ResourceLocation recipeId, JsonObject json) {
		String s = JSONUtils.getString(json, "group", "");
		Map<String, Ingredient> map = SolderingRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
		String[] astring = SolderingRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern"));
		int i = astring[0].length();
		int j = astring.length;
		NonNullList<Ingredient> nonnulllist = SolderingRecipe.deserializeIngredients(astring, map, i, j);
		ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
		Ingredient solderingIron = Ingredient.deserialize(solderingIronTag);
		return new SolderingRecipe(recipeId, s, i, j, solderingIron, nonnulllist, itemstack);
	}

	public SolderingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int i = buffer.readVarInt();
		int j = buffer.readVarInt();
		String s = buffer.readString(32767);
		NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

		for (int k = 0; k < nonnulllist.size(); ++k) {
			nonnulllist.set(k, Ingredient.read(buffer));
		}

		ItemStack itemstack = buffer.readItemStack();
		Ingredient solderingIron = Ingredient.read(buffer);
		return new SolderingRecipe(recipeId, s, i, j, solderingIron, nonnulllist, itemstack);
	}

	public void write(PacketBuffer buffer, SolderingRecipe recipe) {
		buffer.writeVarInt(recipe.recipeWidth);
		buffer.writeVarInt(recipe.recipeHeight);
		buffer.writeString(recipe.group);

		for (Ingredient ingredient : recipe.recipeItems) {
			ingredient.write(buffer);
		}

		buffer.writeItemStack(recipe.recipeOutput);
		recipe.getSolderingIron().write(buffer);
	}
}