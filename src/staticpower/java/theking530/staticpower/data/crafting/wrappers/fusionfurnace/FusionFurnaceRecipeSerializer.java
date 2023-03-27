package theking530.staticpower.data.crafting.wrappers.fusionfurnace;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;

public class FusionFurnaceRecipeSerializer extends StaticPowerRecipeSerializer<FusionFurnaceRecipe> {
	@Override
	public Codec<FusionFurnaceRecipe> getCodec() {
		return FusionFurnaceRecipe.CODEC;
	}

	@Override
	public FusionFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int inputCount = buffer.readByte();
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();
		for (int i = 0; i < inputCount; i++) {
			inputs.add(StaticPowerIngredient.readFromBuffer(buffer));
		}

		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);

		// Craete the recipe.
		return new FusionFurnaceRecipe(recipeId, inputs, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FusionFurnaceRecipe recipe) {
		buffer.writeByte(recipe.getInputs().size());

		for (StaticPowerIngredient ing : recipe.getInputs()) {
			ing.writeToBuffer(buffer);
		}

		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
