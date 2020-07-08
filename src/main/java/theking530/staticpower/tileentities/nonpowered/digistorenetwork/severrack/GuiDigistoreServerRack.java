package theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiDigistoreServerRack extends StaticPowerTileEntityGui<ContainerDigistoreServerRack, TileEntityDigistoreServerRack> {

	public GuiDigistoreServerRack(ContainerDigistoreServerRack container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {

	}
}
