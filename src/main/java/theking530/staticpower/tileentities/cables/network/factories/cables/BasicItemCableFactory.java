package theking530.staticpower.tileentities.cables.network.factories.cables;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.cables.item.ItemCableWrapper;

public class BasicItemCableFactory implements ICableWrapperFactory {

	@Override
	public AbstractCableWrapper create(World world, CompoundNBT nbt) {
		return new ItemCableWrapper(world, BlockPos.fromLong(nbt.getLong("position")));
	}

	@Override
	public AbstractCableWrapper create(World world, BlockPos pos) {
		return new ItemCableWrapper(world, pos);
	}
}
