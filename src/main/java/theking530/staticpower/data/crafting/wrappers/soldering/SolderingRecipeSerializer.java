package theking530.staticpower.data.crafting.wrappers.soldering;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class SolderingRecipeSerializer extends StaticPowerRecipeSerializer<SolderingRecipe> {

	@Override
	public Codec<SolderingRecipe> getCodec() {
		return SolderingRecipe.CODEC;
	}

	public SolderingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int width = buffer.readVarInt();
		int height = buffer.readVarInt();
		StaticPowerIngredient solderingIron = StaticPowerIngredient.readFromBuffer(buffer);

		NonNullList<StaticPowerIngredient> nonnulllist = NonNullList.withSize(width * height, StaticPowerIngredient.EMPTY);
		for (int i = 0; i < nonnulllist.size(); i++) {
			nonnulllist.set(i, StaticPowerIngredient.readFromBuffer(buffer));
		}

		StaticPowerOutputItem itemstack = StaticPowerOutputItem.readFromBuffer(buffer);
		return new SolderingRecipe(recipeId, width, height, Optional.of(solderingIron), nonnulllist, itemstack);
	}

	public void toNetwork(FriendlyByteBuf buffer, SolderingRecipe recipe) {
		buffer.writeVarInt(recipe.getRecipeWidth());
		buffer.writeVarInt(recipe.getRecipeHeight());
		recipe.getSolderingIron().writeToBuffer(buffer);
		for (StaticPowerIngredient ingredient : recipe.getInputs()) {
			ingredient.writeToBuffer(buffer);
		}
		recipe.getOutput().writeToBuffer(buffer);
	}
}