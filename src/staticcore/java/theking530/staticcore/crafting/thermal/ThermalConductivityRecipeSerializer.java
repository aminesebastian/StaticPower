package theking530.staticcore.crafting.thermal;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.block.BlockStateIngredient;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.FreezingBehaviour;
import theking530.staticcore.crafting.thermal.ThermalConductivityBehaviours.OverheatingBehaviour;
import theking530.staticcore.fluid.FluidIngredient;

public class ThermalConductivityRecipeSerializer extends StaticPowerRecipeSerializer<ThermalConductivityRecipe> {
	@Override
	public Codec<ThermalConductivityRecipe> getCodec() {
		return ThermalConductivityRecipe.CODEC;
	}

	@Override
	public ThermalConductivityRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		BlockStateIngredient blocks = BlockStateIngredient.readFromBuffer(buffer);
		FluidIngredient fluids = FluidIngredient.readFromBuffer(buffer);

		boolean hasActiveTemperature = buffer.readBoolean();
		float temperature = buffer.readFloat();
		float thermalMass = buffer.readFloat();
		float specificHeat = buffer.readFloat();
		float conductivity = buffer.readFloat();

		// Read the over heat values.
		boolean hasOverheat = buffer.readBoolean();
		OverheatingBehaviour overheatBehaviour = null;
		if (hasOverheat) {
			overheatBehaviour = OverheatingBehaviour.fromNetwork(buffer);
		}

		// Read the freeze values.
		boolean hasFreezing = buffer.readBoolean();
		FreezingBehaviour freezingBehaviour = null;
		if (hasFreezing) {
			freezingBehaviour = FreezingBehaviour.fromNetwork(buffer);
		}

		return new ThermalConductivityRecipe(recipeId, blocks, fluids, hasActiveTemperature, temperature, thermalMass,
				specificHeat, conductivity, overheatBehaviour, freezingBehaviour);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, ThermalConductivityRecipe recipe) {
		recipe.getBlocks().writeToBuffer(buffer);
		recipe.getFluids().writeToBuffer(buffer);

		// Write the int values.
		buffer.writeBoolean(recipe.hasActiveTemperature());
		buffer.writeFloat(recipe.getTemperature());
		buffer.writeFloat(recipe.getMass());
		buffer.writeFloat(recipe.getSpecificHeat());
		buffer.writeFloat(recipe.getConductivity());

		// Write the over heat values.
		buffer.writeBoolean(recipe.hasOverheatingBehaviour());
		if (recipe.hasOverheatingBehaviour()) {
			recipe.getOverheatingBehaviour().toNetwork(buffer);
		}

		// Write the freeze values.
		buffer.writeBoolean(recipe.hasFreezeBehaviour());
		if (recipe.hasFreezeBehaviour()) {
			recipe.getFreezingBehaviour().toNetwork(buffer);
		}
	}
}
