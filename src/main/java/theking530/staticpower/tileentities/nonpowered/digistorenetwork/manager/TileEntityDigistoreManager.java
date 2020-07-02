package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreManager extends BaseDigistoreTileEntity {

	public TileEntityDigistoreManager() {
		super(ModTileEntityTypes.DIGISTORE_MANAGER);
	}
	
	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistoreManager(windowId, inventory, this);
	}
}
