package theking530.staticpower.data.crafting.wrappers.autosmith;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.api.smithingattributes.AbstractAttributeModifier;
import theking530.api.smithingattributes.AttributeModifierRegistry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.powered.autosmith.TileEntityAutoSmith;

public class AutoSmithRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AutoSmithRecipe> {
	public static final AutoSmithRecipeSerializer INSTANCE = new AutoSmithRecipeSerializer();

	private AutoSmithRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "auto_smith_recipe"));
	}

	@Override
	public AutoSmithRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the smith target.
		JsonObject smithingTargetObject = JSONUtils.getJsonObject(json, "smith_target");
		StaticPowerIngredient smithingTarget = StaticPowerIngredient.deserialize(smithingTargetObject);

		// Capture the optional material.
		StaticPowerIngredient modifierMaterial = StaticPowerIngredient.EMPTY;
		if (JSONUtils.hasField(json, "modifier_item")) {
			JsonObject modifierItem = JSONUtils.getJsonObject(json, "modifier_item");
			modifierMaterial = StaticPowerIngredient.deserialize(modifierItem);
		}

		// Capture the optional fluid.
		FluidStack modifiedFlid = FluidStack.EMPTY;
		if (JSONUtils.hasField(json, "modifier_fluid")) {
			JsonObject fluidObject = JSONUtils.getJsonObject(json, "modifier_fluid");
			modifiedFlid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);
		}

		// Get the modifiers.
		JsonArray attributeModifiers = JSONUtils.getJsonArray(json, "attributes");
		AbstractAttributeModifier[] modifiers = new AbstractAttributeModifier[attributeModifiers.size()];
		for (int i = 0; i < attributeModifiers.size(); i++) {
			modifiers[i] = AttributeModifierRegistry.createInstance(attributeModifiers.get(i).getAsJsonObject());
		}

		// Start with the default values.
		int powerCost = TileEntityAutoSmith.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityAutoSmith.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Create the recipe.
		return new AutoSmithRecipe(recipeId, smithingTarget, modifierMaterial, modifiedFlid, modifiers, powerCost, processingTime);
	}

	@Override
	public AutoSmithRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// Read the processing times.
		int power = buffer.readInt();
		int time = buffer.readInt();

		// Read the input item, modifier, and fluid.
		StaticPowerIngredient smithingTarget = StaticPowerIngredient.read(buffer);
		StaticPowerIngredient modifierMaterial = StaticPowerIngredient.read(buffer);
		FluidStack fluidInput = buffer.readFluidStack();

		// Read the modifiers length.
		int modifierCount = buffer.readInt();

		// Read the modifiers.
		AbstractAttributeModifier[] modifiers = new AbstractAttributeModifier[modifierCount];
		for (int i = 0; i < modifierCount; i++) {
			modifiers[i] = AttributeModifierRegistry.createInstance(buffer.readCompoundTag());
		}

		// Create the recipe.
		return new AutoSmithRecipe(recipeId, smithingTarget, modifierMaterial, fluidInput, modifiers, power, time);
	}

	@Override
	public void write(PacketBuffer buffer, AutoSmithRecipe recipe) {
		// Write the processing costs.
		buffer.writeInt(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());

		// Write the input item, modifier, and fluid.
		recipe.getSmithTarget().write(buffer);
		recipe.getModifierMaterial().write(buffer);
		buffer.writeFluidStack(recipe.getModifierFluid());

		// Write the modifiers length.
		buffer.writeInt(recipe.getModifiers().length);

		// Write the modifiers.
		for (AbstractAttributeModifier modifier : recipe.getModifiers()) {
			buffer.writeCompoundTag(modifier.write());
		}
	}
}
