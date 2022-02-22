package theking530.staticpower.tileentities.powered.enchanter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import theking530.staticcore.gui.widgets.progressbars.ArrowProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class GuiEnchanter extends StaticPowerTileEntityGui<ContainerEnchanter, TileEntityEnchanter> {

	public GuiEnchanter(ContainerEnchanter container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 8, 16, 52));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 65, 22, 20, 54, MachineSideMode.Input, getTileEntity()));
		registerWidget(new ArrowProgressBar(120, 40).bindToMachineProcessingComponent(getTileEntity().processingComponent));
		
		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiFluidContainerTab(this.menu, getTileEntity().fluidContainerComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}
}
