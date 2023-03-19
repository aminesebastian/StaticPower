package theking530.staticpower.data.crafting.wrappers.soldering;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.ShapedRecipePattern;

public class SolderingRecipeSerializer extends StaticPowerRecipeSerializer<SolderingRecipe> {

	@Override
	public Codec<SolderingRecipe> getCodec() {
		return SolderingRecipe.CODEC;
	}

	@Override
	public SolderingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		ShapedRecipePattern pattern = ShapedRecipePattern.fromNetwork(buffer);
		Ingredient solderingIron = Ingredient.fromNetwork(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);

		return new SolderingRecipe(recipeId, pattern, Optional.of(solderingIron), output);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SolderingRecipe recipe) {
		recipe.getPattern().toNetwork(buffer);
		recipe.getSolderingIron().toNetwork(buffer);
		recipe.getOutput().writeToBuffer(buffer);
	}
}