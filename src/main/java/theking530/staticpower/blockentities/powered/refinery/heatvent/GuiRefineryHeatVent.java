package theking530.staticpower.blockentities.powered.refinery.heatvent;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;
import theking530.staticpower.blockentities.components.control.RedstoneControlComponent;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiRefineryHeatVent extends StaticPowerTileEntityGui<ContainerRefineryHeatVent, BlockEntityRefineryHeatVent> {

	public GuiRefineryHeatVent(ContainerRefineryHeatVent container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity(), 26, 8, 2, 52));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
	}
}
