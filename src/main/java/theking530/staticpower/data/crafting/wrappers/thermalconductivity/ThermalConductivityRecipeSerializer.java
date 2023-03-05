package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class ThermalConductivityRecipeSerializer extends StaticPowerRecipeSerializer<ThermalConductivityRecipe> {
	@Override
	public Codec<ThermalConductivityRecipe> getCodec() {
		return ThermalConductivityRecipe.CODEC;
	}

	@Override
	public ThermalConductivityRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		// Read the int values.
		int temperature = buffer.readInt();
		boolean hasActiveTemperature = buffer.readBoolean();

		// Read the float values.
		float conductivity = buffer.readFloat();

		// Read the over heat values.
		int overheatTemperature = buffer.readInt();
		BlockState overheatedBlock = NbtUtils.readBlockState(buffer.readNbt());
		StaticPowerOutputItem overheatedItemStack = StaticPowerOutputItem.readFromBuffer(buffer);

		// Read the freeze values.
		int freezingTemperature = buffer.readInt();
		BlockState freezingBlock = NbtUtils.readBlockState(buffer.readNbt());
		StaticPowerOutputItem freezingItemStack = StaticPowerOutputItem.readFromBuffer(buffer);

		// Read Blocks
		byte blockTagCount = buffer.readByte();
		List<ResourceLocation> blocks = new ArrayList<ResourceLocation>();

		for (int i = 0; i < blockTagCount; i++) {
			blocks.add(new ResourceLocation(buffer.readUtf()));
		}

		// Read Fluids
		byte fluidTagCount = buffer.readByte();
		List<ResourceLocation> fluids = new ArrayList<ResourceLocation>();
		for (int i = 0; i < fluidTagCount; i++) {
			fluids.add(new ResourceLocation(buffer.readUtf()));
		}

		return new ThermalConductivityRecipe(recipeId, blocks, fluids, overheatTemperature, overheatedBlock, overheatedItemStack, freezingTemperature, freezingBlock,
				freezingItemStack, temperature, hasActiveTemperature, conductivity);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, ThermalConductivityRecipe recipe) {
		// Write the int values.
		buffer.writeInt(recipe.getTemperature());
		buffer.writeBoolean(recipe.hasActiveTemperature());

		// Write the float values.
		buffer.writeFloat(recipe.getConductivity());

		// Write the over heat values.
		buffer.writeInt(recipe.getOverheatedTemperature());
		buffer.writeNbt(NbtUtils.writeBlockState(recipe.getOverheatedBlock()));
		recipe.getOverheatedItem().writeToBuffer(buffer);

		// Write the freeze values.
		buffer.writeInt(recipe.getFreezingTemperature());
		buffer.writeNbt(NbtUtils.writeBlockState(recipe.getFreezingBlock()));
		recipe.getFreezingItem().writeToBuffer(buffer);

		// Write Blocks
		buffer.writeByte(recipe.getBlockTags().size());
		for (ResourceLocation tag : recipe.getBlockTags()) {
			buffer.writeUtf(tag.toString());
		}

		// Write Fluids
		buffer.writeByte(recipe.getFluidTags().size());
		for (ResourceLocation tag : recipe.getFluidTags()) {
			buffer.writeUtf(tag.toString());
		}
	}
}
