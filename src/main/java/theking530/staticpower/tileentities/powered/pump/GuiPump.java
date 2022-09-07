package theking530.staticpower.tileentities.powered.pump;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiPump extends StaticPowerTileEntityGui<ContainerPump, TileEntityPump> {
	@SuppressWarnings("unused")
	private GuiInfoTab infoTab;

	public GuiPump(ContainerPump container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 174);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 10, 16, 44));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 71, 20, 28, 52));
		registerWidget(new ArrowProgressBar(106, 20).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(infoTab = new GuiInfoTab(100));
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
	}
}
