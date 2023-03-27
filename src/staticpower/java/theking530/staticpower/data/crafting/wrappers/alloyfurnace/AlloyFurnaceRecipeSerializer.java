package theking530.staticpower.data.crafting.wrappers.alloyfurnace;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;

public class AlloyFurnaceRecipeSerializer extends StaticPowerRecipeSerializer<AlloyFurnaceRecipe> {
	@Override
	public Codec<AlloyFurnaceRecipe> getCodec() {
		return AlloyFurnaceRecipe.CODEC;
	}

	@Override
	public AlloyFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input1 = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerIngredient input2 = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);
		float experience = buffer.readFloat();
		return new AlloyFurnaceRecipe(recipeId, input1, input2, output, experience, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, AlloyFurnaceRecipe recipe) {
		recipe.getInput1().writeToBuffer(buffer);
		recipe.getInput2().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
		buffer.writeFloat(recipe.getExperience());
	}
}
