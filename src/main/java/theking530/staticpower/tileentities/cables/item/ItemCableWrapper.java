package theking530.staticpower.tileentities.cables.item;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.cables.CableType;

public class ItemCableWrapper extends AbstractCableWrapper {

	public ItemCableWrapper(World world, BlockPos position) {
		super(world, position, CableType.BASIC_POWER);
	}

	@Override
	public boolean isAttachedOnSide(Direction direction) {
		return false;
	}

	@Override
	public boolean isConnectedToCableOnSide(Direction direction) {
		return false;
	}
}
