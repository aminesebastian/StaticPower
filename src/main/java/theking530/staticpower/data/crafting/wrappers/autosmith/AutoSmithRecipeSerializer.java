package theking530.staticpower.data.crafting.wrappers.autosmith;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe.RecipeModifierWrapper;

public class AutoSmithRecipeSerializer extends StaticPowerRecipeSerializer<AutoSmithRecipe> {
	public static final AutoSmithRecipeSerializer INSTANCE = new AutoSmithRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "auto_smith_recipe");

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

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.autoSmithProcessingTime.get(), StaticPowerConfig.SERVER.autoSmithPowerUsage.get(),
				json);

		// Capture the repair amount if provided.
		int repairAmount = 0;
		if (GsonHelper.isValidNode(json, "repair_amount")) {
			repairAmount = GsonHelper.getAsInt(json, "repair_amount");
		}

		// Create the recipe.
		return new AutoSmithRecipe(recipeId, smithingTarget, modifierMaterial, modifiedFlid, modifiers, repairAmount, processing);
	}

	@Override
	public AutoSmithRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		// Read the processing times.
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
		return new AutoSmithRecipe(recipeId, smithingTarget, modifierMaterial, fluidInput, modifiers, repairAmount, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, AutoSmithRecipe recipe) {
		// Write the processing costs.
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

		recipe.getProcessingSection().writeToBuffer(buffer);
	}

	@Override
	public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
		return INSTANCE;
	}
	
	@Override
	public ResourceLocation getRegistryName() {
		return ID;
	}
}
