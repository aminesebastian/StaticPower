package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class ThermalConductivityRecipeSerializer extends StaticPowerRecipeSerializer<ThermalConductivityRecipe> {
	public static final ThermalConductivityRecipeSerializer INSTANCE = new ThermalConductivityRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "thermal_conducitity");

	@Override
	public ThermalConductivityRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Allocate the blocks.
		ResourceLocation[] blocks = new ResourceLocation[0];

		// Check for blocks.
		if (json.has("blocks")) {
			// Get the block tag array.
			JsonArray blocksArray = json.get("blocks").getAsJsonArray();
			blocks = new ResourceLocation[blocksArray.size()];
			for (int i = 0; i < blocks.length; i++) {
				blocks[i] = new ResourceLocation(blocksArray.get(i).getAsString());
			}
		}

		// Allocate the fluids.
		ResourceLocation[] fluids = new ResourceLocation[0];

		// Check for blocks.
		if (json.has("fluids")) {
			// Get the block tag array.
			JsonArray fluidsArray = json.get("fluids").getAsJsonArray();
			fluids = new ResourceLocation[fluidsArray.size()];
			for (int i = 0; i < fluids.length; i++) {
				fluids[i] = new ResourceLocation(fluidsArray.get(i).getAsString());
			}
		}

		// Make sure this is a one or the other kind of recipe.
		if (fluids.length > 0 && blocks.length > 0) {
			throw new RuntimeException(String.format("Recipe: %1$s cannot define both blocks and fluids in the same recipe.", recipeId.toString()));
		}

		// Capture the heating amount.
		int temperature = 0;
		boolean hasActiveTemperature = false;
		if (json.has("temperature")) {
			temperature = json.get("temperature").getAsInt();
			hasActiveTemperature = true;
		}

		// Allocate the overheating values.
		BlockState overheatedBlock = Blocks.VOID_AIR.defaultBlockState();
		StaticPowerOutputItem overheatedItemStack = StaticPowerOutputItem.EMPTY;
		int overheatTemperature = Integer.MAX_VALUE;

		// Read the overheating values if they are defined.
		if (json.has("overheating")) {
			JsonObject overheatingElement = json.get("overheating").getAsJsonObject();
			if (overheatingElement.has("block")) {
				CompoundTag blockNBT = null;
				try {
					blockNBT = TagParser.parseTag(overheatingElement.get("block").toString());
					overheatedBlock = NbtUtils.readBlockState(blockNBT);
				} catch (CommandSyntaxException e) {
					StaticPower.LOGGER.error(
							String.format("An error occured when attempting to deserialize the value: %1$s into a BlockState.", overheatingElement.get("block").toString()), e);
				}
			}
			if (overheatingElement.has("item")) {
				overheatedItemStack = StaticPowerOutputItem.parseFromJSON(overheatingElement.get("item").getAsJsonObject());
			}
			overheatTemperature = overheatingElement.get("temperature").getAsInt();
		}

		// Allocate the freezing values.
		BlockState freezingBlock = Blocks.VOID_AIR.defaultBlockState();
		StaticPowerOutputItem freezingItemStack = StaticPowerOutputItem.EMPTY;
		int freezingTemperature = Integer.MAX_VALUE;

		// Read the freezing values if they are defined.
		if (json.has("freezing")) {
			JsonObject freezingElement = json.get("overheating").getAsJsonObject();
			if (freezingElement.has("block")) {
				CompoundTag blockNBT = null;
				try {
					blockNBT = TagParser.parseTag(freezingElement.get("block").toString());
					freezingBlock = NbtUtils.readBlockState(blockNBT);
				} catch (CommandSyntaxException e) {
					StaticPower.LOGGER
							.error(String.format("An error occured when attempting to deserialize the value: %1$s into a BlockState.", freezingElement.get("block").toString()), e);
				}
			}
			if (freezingElement.has("item")) {
				freezingItemStack = StaticPowerOutputItem.parseFromJSON(freezingElement.get("item").getAsJsonObject());
			}
			freezingTemperature = freezingElement.get("temperature").getAsInt();
		}

		float conductivity = 1.0f;
		if (json.has("conductivity")) {
			conductivity = json.get("conductivity").getAsFloat();
		}

		// Create the recipe.
		return new ThermalConductivityRecipe(recipeId, blocks, fluids, overheatTemperature, overheatedBlock, overheatedItemStack, freezingTemperature, freezingBlock,
				freezingItemStack, temperature, hasActiveTemperature, conductivity);
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
		ResourceLocation[] blocks = new ResourceLocation[blockTagCount];
		for (int i = 0; i < blockTagCount; i++) {
			blocks[i] = new ResourceLocation(buffer.readUtf());
		}

		// Read Fluids
		byte fluidTagCount = buffer.readByte();
		ResourceLocation[] fluids = new ResourceLocation[fluidTagCount];
		for (int i = 0; i < fluidTagCount; i++) {
			fluids[i] = new ResourceLocation(buffer.readUtf());
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
		buffer.writeByte(recipe.getBlockTags().length);
		for (ResourceLocation tag : recipe.getBlockTags()) {
			buffer.writeUtf(tag.toString());
		}

		// Write Fluids
		buffer.writeByte(recipe.getFluidTags().length);
		for (ResourceLocation tag : recipe.getFluidTags()) {
			buffer.writeUtf(tag.toString());
		}
	}
}
