package theking530.staticpower.data.crafting.wrappers.autosmith;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe.RecipeModifierWrapper;

public class AutoSmithRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<AutoSmithRecipe> {
	public static final AutoSmithRecipeSerializer INSTANCE = new AutoSmithRecipeSerializer();

	private AutoSmithRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "auto_smith_recipe"));
	}

	@Override
	public AutoSmithRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the smith target.
		StaticPowerIngredient smithingTarget = StaticPowerIngredient.EMPTY;
		if (GsonHelper.isValidNode(json, "smith_target")) {
			JsonObject smithingTargetObject = GsonHelper.getAsJsonObject(json, "smith_target");
			smithingTarget = StaticPowerIngredient.deserialize(smithingTargetObject);
		}

		// Capture the optional material.
		StaticPowerIngredient modifierMaterial = StaticPowerIngredient.EMPTY;
		if (GsonHelper.isValidNode(json, "modifier_item")) {
			JsonObject modifierItem = GsonHelper.getAsJsonObject(json, "modifier_item");
			modifierMaterial = StaticPowerIngredient.deserialize(modifierItem);
		}

		// Capture the optional fluid.
		FluidStack modifiedFlid = FluidStack.EMPTY;
		if (GsonHelper.isValidNode(json, "modifier_fluid")) {
			JsonObject fluidObject = GsonHelper.getAsJsonObject(json, "modifier_fluid");
			modifiedFlid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);
		}

		// Get the modifiers.
		RecipeModifierWrapper[] modifiers;
		if (GsonHelper.isValidNode(json, "attributes")) {
			JsonArray attributeModifiers = GsonHelper.getAsJsonArray(json, "attributes");
			modifiers = new RecipeModifierWrapper[attributeModifiers.size()];
			for (int i = 0; i < attributeModifiers.size(); i++) {
				modifiers[i] = new RecipeModifierWrapper(attributeModifiers.get(i).getAsJsonObject());
			}
		} else {
			modifiers = new RecipeModifierWrapper[0];
		}

		// Start with the default values.
		long powerCost = StaticPowerConfig.SERVER.autoSmithPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.autoSmithProcessingTime.get();
		int repairAmount = 0;

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Capture the repair amount if provided.
		if (GsonHelper.isValidNode(json, "repair_amount")) {
			repairAmount = GsonHelper.getAsInt(json, "repair_amount");
		}

		// Create the recipe.
		return new AutoSmithRecipe(recipeId, smithingTarget, modifierMaterial, modifiedFlid, modifiers, repairAmount, powerCost, processingTime);
	}

	@Override
	public AutoSmithRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		// Read the processing times.
		long power = buffer.readLong();
		int time = buffer.readInt();
		int repairAmount = buffer.readInt();

		// Read the input item, modifier, and fluid.
		StaticPowerIngredient smithingTarget = StaticPowerIngredient.read(buffer);
		StaticPowerIngredient modifierMaterial = StaticPowerIngredient.read(buffer);
		FluidStack fluidInput = buffer.readFluidStack();

		// Read the modifiers length.
		int modifierCount = buffer.readInt();

		// Read the modifiers.
		RecipeModifierWrapper[] modifiers = new RecipeModifierWrapper[modifierCount];
		for (int i = 0; i < modifierCount; i++) {
			modifiers[i] = new RecipeModifierWrapper(buffer);
		}

		// Create the recipe.
		return new AutoSmithRecipe(recipeId, smithingTarget, modifierMaterial, fluidInput, modifiers, repairAmount, power, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, AutoSmithRecipe recipe) {
		// Write the processing costs.
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeInt(recipe.getRepairAmount());

		// Write the input item, modifier, and fluid.
		recipe.getSmithTarget().write(buffer);
		recipe.getModifierMaterial().write(buffer);
		buffer.writeFluidStack(recipe.getModifierFluid());

		// Write the modifiers length.
		buffer.writeInt(recipe.getModifiers().length);

		// Write the modifiers.
		for (RecipeModifierWrapper modifier : recipe.getModifiers()) {
			buffer.writeNbt(modifier.serialize());
		}
	}
}
