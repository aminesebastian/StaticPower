package theking530.staticpower.data.crafting.wrappers.autosmith;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe.RecipeModifierWrapper;

public class AutoSmithRecipeSerializer extends StaticPowerRecipeSerializer<AutoSmithRecipe> {
	@Override
	public Codec<AutoSmithRecipe> getCodec() {
		return AutoSmithRecipe.CODEC;
	}

	@Override
	public AutoSmithRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		// Read the processing times.
		int repairAmount = buffer.readInt();

		// Read the input item, modifier, and fluid.
		StaticPowerIngredient smithingTarget = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerIngredient modifierMaterial = StaticPowerIngredient.readFromBuffer(buffer);
		FluidIngredient fluidInput = FluidIngredient.readFromBuffer(buffer);

		// Read the modifiers length.
		int modifierCount = buffer.readInt();

		// Read the modifiers.
		List<RecipeModifierWrapper> modifiers = new ArrayList<RecipeModifierWrapper>();
		for (int i = 0; i < modifierCount; i++) {
			modifiers.add(RecipeModifierWrapper.readFromBuffer(buffer));
		}

		// Create the recipe.
		return new AutoSmithRecipe(recipeId, smithingTarget, modifierMaterial, fluidInput, modifiers, repairAmount, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, AutoSmithRecipe recipe) {
		// Write the processing costs.
		buffer.writeInt(recipe.getRepairAmount());

		// Write the input item, modifier, and fluid.
		recipe.getSmithTarget().writeToBuffer(buffer);
		recipe.getModifierMaterial().writeToBuffer(buffer);
		recipe.getModifierFluid().writeToBuffer(buffer);

		// Write the modifiers length.
		buffer.writeInt(recipe.getModifiers().size());

		// Write the modifiers.
		for (RecipeModifierWrapper modifier : recipe.getModifiers()) {
			modifier.writeToBuffer(buffer);
		}

		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
