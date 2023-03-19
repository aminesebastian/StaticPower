package theking530.staticpower.data.crafting.wrappers.bottler;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticcore.fluid.FluidIngredient;

public class BottlerRecipeSerializer extends StaticPowerRecipeSerializer<BottleRecipe> {
	@Override
	public Codec<BottleRecipe> getCodec() {
		return BottleRecipe.CODEC;
	}

	@Override
	public BottleRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient emptyBottle = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem filledBottle = StaticPowerOutputItem.readFromBuffer(buffer);
		FluidIngredient fluid = FluidIngredient.readFromBuffer(buffer);
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);
		return new BottleRecipe(recipeId, emptyBottle, filledBottle, fluid, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, BottleRecipe recipe) {
		recipe.getEmptyBottle().writeToBuffer(buffer);
		recipe.getFilledBottle().writeToBuffer(buffer);
		recipe.getFluid().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
