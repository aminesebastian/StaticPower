package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class ThermalConductivityRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ThermalConductivityRecipe> {
	public static final ThermalConductivityRecipeSerializer INSTANCE = new ThermalConductivityRecipeSerializer();

	private ThermalConductivityRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "thermal_conducitity"));
	}

	@Override
	public ThermalConductivityRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Allocate the blocks.
		ResourceLocation[] blocks = null;

		// Check for blocks.
		if (json.has("blocks")) {
			// Get the block tag array.
			JsonArray blocksArray = json.get("blocks").getAsJsonArray();
			blocks = new ResourceLocation[blocksArray.size()];
			for (int i = 0; i < blocks.length; i++) {
				blocks[i] = new ResourceLocation(blocksArray.get(i).getAsString());
			}
		}

		// Get the fluid stack if it exists.
		FluidStack fluid = FluidStack.EMPTY;
		if (json.has("fluid")) {
			JsonObject fluidObject = json.get("fluid").getAsJsonObject();
			fluid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);
		}

		// Capture the conductivity.
		float thermalConductivity = json.get("conductivity").getAsFloat();

		// Capture the heating amount.
		float heatAmount = 0.0f;
		if (json.has("heating_amount")) {
			heatAmount = json.get("heating_amount").getAsFloat();
		}

		// Create the recipe.
		return new ThermalConductivityRecipe(recipeId, blocks, fluid, thermalConductivity, heatAmount);
	}

	@Override
	public ThermalConductivityRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		FluidStack fluid = buffer.readFluidStack();
		float cond = buffer.readFloat();
		float supply = buffer.readFloat();
		int tagCount = buffer.readByte();

		ResourceLocation[] blocks = new ResourceLocation[tagCount];
		for (int i = 0; i < tagCount; i++) {
			blocks[i] = new ResourceLocation(buffer.readString());
		}

		return new ThermalConductivityRecipe(recipeId, blocks, fluid, cond, supply);
	}

	@Override
	public void write(PacketBuffer buffer, ThermalConductivityRecipe recipe) {
		buffer.writeFluidStack(recipe.getFluid());
		buffer.writeFloat(recipe.getThermalConductivity());
		buffer.writeFloat(recipe.getHeatAmount());

		buffer.writeByte(recipe.getBlockTags().length);
		for (ResourceLocation tag : recipe.getBlockTags()) {
			buffer.writeString(tag.toString());
		}
	}
}
