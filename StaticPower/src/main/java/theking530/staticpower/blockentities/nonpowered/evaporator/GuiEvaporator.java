package theking530.staticpower.blockentities.nonpowered.evaporator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.blockentity.components.control.RedstoneControlComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.gui.screens.StaticCoreBlockEntityScreen;
import theking530.staticcore.gui.widgets.progressbars.FluidProgressBar;
import theking530.staticcore.gui.widgets.tabs.BaseGuiTab.TabSide;
import theking530.staticcore.gui.widgets.tabs.GuiMachineFluidTab;
import theking530.staticcore.gui.widgets.tabs.GuiPassiveHeatTab;
import theking530.staticcore.gui.widgets.tabs.GuiSideConfigTab;
import theking530.staticcore.gui.widgets.tabs.redstonecontrol.GuiTileEntityRedstoneTab;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarFromTank;
import theking530.staticcore.gui.widgets.valuebars.GuiHeatBarFromHeatStorage;

public class GuiEvaporator extends StaticCoreBlockEntityScreen<ContainerEvaporator, BlockEntityEvaporator> {
	private FluidProgressBar progressBar;

	public GuiEvaporator(ContainerEvaporator container, Inventory invPlayer, Component name) {
		super(container, invPlayer, name, 176, 178);
	}

	@Override
	public void initializeGui() {
		registerWidget(new GuiHeatBarFromHeatStorage(getTileEntity().heatStorage, 8, 18, 16, 62));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().inputTankComponent, 50, 20, 20, 58, MachineSideMode.Input, getTileEntity()));
		registerWidget(new GuiFluidBarFromTank(getTileEntity().outputTankComponent, 106, 20, 20, 58, MachineSideMode.Output, getTileEntity()));
		registerWidget(progressBar = (FluidProgressBar) new FluidProgressBar(75, 46, 26, 5).bindToMachineProcessingComponent(getTileEntity().processingComponent));

		getTabManager().registerTab(new GuiTileEntityRedstoneTab(getTileEntity().getComponent(RedstoneControlComponent.class)));
		getTabManager().registerTab(new GuiSideConfigTab(getTileEntity()));

		getTabManager().registerTab(new GuiPassiveHeatTab(getTileEntity().heatStorage).setTabSide(TabSide.LEFT), true);
		getTabManager().registerTab(new GuiMachineFluidTab(getTileEntity().inputTankComponent).setTabSide(TabSide.LEFT));

		setOutputSlotSize(20);
	}

	@Override
	public void updateData() {
		// If the recipe is non-null, render the fluid progress bar.
		if (getTileEntity().processingComponent.hasProcessingStarted()) {
			FluidStack fluid = getTileEntity().inputTankComponent.getFluid();
			progressBar.setFluidStack(fluid);
		} else {
			progressBar.setFluidStack(FluidStack.EMPTY);
		}
	}
}
