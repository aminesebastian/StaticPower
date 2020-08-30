package theking530.staticpower.tileentities.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBreakSerializeable {
	public static final  String SERIALIZEABLE_NBT = "SerializableNbt";

	public CompoundNBT serializeOnBroken(CompoundNBT nbt);

	public void deserializeOnPlaced(CompoundNBT nbt, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack);

	public boolean shouldSerializeWhenBroken();

	public boolean shouldDeserializeWhenPlaced(CompoundNBT nbt, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack);
}
