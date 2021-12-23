package theking530.staticpower.data.crafting.wrappers.bottler;

import com.google.gson.JsonObject;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class BottlerRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BottleRecipe> {
	public static final BottlerRecipeSerializer INSTANCE = new BottlerRecipeSerializer();

	private BottlerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "bottler_recipe"));
	}

	@Override
	public BottleRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the empty bottle.
		JsonObject emptyBottleObject = GsonHelper.getAsJsonObject(json, "empty_bottle");
		ItemStack emptyBottle = StaticPowerJsonParsingUtilities.parseItemWithNbt(emptyBottleObject);

		// Capture the empty bottle.
		JsonObject filledBottleObject = GsonHelper.getAsJsonObject(json, "filled_bottle");
		ItemStack filledBottle = StaticPowerJsonParsingUtilities.parseItemWithNbt(filledBottleObject);

		// Capture the contained fluid.
		JsonObject fluidObject = GsonHelper.getAsJsonObject(json, "fluid");
		FluidStack containedFluid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);

		// Create the recipe.
		return new BottleRecipe(recipeId, filledBottle, emptyBottle, containedFluid);
	}

	@Override
	public BottleRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		ItemStack empty = buffer.readItem();
		ItemStack filled = buffer.readItem();
		FluidStack fluid = buffer.readFluidStack();
		// Create the recipe.
		return new BottleRecipe(recipeId, filled, empty, fluid);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, BottleRecipe recipe) {
		buffer.writeItem(recipe.getEmptyBottle());
		buffer.writeItem(recipe.getFilledBottle());
		buffer.writeFluidStack(recipe.getFluid());
	}
}
