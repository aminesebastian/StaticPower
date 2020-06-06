package theking530.staticpower.tileentities.nonpowered.digistorenetwork.ioport;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreIOPort extends BaseDigistoreTileEntity {

	public TileEntityDigistoreIOPort() {
		super(ModTileEntityTypes.CHARGING_STATION);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return null;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}
}
