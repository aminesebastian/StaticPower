package theking530.staticpower.data.crafting.wrappers.centrifuge;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class CentrifugeRecipeSerializer extends StaticPowerRecipeSerializer<CentrifugeRecipe> {
	@Override
	public Codec<CentrifugeRecipe> getCodec() {
		return CentrifugeRecipe.CODEC;
	}

	@Override
	public CentrifugeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int speed = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output1 = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem output2 = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem output3 = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new CentrifugeRecipe(recipeId, input, output1, output2, output3, speed, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CentrifugeRecipe recipe) {
		buffer.writeInt(recipe.getMinimumSpeed());
		recipe.getInput().writeToBuffer(buffer);
		recipe.getOutput1().writeToBuffer(buffer);
		recipe.getOutput2().writeToBuffer(buffer);
		recipe.getOutput3().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
