package theking530.staticcore.cablenetwork.destinations.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import theking530.staticcore.cablenetwork.destinations.CableDestination;

public class CableDestinationCapability extends CableDestination {
	private final Capability<?> capability;

	public CableDestinationCapability(Capability<?> capability) {
		this.capability = capability;
	}

	public Capability<?> getCapability() {
		return capability;
	}

	@Override
	public boolean match(Level level, BlockPos cablePosition, Direction cableSide, BlockPos blockPosition, Direction blockSide, BlockEntity entity) {
		if (entity != null) {
			if (entity.getCapability(capability, blockSide).isPresent()) {
				return true;
			}
		}
		return false;
	}
}
