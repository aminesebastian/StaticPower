package theking530.staticpower.blockentities.machines.vulcanizer;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiMachinePowerInfoTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.tabs.slottabs.GuiFluidContainerTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiPowerBarFromStorage;

public class GuiVulcanizer extends StaticCoreBlockEntityScreen<ContainerVulcanizer, BlockEntityVulcanizer> {
	private FluidProgressBar progressBar;

	public GuiVulcanizer(ContainerVulcanizer container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiPowerBarFromStorage(getTileEntity().powerStorage, 8, 8, 16, 52));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().fluidTankComponent, 50, 22, 20, 54,
				MachineSideMode.Input, getTileEntity()));
		registerWidget(progressBar = (FluidProgressBar) new FluidProgressBar(76, 45, 32, 5)
				.bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(
				new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));
		getTabManager().registerTab(new GuiMachinePowerInfoTab(getTileEntity().powerStorage).setTabSide(TabSide.LEFT),
				true);
		getTabManager()
				.registerTab(new GuiMachineFluidTab(getTileEntity().fluidTankComponent).setTabSide(TabSide.LEFT));
		getTabManager().registerTab(
				new GuiFluidContainerTab(this.menu, getTileEntity().fluidContainerComponent).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		super.updateData();
		if (getTileEntity().processingComponent.hasProcessingStarted()) {
			FluidStack fluid = getTileEntity().fluidTankComponent.getFluid();
			progressBar.setFluidStack(fluid);
		} else {
			progressBar.setFluidStack(FluidStack.EMPTY);
		}
	}
}
