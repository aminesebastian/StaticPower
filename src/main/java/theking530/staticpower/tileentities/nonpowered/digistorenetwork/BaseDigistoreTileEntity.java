package theking530.staticpower.tileentities.nonpowered.digistorenetwork;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.TileEntityDigistoreManager;

public abstract class BaseDigistoreTileEntity extends TileEntityBase {

	private TileEntityDigistoreManager manager;

	public BaseDigistoreTileEntity(TileEntityType<?> teType) {
		super(teType);
	}

	public void setManager(TileEntityDigistoreManager newManager) {
		manager = newManager;
	}

	public TileEntityDigistoreManager getManager() {
		if (manager != null && !manager.getNetwork().getAllNetworkTiles().containsKey(getPos())) {
			manager = null;
		}
		return manager;
	}

	public boolean isManaged() {
		return getManager() != null;
	}
}
