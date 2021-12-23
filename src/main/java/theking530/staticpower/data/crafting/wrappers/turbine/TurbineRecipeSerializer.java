package theking530.staticpower.data.crafting.wrappers.turbine;

import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class TurbineRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<TurbineRecipe> {
	public static final TurbineRecipeSerializer INSTANCE = new TurbineRecipeSerializer();

	private TurbineRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "turbine_recipe"));
	}

	@Override
	public TurbineRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Deserialize the fluid input..
		FluidStack fluidInput = FluidStack.EMPTY;
		if (GsonHelper.isValidNode(json, "input")) {
			fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(json, "input"));
		}

		// Deserialize the fluid output if it exists.
		FluidStack fluidOutput = FluidStack.EMPTY;
		if (GsonHelper.isValidNode(json, "output")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(json, "output"));
		}

		// Get the generation amount.
		int generationAmount = json.get("generation").getAsInt();

		return new TurbineRecipe(recipeId, fluidInput, fluidOutput, generationAmount);
	}

	@Override
	public TurbineRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidStack input = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();
		int generation = buffer.readInt();
		return new TurbineRecipe(recipeId, input, output, generation);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, TurbineRecipe recipe) {
		buffer.writeFluidStack(recipe.getInput());
		buffer.writeFluidStack(recipe.getOutput());
		buffer.writeInt(recipe.getGenerationAmount());
	}
}
