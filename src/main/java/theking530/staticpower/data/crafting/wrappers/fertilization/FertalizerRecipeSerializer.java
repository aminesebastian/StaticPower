package theking530.staticpower.data.crafting.wrappers.fertilization;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class FertalizerRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FertalizerRecipe> {
	public static final FertalizerRecipeSerializer INSTANCE = new FertalizerRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(FertalizerRecipeSerializer.class);

	private FertalizerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "farming_fertalizer_recipe"));
	}

	@Override
	public FertalizerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Deserialize the fluid input.
		FluidStack fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("fertilizer"));

		// Return null if the output fluid is null.
		if (fluidInput == null) {
			LOGGER.error(String.format("Encounetered a fertalizer recipe with no input fertalizer fluid defined...skipping. %1$s", recipeId));
			return null;
		}

		// Get the fertalization amount.
		float fertalizationAmount = json.get("fertilization_chance").getAsFloat();

		// Create the recipe.
		return new FertalizerRecipe(recipeId, fluidInput, fertalizationAmount);
	}

	@Override
	public FertalizerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidStack fluid = buffer.readFluidStack();
		float fertilizer = buffer.readFloat();
		// Create the recipe.
		return new FertalizerRecipe(recipeId, fluid, fertilizer);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FertalizerRecipe recipe) {
		buffer.writeFluidStack(recipe.getRequiredFluid());
		buffer.writeFloat(recipe.getFertalizationAmount());
	}
}
