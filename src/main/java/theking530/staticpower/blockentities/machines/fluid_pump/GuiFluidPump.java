package theking530.staticpower.blockentities.machines.fluid_pump;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiFluidPump extends StaticPowerTileEntityGui<ContainerFluidPump, BlockEntityFluidPump> {
	@SuppressWarnings("unused")
	private GuiInfoTab infoTab;

	public GuiFluidPump(ContainerFluidPump container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 72, 24, 28, 54, MachineSideMode.Input, getTileEntity()));
		tabManager.registerTab(infoTab = new GuiInfoTab(getTitle(), 100));

		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT), true);
	}
}
