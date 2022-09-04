package theking530.staticpower.tileentities.powered.pumpjack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.tabs.GuiInfoTab;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;

public class GuiPumpJack extends StaticPowerTileEntityGui<ContainerPumpJack, TileEntityPumpJack> {
	@SuppressWarnings("unused")
	private GuiInfoTab infoTab;

	public GuiPumpJack(ContainerPumpJack container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 174);
	}

	@Override
	public void initializeGui() {
//		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage, 8, 10, 16, 44));
//		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 71, 20, 28, 52));
//		registerWidget(new ArrowProgressBar(106, 20).bindToMachineProcessingComponent(getTileEntity().processingComponent));
//
//		getTabManager().registerTab(infoTab = new GuiInfoTab(100));
//		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().redstoneControlComponent));
//		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
//
//		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
//		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
	}
}
