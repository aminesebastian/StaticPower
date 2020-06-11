package theking530.staticpower.tileentities.components;

import net.minecraft.util.ResourceLocation;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.network.CableNetworkManager;
import theking530.staticpower.tileentities.network.factories.cables.CableWrapperRegistry;

public class CableWrapperProviderComponent extends AbstractTileEntityComponent {
	private ResourceLocation Type;

	public CableWrapperProviderComponent(String name, ResourceLocation type) {
		super(name);
		Type = type;
	}

	public ResourceLocation getCableType() {
		return Type;
	}

	@Override
	public void onOwningTileEntityValidate() {
		super.onOwningTileEntityValidate();
		// If we're on the server, check to see if the cable network manager for this
		// world is tracking a cable at this position. If it is not, add this cable for
		// tracking.
		if (!getTileEntity().getWorld().isRemote) {
			CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
			if (!manager.isTrackingCable(getTileEntity().getPos())) {
				AbstractCableWrapper wrapper = CableWrapperRegistry.get().create(Type, getTileEntity().getWorld(), getTileEntity().getPos());
				if (wrapper != null) {
					manager.addCable(wrapper);
				} else {
					throw new RuntimeException(String.format("Cable supplier for TileEntity at Position: %1$s supplied a null CableWrapper.", getTileEntity().getPos()));
				}
			}
		}
	}

	@Override
	public void onOwningTileEntityRemoved() {
		super.onOwningTileEntityRemoved();
		// If we're on the server, get the cable manager and remove the cable at the
		// current position.
		if (!getTileEntity().getWorld().isRemote) {
			CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
			manager.removeCable(getTileEntity().getPos());
		}
	}
}
