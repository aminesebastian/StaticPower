package theking530.staticpower.data.crafting.wrappers.fermenter;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.wrappers.StaticPowerJsonParsingUtilities;
import theking530.staticpower.utilities.Reference;

public class FermenterRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FermenterRecipe> {
	public static final FermenterRecipeSerializer INSTANCE = new FermenterRecipeSerializer();

	private FermenterRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "fermenter_recipe"));
	}

	@Override
	public FermenterRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		Ingredient input = Ingredient.deserialize(inputElement);

		// Get the fluid output.
		JsonObject outputElement = JSONUtils.getJsonObject(json, "output");
		FluidStack fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(outputElement);

		// Create the recipe.
		return new FermenterRecipe(recipeId, input, fluidOutput);
	}

	@Override
	public FermenterRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, FermenterRecipe recipe) {
	}
}
