package theking530.staticpower.cables.digistore;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.tileentities.TileEntityBase;

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
		if (getWorld().getTileEntity(blockPosition) instanceof TileEntityDigistoreWire) {
			return CableConnectionState.CABLE;
		} else if (getWorld().getTileEntity(blockPosition) instanceof TileEntityBase) {
			TileEntityBase te = (TileEntityBase) getWorld().getTileEntity(blockPosition);
			if (te.hasComponentOfType(DigistoreCableProviderComponent.class)) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

}
