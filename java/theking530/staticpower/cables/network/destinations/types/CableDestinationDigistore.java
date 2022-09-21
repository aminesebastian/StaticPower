package theking530.staticpower.cables.network.destinations.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.digistore.DigistoreCableProviderComponent;
import theking530.staticpower.cables.network.destinations.CableDestination;

public class CableDestinationDigistore extends CableDestination {

	@Override
	public boolean match(Level level, BlockPos cablePosition, Direction cableSide, BlockPos blockPosition, Direction blockSide, BlockEntity entity) {
		if (entity != null) {
			if (entity.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY, blockSide).isPresent()) {
				return true;
			}

			// TODO: This should be a capability check!
			BlockEntityBase baseTe = (BlockEntityBase) entity;
			if (baseTe != null) {
				if (baseTe.hasComponentOfType(DigistoreCableProviderComponent.class)) {
					return true;
				}
			}

		}
		return false;
	}
}
