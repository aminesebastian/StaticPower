package theking530.staticpower.data.crafting.wrappers.farmer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class FarmingFertalizerRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FarmingFertalizerRecipe> {
	public static final FarmingFertalizerRecipeSerializer INSTANCE = new FarmingFertalizerRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(FarmingFertalizerRecipeSerializer.class);

	private FarmingFertalizerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "farming_fertalizer_recipe"));
	}

	@Override
	public FarmingFertalizerRecipe read(ResourceLocation recipeId, JsonObject json) {
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
		return new FarmingFertalizerRecipe(recipeId, fluidInput, fertalizationAmount);
	}

	@Override
	public FarmingFertalizerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		FluidStack fluid = buffer.readFluidStack();
		float fertilizer = buffer.readFloat();
		// Create the recipe.
		return new FarmingFertalizerRecipe(recipeId, fluid, fertilizer);
	}

	@Override
	public void write(PacketBuffer buffer, FarmingFertalizerRecipe recipe) {
		buffer.writeFluidStack(recipe.getRequiredFluid());
		buffer.writeFloat(recipe.getFertalizationAmount());
	}
}
