package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class ThermalConductivityRecipeSerializer extends StaticPowerRecipeSerializer<ThermalConductivityRecipe> {
	public static final ThermalConductivityRecipeSerializer INSTANCE = new ThermalConductivityRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "thermal_conducitity");

	@Override
	public ThermalConductivityRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
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

		// Capture the conductivity.
		float thermalConductivity = json.get("conductivity").getAsFloat();

		// Capture the heating amount.
		float heatAmount = 0.0f;
		if (json.has("heating_amount")) {
			heatAmount = json.get("heating_amount").getAsFloat();
		}

		// Allocate the overheating values.
		BlockState overheatedBlock = Blocks.VOID_AIR.defaultBlockState();
		ProbabilityItemStackOutput overheatedItemStack = ProbabilityItemStackOutput.EMPTY;
		float overheatTemperature = Integer.MAX_VALUE;

		// Read the overheating values if they are defined.
		if (json.has("overheating")) {
			// Get the overheating object.
			JsonObject overheatingElement = json.get("overheating").getAsJsonObject();

			// Handle the block.
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

			// Handle the item.
			if (overheatingElement.has("item")) {
				overheatedItemStack = ProbabilityItemStackOutput.parseFromJSON(overheatingElement.get("item").getAsJsonObject());
			}

			// Get the temperature.
			overheatTemperature = overheatingElement.get("temperature").getAsFloat();
		}

		// Create the recipe.
		return new ThermalConductivityRecipe(recipeId, blocks, fluids, overheatedBlock, overheatedItemStack, overheatTemperature, thermalConductivity, heatAmount);
	}

	@Override
	public ThermalConductivityRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		// Read the float values.
		float conductivity = buffer.readFloat();
		float supply = buffer.readFloat();
		// Read the over heat values.
		float overheatTemp = buffer.readFloat();
		ProbabilityItemStackOutput overheatedItemStack = ProbabilityItemStackOutput.readFromBuffer(buffer);
		BlockState overheatedBlock = NbtUtils.readBlockState(buffer.readNbt());

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

		return new ThermalConductivityRecipe(recipeId, blocks, fluids, overheatedBlock, overheatedItemStack, overheatTemp, conductivity, supply);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, ThermalConductivityRecipe recipe) {
		// Write the float values.
		buffer.writeFloat(recipe.getThermalConductivity());
		buffer.writeFloat(recipe.getHeatAmount());

		// Write the over heat values.
		buffer.writeFloat(recipe.getOverheatedTemperature());
		recipe.getOverheatedItem().writeToBuffer(buffer);
		buffer.writeNbt(NbtUtils.writeBlockState(recipe.getOverheatedBlock()));

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

	@Override
	public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
		return INSTANCE;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return ID;
	}
}
