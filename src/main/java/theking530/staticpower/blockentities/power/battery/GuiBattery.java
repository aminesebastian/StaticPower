package theking530.staticpower.blockentities.power.battery;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiBattery extends StaticPowerTileEntityGui<ContainerBattery, BlockEntityBattery> {
	protected GuiInfoTab infoTab;

	public GuiBattery(ContainerBattery container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 75, 20, 26, 51));

		getTabManager().registerTab(infoTab = new GuiInfoTab("Battery", 120));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
	}
}
