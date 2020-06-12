package theking530.staticpower.tileentities.cables.item;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.network.factories.cables.CableTypes;

public class ItemCableWrapper extends AbstractCableWrapper {

	public ItemCableWrapper(World world, BlockPos position) {
		super(world, position, CableTypes.BASIC_ITEM);
	}

	@Override
	public CableAttachmentState getSideAttachmentType(Direction direction) {
		return CableAttachmentState.NONE;
	}
}
