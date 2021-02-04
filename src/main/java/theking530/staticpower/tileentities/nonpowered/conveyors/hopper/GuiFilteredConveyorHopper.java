package theking530.staticpower.tileentities.nonpowered.conveyors.hopper;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiFilteredConveyorHopper extends StaticPowerTileEntityGui<ContainerFilteredConveyorHopper, TileEntityConveyorHopper> {
	@SuppressWarnings("unused")
	private GuiInfoTab infoTab;

	public GuiFilteredConveyorHopper(ContainerFilteredConveyorHopper container, PlayerInventory invPlayer, ITextComponent name) {
		super(container, invPlayer, name, 176, 125);
	}

	@Override
	public void initializeGui() {
		tabManager.registerTab(infoTab = new GuiInfoTab(getTitle(), 100));
	}
}
