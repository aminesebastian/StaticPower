package theking530.staticpower.data.crafting.wrappers.fluidgenerator;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class FluidGeneratorRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FluidGeneratorRecipe> {
	public static final FluidGeneratorRecipeSerializer INSTANCE = new FluidGeneratorRecipeSerializer();

	private FluidGeneratorRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "fluid_generator_recipe"));
	}

	@Override
	public FluidGeneratorRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the contained fluid.
		JsonObject fluidObject = json.get("fluid").getAsJsonObject();
		FluidStack containedFluid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);

		// Capture the generated power.
		int powerAmount = json.get("power").getAsInt();

		// Create the recipe.
		return new FluidGeneratorRecipe(recipeId, containedFluid, powerAmount);
	}

	@Override
	public FluidGeneratorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int power = buffer.readInt();
		FluidStack fluid = buffer.readFluidStack();
		// Create the recipe.
		return new FluidGeneratorRecipe(recipeId, fluid, power);
	}

	@Override
	public void write(PacketBuffer buffer, FluidGeneratorRecipe recipe) {
		buffer.writeInt(recipe.getPowerGeneration());
		buffer.writeFluidStack(recipe.getFluid());
	}
}
