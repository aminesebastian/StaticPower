package theking530.staticpower.blockentities.nonpowered.conveyors.hopper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;

public class GuiFilteredConveyorHopper extends StaticCoreBlockEntityScreen<ContainerFilteredConveyorHopper, BlockEntityConveyorHopper> {
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
