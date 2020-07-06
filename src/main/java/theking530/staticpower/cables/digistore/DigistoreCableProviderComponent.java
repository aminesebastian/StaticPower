package theking530.staticpower.cables.digistore;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.tileentities.TileEntityBase;

public class DigistoreCableProviderComponent extends AbstractCableProviderComponent {

	public DigistoreCableProviderComponent(String name) {
		super(name, CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return attachment.getItem() == ModItems.DigistoreTerminalAttachment;
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, @Nullable TileEntity te, BlockPos blockPosition) {
		if (te instanceof TileEntityDigistoreWire) {
			return CableConnectionState.CABLE;
		} else if (te instanceof TileEntityBase) {
			TileEntityBase baseTe = (TileEntityBase) te;
			if (baseTe.hasComponentOfType(DigistoreCableProviderComponent.class)) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

}
