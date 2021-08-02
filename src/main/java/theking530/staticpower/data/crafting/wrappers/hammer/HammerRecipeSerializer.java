package theking530.staticpower.data.crafting.wrappers.hammer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class HammerRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<HammerRecipe> {
	public static final HammerRecipeSerializer INSTANCE = new HammerRecipeSerializer();
	private final JsonElement hammerTag;

	private HammerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "hammer_recipe"));
		hammerTag = JSONUtils.fromJson("{ \"tag\":\"staticpower:hammer\" }");
	}

	@Override
	public HammerRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Create the hammer ingredient.
		Ingredient hammer = Ingredient.deserialize(hammerTag);

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
	public HammerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		boolean isBlockType = buffer.readBoolean();
		ProbabilityItemStackOutput outputs = ProbabilityItemStackOutput.readFromBuffer(buffer);
		Ingredient hammer = Ingredient.read(buffer);

		if (isBlockType) {
			ResourceLocation input = new ResourceLocation(buffer.readString());
			return new HammerRecipe(recipeId, hammer, input, outputs);
		} else {
			StaticPowerIngredient inputItem = StaticPowerIngredient.read(buffer);
			return new HammerRecipe(recipeId, hammer, inputItem, outputs);
		}
	}

	@Override
	public void write(PacketBuffer buffer, HammerRecipe recipe) {
		buffer.writeBoolean(recipe.isBlockType());
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getHammer().write(buffer);

		if (recipe.isBlockType()) {
			buffer.writeString(recipe.getRawInputTag().toString());
		} else {
			recipe.getInputItem().write(buffer);
		}
	}
}
