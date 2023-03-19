package theking530.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface IBreakSerializeable {
	public static final String SERIALIZEABLE_NBT = "SerializableNbt";

	public CompoundTag serializeOnBroken(CompoundTag nbt);

	public void deserializeOnPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack);

	public boolean shouldSerializeWhenBroken(Player player);

	public boolean shouldDeserializeWhenPlaced(CompoundTag nbt, Level world, BlockPos pos, BlockState state,
			LivingEntity placer, ItemStack stack);

	public static void deserializeToTileEntity(Level world, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack) {
		// Check to make sure there is a tile entity, the stack has a tag, and that tag
		// contains the serializeable data.
		if (world.getBlockEntity(pos) != null && stack.hasTag() && stack.getTag().contains(SERIALIZEABLE_NBT)) {
			// Get the tile entity at that location.
			BlockEntity te = world.getBlockEntity(pos);

			// If it is break serializeable, get an instance.
			if (te instanceof IBreakSerializeable) {
				IBreakSerializeable serializeable = (IBreakSerializeable) te;

				// Get the serialize nbt.
				CompoundTag serializeNbt = stack.getTag().getCompound(SERIALIZEABLE_NBT);

				// Perform the deserialization.
				serializeable.deserializeOnPlaced(serializeNbt, world, pos, state, placer, stack);
			}
		}
	}

	public static ItemStack createItemDrop(Block block, Player player, BlockGetter world, BlockPos pos) {
		// Create a new itemstack to represent this block.
		ItemStack blockStack = new ItemStack(block.asItem());

		// If there is a tile entity that is serializeable, get it.
		if (world.getBlockEntity(pos) != null && world.getBlockEntity(pos) instanceof IBreakSerializeable) {
			// Get a handle to the serializeable tile entity.
			IBreakSerializeable tempSerializeable = (IBreakSerializeable) world.getBlockEntity(pos);

			if (tempSerializeable.shouldSerializeWhenBroken(player)) {
				// Create a new nbt to hold our serializeable data.
				CompoundTag serializeabltNbt = new CompoundTag();

				// Serialize the tile entity and then store it on the serializeabltNbt.
				tempSerializeable.serializeOnBroken(serializeabltNbt);

				// Then, add the serializeabltNbt to the itemstack.
				blockStack.getOrCreateTag().put(IBreakSerializeable.SERIALIZEABLE_NBT, serializeabltNbt);
			}
		}

		// Return the output.
		return blockStack;
	}

	public static boolean doesItemStackHaveSerializeData(ItemStack stack) {
		return stack.hasTag() && stack.getTag().contains(SERIALIZEABLE_NBT);
	}

	public static CompoundTag getSerializeDataFromItemStack(ItemStack stack) {
		if (doesItemStackHaveSerializeData(stack)) {
			return stack.getTag().getCompound(SERIALIZEABLE_NBT);
		}
		return null;
	}
}
