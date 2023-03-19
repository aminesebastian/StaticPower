package theking530.staticpower.data.crafting.wrappers.former;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;

public class FormerRecipeSerializer extends StaticPowerRecipeSerializer<FormerRecipe> {
	@Override
	public Codec<FormerRecipe> getCodec() {
		return FormerRecipe.CODEC;
	}

	@Override
	public FormerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerIngredient mold = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new FormerRecipe(recipeId, input, mold, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FormerRecipe recipe) {
		recipe.getInputIngredient().writeToBuffer(buffer);
		recipe.getRequiredMold().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
