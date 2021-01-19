package theking530.staticpower.tileentities.interfaces;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;

public interface IBreakSerializeable {
	public static final String SERIALIZEABLE_NBT = "SerializableNbt";

	public CompoundNBT serializeOnBroken(CompoundNBT nbt);

	public void deserializeOnPlaced(CompoundNBT nbt, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack);

	public boolean shouldSerializeWhenBroken();

	public boolean shouldDeserializeWhenPlaced(CompoundNBT nbt, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack);

	public static void deserializeToTileEntity(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		// Wrap in try catch to be safe.
		try {
			// Check to make sure there is a tile entity, the stack has a tag, and that tag
			// contains the serializeable data.
			if (world.getTileEntity(pos) != null && stack.hasTag() && stack.getTag().contains(SERIALIZEABLE_NBT)) {
				// Get the tile entity at that location.
				TileEntity te = world.getTileEntity(pos);

				// If it is break serializeable, get an instance.
				if (te instanceof IBreakSerializeable) {
					IBreakSerializeable serializeable = (IBreakSerializeable) te;

					// Get the serialize nbt.
					CompoundNBT serializeNbt = stack.getTag().getCompound(SERIALIZEABLE_NBT);

					// Perform the deserialization.
					serializeable.deserializeOnPlaced(serializeNbt, world, pos, state, placer, stack);
				}
			}
		} catch (Exception e) {
			StaticPower.LOGGER.error(String.format("Failed to deserialize instance of IBreakSerializeable at location: %1$s using ItemStack: %2$s.", pos, stack), e);
		}
	}

	public static ItemStack createItemDrop(Block block, PlayerEntity player, IBlockReader world, BlockPos pos) {
		// Create a new itemstack to represent this block.
		ItemStack blockStack = new ItemStack(block.asItem());

		// If there is a tile entity that is serializeable, get it.
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof IBreakSerializeable) {
			// Get a handle to the serializeable tile entity.
			IBreakSerializeable tempSerializeable = (IBreakSerializeable) world.getTileEntity(pos);

			if (tempSerializeable.shouldSerializeWhenBroken()) {
				// Create a new nbt to hold our serializeable data.
				CompoundNBT serializeabltNbt = new CompoundNBT();

				// Serialize the tile entity and then store it on the serializeabltNbt.
				tempSerializeable.serializeOnBroken(serializeabltNbt);

				// Then, add the serializeabltNbt to the itemstack.
				blockStack.getOrCreateTag().put(IBreakSerializeable.SERIALIZEABLE_NBT, serializeabltNbt);
			}
		}

		// Return the output.
		return blockStack;
	}
}
