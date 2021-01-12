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
		Ingredient solderingIron = Ingredient.deserialize(hammerTag);

		// Capture the input ingredient.
		String inputTag = json.get("input_block").getAsString();

		// Get the item output.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.parseFromJSON(json.get("output").getAsJsonObject());

		// Create the recipe.
		return new HammerRecipe(recipeId, solderingIron, new ResourceLocation(inputTag), itemOutput);
	}

	@Override
	public HammerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		ResourceLocation input = new ResourceLocation(buffer.readString());
		ProbabilityItemStackOutput outputs = ProbabilityItemStackOutput.readFromBuffer(buffer);
		Ingredient hammer = Ingredient.read(buffer);

		// Create the recipe.
		return new HammerRecipe(recipeId, hammer, input, outputs);
	}

	@Override
	public void write(PacketBuffer buffer, HammerRecipe recipe) {
		buffer.writeString(recipe.getRawInputTag().toString());
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getHammer().write(buffer);
	}
}
