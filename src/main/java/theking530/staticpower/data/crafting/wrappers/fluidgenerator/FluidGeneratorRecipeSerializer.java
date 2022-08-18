package theking530.staticpower.data.crafting.wrappers.fluidgenerator;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class FluidGeneratorRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FluidGeneratorRecipe> {
	public static final FluidGeneratorRecipeSerializer INSTANCE = new FluidGeneratorRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "fluid_generator_recipe");

	@Override
	public FluidGeneratorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the contained fluid.
		JsonObject fluidObject = json.get("fluid").getAsJsonObject();
		FluidStack containedFluid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);

		// Capture the generated power.
		int powerAmount = json.get("power").getAsInt();

		// Create the recipe.
		return new FluidGeneratorRecipe(recipeId, containedFluid, powerAmount);
	}

	@Override
	public FluidGeneratorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int power = buffer.readInt();
		FluidStack fluid = buffer.readFluidStack();
		// Create the recipe.
		return new FluidGeneratorRecipe(recipeId, fluid, power);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FluidGeneratorRecipe recipe) {
		buffer.writeInt(recipe.getPowerGeneration());
		buffer.writeFluidStack(recipe.getFluid());
	}
}
