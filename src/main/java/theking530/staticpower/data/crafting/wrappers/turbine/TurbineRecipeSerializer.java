package theking530.staticpower.data.crafting.wrappers.turbine;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class TurbineRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TurbineRecipe> {
	public static final TurbineRecipeSerializer INSTANCE = new TurbineRecipeSerializer();

	private TurbineRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "turbine_recipe"));
	}

	@Override
	public TurbineRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Deserialize the fluid input..
		FluidStack fluidInput = FluidStack.EMPTY;
		if (JSONUtils.hasField(json, "input")) {
			fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(JSONUtils.getJsonObject(json, "input"));
		}

		// Deserialize the fluid output if it exists.
		FluidStack fluidOutput = FluidStack.EMPTY;
		if (JSONUtils.hasField(json, "output")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(JSONUtils.getJsonObject(json, "output"));
		}

		// Get the generation amount.
		int generationAmount = json.get("generation").getAsInt();

		return new TurbineRecipe(recipeId, fluidInput, fluidOutput, generationAmount);
	}

	@Override
	public TurbineRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		FluidStack input = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();
		int generation = buffer.readInt();
		return new TurbineRecipe(recipeId, input, output, generation);
	}

	@Override
	public void write(PacketBuffer buffer, TurbineRecipe recipe) {
		buffer.writeFluidStack(recipe.getInput());
		buffer.writeFluidStack(recipe.getOutput());
		buffer.writeInt(recipe.getGenerationAmount());
	}
}
