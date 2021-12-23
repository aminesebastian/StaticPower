package theking530.staticpower.tileentities.nonpowered.conveyors.hopper;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiFilteredConveyorHopper extends StaticPowerTileEntityGui<ContainerFilteredConveyorHopper, TileEntityConveyorHopper> {
	@SuppressWarnings("unused")
	private GuiInfoTab infoTab;

	public GuiFilteredConveyorHopper(ContainerFilteredConveyorHopper container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 125);
	}

	@Override
	public void initializeGui() {
		tabManager.registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
	}
}
