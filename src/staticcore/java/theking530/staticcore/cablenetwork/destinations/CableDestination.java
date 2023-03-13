package theking530.staticcore.cablenetwork.destinations;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class CableDestination {

	public abstract boolean match(Level level, BlockPos cablePosition, Direction cableSide, BlockPos blockPosition, Direction blockSide, @Nullable BlockEntity entity);

}
