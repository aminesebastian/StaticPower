package theking530.staticpower.data.crafting.wrappers.grinder;

import java.util.LinkedList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;

public class GrinderRecipeSerializer extends StaticPowerRecipeSerializer<GrinderRecipe> {
	@Override
	public Codec<GrinderRecipe> getCodec() {
		return GrinderRecipe.CODEC;
	}

	@Override
	public GrinderRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		int outputCount = buffer.readByte();
		List<StaticPowerOutputItem> outputs = new LinkedList<StaticPowerOutputItem>();
		for (int i = 0; i < outputCount; i++) {
			outputs.add(StaticPowerOutputItem.readFromBuffer(buffer));
		}
		return new GrinderRecipe(recipeId, input, MachineRecipeProcessingSection.fromBuffer(buffer), outputs);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, GrinderRecipe recipe) {
		recipe.getInputIngredient().writeToBuffer(buffer);
		buffer.writeByte(recipe.getOutputItems().size());
		for (int i = 0; i < recipe.getOutputItems().size(); i++) {
			recipe.getOutputItems().get(i).writeToBuffer(buffer);
		}
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
