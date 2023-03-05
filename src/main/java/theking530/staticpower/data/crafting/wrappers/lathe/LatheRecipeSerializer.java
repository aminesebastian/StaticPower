package theking530.staticpower.data.crafting.wrappers.lathe;

import com.mojang.serialization.Codec;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class LatheRecipeSerializer extends StaticPowerRecipeSerializer<LatheRecipe> {
	@Override
	public Codec<LatheRecipe> getCodec() {
		return LatheRecipe.CODEC;
	}

	@Override
	public LatheRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int width = buffer.readVarInt();
		int height = buffer.readVarInt();

		NonNullList<StaticPowerIngredient> nonnulllist = NonNullList.withSize(width * height, StaticPowerIngredient.EMPTY);
		for (int k = 0; k < nonnulllist.size(); ++k) {
			nonnulllist.set(k, StaticPowerIngredient.readFromBuffer(buffer));
		}

		StaticPowerOutputItem primary = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem secondary = StaticPowerOutputItem.readFromBuffer(buffer);
		FluidStack outFluid = buffer.readFluidStack();

		return new LatheRecipe(recipeId, width, height, nonnulllist, primary, secondary, outFluid, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, LatheRecipe recipe) {
		buffer.writeVarInt(recipe.recipeWidth);
		buffer.writeVarInt(recipe.recipeHeight);

		for (StaticPowerIngredient ing : recipe.getInputs()) {
			ing.writeToBuffer(buffer);
		}

		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
