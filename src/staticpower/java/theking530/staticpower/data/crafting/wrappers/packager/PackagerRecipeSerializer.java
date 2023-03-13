package theking530.staticpower.data.crafting.wrappers.packager;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class PackagerRecipeSerializer extends StaticPowerRecipeSerializer<PackagerRecipe> {
	public static final PackagerRecipeSerializer INSTANCE = new PackagerRecipeSerializer();

	@Override
	public Codec<PackagerRecipe> getCodec() {
		return PackagerRecipe.CODEC;
	}

	@Override
	public PackagerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int size = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem outputs = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new PackagerRecipe(recipeId, size, input, outputs, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, PackagerRecipe recipe) {
		buffer.writeInt(recipe.getSize());
		recipe.getInputIngredient().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
