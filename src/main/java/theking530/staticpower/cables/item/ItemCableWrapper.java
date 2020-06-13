package theking530.staticpower.cables.item;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.cables.AbstractCableWrapper;
import theking530.staticpower.cables.network.factories.cables.CableTypes;

public class ItemCableWrapper extends AbstractCableWrapper {

	public ItemCableWrapper(World world, BlockPos position) {
		super(world, position, CableTypes.BASIC_ITEM);
	}
}
