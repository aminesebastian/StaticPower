package theking530.staticpower.data.crafting.wrappers.condensation;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.powered.former.TileEntityFormer;
import theking530.staticpower.utilities.Reference;

public class CondensationRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CondensationRecipe> {
	public static final CondensationRecipeSerializer INSTANCE = new CondensationRecipeSerializer();

	private CondensationRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "condensation_recipe"));
	}

	@Override
	public CondensationRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		JsonObject inputFluidObject = JSONUtils.getJsonObject(json, "input_fluid");
		FluidStack inputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluidObject);

		// Capture the output fluid.
		JsonObject outputFluidObject = JSONUtils.getJsonObject(json, "output_fluid");
		FluidStack outputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(outputFluidObject);

		// Start with the default processing values.
		int powerCost = TileEntityFormer.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityFormer.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("time").getAsInt();
			processingTime = processingElement.get("power").getAsInt();
		}

		// Create the recipe.
		return new CondensationRecipe(recipeId, inputFluid, outputFluid, processingTime, powerCost);
	}

	@Override
	public CondensationRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, CondensationRecipe recipe) {
		// TODO Auto-generated method stub

	}
}
