package theking530.staticpower.blockentities.digistorenetwork.severrack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;

public class GuiDigistoreServerRack extends StaticCoreBlockEntityScreen<ContainerDigistoreServerRack, BlockEntityDigistoreServerRack> {

	public GuiDigistoreServerRack(ContainerDigistoreServerRack container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 192);
	}

	@Override
	public void initializeGui() {

	}
}
