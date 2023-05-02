package theking530.staticcore.crafting.thermal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.crafting.StaticPowerOutputItem;

public class ThermalConductivityBehaviours {
	private final int temperature;
	private final BlockState block;
	private final StaticPowerOutputItem itemstack;

	public ThermalConductivityBehaviours(int temperature, BlockState block, StaticPowerOutputItem itemstack) {
		this.temperature = temperature;
		this.block = block;
		this.itemstack = itemstack;
	}

	public int getTemperature() {
		return temperature;
	}

	public BlockState getBlockState() {
		return block;
	}

	public StaticPowerOutputItem getItem() {
		return itemstack;
	}

	public boolean hasBlock() {
		return block != Blocks.AIR.defaultBlockState();
	}

	public boolean hasItem() {
		return !itemstack.isEmpty();
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeInt(getTemperature());
		buffer.writeNbt(NbtUtils.writeBlockState(getBlockState()));
		getItem().writeToBuffer(buffer);
	}

	public static class OverheatingBehaviour extends ThermalConductivityBehaviours {

		public static final Codec<OverheatingBehaviour> CODEC = RecordCodecBuilder
				.create(instance -> instance
						.group(Codec.INT.optionalFieldOf("temperature", 0).forGetter(recipe -> recipe.getTemperature()),
								BlockState.CODEC.optionalFieldOf("block", Blocks.AIR.defaultBlockState())
										.forGetter(recipe -> recipe.getBlockState()),
								StaticPowerOutputItem.CODEC.optionalFieldOf("item", StaticPowerOutputItem.EMPTY)
										.forGetter(recipe -> recipe.getItem()))
						.apply(instance, OverheatingBehaviour::new));

		public OverheatingBehaviour(int temperature, BlockState block, StaticPowerOutputItem itemstack) {
			super(temperature, block, itemstack);
		}

		public static OverheatingBehaviour fromNetwork(FriendlyByteBuf buffer) {
			int freezingTemperature = buffer.readInt();
			BlockState freezingBlock = NbtUtils.readBlockState(buffer.readNbt());
			StaticPowerOutputItem freezingItemStack = StaticPowerOutputItem.readFromBuffer(buffer);
			return new OverheatingBehaviour(freezingTemperature, freezingBlock, freezingItemStack);
		}
	}

	public static class FreezingBehaviour extends ThermalConductivityBehaviours {
		public static final Codec<FreezingBehaviour> CODEC = RecordCodecBuilder
				.create(instance -> instance
						.group(Codec.INT.optionalFieldOf("temperature", 0).forGetter(recipe -> recipe.getTemperature()),
								BlockState.CODEC.optionalFieldOf("block", Blocks.AIR.defaultBlockState())
										.forGetter(recipe -> recipe.getBlockState()),
								StaticPowerOutputItem.CODEC.optionalFieldOf("item", StaticPowerOutputItem.EMPTY)
										.forGetter(recipe -> recipe.getItem()))
						.apply(instance, FreezingBehaviour::new));

		public FreezingBehaviour(int temperature, BlockState block, StaticPowerOutputItem itemstack) {
			super(temperature, block, itemstack);
		}

		public static FreezingBehaviour fromNetwork(FriendlyByteBuf buffer) {
			int freezingTemperature = buffer.readInt();
			BlockState freezingBlock = NbtUtils.readBlockState(buffer.readNbt());
			StaticPowerOutputItem freezingItemStack = StaticPowerOutputItem.readFromBuffer(buffer);
			return new FreezingBehaviour(freezingTemperature, freezingBlock, freezingItemStack);
		}
	}
}
