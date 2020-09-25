package theking530.staticpower.data.crafting.wrappers.bottler;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class BottlerRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BottleRecipe> {
	public static final BottlerRecipeSerializer INSTANCE = new BottlerRecipeSerializer();

	private BottlerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "bottler_recipe"));
	}

	@Override
	public BottleRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the empty bottle.
		JsonObject emptyBottleObject = JSONUtils.getJsonObject(json, "empty_bottle");
		ItemStack emptyBottle = StaticPowerJsonParsingUtilities.parseItemWithNbt(emptyBottleObject);

		// Capture the empty bottle.
		JsonObject filledBottleObject = JSONUtils.getJsonObject(json, "filled_bottle");
		ItemStack filledBottle = StaticPowerJsonParsingUtilities.parseItemWithNbt(filledBottleObject);

		// Capture the contained fluid.
		JsonObject fluidObject = JSONUtils.getJsonObject(json, "fluid");
		FluidStack containedFluid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);

		// Create the recipe.
		return new BottleRecipe(recipeId, filledBottle, emptyBottle, containedFluid);
	}

	@Override
	public BottleRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		ItemStack empty = buffer.readItemStack();
		ItemStack filled = buffer.readItemStack();
		FluidStack fluid = buffer.readFluidStack();
		// Create the recipe.
		return new BottleRecipe(recipeId, filled, empty, fluid);
	}

	@Override
	public void write(PacketBuffer buffer, BottleRecipe recipe) {
		buffer.writeItemStack(recipe.getEmptyBottle());
		buffer.writeItemStack(recipe.getFilledBottle());
		buffer.writeFluidStack(recipe.getFluid());
	}
}
