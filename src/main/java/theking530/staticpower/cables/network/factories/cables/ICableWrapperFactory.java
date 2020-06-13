package theking530.staticpower.cables.network.factories.cables;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.cables.AbstractCableWrapper;

public interface ICableWrapperFactory {
	public AbstractCableWrapper create(World world, BlockPos pos);
	public AbstractCableWrapper create(World world, CompoundNBT nbt);
}
