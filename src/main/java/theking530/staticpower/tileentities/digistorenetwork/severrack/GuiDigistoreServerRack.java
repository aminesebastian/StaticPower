package theking530.staticpower.tileentities.digistorenetwork.severrack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiDigistoreServerRack extends StaticPowerTileEntityGui<ContainerDigistoreServerRack, TileEntityDigistoreServerRack> {

	public GuiDigistoreServerRack(ContainerDigistoreServerRack container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 185);
	}

	@Override
	public void initializeGui() {

	}
}
