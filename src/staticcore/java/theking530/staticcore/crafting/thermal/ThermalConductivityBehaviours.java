package theking530.staticcore.crafting.thermal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import theking530.staticcore.crafting.StaticPowerOutputItem;

public class ThermalConductivityBehaviours {
	private final int temperature;
	private final BlockState block;
	private final StaticPowerOutputItem itemstack;
	private final boolean destroyExisting;

	public ThermalConductivityBehaviours(int temperature, BlockState block, StaticPowerOutputItem itemstack,
			boolean destroyExisting) {
		this.temperature = temperature;
		this.block = block;
		this.itemstack = itemstack;
		this.destroyExisting = destroyExisting;
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

	public boolean shouldDestroyExisting() {
		return destroyExisting;
	}

	public boolean hasBlock() {
		return block != Blocks.AIR.defaultBlockState();
	}

	public boolean hasItem() {
		return !itemstack.isEmpty();
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeInt(getTemperature());
		buffer.writeBoolean(shouldDestroyExisting());
		buffer.writeNbt(NbtUtils.writeBlockState(getBlockState()));
		getItem().writeToBuffer(buffer);
	}

	public static class OverheatingBehaviour extends ThermalConductivityBehaviours {

		public static final Codec<OverheatingBehaviour> CODEC = RecordCodecBuilder.create(instance -> instance
				.group(Codec.INT.optionalFieldOf("temperature", 0).forGetter(recipe -> recipe.getTemperature()),
						BlockState.CODEC.optionalFieldOf("block", Blocks.AIR.defaultBlockState())
								.forGetter(recipe -> recipe.getBlockState()),
						StaticPowerOutputItem.CODEC
								.optionalFieldOf("item", StaticPowerOutputItem.EMPTY)
								.forGetter(recipe -> recipe.getItem()),
						Codec.BOOL.optionalFieldOf("destroyExisting", true)
								.forGetter(recipe -> recipe.shouldDestroyExisting()))
				.apply(instance, OverheatingBehaviour::new));

		public OverheatingBehaviour(int temperature, Block block) {
			this(temperature, block.defaultBlockState(), StaticPowerOutputItem.EMPTY);
		}

		public OverheatingBehaviour(int temperature, BlockState block) {
			this(temperature, block, StaticPowerOutputItem.EMPTY);
		}

		public OverheatingBehaviour(int temperature, StaticPowerOutputItem itemstack) {
			this(temperature, Blocks.AIR.defaultBlockState(), itemstack);
		}

		public OverheatingBehaviour(int temperature, Fluid fluid) {
			this(temperature, fluid.getStateDefinition().any().createLegacyBlock(), StaticPowerOutputItem.EMPTY);
		}

		public OverheatingBehaviour(int temperature) {
			this(temperature, Blocks.AIR.defaultBlockState(), StaticPowerOutputItem.EMPTY);
		}

		public OverheatingBehaviour(int temperature, BlockState block, StaticPowerOutputItem itemstack) {
			this(temperature, block, itemstack, true);
		}

		public OverheatingBehaviour(int temperature, BlockState block, StaticPowerOutputItem itemstack,
				boolean destroy) {
			super(temperature, block, itemstack, destroy);
		}

		public static OverheatingBehaviour fromNetwork(FriendlyByteBuf buffer) {
			int freezingTemperature = buffer.readInt();
			boolean shouldDestroy = buffer.readBoolean();
			BlockState freezingBlock = NbtUtils.readBlockState(buffer.readNbt());
			StaticPowerOutputItem freezingItemStack = StaticPowerOutputItem.readFromBuffer(buffer);
			return new OverheatingBehaviour(freezingTemperature, freezingBlock, freezingItemStack, shouldDestroy);
		}
	}

	public static class FreezingBehaviour extends ThermalConductivityBehaviours {
		public static final Codec<FreezingBehaviour> CODEC = RecordCodecBuilder.create(instance -> instance
				.group(Codec.INT.optionalFieldOf("temperature", 0).forGetter(recipe -> recipe.getTemperature()),
						BlockState.CODEC.optionalFieldOf("block", Blocks.AIR.defaultBlockState())
								.forGetter(recipe -> recipe.getBlockState()),
						StaticPowerOutputItem.CODEC
								.optionalFieldOf("item", StaticPowerOutputItem.EMPTY)
								.forGetter(recipe -> recipe.getItem()),
						Codec.BOOL.optionalFieldOf("destroyExisting", true)
								.forGetter(recipe -> recipe.shouldDestroyExisting()))
				.apply(instance, FreezingBehaviour::new));

		public FreezingBehaviour(int temperature, Block block) {
			this(temperature, block.defaultBlockState(), StaticPowerOutputItem.EMPTY);

		}

		public FreezingBehaviour(int temperature, BlockState block) {
			this(temperature, block, StaticPowerOutputItem.EMPTY);
		}

		public FreezingBehaviour(int temperature, StaticPowerOutputItem itemstack) {
			this(temperature, Blocks.AIR.defaultBlockState(), itemstack);
		}

		public FreezingBehaviour(int temperature, Fluid fluid) {
			this(temperature, fluid.getStateDefinition().any().createLegacyBlock(), StaticPowerOutputItem.EMPTY);
		}

		public FreezingBehaviour(int temperature) {
			this(temperature, Blocks.AIR.defaultBlockState(), StaticPowerOutputItem.EMPTY);
		}

		public FreezingBehaviour(int temperature, BlockState block, StaticPowerOutputItem itemstack) {
			this(temperature, block, itemstack, true);
		}

		public FreezingBehaviour(int temperature, BlockState block, StaticPowerOutputItem itemstack, boolean destroy) {
			super(temperature, block, itemstack, destroy);
		}

		public static FreezingBehaviour fromNetwork(FriendlyByteBuf buffer) {
			int freezingTemperature = buffer.readInt();
			boolean shouldDestroy = buffer.readBoolean();
			BlockState freezingBlock = NbtUtils.readBlockState(buffer.readNbt());
			StaticPowerOutputItem freezingItemStack = StaticPowerOutputItem.readFromBuffer(buffer);
			return new FreezingBehaviour(freezingTemperature, freezingBlock, freezingItemStack, shouldDestroy);
		}
	}
}
