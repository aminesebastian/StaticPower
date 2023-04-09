package theking530.staticcore.productivity.product.power;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class PowerProductInterfaceId {
	private final Block blockSource;

	public PowerProductInterfaceId(Block blockSource) {
		this.blockSource = blockSource;
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putString("block", ForgeRegistries.BLOCKS.getKey(blockSource).toString());
		return output;
	}

	public static PowerProductInterfaceId deserialize(CompoundTag tag) {
		ResourceLocation key = new ResourceLocation(tag.getString("block"));
		return new PowerProductInterfaceId(ForgeRegistries.BLOCKS.getValue(key));
	}

	public Block getBlockSource() {
		return blockSource;
	}

	public int getProductHashCode() {
		return ForgeRegistries.BLOCKS.getKey(blockSource).hashCode();
	}
}
