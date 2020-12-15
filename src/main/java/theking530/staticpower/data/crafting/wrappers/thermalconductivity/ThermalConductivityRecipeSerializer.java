package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;

public class ThermalConductivityRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ThermalConductivityRecipe> {
	public static final ThermalConductivityRecipeSerializer INSTANCE = new ThermalConductivityRecipeSerializer();

	private ThermalConductivityRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "thermal_conducitity"));
	}

	@Override
	public ThermalConductivityRecipe read(ResourceLocation recipeId, JsonObject json) {
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

		// Capture the conductivity.
		float thermalConductivity = json.get("conductivity").getAsFloat();

		// Capture the heating amount.
		float heatAmount = 0.0f;
		if (json.has("heating_amount")) {
			heatAmount = json.get("heating_amount").getAsFloat();
		}

		// Allocate the overheating values.
		BlockState overheatedBlock = Blocks.VOID_AIR.getDefaultState();
		ProbabilityItemStackOutput overheatedItemStack = ProbabilityItemStackOutput.EMPTY;
		float overheatTemperature = Integer.MAX_VALUE;

		// Read the overheating values if they are defined.
		if (json.has("overheating")) {
			// Get the overheating object.
			JsonObject overheatingElement = json.get("overheating").getAsJsonObject();

			// Handle the block.
			if (overheatingElement.has("block")) {
				CompoundNBT blockNBT = null;
				try {
					blockNBT = JsonToNBT.getTagFromJson(overheatingElement.get("block").toString());
					overheatedBlock = NBTUtil.readBlockState(blockNBT);
				} catch (CommandSyntaxException e) {
					StaticPower.LOGGER.error(String.format("An error occured when attempting to deserialize the value: %1$s into a BlockState.", overheatingElement.get("block").toString()),
							e);
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
	public ThermalConductivityRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// Read the float values.
		float conductivity = buffer.readFloat();
		float supply = buffer.readFloat();

		// Read the over heat values.
		float overheatTemp = buffer.readFloat();
		BlockState overheatedBlock = NBTUtil.readBlockState(buffer.readCompoundTag());
		ProbabilityItemStackOutput overheatedItemStack = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Read Blocks
		byte blockTagCount = buffer.readByte();
		ResourceLocation[] blocks = new ResourceLocation[blockTagCount];
		for (int i = 0; i < blockTagCount; i++) {
			blocks[i] = new ResourceLocation(buffer.readString());
		}

		// Read Fluids
		byte fluidTagCount = buffer.readByte();
		ResourceLocation[] fluids = new ResourceLocation[fluidTagCount];
		for (int i = 0; i < fluidTagCount; i++) {
			fluids[i] = new ResourceLocation(buffer.readString());
		}

		return new ThermalConductivityRecipe(recipeId, blocks, fluids, overheatedBlock, overheatedItemStack, overheatTemp, conductivity, supply);
	}

	@Override
	public void write(PacketBuffer buffer, ThermalConductivityRecipe recipe) {
		// Write the float values.
		buffer.writeFloat(recipe.getThermalConductivity());
		buffer.writeFloat(recipe.getHeatAmount());

		// Write the over heat values.
		buffer.writeFloat(recipe.getOverheatedTemperature());
		recipe.getOverheatedItem().writeToBuffer(buffer);
		buffer.writeCompoundTag(NBTUtil.writeBlockState(recipe.getOverheatedBlock()));

		// Write Blocks
		buffer.writeByte(recipe.getBlockTags().length);
		for (ResourceLocation tag : recipe.getBlockTags()) {
			buffer.writeString(tag.toString());
		}

		// Write Fluids
		buffer.writeByte(recipe.getFluidTags().length);
		for (ResourceLocation tag : recipe.getFluidTags()) {
			buffer.writeString(tag.toString());
		}
	}
}
