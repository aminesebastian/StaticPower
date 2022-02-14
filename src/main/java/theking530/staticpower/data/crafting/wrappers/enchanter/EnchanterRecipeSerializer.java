package theking530.staticpower.data.crafting.wrappers.enchanter;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.EnchantmentRecipeWrapper;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class EnchanterRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<EnchanterRecipe> {
	public static final EnchanterRecipeSerializer INSTANCE = new EnchanterRecipeSerializer();

	private EnchanterRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "enchanter_recipe"));
	}

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

		// Start with the default values.
		long powerCost = StaticPowerConfig.SERVER.cruciblePowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.crucibleProcessingTime.get();

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Create the recipe.
		return new EnchanterRecipe(recipeId, inputs, fluidInput, processingTime, powerCost, enchantments);
	}

	@Override
	public EnchanterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
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

		return new EnchanterRecipe(recipeId, inputs, inputFluid, time, power, enchantments);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, EnchanterRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
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
	}
}
