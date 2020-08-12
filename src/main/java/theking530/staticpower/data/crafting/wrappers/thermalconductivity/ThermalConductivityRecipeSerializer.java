package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.utilities.Reference;

public class ThermalConductivityRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ThermalConductivityRecipe> {
	public static final ThermalConductivityRecipeSerializer INSTANCE = new ThermalConductivityRecipeSerializer();

	private ThermalConductivityRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "thermal_conducitity"));
	}

	@Override
	public ThermalConductivityRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input blocks if it exists.
		Ingredient blocks = Ingredient.EMPTY;
		if (json.has("block")) {
			JsonObject inputElement = json.get("block").getAsJsonObject();
			blocks = Ingredient.deserialize(inputElement);
		}

		// Get the fluid stack if it exists.
		FluidStack fluid = FluidStack.EMPTY;
		if (json.has("fluid")) {
			JsonObject fluidObject = json.get("fluid").getAsJsonObject();
			fluid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);
		}

		// Capture the conductivity.
		float thermalConductivity = json.get("conductivity").getAsFloat();

		// Create the recipe.
		return new ThermalConductivityRecipe(recipeId, blocks, fluid, thermalConductivity);
	}

	@Override
	public ThermalConductivityRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, ThermalConductivityRecipe recipe) {
	}
}