package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.cables.ServerCable.CableConnectionState;
import theking530.staticpower.tileentities.cables.network.modules.factories.CableNetworkModuleTypes;

public class DigistoreCableProviderComponent extends AbstractCableProviderComponent {

	public DigistoreCableProviderComponent(String name) {
		super(name, CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, BlockPos blockPosition) {
		// Check to see if there is a cable on this side that can connect to this one.
		// If true, connect. If not, check if there is a TE that we can connect to. If
		// not, return non.
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (otherProvider != null && otherProvider.shouldConnectionToCable(this, side)) {
			return CableConnectionState.CABLE;
		} else if (getWorld().getTileEntity(blockPosition) instanceof TileEntityBase && otherProvider == null) {
			TileEntityBase te = (TileEntityBase) getWorld().getTileEntity(blockPosition);
			if (te.hasComponentOfType(DigistoreCableProviderComponent.class)) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

}
