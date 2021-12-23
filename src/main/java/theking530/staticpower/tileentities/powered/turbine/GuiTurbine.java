package theking530.staticpower.tileentities.powered.turbine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import theking530.staticcore.gui.widgets.progressbars.FireProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiUpgradeTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromEnergyStorage;
import theking530.staticpower.client.gui.StaticPowerTileEntityGui;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipe;
import theking530.staticpower.tileentities.components.control.AbstractProcesingComponent.ProcessingCheckState;
import theking530.staticpower.tileentities.components.control.RedstoneControlComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;

public class GuiTurbine extends StaticPowerTileEntityGui<ContainerTurbine, TileEntityTurbine> {
	private FireProgressBar progressBar;

	public GuiTurbine(ContainerTurbine container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 166);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromEnergyStorage(getTileEntity().energyStorage.getStorage(), 8, 12, 16, 64));
		registerWidget(progressBar = new FireProgressBar(80, 58));

		registerWidget(new GuiFluidBarFromTank(getTileEntity().inputFluidTankComponent, 50, 18, 16, 58, MachineSideMode.Output, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().outputFluidTankComponent, 110, 18, 16, 58, MachineSideMode.Output, getTileEntity()));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().energyStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().inputFluidTankComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(new GuiUpgradeTab(menu, getTileEntity().upgradesInventory).setTabSide(TabSide.LEFT));

		setInputSlotSize(24);
	}

	@Override
	public void updateData() {
		// Get the recipe if there is one.
		TurbineRecipe recipe = this.getTileEntity().getRecipe().orElse(null);

		// If there is one, update the error states.
		if (recipe != null) {
			ProcessingCheckState state = this.getTileEntity().getProcessingState(recipe);
			progressBar.setErrorState(state.isError());
			progressBar.setErrorMessage(state.getErrorMessage());
		}
	}
}
