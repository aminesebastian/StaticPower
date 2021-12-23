package theking530.staticpower.tileentities.nonpowered.directdropper;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiDirectDropper extends StaticPowerTileEntityGui<ContainerDirectDropper, TileEntityDirectDropper> {
	public GuiDirectDropper(ContainerDirectDropper container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
	}
}
