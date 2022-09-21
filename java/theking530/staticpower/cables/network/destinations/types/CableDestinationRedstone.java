package theking530.staticpower.cables.network.destinations.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticpower.cables.network.destinations.CableDestination;

public class CableDestinationRedstone extends CableDestination {

	@Override
	public boolean match(Level level, BlockPos cablePosition, Direction cableSide, BlockPos blockPosition, Direction blockSide, BlockEntity entity) {
		// Check for redstone power.
		if (level.getBlockState(blockPosition).getDirectSignal(level, blockPosition, blockSide) > 0
				|| level.getBlockState(blockPosition).getDirectSignal(level, blockPosition, blockSide) > 0) {
			return true;
		}
		return false;
	}
}
