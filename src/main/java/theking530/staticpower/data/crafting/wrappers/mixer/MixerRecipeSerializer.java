package theking530.staticpower.data.crafting.wrappers.mixer;

import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class MixerRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MixerRecipe> {
	public static final MixerRecipeSerializer INSTANCE = new MixerRecipeSerializer();

	private MixerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "mixer_recipe"));
	}

	@Override
	public MixerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		StaticPowerIngredient input1 = StaticPowerIngredient.EMPTY;
		StaticPowerIngredient input2 = StaticPowerIngredient.EMPTY;
		FluidStack fluidInput1 = FluidStack.EMPTY;
		FluidStack fluidInput2 = FluidStack.EMPTY;

		// Capture the input ingredients.
		if (json.has("item_input_1")) {
			input1 = StaticPowerIngredient.deserialize(json.get("item_input_1"));
		}
		if (json.has("item_input_2")) {
			input2 = StaticPowerIngredient.deserialize(json.get("item_input_2"));
		}

		// Capture the input fluids.
		if (json.has("fluid_input_1")) {
			fluidInput1 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("fluid_input_1"));
		}
		if (json.has("fluid_input_2")) {
			fluidInput2 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("fluid_input_2"));
		}

		// Start with the default values.
		long powerCost = StaticPowerConfig.SERVER.mixerPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.mixerProcessingTime.get();

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the fluid result.
		FluidStack output = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("result"));

		// Create the recipe.
		return new MixerRecipe(recipeId, input1, input2, fluidInput1, fluidInput2, output, processingTime, powerCost);
	}

	@Override
	public MixerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();

		StaticPowerIngredient input1 = StaticPowerIngredient.read(buffer);
		StaticPowerIngredient input2 = StaticPowerIngredient.read(buffer);
		FluidStack fluidInput1 = buffer.readFluidStack();
		FluidStack fluidInput2 = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();

		// Create the recipe.
		return new MixerRecipe(recipeId, input1, input2, fluidInput1, fluidInput2, output, time, power);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, MixerRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getPrimaryItemInput().write(buffer);
		recipe.getSecondaryItemInput().write(buffer);
		buffer.writeFluidStack(recipe.getPrimaryFluidInput());
		buffer.writeFluidStack(recipe.getSecondaryFluidInput());
		buffer.writeFluidStack(recipe.getOutput());
	}
}
