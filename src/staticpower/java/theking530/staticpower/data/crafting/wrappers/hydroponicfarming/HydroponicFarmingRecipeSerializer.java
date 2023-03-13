package theking530.staticpower.data.crafting.wrappers.hydroponicfarming;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class HydroponicFarmingRecipeSerializer extends StaticPowerRecipeSerializer<HydroponicFarmingRecipe> {
	@Override
	public Codec<HydroponicFarmingRecipe> getCodec() {
		return HydroponicFarmingRecipe.CODEC;
	}

	@Override
	public HydroponicFarmingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		return new HydroponicFarmingRecipe(recipeId, input, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, HydroponicFarmingRecipe recipe) {
		recipe.getInput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
