package theking530.staticpower.data.crafting.wrappers.enchanter;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.EnchantmentRecipeWrapper;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class EnchanterRecipeSerializer extends StaticPowerRecipeSerializer<EnchanterRecipe> {
	public static final EnchanterRecipeSerializer INSTANCE = new EnchanterRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "enchanter_recipe");

	@Override
	public EnchanterRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Get the fluid input.
		FluidStack fluidInput = FluidStack.EMPTY;
		if (json.has("input_fluid")) {
			JsonObject inputFluid = GsonHelper.getAsJsonObject(json, "input_fluid");
			fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluid);
		}

		// Get the inputs.
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();
		if (json.has("input_items")) {
			JsonArray inputElements = GsonHelper.getAsJsonArray(json, "input_items");
			for (JsonElement element : inputElements) {
				inputs.add(StaticPowerIngredient.deserialize(element.getAsJsonObject()));
			}
		}

		// Get the enchantments.
		List<EnchantmentRecipeWrapper> enchantments = new ArrayList<EnchantmentRecipeWrapper>();
		JsonArray enchantmentsElement = GsonHelper.getAsJsonArray(json, "enchantments");
		for (JsonElement element : enchantmentsElement) {
			enchantments.add(EnchantmentRecipeWrapper.fromJson(element.getAsJsonObject()));
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.enchanterProcessingTime,
				StaticPowerConfig.SERVER.enchanterPowerUsage, json);

		// Create the recipe.
		return new EnchanterRecipe(recipeId, inputs, fluidInput, enchantments, processing);
	}

	@Override
	public EnchanterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidStack inputFluid = buffer.readFluidStack();

		// Read all the inputs.
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();
		for (int i = 0; i < buffer.readByte(); i++) {
			inputs.add(StaticPowerIngredient.read(buffer));
		}

		// Read all the enchantments.
		List<EnchantmentRecipeWrapper> enchantments = new ArrayList<EnchantmentRecipeWrapper>();
		for (int i = 0; i < buffer.readByte(); i++) {
			enchantments.add(EnchantmentRecipeWrapper.fromBuffer(buffer));
		}

		return new EnchanterRecipe(recipeId, inputs, inputFluid, enchantments, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, EnchanterRecipe recipe) {
		buffer.writeFluidStack(recipe.getInputFluidStack());

		// Write the items.
		buffer.writeByte(recipe.getInputIngredients().size());
		for (StaticPowerIngredient ing : recipe.getInputIngredients()) {
			ing.write(buffer);
		}

		// Write the enchantments.
		buffer.writeByte(recipe.getEnchantments().size());
		for (EnchantmentRecipeWrapper wrapper : recipe.getEnchantments()) {
			wrapper.writeToBuffer(buffer);
		}
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
