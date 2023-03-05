package theking530.staticpower.data.crafting.wrappers.vulcanizer;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class VulcanizerRecipeSerializer extends StaticPowerRecipeSerializer<VulcanizerRecipe> {

	@Override
	public Codec<VulcanizerRecipe> getCodec() {
		return VulcanizerRecipe.CODEC;
	}

	@Override
	public VulcanizerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidIngredient input = FluidIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);
		return new VulcanizerRecipe(recipeId, input, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, VulcanizerRecipe recipe) {
		recipe.getInputFluid().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
